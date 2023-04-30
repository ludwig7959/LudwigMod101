package xyz.ludwicz.ludwigmod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.ludwicz.ludwigmod.LudwigConfig;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Unique
    private int xShift;

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void hide(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if(config != null && config.hideScoreboard)
            ci.cancel();
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private String modifyScore(String score, MatrixStack matrices, ScoreboardObjective objective) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();

        return config != null && config.hideScoreboardScores ? "" : score;
    }

     @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 2)
    private int modifySeperatorWidth(int seperatorWidth, MatrixStack matrices, ScoreboardObjective objective) {
         LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();

         return config != null && config.hideScoreboardScores ? 0 : seperatorWidth;
    }

    @Redirect(method = "renderScoreboardSidebar", slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I", ordinal = 0))
    private int modifyScoreWidth(TextRenderer textRenderer, String score, MatrixStack matrices, ScoreboardObjective objective) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();

        return config != null && config.hideScoreboardScores ? 0 : textRenderer.getWidth(score);
    }

    /* @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 5)
    private int modifyX1(int x1) {
        if (Settings.Sidebar.getPosition() == SidebarPosition.LEFT) {
            xShift = x1 - 1;
            return 1;
        }
        return x1;
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 11)
    private int modifyX2(int x2) {
        if (Settings.Sidebar.getPosition() == SidebarPosition.LEFT) {
            return x2 - xShift;
        }
        return x2;
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 8)
    private int modifyHeadingBackgroundColor(int color) {
        return Settings.Sidebar.Background.getHeadingColor();
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 7)
    private int modifyBackgroundColor(int color) {
        return Settings.Sidebar.Background.getColor();
    }

    @ModifyArg(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", ordinal = 1), index = 4)
    private int modifyHeadingColor(int color) {
        return Settings.Sidebar.Text.getHeadingColor().getRgb();
    }

    @ModifyArg(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", ordinal = 0), index = 4)
    private int modifyTextColor(int color) {
        return Settings.Sidebar.Text.getColor().getRgb();
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I", ordinal = 0))
    private int modifyScoreColor(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        return textRenderer.draw(matrices, Formatting.strip(text), x, y, Settings.Sidebar.Text.getScoreColor().getRgb());
    } */
}
