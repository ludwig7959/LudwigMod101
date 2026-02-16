package xyz.ludwicz.ludwigmod.borderless;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import xyz.ludwicz.ludwigmod.LudwigConfig;

public class BorderlessFullScreen {
    public static CustomWindowDimensions customWindowDimensions = CustomWindowDimensions.INITIAL;
    public static int forceWindowMonitor = -1;

    public static class CustomWindowDimensions {
        public static transient final CustomWindowDimensions INITIAL = new CustomWindowDimensions();

        public final boolean enabled;
        public final int x;
        public final int y;
        public final int width;
        public final int height;
        public boolean useMonitorCoordinates;

        private CustomWindowDimensions() {
            enabled = false;
            x = 0;
            y = 0;
            width = 0;
            height = 0;
            useMonitorCoordinates = true;
        }

        public CustomWindowDimensions(boolean enabled, int x, int y, int width, int height, boolean useMonitorCoordinates) {
            this.enabled = enabled;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.useMonitorCoordinates = useMonitorCoordinates;
        }

        public CustomWindowDimensions setEnabled(boolean enabled) {
            return new CustomWindowDimensions(enabled, x, y, width, height, useMonitorCoordinates);
        }

        public CustomWindowDimensions setX(int x) {
            return new CustomWindowDimensions(enabled, x, y, width, height, useMonitorCoordinates);
        }

        public CustomWindowDimensions setY(int y) {
            return new CustomWindowDimensions(enabled, x, y, width, height, useMonitorCoordinates);
        }

        public CustomWindowDimensions setWidth(int width) {
            return new CustomWindowDimensions(enabled, x, y, width, height, useMonitorCoordinates);
        }

        public CustomWindowDimensions setHeight(int height) {
            return new CustomWindowDimensions(enabled, x, y, width, height, useMonitorCoordinates);
        }

        public CustomWindowDimensions setUseMonitorCoordinates(boolean useMonitorCoordinates) {
            return new CustomWindowDimensions(enabled, x, y, width, height, useMonitorCoordinates);
        }
    }

    private transient static boolean enabledPending = true;
    private transient static boolean enabledDirty = false;

    public static void setEnabledPending(boolean en) {
        if (enabledPending != en) {
            enabledPending = en;
            enabledDirty = (en != isEnabled());
        }
    }

    public static boolean isEnabledOrPending() {
        return enabledDirty ? enabledPending : isEnabled();
    }

    public static boolean isEnabled() {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (config == null)
            return false;

        return config.borderlessFullscreen && !MinecraftClient.IS_SYSTEM_MAC;
    }

}
