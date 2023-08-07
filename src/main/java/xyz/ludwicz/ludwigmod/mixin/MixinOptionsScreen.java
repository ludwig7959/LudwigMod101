package xyz.ludwicz.ludwigmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import xyz.ludwicz.ludwigmod.resourcepack.gui.FolderedResourcePackScreen;

import java.nio.file.Path;
import java.util.ArrayList;

@Mixin(OptionsScreen.class)
public abstract class MixinOptionsScreen {
    @Shadow
    protected abstract void refreshResourcePacks(ResourcePackManager resourcePackManager);

    /**
     * @author recursiveresources
     * @reason Replace the resource packs screen with a custom one.
     */
    @Overwrite
    private void method_19824(ButtonWidget button) {
        var client = MinecraftClient.getInstance();
        var packRoots = new ArrayList<Path>();
        packRoots.add(client.getResourcePackDir().toPath());

        client.setScreen(new FolderedResourcePackScreen(
                (Screen) (Object) this, client.getResourcePackManager(),
                this::refreshResourcePacks, client.getResourcePackDir(),
                Text.translatable("resourcePack.title"),
                packRoots
        ));
    }
}