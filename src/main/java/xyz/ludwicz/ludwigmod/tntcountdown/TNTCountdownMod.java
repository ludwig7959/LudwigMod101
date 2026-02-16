package xyz.ludwicz.ludwigmod.tntcountdown;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;

public class TNTCountdownMod {
    public static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static Text getTime(double ticks) {
        Formatting format = Formatting.WHITE;
        var colored = areChatColors();

        double timing = ticks / 20;

        if (!colored) {
            return Text.of(decimalFormat.format(timing)).copy().formatted(Formatting.WHITE);
        } else {
            if (timing > 7d) {
                format = Formatting.DARK_AQUA;
            } else if (timing > 6d) {
                format = Formatting.AQUA;
            } else if (timing > 4d) {
                format = Formatting.DARK_GREEN;
            } else if (timing > 3d) {
                format = Formatting.GREEN;
            } else if (timing > 2d) {
                format = Formatting.GOLD;
            } else if (timing > 1d) {
                format = Formatting.RED;
            } else if (timing > 0d) {
                format = Formatting.DARK_RED;
            }
            return Text.of(decimalFormat.format(timing)).copy().formatted(format);
        }
    }

    private static boolean areChatColors() {
        return MinecraftClient.getInstance().options.getChatColors().getValue();
    }
}
