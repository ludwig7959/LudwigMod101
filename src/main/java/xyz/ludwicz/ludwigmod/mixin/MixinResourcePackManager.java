package xyz.ludwicz.ludwigmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.ludwicz.ludwigmod.resourcepack.pack.NestedFolderPackProvider;

import java.util.HashSet;
import java.util.Set;

@Mixin(ResourcePackManager.class)
public abstract class MixinResourcePackManager {
    @Shadow
    @Final
    @Mutable
    private Set<ResourcePackProvider> providers;

    @Inject(
            method = "<init>(Lnet/minecraft/resource/ResourcePackProfile$Factory;[Lnet/minecraft/resource/ResourcePackProvider;)V",
            at = @At("RETURN")
    )
    private void recursiveresources$onInit(CallbackInfo ci) {
        // Only add our own provider if this is the manager of client
        // resource packs, we wouldn't want to mess with datapacks
        if (providers.stream().anyMatch(provider -> provider == MinecraftClient.getInstance().getResourcePackProvider())) {
            var client = MinecraftClient.getInstance();

            providers = new HashSet<>(providers);
            providers.add(new NestedFolderPackProvider(client.getResourcePackDir()));
        }
    }
}