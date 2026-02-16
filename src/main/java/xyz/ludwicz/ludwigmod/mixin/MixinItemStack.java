package xyz.ludwicz.ludwigmod.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.ludwicz.ludwigmod.saturation.SaturationMod;
import xyz.ludwicz.ludwigmod.tooltip.TooltipMod;

import java.util.List;

@Mixin(ItemStack.class)
public class MixinItemStack {
    @Inject(at = @At("RETURN"), method = "getTooltip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;")
    private void getTooltipFromItem(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info) {
        TooltipMod.onItemTooltip((ItemStack) (Object) this, player, context, info.getReturnValue());
    }
}