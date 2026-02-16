package xyz.ludwicz.ludwigmod.gui;

import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import xyz.ludwicz.ludwigmod.util.Rectangle;

import java.util.HashSet;
import java.util.Optional;

public class SnapManager {

    private static final int DISTANCE = 4;

    private final MinecraftClient client = MinecraftClient.getInstance();

    private final HashSet<Integer> x = new HashSet<>();
    private final HashSet<Integer> y = new HashSet<>();
    @Setter
    private Rectangle current;

    public static Optional<Integer> getNearby(int pos, HashSet<Integer> set, int distance) {
        for (Integer integer : set) {
            if (integer - distance <= pos && integer + distance >= pos) {
                return Optional.of(integer);
            }
        }
        return Optional.empty();
    }

    public Integer getCurrentXSnap() {
        Integer xSnap = getNearby(current.x(), x, DISTANCE).orElse(null);
        if (xSnap != null) {
            return xSnap;
        } else if ((xSnap = getNearby(current.x() + current.width(), x, DISTANCE).orElse(null)) != null) {
            return xSnap - current.width();
        } else if ((xSnap = getHalfXSnap()) != null) {
            return xSnap - (current.width() / 2);
        }
        return null;
    }

    public Integer getRawXSnap() {
        Integer xSnap = getNearby(current.x(), x, DISTANCE).orElse(null);
        if (xSnap != null) {
            return xSnap;
        } else if ((xSnap = getNearby(current.x() + current.width(), x, DISTANCE).orElse(null)) != null) {
            return xSnap;
        } else if ((xSnap = getHalfXSnap()) != null) {
            return xSnap;
        }
        return null;
    }

    public Integer getCurrentYSnap() {
        Integer ySnap = getNearby(current.y(), y, DISTANCE).orElse(null);
        if (ySnap != null) {
            return ySnap;
        } else if ((ySnap = getNearby(current.y() + current.height(), y, DISTANCE).orElse(null)) != null) {
            return ySnap - current.height();
        } else if ((ySnap = getHalfYSnap()) != null) {
            return ySnap - (current.height() / 2);
        }
        return null;
    }

    public Integer getHalfYSnap() {
        int height = client.getWindow().getScaledHeight() / 2;
        int pos = current.y() + Math.round((float) current.height() / 2);
        if (height - DISTANCE <= pos && height + DISTANCE >= pos) {
            return height;
        }
        return null;
    }

    public Integer getHalfXSnap() {
        int width = client.getWindow().getScaledWidth() / 2;
        int pos = current.x() + Math.round((float) current.width() / 2);
        if (width - DISTANCE <= pos && width + DISTANCE >= pos) {
            return width;
        }
        return null;
    }

    public Integer getRawYSnap() {
        Integer ySnap = getNearby(current.y(), y, DISTANCE).orElse(null);
        if (ySnap != null) {
            return ySnap;
        } else if ((ySnap = getNearby(current.y() + current.height(), y, DISTANCE).orElse(null)) != null) {
            return ySnap;
        } else if ((ySnap = getHalfYSnap()) != null) {
            return ySnap;
        }
        return null;
    }
}
