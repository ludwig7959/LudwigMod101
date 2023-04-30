package xyz.ludwicz.ludwigmod;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;

@Config(name = "ludwigmod")
public class LudwigConfig implements ConfigData {

    // Visual

    @Category("visual")
    public boolean clearHitboxes = true;

    @Category("visual")
    public boolean enchantmentGlint = true;

    @Category("visual")
    public boolean itemModelFix = true;

    // Performance

    @Category("performance")
    public boolean instantlyCloseLoadingScreen = true;

    // Scoreboard

    @Category("scoreboard")
    public boolean hideScoreboard = false;

    @Category("scoreboard")
    public boolean hideScoreboardScores = true;

    // NameTag

    @Category("nametag")
    public boolean thirdPersonNametag = true;
}
