package xyz.ludwicz.ludwigmod.mixin;

import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DownloadingTerrainScreen.class)
public class MixinDownloadingTerrainScreen {

    @Shadow
    private boolean closeOnNextTick;

    @Inject(at = @At("HEAD"), method = "setReady")
    public void modifyCloseOnNextTick(final CallbackInfo ci) {
        this.closeOnNextTick = true;
    }
}
