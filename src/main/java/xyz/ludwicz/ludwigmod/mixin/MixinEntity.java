package xyz.ludwicz.ludwigmod.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.ludwicz.ludwigmod.freelook.FreelookMod;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Inject(
            method = "changeLookDirection",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void changeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if (FreelookMod.isFreelookEnabled()) {
            FreelookMod.setPitch(Math.max(-90F, Math.min(90F, (float) (FreelookMod.getPitch() + (cursorDeltaY * 0.15)))));
            FreelookMod.setYaw(FreelookMod.getYaw() + (float) (cursorDeltaX * 0.15));
            ci.cancel();
        }
    }
}