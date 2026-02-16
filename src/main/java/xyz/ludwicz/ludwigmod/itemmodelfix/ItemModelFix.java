package xyz.ludwicz.ludwigmod.itemmodelfix;

import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.ItemModelGenerator.Frame;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Locale;

public class ItemModelFix {

    private static final Identifier BLOCK_ATLAS = new Identifier("textures/atlas/blocks.png");

    private static final boolean IS_MAC = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac");

    private static double expansion;
    private static double indent;
    private static double shrinkMult;

    static {
        expansion = IS_MAC ? 0.04 : 0.002;
        indent = IS_MAC ? 0.0099 : 0.0001;
        shrinkMult = IS_MAC ? 1 : 0;
    }

    //who needs anti atlas bleeding when it doesn't occur even on mipmap 4 high render distance lol
    //apparently on mac os it does waaa
    public static float getShrinkRatio(Identifier atlasLocation, float defaultValue, float returnValue) {
        if (atlasLocation.equals(BLOCK_ATLAS) && defaultValue == returnValue) {
            return (float) (defaultValue * shrinkMult);
        }
        return -1;
    }

    public static void createOrExpandSpan(List<Frame> listSpans, ItemModelGenerator.Side spanFacing,
                                          int pixelX, int pixelY) {
        int length;
        ItemModelGenerator.Frame existingSpan = null;
        for (ItemModelGenerator.Frame span2 : listSpans) {
            if (span2.getSide() == spanFacing) {
                int i = spanFacing.isVertical() ? pixelY : pixelX;
                if (span2.getLevel() != i) continue;
                //skips faces with transparent pixels so we can enlarge safely
                if (expansion != 0 && span2.getMax() != (!spanFacing.isVertical() ? pixelY : pixelX) - 1)
                    continue;
                existingSpan = span2;
                break;
            }
        }


        length = spanFacing.isVertical() ? pixelX : pixelY;
        if (existingSpan == null) {
            int newStart = spanFacing.isVertical() ? pixelY : pixelX;
            listSpans.add(new ItemModelGenerator.Frame(spanFacing, length, newStart));
        } else {
            existingSpan.expand(length);
        }
    }

    public static void enlargeFaces(CallbackInfoReturnable<List<ModelElement>> cir) {
        double inc = indent;
        double inc2 = expansion;
        for (var e : cir.getReturnValue()) {
            Vector3f from = e.from;
            Vector3f to = e.to;

            var set = e.faces.keySet();
            if (set.size() == 1) {
                var dir = set.stream().findAny().get();
                switch (dir) {
                    case UP -> {
                        from.set(from.x() - inc2, from.y() - inc, from.z() - inc2);
                        to.set(to.x() + inc2, to.y() - inc, to.z() + inc2);
                    }
                    case DOWN -> {
                        from.set(from.x() - inc2, from.y() + inc, from.z() - inc2);
                        to.set(to.x() + inc2, to.y() + inc, to.z() + inc2);
                    }
                    case WEST -> {
                        from.set(from.x() - inc, from.y() + inc2, from.z() - inc2);
                        to.set(to.x() - inc, to.y() - inc2, to.z() + inc2);
                    }
                    case EAST -> {
                        from.set(from.x() + inc, from.y() + inc2, from.z() - inc2);
                        to.set(to.x() + inc, to.y() - inc2, to.z() + inc2);
                    }
                }
            }
        }
    }
}
