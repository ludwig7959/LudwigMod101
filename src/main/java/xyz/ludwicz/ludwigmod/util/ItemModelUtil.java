package xyz.ludwicz.ludwigmod.util;

import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

import java.util.EnumMap;
import java.util.Map;

public class ItemModelUtil {


    public static ModelElement createHorizontalOutlineElement(Direction direction, int layer, String key, int start, int end, int y, int height, float animationFrameDelta, float xFactor, float yFactor) {
        Map<Direction, ModelElementFace> faces = new EnumMap<>(Direction.class);
        faces.put(direction, new ModelElementFace(null, layer, key, createUnlerpedTexture(new float[] { start / xFactor, y / yFactor, (end + 1) / xFactor, (y + 1) / yFactor }, 0, animationFrameDelta)));
        return new ModelElement(new Vec3f(start / xFactor, (height - (y + 1)) / yFactor, 7.5F), new Vec3f((end + 1) / xFactor, (height - y) / yFactor, 8.5F), faces, null, true);
    }

    public static ModelElement createVerticalOutlineElement(Direction direction, int layer, String key, int start, int end, int x, int height, float animationFrameDelta, float xFactor, float yFactor) {
        Map<Direction, ModelElementFace> faces = new EnumMap<>(Direction.class);
        faces.put(direction, new ModelElementFace(null, layer, key, createUnlerpedTexture(new float[] { (x + 1) / xFactor, start / yFactor, x / xFactor, (end + 1) / yFactor }, 0, animationFrameDelta)));
        return new ModelElement(new Vec3f(x / xFactor, (height - (end + 1)) / yFactor, 7.5F), new Vec3f((x + 1) / xFactor, (height - start) / yFactor, 8.5F), faces, null, true);
    }

    public static ModelElementTexture createUnlerpedTexture(float[] uvs, int rotation, float delta) {
        return new ModelElementTexture(unlerpUVs(uvs, delta), rotation);
    }
    public static float unlerp(float delta, float start, float end) {
        return (start - delta * end) / (1 - delta);
    }

    public static float[] unlerpUVs(float[] uvs, float delta) {
        float centerU = (uvs[0] + uvs[2]) / 2.0F;
        float centerV = (uvs[1] + uvs[3]) / 2.0F;
        uvs[0] = unlerp(delta, uvs[0], centerU);
        uvs[2] = unlerp(delta, uvs[2], centerU);
        uvs[1] = unlerp(delta, uvs[1], centerV);
        uvs[3] = unlerp(delta, uvs[3], centerV);
        return uvs;
    }

    public static boolean isPixelOutsideSprite(Sprite sprite, int x, int y) {
        return x < 0 || y < 0 || x >= sprite.getWidth() || y >= sprite.getHeight();
    }

    public static boolean isPixelTransparent(Sprite sprite, int frame, int x, int y) {
        return isPixelOutsideSprite(sprite, x, y) ? true : sprite.isPixelTransparent(frame, x, y);
    }

    public static boolean isPixelAlwaysTransparent(Sprite sprite, int[] frames, int x, int y) {
        for (int frame : frames) {
            if (!isPixelTransparent(sprite, frame, x, y)) {
                return false;
            }
        }
        return true;
    }

    public static boolean doesPixelHaveEdge(Sprite sprite, int[] frames, int x, int y, PixelDirection direction) {
        int x1 = x + direction.getOffsetX();
        int y1 = y + direction.getOffsetY();
        if (isPixelOutsideSprite(sprite, x1, y1)) {
            return true;
        }
        for (int frame : frames) {
            if (!isPixelTransparent(sprite, frame, x, y) && isPixelTransparent(sprite, frame, x1, y1)) {
                return true;
            }
        }
        return false;
    }

    public enum PixelDirection {
        LEFT(Direction.WEST, -1, 0),
        RIGHT(Direction.EAST, 1, 0),
        UP(Direction.UP, 0, -1),
        DOWN(Direction.DOWN, 0, 1);

        public static final PixelDirection[] VALUES = values();

        private final Direction direction;
        private final int offsetX;
        private final int offsetY;

        PixelDirection(Direction direction, int offsetX, int offsetY) {
            this.direction = direction;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public Direction getDirection() {
            return direction;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public boolean isVertical() {
            return this == DOWN || this == UP;
        }
    }
}
