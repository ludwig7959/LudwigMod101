package xyz.ludwicz.ludwigmod.potionhud;

import com.google.common.collect.Ordering;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import xyz.ludwicz.ludwigmod.LudwigConfig;

import java.util.Collection;

public class PotionHUDMod {

    public static void renderCompactStatusEffects(Collection<StatusEffectInstance> collection) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if(config == null)
            return;

        int i = 0;
        int j = 0;
        for(StatusEffectInstance inst : Ordering.natural().reverse().sortedCopy(collection)) {
            if (inst.shouldShowIcon()) {
                int k = MinecraftClient.getInstance().getWindow().getScaledWidth();
                int l = 24;

                if (inst.getEffectType().isBeneficial()) {
                    k -= 25 * i;
                    i++;
                } else {
                    k -= 25 * j;
                    l += 26;
                    j++;
                }

                float fontSize = 0.8f;
                MatrixStack matrixStack = new MatrixStack();
                matrixStack.scale(fontSize, fontSize, 1f);
                String duration = inst.isPermanent() ? "∞" : StringHelper.formatTicks(MathHelper.floor((float) inst.getDuration()));
                DrawableHelper.drawCenteredTextWithShadow(matrixStack, MinecraftClient.getInstance().textRenderer, Text.of(duration).asOrderedText(), (int) ((k - 12) / fontSize), (int) ((l - 5) / fontSize), inst.getDuration() > 200 ? config.potionHudTimerColor : config.potionHudTimerRunningOutColor);
                // drawRightAlign(matrices, formatDuration(inst), k - 3.25f, l - 0.25f, color, true, fontSize);
            }
        }
    }
}
