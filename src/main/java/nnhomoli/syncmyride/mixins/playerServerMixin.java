package nnhomoli.syncmyride.mixins;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.IVehicle;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.PlayerServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import nnhomoli.syncmyride.lib.trackerImplMethods;

@Mixin(value = PlayerServer.class,remap = false,priority = 700)
abstract class playerServerMixin extends Entity {
	public playerServerMixin(World world) {super(world);}

	@Inject(method="startRiding",at= @At(value = "INVOKE", target = "Lnet/minecraft/server/net/handler/PacketHandlerServer;sendPacket(Lnet/minecraft/core/net/packet/Packet;)V"), cancellable = true)
	public void syncRiding(IVehicle vehicle, CallbackInfo ci) {
		PlayerServer p = (PlayerServer)(Object)this;
		((trackerImplMethods)p.mcServer.getEntityTracker(p.dimension)).syncMyRide$updateVehicleForTrackedPlayersAndEntity(p);
		ci.cancel();
	}
	@Inject(method = "startRiding",at = @At(value = "FIELD", target = "Lnet/minecraft/server/entity/player/PlayerServer;playerNetServerHandler:Lnet/minecraft/server/net/handler/PacketHandlerServer;"), cancellable = true)
	public void startRiding(IVehicle vehicle, CallbackInfo ci) {
		if(this.vehicle != vehicle) ci.cancel();
	}

	@Override
	public void rideTick() {
//		PlayerServer p = (PlayerServer)(Object)this;
//		if(this.isSneaking()){
//			Entity e = this.vehicle.ejectRider();
//			if(e != getPassenger()) ((trackerImplMethods)p.mcServer.getEntityTracker(p.dimension)).syncMyRide$updateVehicleForTrackedPlayersAndEntity(p);
//			return;
//		}
		super.rideTick();
	}
	@Override
	public Entity ejectRider() {
		PlayerServer p = (PlayerServer)(Object)this;
		Entity e = super.ejectRider();
		if(e != getPassenger()) {
			((trackerImplMethods)p.mcServer.getEntityTracker(p.dimension)).syncMyRide$updateVehicleForTrackedPlayersAndEntity(e);
		}
		return e;
	}
}
