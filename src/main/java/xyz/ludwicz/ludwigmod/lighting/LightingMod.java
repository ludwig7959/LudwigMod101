package xyz.ludwicz.ludwigmod.lighting;

import net.minecraft.client.MinecraftClient;

public class LightingMod {

    public static double minBrightness = -1.0;
    public static double maxBrightness = 12.0;
    private static double brightnessSliderInterval = 0.05;
    private static double step = 0.1;

    public static MinecraftClient client;

    public static void changeBrightness(double brightness) {
        MinecraftClient.getInstance().options.getGamma().setValue(brightness);
    }
}