package nnhomoli.syncmyride.mixins;

import net.minecraft.core.net.packet.PacketUpdatePlayerState;
import net.minecraft.server.net.handler.PacketHandlerServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PacketHandlerServer.class,remap = false,priority = 700)
abstract class packetHandlerServerMixin {
	@Inject(method = "handlePlayerState",at= @At(value = "INVOKE", target = "Lnet/minecraft/core/world/IVehicle;ejectRider()Lnet/minecraft/core/entity/Entity;"), cancellable = true)
	public void handlePlayerState(PacketUpdatePlayerState updatePlayerStatePacket, CallbackInfo ci) {
		ci.cancel();
	}
}
