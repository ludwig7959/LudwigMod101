package xyz.ludwicz.ludwigmod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.ludwicz.ludwigmod.LudwigConfig;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

    @ModifyVariable(method = "getArmorGlintConsumer", at = @At("HEAD"), ordinal = 1)
    private static boolean modifyArmorGlint(boolean original) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();

        return config != null && !config.enchantmentGlint ? false : original;
    }

    @ModifyVariable(method = "getItemGlintConsumer", at = @At("HEAD"), ordinal = 1)
    private static boolean modifyItemGlint(boolean original) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();

        return config != null && !config.enchantmentGlint ? false : original;
    }

    @ModifyVariable(method = "getDirectItemGlintConsumer", at = @At("HEAD"), ordinal = 1)
    private static boolean modifyDirectItemGlint(boolean original) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();

        return config != null && !config.enchantmentGlint ? false : original;
    }
}
