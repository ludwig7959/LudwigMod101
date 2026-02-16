package xyz.ludwicz.ludwigmod.scoreboard;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import xyz.ludwicz.ludwigmod.LudwigConfig;
import xyz.ludwicz.ludwigmod.hud.HudElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardHUD extends HudElement {

    private static final String SCOREBOARD_JOINER = ": ";

    public static final ScoreboardObjective PLACEHOLDER = Util.make(() -> {
        Scoreboard placeScore = new Scoreboard();
        ScoreboardObjective objective = placeScore.addObjective("placeholder", ScoreboardCriterion.DUMMY,
                Text.literal("Scoreboard"),
                ScoreboardCriterion.RenderType.INTEGER);
        ScoreboardPlayerScore dark = placeScore.getPlayerScore("DarkKronicle", objective);
        dark.setScore(8780);

        ScoreboardPlayerScore moeh = placeScore.getPlayerScore("moehreag", objective);
        moeh.setScore(743);

        ScoreboardPlayerScore kode = placeScore.getPlayerScore("TheKodeToad", objective);
        kode.setScore(2948);

        placeScore.setObjectiveSlot(1, objective);
        return objective;
    });

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TextRenderer textRenderer = client.textRenderer;

    @Override
    public void render(DrawContext context, float delta) {
        context.getMatrices().push();
        scale(context);
        renderComponent(context, delta);
        context.getMatrices().pop();
    }

    @Override
    public void renderComponent(DrawContext context, float delta) {
        Scoreboard scoreboard = this.client.world.getScoreboard();
        ScoreboardObjective scoreboardObjective = null;
        Team team = scoreboard.getPlayerTeam(this.client.player.getEntityName());
        if (team != null) {
            int t = team.getColor().getColorIndex();
            if (t >= 0) {
                scoreboardObjective = scoreboard.getObjectiveForSlot(3 + t);
            }
        }

        ScoreboardObjective scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
        if (scoreboardObjective2 != null) {
            this.renderScoreboardSidebar(context, scoreboardObjective2);
        }
    }

    @Override
    public void renderPlaceholder(DrawContext context, float delta) {

    }

    @Override
    public float getScale() {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (config == null)
            return 1.0f;

        float scale = config.scoreboardScale;

        return (scale > 1.5f) ? 1.5f : (scale < 0.5f) ? 0.5f : scale;
    }

    @Override
    public void setScale() {

    }

    private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        boolean hideScores = false;
        if(config != null) {
            hideScores = config.hideScoreboardScores;
        }

        int i;
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(objective);
        List<ScoreboardPlayerScore> list = collection.stream().filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")).collect(Collectors.toList());
        collection = list.size() > 15 ? Lists.newArrayList(Iterables.skip(list, collection.size() - 15)) : list;
        ArrayList<Pair<ScoreboardPlayerScore, MutableText>> list2 = Lists.newArrayListWithCapacity(collection.size());
        Text sidebarTitle = objective.getDisplayName();
        int sidebarWidth = i = textRenderer.getWidth(sidebarTitle);
        int seperatorWidth = hideScores ? 0 :textRenderer.getWidth(SCOREBOARD_JOINER);
        for (ScoreboardPlayerScore scoreboardPlayerScore : collection) {
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            MutableText text2 = Team.decorateName(team, Text.literal(scoreboardPlayerScore.getPlayerName()));
            list2.add(Pair.of(scoreboardPlayerScore, text2));
            sidebarWidth = Math.max(sidebarWidth, textRenderer.getWidth(text2) + seperatorWidth + textRenderer.getWidth(Integer.toString(scoreboardPlayerScore.getScore())));
        }

        int scaledWindowWidth = context.getScaledWindowWidth();
        int scaledWindowHeight = context.getScaledWindowHeight();

        int bodyHeight = collection.size() * textRenderer.fontHeight;
        int m = scaledWindowHeight / 2 + bodyHeight / 3;
        int o = scaledWindowWidth - sidebarWidth - 3;
        int j = 0;
        int sidebarColor = this.client.options.getTextBackgroundColor(0.3f);
        int sidebarTitleColor = this.client.options.getTextBackgroundColor(0.4f);
        for (Pair<ScoreboardPlayerScore, MutableText> pair : list2) {
            ScoreboardPlayerScore scoreboardPlayerScore2 = pair.getFirst();
            Text scoreText = pair.getSecond();
            String score = hideScores ? "" : "" + Formatting.RED + scoreboardPlayerScore2.getScore();
            int s = o;
            int t = m - ++j * textRenderer.fontHeight;
            int u = scaledWindowWidth - 3 + 2;
            context.fill(s - 2, t, u, t + textRenderer.fontHeight, sidebarColor);
            context.drawText(textRenderer, scoreText, s, t, -1, false);
            context.drawText(textRenderer, score, u - textRenderer.getWidth(score), t, -1, false);
            if (j != collection.size()) continue;
            context.fill(s - 2, t - textRenderer.fontHeight - 1, u, t - 1, sidebarTitleColor);
            context.fill(s - 2, t - 1, u, t, sidebarColor);
            context.drawText(textRenderer, sidebarTitle, s + sidebarWidth / 2 - i / 2, t - textRenderer.fontHeight, -1, false);
        }
    }
}
