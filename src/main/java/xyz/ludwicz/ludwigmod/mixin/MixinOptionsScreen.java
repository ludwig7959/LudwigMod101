package xyz.ludwicz.ludwigmod.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.Text;
import nl.enjarai.shared_resources.api.DefaultGameResources;
import nl.enjarai.shared_resources.api.GameResourceHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import xyz.ludwicz.ludwigmod.resourcepack.gui.FolderedResourcePackScreen;

import java.nio.file.Path;
import java.util.ArrayList;

@Mixin(OptionsScreen.class)
public abstract class MixinOptionsScreen extends Screen {

    protected MixinOptionsScreen(Text title) {
        super(title);
    }

    @Shadow
    protected abstract void refreshResourcePacks(ResourcePackManager resourcePackManager);

    @Overwrite
    private Screen method_47631() {
        var client = MinecraftClient.getInstance();
        var packRoots = new ArrayList<Path>();
        packRoots.add(client.getResourcePackDir());

        if (FabricLoader.getInstance().isModLoaded("shared-resources")) {
            var directory = GameResourceHelper.getPathFor(DefaultGameResources.RESOURCEPACKS);

            if (directory != null) {
                packRoots.add(directory);
            }
        }

        return new FolderedResourcePackScreen(
                this, client.getResourcePackManager(),
                this::refreshResourcePacks, client.getResourcePackDir().toFile(),
                Text.translatable("resourcePack.title"),
                packRoots
        );
    }
}