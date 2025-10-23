package xyz.nucleoid.isekai;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import org.apache.commons.io.FileUtils;
import xyz.nucleoid.isekai.mixin.MinecraftServerAccess;

import java.io.File;
import java.io.IOException;

final class RuntimeWorldManager {
    private final MinecraftServer server;
    private final MinecraftServerAccess serverAccess;

    RuntimeWorldManager(MinecraftServer server) {
        this.server = server;
        this.serverAccess = (MinecraftServerAccess) server;
    }

    RuntimeWorld add(ResourceKey<Level> worldKey, RuntimeWorldConfig config, RuntimeWorld.Style style) {
        LevelStem options = config.createDimensionOptions(this.server);

        if (style == RuntimeWorld.Style.TEMPORARY) {
            ((IsekaiLevelStem) (Object) options).isekai$setSave(false);
        }
        ((IsekaiLevelStem) (Object) options).isekai$setSaveProperties(false);
        MappedRegistry<LevelStem> dimensionsRegistry = getDimensionsRegistry(this.server);

        boolean isFrozen = ((RemoveFromRegistry<?>) dimensionsRegistry).fantasy$isFrozen();
        ((RemoveFromRegistry<?>) dimensionsRegistry).fantasy$setFrozen(false);

        var key = ResourceKey.create(Registries.LEVEL_STEM, worldKey.registry());
        if (!dimensionsRegistry.containsKey(key)) {
            dimensionsRegistry.register(key, options, Lifecycle.stable());
        }

        ((RemoveFromRegistry<?>) dimensionsRegistry).fantasy$setFrozen(isFrozen);
        RuntimeWorld world = config.getWorldConstructor().createWorld(this.server, worldKey, config, style);

        this.serverAccess.getLevels().put(world.dimension(), world);
        MinecraftForge.EVENT_BUS.post(new LevelEvent.Load(world));

        // tick the world to ensure it is ready for use right away
        world.tick(() -> true);

        return world;
    }

    void delete(ServerLevel world) {
        ResourceKey<Level> dimensionKey = world.dimension();

        if (this.serverAccess.getLevels().remove(dimensionKey, world)) {
            MinecraftForge.EVENT_BUS.post(new LevelEvent.Unload(world));

            MappedRegistry<LevelStem> dimensionsRegistry = getDimensionsRegistry(this.server);
            RemoveFromRegistry.remove(dimensionsRegistry, dimensionKey.registry());

            LevelStorageSource.LevelStorageAccess session = this.serverAccess.getStorageSource();
            File worldDirectory = session.getDimensionPath(dimensionKey).toFile();
            if (worldDirectory.exists()) {
                try {
                    FileUtils.deleteDirectory(worldDirectory);
                } catch (IOException e) {
                    Isekai.LOGGER.warn("Failed to delete world directory", e);
                    try {
                        FileUtils.forceDeleteOnExit(worldDirectory);
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    void unload(ServerLevel world) {
        ResourceKey<Level> dimensionKey = world.dimension();

        if (this.serverAccess.getLevels().remove(dimensionKey, world)) {
            world.save(new ProgressListener() {
                @Override
                public void progressStartNoAbort(Component component) {
                }

                @Override
                public void progressStart(Component header) {
                }

                @Override
                public void progressStage(Component stage) {
                }

                @Override
                public void progressStagePercentage(int percentage) {
                }

                @Override
                public void stop() {
                }
            }, true, false);

            MinecraftForge.EVENT_BUS.post(new LevelEvent.Unload(world));

            MappedRegistry<LevelStem> dimensionsRegistry = getDimensionsRegistry(RuntimeWorldManager.this.server);
            RemoveFromRegistry.remove(dimensionsRegistry, dimensionKey.registry());
        }
    }

    private static MappedRegistry<LevelStem> getDimensionsRegistry(MinecraftServer server) {
        RegistryAccess registryManager = server.registries().compositeAccess();
        return (MappedRegistry<LevelStem>) registryManager.lookupOrThrow(Registries.LEVEL_STEM);
    }
}
