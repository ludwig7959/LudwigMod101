package xyz.ludwicz.ludwigmod.resourcepack.gui;

import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.util.Identifier;

public class SilentTexturedButtonWidget extends TexturedButtonWidget {
    public SilentTexturedButtonWidget(int x, int y, int width, int height, int u, int v, Identifier texture, PressAction pressAction) {
        super(x, y, width, height, u, v, texture, pressAction);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // Do nothing
    }
}