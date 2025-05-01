package nnhomoli.syncmyride.mixins;

import net.minecraft.core.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class,remap=false,priority = 700)
public class playerMixin {
	@Inject(method = "rideTick",at = @At("HEAD"), cancellable = true)
	public void rideTick(CallbackInfo ci) {
		Player p = (Player)(Object)this;
		if(p.isSneaking() && p.vehicle != null){
			p.vehicle.ejectRider();
			if(p.vehicle == null) ci.cancel();
		}
	}
}
