package nnhomoli.syncmyride.mixins;

import net.minecraft.core.entity.Entity;

import net.minecraft.core.world.IVehicle;
import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class,remap=false,priority = 700)
abstract class entityMixin {
	@Shadow public abstract boolean isSneaking();
	@Shadow @Nullable public IVehicle vehicle;

	@Inject(method = "rideTick",at = @At("HEAD"), cancellable = true)
	public void rideTick(CallbackInfo ci) {
		if(isSneaking() && vehicle != null){
			vehicle.ejectRider();
			if(vehicle == null) ci.cancel();
		}
	}
	@Inject(method = "startRiding",at=@At("HEAD"),cancellable = true)
	public void startRiding(CallbackInfo ci) {
		if(isSneaking()) ci.cancel();
	}
}
