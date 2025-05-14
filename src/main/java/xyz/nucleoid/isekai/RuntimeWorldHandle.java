package xyz.nucleoid.isekai;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class RuntimeWorldHandle {
    private final Isekai isekai;
    private final ServerLevel world;

    RuntimeWorldHandle(Isekai isekai, ServerLevel world) {
        this.isekai = isekai;
        this.world = world;
    }

    public void setTickWhenEmpty(boolean tickWhenEmpty) {
        ((IsekaiWorldAccess) this.world).isekai$setTickWhenEmpty(tickWhenEmpty);
    }

    /**
     * Deletes the world, including all stored files
     */
    public void delete() {
        this.isekai.enqueueWorldDeletion(this.world);
    }

    /**
     * Unloads the world. It only deletes the files if world is temporary.
     */
    public void unload() {
        if (this.world instanceof RuntimeWorld runtimeWorld && runtimeWorld.style == RuntimeWorld.Style.TEMPORARY) {
            this.isekai.enqueueWorldDeletion(this.world);
        } else {
            this.isekai.enqueueWorldUnloading(this.world);
        }
    }

    public ServerLevel asWorld() {
        return this.world;
    }

    public ResourceKey<Level> getRegistryKey() {
        return this.world.dimension();
    }
}
