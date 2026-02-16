package xyz.ludwicz.ludwigmod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.ludwicz.ludwigmod.LudwigConfig;
import xyz.ludwicz.ludwigmod.potionhud.PotionHUDMod;
import xyz.ludwicz.ludwigmod.potionhud.PotionHudType;
import xyz.ludwicz.ludwigmod.saturation.SaturationMod;

import java.util.Collection;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Unique
    private int xShift;

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void hide(DrawContext drawContext, ScoreboardObjective objective, CallbackInfo ci) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (config != null && config.hideScoreboard)
            ci.cancel();
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private String modifyScore(String score, DrawContext drawContext, ScoreboardObjective objective) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();

        return config != null && config.hideScoreboardScores ? "" : score;
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 2)
    private int modifySeperatorWidth(int seperatorWidth, DrawContext drawContext, ScoreboardObjective objective) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();

        return config != null && config.hideScoreboardScores ? 0 : seperatorWidth;
    }

    @Redirect(method = "renderScoreboardSidebar", slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I", ordinal = 0))
    private int modifyScoreWidth(TextRenderer textRenderer, String score, DrawContext drawContext, ScoreboardObjective objective) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();

        return config != null && config.hideScoreboardScores ? 0 : textRenderer.getWidth(score);
    }

    @Inject(
            method = "renderStatusEffectOverlay",
            slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V")),
            at = @At(
                    "TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void renderCompactStatusEffect(DrawContext drawContext, CallbackInfo ci, Collection<StatusEffectInstance> collection) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (config == null || !config.potionHudEnabled || config.potionHudType != PotionHudType.COMPACT)
            return;

        PotionHUDMod.renderCompactStatusEffects(collection);
    }

    @Inject(at = @At(value = "CONSTANT", args = "stringValue=food", shift = At.Shift.BY, by = 2), method = "renderStatusBars")
    private void renderFoodPre(DrawContext drawContext, CallbackInfo info) {
        SaturationMod.preRenderHUD(drawContext);
    }

    @Inject(slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=food")), at = @At(value = "xyz.ludwicz.ludwigmod.mixin.util.BeforeInc", args = "intValue=-10", ordinal = 0), method = "renderStatusBars")
    private void renderFoodPost(DrawContext drawContext, CallbackInfo info) {
        SaturationMod.renderHUD(drawContext);
    }
}
