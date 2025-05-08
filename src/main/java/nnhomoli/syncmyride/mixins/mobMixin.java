package nnhomoli.syncmyride.mixins;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Mob.class,remap=false,priority = 700)
public class mobMixin {
	@Inject(method="onDeath",at=@At("HEAD"))
	public void onDeath(Entity entityKilledBy, CallbackInfo ci) {
		Mob m = (Mob)(Object) this;
		m.ejectRider();
	}
}
