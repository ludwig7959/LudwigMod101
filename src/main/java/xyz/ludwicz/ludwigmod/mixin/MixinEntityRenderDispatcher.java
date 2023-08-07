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
import xyz.ludwicz.ludwigmod.hitbox.HitboxMod;


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

        HitboxMod.renderClearHitbox(matrix, entity);

        ci.cancel();
    }
}
