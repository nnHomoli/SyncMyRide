package nnhomoli.syncmyride.mixins;

import net.minecraft.core.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class,remap=false,priority = 700)
public class entityMixin {
	@Inject(method = "rideTick",at = @At("HEAD"), cancellable = true)
	public void rideTick(CallbackInfo ci) {
		Entity e = (Entity)(Object)this;
		if(e.isSneaking() && e.vehicle != null){
			e.vehicle.ejectRider();
			if(e.vehicle == null) ci.cancel();
		}
	}
	@Inject(method = "startRiding",at=@At("HEAD"),cancellable = true)
	public void startRiding(CallbackInfo ci) {
		Entity e = (Entity)(Object)this;
		if(e.isSneaking()) ci.cancel();
	}
}
