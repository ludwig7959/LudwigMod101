package xyz.ludwicz.ludwigmod.hud;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import xyz.ludwicz.ludwigmod.util.Position;
import xyz.ludwicz.ludwigmod.util.Rectangle;

public abstract class HudElement {

    @Getter
    @Setter
    private boolean enabled;

    @Getter
    private HudAnchor anchor;

    @Getter
    private Position position;
    @Getter
    private Position scaledPosition;

    @Getter
    @Setter
    protected int width;
    @Getter
    @Setter
    protected int height;

    abstract public void render(DrawContext context, float delta);

    abstract public void renderComponent(DrawContext context, float delta);

    abstract public void renderPlaceholder(DrawContext context, float delta);

    public void scale(DrawContext context) {
        float scale = getScale();
        context.getMatrices().scale(scale, scale, 1);
    }

    public int getX() {
        return anchor.getX(getRawX(), getWidth());
    }

    public int getY() {
        return anchor.getY(getRawY(), getHeight());
    }

    public int getScaledX() {
        return anchor.getX(getRawScaledX(), getScaledWidth());
    }

    public int getScaledY() {
        return anchor.getY(getRawScaledY(), getScaledHeight());
    }

    public int getRawScaledX() {
        return (int) (getX() * getScale());
    }

    public int getRawScaledY() {
        return (int) (getY() * getScale());
    }

    public int getRawX() {
        return getPosition().x();
    }

    public int getRawY() {
        return getPosition().y();
    }

    public int getScaledWidth() {
        return (int) (getWidth() * getScale());
    }

    public int getScaledHeight() {
        return (int) (getHeight() * getScale());
    }

    public Rectangle getBounds() {
        Position pos = getPosition();
        return new Rectangle(pos.x(), pos.y(), getWidth(), getHeight());
    }

    public Rectangle getScaledBounds() {
        Position pos = getScaledPosition();
        return new Rectangle(pos.x(), pos.y(), getScaledWidth(), getScaledHeight());
    }

    abstract public float getScale();

    abstract public void setScale();
}
