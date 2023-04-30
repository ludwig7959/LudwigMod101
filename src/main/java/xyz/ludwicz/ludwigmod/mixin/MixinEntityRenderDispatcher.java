package xyz.ludwicz.ludwigmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.ludwicz.ludwigmod.LudwigConfig;


@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {

    @Inject(
            method = {"renderHitbox"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private static void renderHitBox(MatrixStack matrix, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if(config == null || !config.clearHitboxes)
            return;

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
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableCull();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
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

        ci.cancel();
    }
}
