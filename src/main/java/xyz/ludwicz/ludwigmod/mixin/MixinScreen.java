package xyz.ludwicz.ludwigmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.ludwicz.ludwigmod.menublur.MenuBlurMod;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    @Nullable
    protected MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "renderBackground")
    public void blur$getBackgroundEnabled(DrawContext context, CallbackInfo ci) {
        if (this.client != null && this.client.world != null) {
            MenuBlurMod.screenHasBackground = true;
        }
    }

    @ModifyConstant(
            method = "renderBackground",
            constant = @Constant(intValue = -1072689136))
    private int blur$getFirstBackgroundColor(int color) {
        return MenuBlurMod.getBackgroundColor(true);
    }

    @ModifyConstant(
            method = "renderBackground",
            constant = @Constant(intValue = -804253680))
    private int blur$getSecondBackgroundColor(int color) {
        return MenuBlurMod.getBackgroundColor(true);
    }
}