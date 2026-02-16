package xyz.ludwicz.ludwigmod.mixin;

import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.ludwicz.ludwigmod.itemmodelfix.ItemModelFix;

import java.util.List;

@Mixin(ItemModelGenerator.class)
public class MixinItemModelGenerator {

    @Inject(method = "addSubComponents", at = @At("RETURN"))
    public void increaseSide(SpriteContents spriteContents, String string, int tintIndex,
                             CallbackInfoReturnable<List<ModelElement>> cir) {
        ItemModelFix.enlargeFaces(cir);
    }

    @Overwrite
    private void buildCube(List<ItemModelGenerator.Frame> listSpans, ItemModelGenerator.Side spanFacing, int pixelX, int pixelY) {
        ItemModelFix.createOrExpandSpan(listSpans, spanFacing, pixelX, pixelY);
    }
}
