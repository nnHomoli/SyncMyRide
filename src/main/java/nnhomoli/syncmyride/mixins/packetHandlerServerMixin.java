package nnhomoli.syncmyride.mixins;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.IVehicle;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import nnhomoli.syncmyride.lib.trackerImplMethods;

@Mixin(value = PacketHandlerServer.class,remap = false)
class packetHandlerServerMixin {
	@Shadow private PlayerServer playerEntity;

	@Redirect(method = "handlePlayerState",at= @At(value = "INVOKE", target = "Lnet/minecraft/core/world/IVehicle;ejectRider()Lnet/minecraft/core/entity/Entity;"))
	public Entity handlePlayerState(IVehicle instance) {
		Entity e = instance.ejectRider();
		if(playerEntity.vehicle != instance) {
			((trackerImplMethods)playerEntity.mcServer.getEntityTracker(playerEntity.dimension)).syncMyRide$updateVehicleForTrackedPlayersAndEntity(playerEntity);
		}
		return e;
	}
}
