package xyz.nucleoid.isekai;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xyz.nucleoid.isekai.util.TransientChunkGenerator;
import xyz.nucleoid.isekai.util.VoidChunkGenerator;

@Mod(Isekai.ID)
public final class IsekaiInitializer {
    public static ResourceKey<DimensionType> DEFAULT_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, resource("default"));
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, Isekai.ID);

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<VoidChunkGenerator>> VOID_GENERATOR = CHUNK_GENERATORS.register("void", () -> VoidChunkGenerator.CODEC);
    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<? extends ChunkGenerator>> TRANSIENT_GENERATOR = CHUNK_GENERATORS.register("transient", () -> TransientChunkGenerator.CODEC);

    public IsekaiInitializer(IEventBus eventBus) {
        IEventBus forgeEventBus = NeoForge.EVENT_BUS;

        CHUNK_GENERATORS.register(eventBus);

        forgeEventBus.addListener(IsekaiInitializer::onServerPreTick);
        forgeEventBus.addListener(IsekaiInitializer::onServerStopping);
    }

    private static void onServerPreTick(ServerTickEvent.Pre event) {
        Isekai fantasy = Isekai.get(event.getServer());
        fantasy.tick();
    }

    private static void onServerStopping(ServerStoppingEvent event) {
        Isekai fantasy = Isekai.get(event.getServer());
        fantasy.onServerStopping();
    }

    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(Isekai.ID, path);
    }
}
