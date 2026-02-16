package xyz.ludwicz.ludwigmod.menublur;

import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.Uniform1f;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import xyz.ludwicz.ludwigmod.LudwigConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuBlurMod {
    public static List<String> exclusions = new ArrayList<>();

    public static long start;
    public static String prevScreen;
    public static boolean screenHasBackground;

    private static final ManagedShaderEffect blur = ShaderEffectManager.getInstance().manage(new Identifier("ludwigmod", "shaders/post/fade_in_blur.json"),
            shader -> shader.setUniformValue("Radius", AutoConfig.getConfigHolder(LudwigConfig.class).getConfig() == null ? 4F : (float) AutoConfig.getConfigHolder(LudwigConfig.class).getConfig().menuBlurStrength));
    private static final Uniform1f blurProgress = blur.findUniform1f("Progress");

    public static void init() {
        exclusions.add(ChatScreen.class.getName());
        exclusions.add("com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiOverlay$UserInputGuiScreen");
        exclusions.add("ai.arcblroth.projectInception.client.InceptionInterfaceScreen");
        exclusions.add("net.optifine.gui.GuiChatOF");
        exclusions.add("io.github.darkkronicle.advancedchatcore.chat.AdvancedChatScreen");
        exclusions.add("net.coderbot.iris.gui.screen.ShaderPackScreen");

        ShaderEffectRenderCallback.EVENT.register((deltaTick) -> {
            if (start > 0) {
                blurProgress.set(getProgress(MinecraftClient.getInstance().currentScreen != null));
                blur.render(deltaTick);
            }
        });
    }

    private static boolean doFade = false;

    public static void onScreenChange(Screen newGui) {
        if (MinecraftClient.getInstance().world != null) {
            boolean excluded = newGui == null || exclusions.stream().anyMatch(exclusion -> newGui.getClass().getName().contains(exclusion));
            if (!excluded) {
                screenHasBackground = false;
                blur.setUniformValue("Radius", (float) AutoConfig.getConfigHolder(LudwigConfig.class).getConfig().menuBlurStrength);
                if (doFade) {
                    start = System.currentTimeMillis();
                    doFade = false;
                }
                prevScreen = newGui.getClass().getName();
            } else if (newGui == null && !Objects.equals(prevScreen, "")) {
                blur.setUniformValue("Radius", (float) AutoConfig.getConfigHolder(LudwigConfig.class).getConfig().menuBlurStrength);
                start = System.currentTimeMillis();
                doFade = true;
            } else {
                screenHasBackground = false;
                start = -1;
                doFade = true;
                prevScreen = "";
            }
        }
    }

    private static float getProgress(boolean fadeIn) {
        float x;
        if (fadeIn) {
            x = Math.min((System.currentTimeMillis() - start) / 200f, 1);
            x *= (2 - x);  // easeInCubic
        }
        else {
            x = Math.max(1 + (start - System.currentTimeMillis()) / 200f, 0);
            x *= (2 - x);  // easeOutCubic
            if (x <= 0) {
                start = 0;
                screenHasBackground = false;
            }
        }
        return x;
    }

    public static int getBackgroundColor(boolean fadeIn) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        int color = config.menuBlurColor;
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int b = (color >> 8) & 0xFF;
        int g = color & 0xFF;
        float prog = getProgress(fadeIn);
        a *= prog;
        r *= prog;
        g *= prog;
        b *= prog;
        return a << 24 | r << 16 | b << 8 | g;
    }
}
