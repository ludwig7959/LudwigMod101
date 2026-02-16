package xyz.ludwicz.ludwigmod.hitbox;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class HitboxMod {

    public static void renderClearHitbox(MatrixStack matrix, Entity entity) {
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        Matrix4f matrix4f = matrix.peek().getPositionMatrix();
        Matrix3f matrix3f = matrix.peek().getNormalMatrix();
        float f = (float) box.maxX;
        float g = (float) box.maxY;
        float h = (float) box.maxZ;
        float i = (float) box.minX;
        float j = (float) box.minY;
        float k = (float) box.minZ;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableCull();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // RenderSystem.disableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        //RenderSystem.setShader(class_757::method_34540);
        RenderSystem.applyModelViewMatrix();
        buffer.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix4f, f, g, h).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        buffer.vertex(matrix4f, i, g, h).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        buffer.vertex(matrix4f, f, g, h).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix4f, f, j, h).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix4f, f, g, h).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        buffer.vertex(matrix4f, f, g, k).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        buffer.vertex(matrix4f, i, g, h).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix4f, i, j, h).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix4f, i, j, h).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, -1.0F, 0.0F, 0.0F).next();
        buffer.vertex(matrix4f, f, j, h).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, -1.0F, 0.0F, 0.0F).next();
        buffer.vertex(matrix4f, f, j, h).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        buffer.vertex(matrix4f, f, j, k).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        buffer.vertex(matrix4f, f, j, k).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
        buffer.vertex(matrix4f, f, g, k).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
        buffer.vertex(matrix4f, f, g, k).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        buffer.vertex(matrix4f, i, g, k).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        buffer.vertex(matrix4f, i, g, k).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 0.0F, -1.0F).next();
        buffer.vertex(matrix4f, i, g, h).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 0.0F, -1.0F).next();
        buffer.vertex(matrix4f, f, j, k).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        buffer.vertex(matrix4f, i, j, k).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 1.0F, 0.0F, 0.0F).next();
        buffer.vertex(matrix4f, i, g, k).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix4f, i, j, k).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        buffer.vertex(matrix4f, i, j, h).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        buffer.vertex(matrix4f, i, j, k).color(1.0f, 1.0f, 1.0f, 1.0F).normal(matrix3f, 0.0F, 0.0F, 1.0F).next();
        tessellator.draw();
    }
}
