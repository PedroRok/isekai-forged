package xyz.nucleoid.isekai.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.isekai.util.NoiseGeneratorSettingsProvider;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;dummy()Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;"))
    private NoiseGeneratorSettings isekai$useProvidedChunkGeneratorSettings(Operation<NoiseGeneratorSettings> original, @Local(argsOnly = true) ChunkGenerator chunkGenerator) {
        if (chunkGenerator instanceof NoiseGeneratorSettingsProvider provider) {
            NoiseGeneratorSettings settings = provider.getSettings();
            if (settings != null) return settings;
        }

        return original.call();
    }
}
