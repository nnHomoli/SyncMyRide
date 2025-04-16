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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import static nnhomoli.syncmyride.lib.dummy.getDummy;
import static nnhomoli.syncmyride.SyncMyRide.getVehicleDelay;

@Mixin(value = EntityTrackerEntryImpl.class,remap = false,priority = 700)
abstract class entryImplMixin {
	@Shadow public abstract void removeTrackedPlayerSymmetric(Player player);
	@Shadow public abstract Entity getTrackedEntity();
	@Shadow public Set<PlayerServer> trackedPlayers;

	@Unique private final HashMap<UUID, List<Integer>> dummies = new HashMap<>();
	@Unique private final HashMap<UUID,Integer> dummyAge = new HashMap<>();
	@Unique private boolean threadSafe = true;
	@Unique private IVehicle lastTrackedVehicle = null;

	@Inject(method = "tick",at=@At("RETURN"))
	public void tickStuff(List<Player> list, CallbackInfo ci) {
		if(lastTrackedVehicle != getTrackedEntity().vehicle) {
			updateVehicleForTrackedPlayers();
			if(getTrackedEntity() instanceof PlayerServer) updateVehicle((PlayerServer) getTrackedEntity());
			lastTrackedVehicle = getTrackedEntity().vehicle;
		}

		if(!threadSafe) return;
		for(UUID u : dummyAge.keySet()) {
			int age = dummyAge.get(u);
			++age;
			dummyAge.put(u, age);
			if (age >= 6000) {
				updateVehicle((PlayerServer) getTrackedEntity().world.getPlayerEntityByUUID(u));
			}

			if(!threadSafe) break;
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
		threadSafe = false;

		if(!dummies.containsKey(p.uuid)) return;
		for(int dum : dummies.get(p.uuid)) p.playerNetServerHandler.sendPacket(new PacketRemoveEntity(dum));
		dummies.remove(p.uuid);
		dummyAge.remove(p.uuid);

		threadSafe = true;
	}
	@Inject(method = "removeTrackedPlayerSymmetric",at=@At("TAIL"))
	public void removeTrackedPlayerSymmetric(Player player, CallbackInfo ci) {
		if(!(player instanceof PlayerServer)) return;

		removeDummies((PlayerServer)player);
	}
	@Inject(method = "removeFromTrackedPlayers",at=@At("TAIL"))
	public void removeFromTrackedPlayers(Player player, CallbackInfo ci) {
		threadSafe = false;
		dummies.remove(player.uuid);
		dummyAge.remove(player.uuid);
		threadSafe = true;
	}
	@Unique
	public void updateVehicle(PlayerServer p) {
		Entity me = getTrackedEntity();
		if(dummies.containsKey(p.uuid)) removeDummies(p);

		if(!dummies.containsKey(p.uuid)) {
			IVehicle rv = me.vehicle;

			EntityItem lastDummy = null;
			if(rv instanceof Entity && p.getPassenger() != me) {

				Entity v = (Entity) rv;
				EntityItem dataDummy = getDummy(me);
				for (int i = 0; i < Math.round(v.getRideHeight() / (dataDummy.bbHeight + dataDummy.yd)); i++) {
					threadSafe = false;
					EntityItem newDummy = getDummy(me);

					p.playerNetServerHandler.sendPacket(new PacketAddItemEntity(newDummy));

					if (lastDummy == null)
						p.playerNetServerHandler.sendPacket(new PacketSetRiding(newDummy, (Entity) me.vehicle));
					else p.playerNetServerHandler.sendPacket(new PacketSetRiding(newDummy, lastDummy));

					lastDummy = newDummy;

					dummies.computeIfAbsent(p.uuid,pl -> new ArrayList<>()).add(newDummy.id);
					dummyAge.put(p.uuid,0);
				}

				threadSafe = true;
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
		PlayerServer p = (PlayerServer) player;
		Runnable task = () -> {
			if(!trackedPlayers.contains(p)) return;
			updateVehicle(p);
		};
		Executors.newSingleThreadScheduledExecutor().schedule(task,getVehicleDelay(), TimeUnit.SECONDS);
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
