package xyz.ludwicz.ludwigmod.util;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public Position subtract(int x, int y) {
        return new Position(this.x - x, this.y - y);
    }

    public Position subtract(Position position) {
        return new Position(position.x(), position.y());
    }

    public Position divide(float scale) {
        return new Position((int) (x / scale), (int) (y / scale));
    }
}
