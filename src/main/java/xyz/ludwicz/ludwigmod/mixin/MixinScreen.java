package xyz.ludwicz.ludwigmod.mixin;

import net.minecraft.client.MinecraftClient;
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

import java.util.Locale;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    @Nullable
    protected MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "tick")
    private void blur$reloadShader(CallbackInfo ci) {
        if (this.getClass().toString().toLowerCase(Locale.ROOT).contains("midnightconfigscreen") && this.client != null) {
            MenuBlurMod.onScreenChange(this.client.currentScreen);
        }
    }

    @ModifyConstant(
            method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            constant = @Constant(intValue = -1072689136))
    private int blur$getFirstBackgroundColor(int color) {
        return MenuBlurMod.getBackgroundColor();
    }

    @ModifyConstant(
            method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            constant = @Constant(intValue = -804253680))
    private int blur$getSecondBackgroundColor(int color) {
        return MenuBlurMod.getBackgroundColor();
    }
}