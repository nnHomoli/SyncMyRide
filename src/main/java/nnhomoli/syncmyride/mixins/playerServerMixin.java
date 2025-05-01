package nnhomoli.syncmyride.mixins;

import net.minecraft.core.world.IVehicle;
import net.minecraft.server.entity.player.PlayerServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerServer.class,remap = false,priority = 700)
abstract class playerServerMixin{
	@Inject(method = "startRiding",at = @At(value = "FIELD", target = "Lnet/minecraft/server/entity/player/PlayerServer;playerNetServerHandler:Lnet/minecraft/server/net/handler/PacketHandlerServer;"), cancellable = true)
	public void startRiding(IVehicle vehicle, CallbackInfo ci) {
		ci.cancel();
	}
}
