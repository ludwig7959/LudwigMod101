package xyz.ludwicz.ludwigmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatHud.class)
public class MixinChatHud121 {

    @ModifyConstant(
        method = {
            "<init>(Lnet/minecraft/client/MinecraftClient;)V",
            "addVisibleMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V",
            "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V",
            "addToMessageHistory(Ljava/lang/String;)V"
        },
        constant = @Constant(intValue = 100)
    )
    private int expandChatHistoryLimit(int original) {
        return 16384;
    }
}
