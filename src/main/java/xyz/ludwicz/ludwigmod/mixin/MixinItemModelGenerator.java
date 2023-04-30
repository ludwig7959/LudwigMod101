package xyz.ludwicz.ludwigmod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.ludwicz.ludwigmod.LudwigConfig;
import xyz.ludwicz.ludwigmod.util.ItemModelUtil;
import xyz.ludwicz.ludwigmod.util.ItemModelUtil.PixelDirection;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Mixin(ItemModelGenerator.class)
public class MixinItemModelGenerator {

    @Inject(at = @At(value = "HEAD"), method = "addLayerElements(ILjava/lang/String;Lnet/minecraft/client/texture/Sprite;)Ljava/util/List;", cancellable = true)
    private void onHeadAddLayerElements(int layer, String key, Sprite sprite, CallbackInfoReturnable<List<ModelElement>> cir) {
        LudwigConfig config = AutoConfig.getConfigHolder(LudwigConfig.class).getConfig();
        if (config == null || !config.itemModelFix)
            return;

        List<ModelElement> elements = new ArrayList<>();

        int width = sprite.getWidth();
        int height = sprite.getHeight();
        float xFactor = width / 16.0F;
        float yFactor = height / 16.0F;
        float animationFrameDelta = sprite.getAnimationFrameDelta();
        int[] frames = sprite.getDistinctFrameCount().toArray();

        Map<Direction, ModelElementFace> map = new EnumMap<>(Direction.class);
        map.put(Direction.SOUTH, new ModelElementFace(null, layer, key, ItemModelUtil.createUnlerpedTexture(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0, animationFrameDelta)));
        map.put(Direction.NORTH, new ModelElementFace(null, layer, key, ItemModelUtil.createUnlerpedTexture(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0, animationFrameDelta)));
        elements.add(new ModelElement(new Vec3f(0.0F, 0.0F, 7.5F), new Vec3f(16.0F, 16.0F, 8.5F), map, null, true));

        int first1 = -1;
        int first2 = -1;
        int last1 = -1;
        int last2 = -1;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (!ItemModelUtil.isPixelAlwaysTransparent(sprite, frames, x, y)) {
                    if (ItemModelUtil.doesPixelHaveEdge(sprite, frames, x, y, PixelDirection.DOWN)) {
                        if (first1 == -1) {
                            first1 = x;
                        }
                        last1 = x;
                    }
                    if (ItemModelUtil.doesPixelHaveEdge(sprite, frames, x, y, PixelDirection.UP)) {
                        if (first2 == -1) {
                            first2 = x;
                        }
                        last2 = x;
                    }
                } else {
                    if (first1 != -1) {
                        elements.add(ItemModelUtil.createHorizontalOutlineElement(Direction.DOWN, layer, key, first1, last1, y, height, animationFrameDelta, xFactor, yFactor));
                        first1 = -1;
                    }
                    if (first2 != -1) {
                        elements.add(ItemModelUtil.createHorizontalOutlineElement(Direction.UP, layer, key, first2, last2, y, height, animationFrameDelta, xFactor, yFactor));
                        first2 = -1;
                    }
                }
            }

            if (first1 != -1) {
                elements.add(ItemModelUtil.createHorizontalOutlineElement(Direction.DOWN, layer, key, first1, last1, y, height, animationFrameDelta, xFactor, yFactor));
                first1 = -1;
            }
            if (first2 != -1) {
                elements.add(ItemModelUtil.createHorizontalOutlineElement(Direction.UP, layer, key, first2, last2, y, height, animationFrameDelta, xFactor, yFactor));
                first2 = -1;
            }
        }

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (!ItemModelUtil.isPixelAlwaysTransparent(sprite, frames, x, y)) {
                    if (ItemModelUtil.doesPixelHaveEdge(sprite, frames, x, y, PixelDirection.RIGHT)) {
                        if (first1 == -1) {
                            first1 = y;
                        }
                        last1 = y;
                    }
                    if (ItemModelUtil.doesPixelHaveEdge(sprite, frames, x, y, PixelDirection.LEFT)) {
                        if (first2 == -1) {
                            first2 = y;
                        }
                        last2 = y;
                    }
                } else {
                    if (first1 != -1) {
                        elements.add(ItemModelUtil.createVerticalOutlineElement(Direction.EAST, layer, key, first1, last1, x, height, animationFrameDelta, xFactor, yFactor));
                        first1 = -1;
                    }
                    if (first2 != -1) {
                        elements.add(ItemModelUtil.createVerticalOutlineElement(Direction.WEST, layer, key, first2, last2, x, height, animationFrameDelta, xFactor, yFactor));
                        first2 = -1;
                    }
                }
            }

            if (first1 != -1) {
                elements.add(ItemModelUtil.createVerticalOutlineElement(Direction.EAST, layer, key, first1, last1, x, height, animationFrameDelta, xFactor, yFactor));
                first1 = -1;
            }
            if (first2 != -1) {
                elements.add(ItemModelUtil.createVerticalOutlineElement(Direction.WEST, layer, key, first2, last2, x, height, animationFrameDelta, xFactor, yFactor));
                first2 = -1;
            }
        }

        cir.setReturnValue(elements);
    }
}
