package xyz.ludwicz.ludwigmod.resourcepack.gui;

import net.minecraft.text.Text;

public interface FolderedPackListWidget {
    void recursiveresources$setTitleClickable(Text hoverText, Text tooltip, Runnable clickEvent);
}