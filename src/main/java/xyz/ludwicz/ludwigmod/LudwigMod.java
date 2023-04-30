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

        AutoConfig.register(LudwigConfig.class, Toml4jConfigSerializer::new);
    }
}
