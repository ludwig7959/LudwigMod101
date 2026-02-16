package xyz.ludwicz.ludwigmod.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.ludwicz.ludwigmod.freelook.FreelookMod;

@Mixin(Camera.class)
public abstract class MixinCamera {

    private boolean firstPress = true;

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Shadow
    protected abstract void moveBy(double x, double y, double z);

    @Shadow
    protected abstract double clipToSpace(double desiredCameraDistance);

    @Inject(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (FreelookMod.isFreelookEnabled()) {
            if (firstPress) {
                FreelookMod.setPitch(focusedEntity.getPitch());
                FreelookMod.setYaw(focusedEntity.getYaw());
            }
            firstPress = false;
            this.setRotation(FreelookMod.getYaw(), FreelookMod.getPitch());
        } else {
            firstPress = true;
        }
    }

    @Inject(
            method = "update",
            at = @At("TAIL")
    )
    public void setDistance(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (FreelookMod.isFreelookEnabled()) {
            this.moveBy(-this.clipToSpace(0.0), 0.0, 0.0);
        }
    }

}