package xyz.ludwicz.ludwigmod.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import xyz.ludwicz.ludwigmod.gui.SnapManager;
import xyz.ludwicz.ludwigmod.hud.HudElement;
import xyz.ludwicz.ludwigmod.hud.HudManager;
import xyz.ludwicz.ludwigmod.util.Position;

import java.util.Optional;

public class HUDEditScreen extends Screen {

    private boolean mouseDown = false;
    private HudElement current = null;
    private Position offset = null;
    private SnapManager snap;
    private boolean snapEnabled = true;
    private final MinecraftClient client;
    private final Screen parent;

    protected HUDEditScreen(Screen parent) {
        super(Text.of("Edit HUD"));

        this.parent = parent;
        client = MinecraftClient.getInstance();
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        Optional<HudElement> entry = HudManager.getInstance().getElement(mouseX, mouseY);
        entry.ifPresent(abstractHudEntry -> abstractHudEntry.setHovered(true));
        HudManager.getInstance().renderPlaceholder(context, delta);
        if (mouseDown && snap != null) {
            snap.renderSnaps(context);
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);


        return false;
    }
}
