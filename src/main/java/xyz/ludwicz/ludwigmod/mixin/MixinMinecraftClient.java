package xyz.ludwicz.ludwigmod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.ludwicz.ludwigmod.LudwigConfig;

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
}
