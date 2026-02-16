package xyz.ludwicz.ludwigmod.resourcepack.pack;

import net.minecraft.resource.ResourcePackProfile;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ExternalNestedFolderPackProvider extends NestedFolderPackProvider {
    protected final Supplier<Path> pathSupplier;

    public ExternalNestedFolderPackProvider(Supplier<Path> pathSupplier) {
        super(pathSupplier.get().toFile());
        this.pathSupplier = pathSupplier;
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        Path path = pathSupplier.get();
        if (path == null) return;
        root = path.toFile();
        rootLength = root.getAbsolutePath().length();

        super.register(profileAdder);
    }
}
