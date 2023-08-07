package xyz.ludwicz.ludwigmod.resourcepack.pack;

import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProfile.Factory;
import net.minecraft.resource.ResourcePackProfile.InsertionPosition;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ZipResourcePack;
import org.apache.commons.lang3.StringUtils;
import xyz.ludwicz.ludwigmod.util.ResourcePackUtil;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public class NestedFolderPackProvider implements ResourcePackProvider {
    protected File root;
    protected int rootLength;

    public NestedFolderPackProvider(File root) {
        this.root = root;
        this.rootLength = root.getAbsolutePath().length();
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder, Factory factory) {
        File[] folders = root.listFiles(ResourcePackUtil::isFolderButNotFolderBasedPack);

        for (File folder : ResourcePackUtil.wrap(folders)) {
            processFolder(folder, profileAdder, factory);
        }
    }

    public void processFolder(File folder, Consumer<ResourcePackProfile> profileAdder, Factory factory) {
        if (ResourcePackUtil.isFolderBasedPack(folder)) {
            addPack(folder, profileAdder, factory);
            return;
        }

        File[] zipFiles = folder.listFiles(file -> file.isFile() && file.getName().endsWith(".zip"));

        for (File zipFile : ResourcePackUtil.wrap(zipFiles)) {
            addPack(zipFile, profileAdder, factory);
        }

        File[] nestedFolders = folder.listFiles(File::isDirectory);

        for (File nestedFolder : ResourcePackUtil.wrap(nestedFolders)) {
            processFolder(nestedFolder, profileAdder, factory);
        }
    }

    public void addPack(File fileOrFolder, Consumer<ResourcePackProfile> profileAdder, Factory factory) {
        String displayName = fileOrFolder.getName();
        String name = "file/" + StringUtils.removeStart(
                fileOrFolder.getAbsolutePath().substring(rootLength).replace('\\', '/'), "/");
        ResourcePackProfile info;
        Path rootPath = root.toPath();
        Path filePath = rootPath.relativize(fileOrFolder.toPath());
        FolderedPackSource packSource = new FolderedPackSource(rootPath, filePath);

        if (fileOrFolder.isDirectory()) {
            info = ResourcePackProfile.of(
                    name, false, () -> new DirectoryResourcePack(fileOrFolder),
                    factory, InsertionPosition.TOP, packSource
            );
        } else {
            info = ResourcePackProfile.of(
                    name, false, () -> new ZipResourcePack(fileOrFolder),
                    factory, InsertionPosition.TOP, packSource
            );
        }

        if (info != null) {
            profileAdder.accept(info);
        }
    }
}