package xyz.ludwicz.ludwigmod;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            try {
                return AutoConfig.getConfigScreen(LudwigConfig.class, parent).get();
            } catch (Throwable t) {
                LudwigMod.LOGGER.warn("Failed to build config screen, returning parent screen", t);
                return parent;
            }
        };
    }
}
