package xyz.ludwicz.ludwigmod;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.Config.Gui.Background;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import xyz.ludwicz.ludwigmod.potionhud.PotionHudType;

@Config(name = "ludwigmod")
@Background(Background.TRANSPARENT)
public class LudwigConfig implements ConfigData {

    // Visual

    @Category("visual")
    public boolean clearHitboxes = true;

    // Performance

    @Category("performance")
    public boolean instantlyCloseLoadingScreen = true;
    @Category("performance")
    public boolean borderlessFullscreen = true;

    // Potion HUD

    @Category("potionHud")
    public boolean potionHudEnabled = true;
    @Category("potionHud")
    @EnumHandler(option = EnumDisplayOption.BUTTON)
    public PotionHudType potionHudType = PotionHudType.COMPACT;
    @Category("potionHud")
    @ConfigEntry.ColorPicker
    public int potionHudTimerColor = 0xFFFFFF;
    @Category("potionHud")
    @ConfigEntry.ColorPicker
    public int potionHudTimerRunningOutColor = 0xFF0000;

    // Scoreboard

    @Category("scoreboard")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 2)
    public float scoreboardScale = 1.0f;
    @Category("scoreboard")
    public boolean hideScoreboard = false;
    @Category("scoreboard")
    public boolean hideScoreboardScores = true;

    // Nametag

    @Category("nametag")
    public boolean thirdPersonNametag = true;
    @Category("nametag")
    public boolean showNameTagWhenHudIsDisabled = true;

    // Saturation

    @Category("saturation")
    public boolean showSaturationOverlay = true;
    @Category("saturation")
    public boolean showExhaustionOverlay = true;
    @Category("saturation")
    public boolean showHungerRestoredByFood = true;
    @Category("saturation")
    public boolean showEstimatedHealth = false;

    // Tooltip

    @Category("tooltip")
    public boolean showFoodValuesInTooltip = true;
    @Category("tooltip")
    public boolean showRepairCostInTooltip = true;

    // Menu Blur

    @Category("menuBlur")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
    public int menuBlurStrength = 4;
    @Category("menuBlur")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int menuBlurColor = 0x6F000000;

    // TNT Countdown

    @Category("tntCountdown")
    public boolean tntCountdownEnabled = true;

    // Hurtcam

    @Category("hurtcam")
    public boolean directionalDamageTilt = true;
    @Category("hurtcam")
    public boolean hurtcamEnabled = true;
    @Category("hurtcam")
    public float hurtcamIntensity = 1.0f;
}
