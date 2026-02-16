package xyz.ludwicz.ludwigmod.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class Position {

    int x;
    int y;

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
