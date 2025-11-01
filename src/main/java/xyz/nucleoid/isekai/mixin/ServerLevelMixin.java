package xyz.nucleoid.isekai.mixin;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.isekai.IsekaiWorldAccess;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements IsekaiWorldAccess {
    @Unique private static final int TICK_TIMEOUT = 20 * 15;
    @Unique private boolean isekai$tickWhenEmpty = true;
    @Unique private int isekai$tickTimeout;

    @Shadow public abstract List<ServerPlayer> players();
    @Shadow public abstract ServerChunkCache getChunkSource();

    @Override
    public void isekai$setTickWhenEmpty(boolean tickWhenEmpty) {
        this.isekai$tickWhenEmpty = tickWhenEmpty;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        boolean shouldTick = this.isekai$tickWhenEmpty || !this.isWorldEmpty();
        if (shouldTick) {
            this.isekai$tickTimeout = TICK_TIMEOUT;
        } else if (this.isekai$tickTimeout-- <= 0) {
            ci.cancel();
        }
    }

    @Override
    public boolean fantasy$shouldTick() {
        boolean shouldTick = this.isekai$tickWhenEmpty || !this.isWorldEmpty();
        return shouldTick || this.isekai$tickTimeout > 0;
    }

    @Unique
    private boolean isWorldEmpty() {
        return this.players().isEmpty() && this.getChunkSource().getLoadedChunksCount() <= 0;
    }
}
