package xyz.ludwicz.ludwigmod.hud;

import lombok.Getter;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import xyz.ludwicz.ludwigmod.gui.screen.HUDEditScreen;
import xyz.ludwicz.ludwigmod.util.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HudManager {

    @Getter
    private final static HudManager instance = new HudManager();

    private final List<HudElement> elements = new ArrayList<>();

    private final MinecraftClient client = MinecraftClient.getInstance();

    public HudManager() {

        HudRenderCallback.EVENT.register(this::render);
    }

    public void render(DrawContext context, float delta) {
        if (!(client.currentScreen instanceof HUDEditScreen) && !client.options.debugEnabled) {
            for (HudElement hud : getElements()) {
                if (hud.isEnabled()) {
                    hud.render(context, delta);
                }
            }
        }
    }

    public List<HudElement> getElements() {
        if (elements.size() > 0) {
            return new ArrayList<>(elements);
        }

        return Collections.emptyList();
    }

    public List<HudElement> getMovableElements() {
        if (elements.size() > 0) {
            return elements.stream().filter(element -> element.isEnabled()).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    public Optional<HudElement> getElement(int x, int y) {
        for (HudElement element : getMovableElements()) {
            Rectangle bounds = element.getScaledBounds();
            if (bounds.x() <= x && bounds.x() + bounds.width() >= x && bounds.y() <= y && bounds.y() + bounds.height() >= y) {
                return Optional.of(element);
            }
        }
        return Optional.empty();
    }
}
