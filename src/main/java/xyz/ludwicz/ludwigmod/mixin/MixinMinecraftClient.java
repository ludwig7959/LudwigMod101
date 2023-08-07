package xyz.ludwicz.ludwigmod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.ludwicz.ludwigmod.LudwigConfig;
import xyz.ludwicz.ludwigmod.menublur.MenuBlurMod;
import xyz.ludwicz.ludwigmod.saturation.SaturationMod;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(at = @At("HEAD"), method = "setScreen", cancellable = true)
    public void setScreen(final Screen screen, final CallbackInfo ci) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (config != null && config.instantlyCloseLoadingScreen && screen instanceof DownloadingTerrainScreen) {
            ci.cancel();
            setScreen(null);
        }
    }

    /* @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManagerImpl;registerReloader(Lnet/minecraft/resource/ResourceReloader;)V", ordinal = 21), method = "<init>", cancellable = true)
    public void initializer(RunArgs args, CallbackInfo ci) {

    } */
    @Inject(method = "setScreen",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
                    opcode = Opcodes.PUTFIELD))
    private void onScreenOpen(Screen newScreen, CallbackInfo info) {
        MenuBlurMod.onScreenChange(newScreen);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    void onTick(CallbackInfo info) {
        SaturationMod.tick();
    }
}
