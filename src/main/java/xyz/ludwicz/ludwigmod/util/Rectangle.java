package xyz.ludwicz.ludwigmod.util;


import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@AllArgsConstructor
@Accessors(fluent = true)
public class Rectangle {

    int x;
    int y;
    int width;
    int height;

    public Rectangle offset(int x, int y) {
        return new Rectangle(this.x + x, this.y + y, width, height);
    }
}