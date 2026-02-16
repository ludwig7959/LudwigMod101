package xyz.ludwicz.ludwigmod;

import lombok.Getter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LudwigMod implements ClientModInitializer {

    @Getter
    private static LudwigMod instance;

    public static final Logger LOGGER = LoggerFactory.getLogger("ludwigmod");

    @Override
    public void onInitializeClient() {
        instance = this;

        try {
            AutoConfig.register(LudwigConfig.class, Toml4jConfigSerializer::new);
        } catch (Throwable t) {
            LOGGER.warn("Failed to initialize AutoConfig, continuing without config UI", t);
        }

        initOptionalModule("xyz.ludwicz.ludwigmod.zoom.ZoomMod");
        initOptionalModule("xyz.ludwicz.ludwigmod.freelook.FreelookMod");
        initOptionalModule("xyz.ludwicz.ludwigmod.menublur.MenuBlurMod");
    }

    private void initOptionalModule(String className) {
        try {
            Class<?> moduleClass = Class.forName(className);
            moduleClass.getMethod("init").invoke(null);
        } catch (ClassNotFoundException ignored) {
            LOGGER.debug("Skipping optional module {}", className);
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Failed to initialize optional module {}", className, e);
        }
    }
}
