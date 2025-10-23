package xyz.nucleoid.isekai;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xyz.nucleoid.isekai.util.TransientChunkGenerator;
import xyz.nucleoid.isekai.util.VoidChunkGenerator;

@Mod(Isekai.ID)
public final class IsekaiInitializer {
    public static ResourceKey<DimensionType> DEFAULT_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, resource("default"));
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, Isekai.ID);

    public static final RegistryObject<Codec<? extends ChunkGenerator>, Codec<VoidChunkGenerator>> VOID_GENERATOR = CHUNK_GENERATORS.register("void", () -> VoidChunkGenerator.CODEC);
    public static final RegistryObject<Codec<? extends ChunkGenerator>, Codec<? extends ChunkGenerator>> TRANSIENT_GENERATOR = CHUNK_GENERATORS.register("transient", () -> TransientChunkGenerator.CODEC);

    public IsekaiInitializer(IEventBus eventBus) {
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        CHUNK_GENERATORS.register(eventBus);

        forgeEventBus.addListener(IsekaiInitializer::onServerPreTick);
        forgeEventBus.addListener(IsekaiInitializer::onServerStopping);
    }

    private static void onServerPreTick(TickEvent.ServerTickEvent event) {
        if (event.getPhase().equals(TickEvent.Phase.START)) return;
        Isekai fantasy = Isekai.get(event.getServer());
        fantasy.tick();
    }

    private static void onServerStopping(ServerStoppingEvent event) {
        Isekai fantasy = Isekai.get(event.getServer());
        fantasy.onServerStopping();
    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(Isekai.ID, path);
    }
}
