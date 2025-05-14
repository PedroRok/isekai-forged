package xyz.nucleoid.isekai.util;

import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class VoidWorldProgressListener implements ChunkProgressListener {
    public static final VoidWorldProgressListener INSTANCE = new VoidWorldProgressListener();

    private VoidWorldProgressListener() {
    }

    @Override
    public void updateSpawnPos(ChunkPos spawnPos) {
    }

    @Override
    public void onStatusChange(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
    }
}
