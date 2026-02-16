package xyz.ludwicz.ludwigmod.mixin;

import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.ludwicz.ludwigmod.itemmodelfix.ItemModelFix;

@Mixin(Sprite.class)
public abstract class MixinSprite {
    @Shadow
    protected abstract float getFrameDeltaFactor();

    @Shadow
    public abstract Identifier getAtlasId();

    @Inject(method = "getAnimationFrameDelta", at = @At("RETURN"), cancellable = true)
    public void cancelShrink(CallbackInfoReturnable<Float> cir) {
        var newS = ItemModelFix.getShrinkRatio(this.getAtlasId(), 4.0F / this.getFrameDeltaFactor(), cir.getReturnValueF());
        if (newS != -1) cir.setReturnValue(newS);
    }
}
