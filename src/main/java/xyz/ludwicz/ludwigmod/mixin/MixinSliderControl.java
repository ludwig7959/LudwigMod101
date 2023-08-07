package xyz.ludwicz.ludwigmod.mixin;

import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import net.minecraft.text.TranslatableTextContent;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.ludwicz.ludwigmod.lighting.LightingMod;

@Pseudo
@Mixin(SliderControl.class)
public class MixinSliderControl {
    @Shadow
    @Final
    @Mutable
    private int min;

    @Shadow
    @Final
    @Mutable
    private int max;

    @Shadow
    @Final
    @Mutable
    private int interval;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void init(Option<Integer> option, int min, int max, int interval, ControlValueFormatter mode, CallbackInfo info) {
        if (option.getName().getContent() instanceof TranslatableTextContent content && content.getKey().equals("options.gamma")) {
            this.min = (int) (LightingMod.minBrightness * 100);
            this.max = (int) (LightingMod.maxBrightness * 100);
            this.interval = (int) (0.05 * 100);
        }
    }
}