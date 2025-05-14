package xyz.nucleoid.isekai.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkGenerator;
import xyz.nucleoid.isekai.Isekai;
import xyz.nucleoid.isekai.RuntimeWorldConfig;

import java.util.function.Function;

/**
 * A {@link ChunkGenerator} instance that does not know how to be, and does not care to be serialized.
 * This is particularly useful when creating a temporary world with Fantasy.
 * <p>
 * If serialized, however, it will be loaded as a {@link VoidChunkGenerator void world}.
 *
 * @see Isekai#openTemporaryWorld(RuntimeWorldConfig)
 */
public abstract class TransientChunkGenerator extends ChunkGenerator {
    public static final MapCodec<? extends ChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            RegistryOps.retrieveElement(Biomes.THE_VOID)
    ).apply(i, VoidChunkGenerator::new));

    public TransientChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    public TransientChunkGenerator(BiomeSource biomeSource, Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter) {
        super(biomeSource, generationSettingsGetter);
    }

    @Override
    protected final MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
}
