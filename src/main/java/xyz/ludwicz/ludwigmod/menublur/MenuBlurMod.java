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

public class MenuBlurMod {
    public static List<String> exclusions = new ArrayList<>();

    private static long start;

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
                blurProgress.set(getProgress());
                blur.render(deltaTick);
            }
        });
    }

    private static boolean doFade = false;

    public static void onScreenChange(Screen newGui) {
        if (MinecraftClient.getInstance().world != null) {
            boolean excluded = newGui == null || exclusions.stream().anyMatch(exclusion -> newGui.getClass().getName().contains(exclusion));
            if (!excluded) {
                blur.setUniformValue("Radius", (float) AutoConfig.getConfigHolder(LudwigConfig.class).getConfig().menuBlurStrength);
                if (doFade) {
                    start = System.currentTimeMillis();
                    doFade = false;
                }
            } else {
                start = -1;
                doFade = true;
            }
        }
    }

    private static float getProgress() {
        float x = Math.min((System.currentTimeMillis() - start) / 200F, 1);
        x *= (2 - x);  // easeOutCubic
        return x;
    }

    public static int getBackgroundColor() {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        int color = config.menuBlurColor;
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int b = (color >> 8) & 0xFF;
        int g = color & 0xFF;
        float prog = getProgress();
        a *= prog;
        r *= prog;
        g *= prog;
        b *= prog;
        return a << 24 | r << 16 | b << 8 | g;
    }
}
