package nnhomoli.syncmyride.mixins;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.PacketSetRiding;
import net.minecraft.core.net.packet.PacketAddItemEntity;
import net.minecraft.core.net.packet.PacketRemoveEntity;
import net.minecraft.core.world.IVehicle;
import net.minecraft.server.entity.EntityTrackerEntryImpl;
import net.minecraft.server.entity.player.PlayerServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;

import static nnhomoli.syncmyride.lib.dummy.getDummy;
import static nnhomoli.syncmyride.SyncMyRide.getVehicleDelay;

@Mixin(value = EntityTrackerEntryImpl.class,remap = false,priority = 700)
abstract class entryImplMixin {
	@Shadow public abstract void removeTrackedPlayerSymmetric(Player player);
	@Shadow public abstract Entity getTrackedEntity();
	@Shadow public Set<PlayerServer> trackedPlayers;

	@Unique private final HashMap<UUID, List<Integer>> dummies = new HashMap<>();
	@Unique private final HashMap<UUID,Integer> dummyAge = new HashMap<>();
	@Unique private IVehicle lastTrackedVehicle = null;
	@Unique private int ticksRan = 0;

	@Inject(method = "tick",at=@At("RETURN"))
	public void tickStuff(List<Player> list, CallbackInfo ci) {
		if(lastTrackedVehicle != getTrackedEntity().vehicle) {
			updateVehicleForTrackedPlayers();
			if(getTrackedEntity() instanceof PlayerServer) updateVehicle((PlayerServer) getTrackedEntity());
			lastTrackedVehicle = getTrackedEntity().vehicle;
		}


		++ticksRan;
		if(ticksRan >= 100) {
			final ArrayList<UUID> needsUpdate = new ArrayList<>();
			final HashMap<UUID,Integer> tmp = new HashMap<>(dummyAge);
			for (UUID u : tmp.keySet()) {
				int age = tmp.get(u);
				age += ticksRan;
				tmp.put(u, age);
				if (age >= 5500) {
					needsUpdate.add(u);;
				}
			}

			dummyAge.putAll(tmp);

			for(UUID u : needsUpdate) {
				PlayerServer p = (PlayerServer) getTrackedEntity().world.getPlayerEntityByUUID(u);
				if (p != null && trackedPlayers.contains(p)) updateVehicle(p);
				else {
					dummyAge.remove(u);
					dummies.remove(u);
				}
			}

			ticksRan = 0;
		}
	}
	@Unique
	public void updateVehicleForTrackedPlayers() {
		for(PlayerServer p : trackedPlayers) {
			updateVehicle(p);
		}
	}
	@Unique
	public void removeDummiesForTrackedPlayers() {
		for(PlayerServer p : trackedPlayers) {
			removeDummies(p);
		}
	}
	@Unique
	public void removeDummies(PlayerServer p) {
		if(dummies.containsKey(p.uuid)) for(int dum : dummies.get(p.uuid)) p.playerNetServerHandler.sendPacket(new PacketRemoveEntity(dum));
		dummies.remove(p.uuid);
		dummyAge.remove(p.uuid);
	}
	@Inject(method = "removeTrackedPlayerSymmetric",at=@At("TAIL"))
	public void removeTrackedPlayerSymmetric(Player player, CallbackInfo ci) {
		if(!(player instanceof PlayerServer)) return;

		removeDummies((PlayerServer)player);
	}
	@Inject(method = "removeFromTrackedPlayers",at=@At("TAIL"))
	public void removeFromTrackedPlayers(Player player, CallbackInfo ci) {
		dummies.remove(player.uuid);
		dummyAge.remove(player.uuid);
	}
	@Unique
	public void updateVehicle(PlayerServer p) {
		Entity me = getTrackedEntity();
		removeDummies(p);

		if(!dummies.containsKey(p.uuid)) {
			IVehicle rv = me.vehicle;

			EntityItem lastDummy = null;
			if(rv instanceof Entity && p.getPassenger() != me) {

				Entity v = (Entity) rv;
				EntityItem dataDummy = getDummy(me);
				for (int i = 0; i < Math.round(v.getRideHeight() / (dataDummy.bbHeight + dataDummy.yd)); i++) {
					EntityItem newDummy = getDummy(me);

					p.playerNetServerHandler.sendPacket(new PacketAddItemEntity(newDummy));

					if (lastDummy == null) p.playerNetServerHandler.sendPacket(new PacketSetRiding(newDummy, (Entity) me.vehicle));
					else p.playerNetServerHandler.sendPacket(new PacketSetRiding(newDummy, lastDummy));

					lastDummy = newDummy;

					dummies.computeIfAbsent(p.uuid,pl -> new ArrayList<>()).add(newDummy.id);
					dummyAge.put(p.uuid,0);
				}

			}

			if(lastDummy != null) {
				p.playerNetServerHandler.sendPacket(new PacketSetRiding(me, lastDummy));
			} else {
				if(rv instanceof Entity) p.playerNetServerHandler.sendPacket(new PacketSetRiding(me, (Entity) me.vehicle));

				else if(rv instanceof TileEntity) {
					TileEntity tv = (TileEntity) rv;
					p.playerNetServerHandler.sendPacket(new PacketSetRiding(me, tv.x,tv.y,tv.z));

				}else {
					EntityItem dummy = getDummy(me);

					p.playerNetServerHandler.sendPacket(new PacketAddItemEntity(dummy));
					p.playerNetServerHandler.sendPacket(new PacketSetRiding(me,dummy));
					p.playerNetServerHandler.sendPacket(new PacketRemoveEntity(dummy.id));
				}
			}
		}
	}
	@Inject(method = "updatePlayerEntity",at= @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/core/net/entity/NetEntityHandler;getSpawnPacket(Lnet/minecraft/core/net/entity/EntityTrackerEntry;)Lnet/minecraft/core/net/packet/Packet;"))
	public void updatePlayerEntity(Player player, CallbackInfo ci) {
		dummyAge.put(player.uuid,5500-getVehicleDelay()*20);
	}
	@Inject(method = "updatePlayerEntity",at= @At(value = "INVOKE", target = "Ljava/util/Set;remove(Ljava/lang/Object;)Z"), cancellable = true)
	public void updatePlayerEntityTail(Player player, CallbackInfo ci) {
		removeTrackedPlayerSymmetric(player);
		ci.cancel();
	}
	@Inject(method = "sendDestroyEntityPacketToTrackedPlayers",at=@At("TAIL"))
	public void sendDestroyEntityPacketToTrackedPlayers(CallbackInfo ci) {
		removeDummiesForTrackedPlayers();
	}
}
