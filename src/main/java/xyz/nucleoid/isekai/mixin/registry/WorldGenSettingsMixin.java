package xyz.nucleoid.isekai.mixin.registry;

import com.google.common.collect.Maps;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.nucleoid.isekai.IsekaiLevelStem;

@Mixin(WorldGenSettings.class)
public class WorldGenSettingsMixin {

    @ModifyArg(
            method = "encode(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/world/level/levelgen/WorldOptions;Lnet/minecraft/world/level/levelgen/WorldDimensions;)Lcom/mojang/serialization/DataResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/WorldGenSettings;<init>(Lnet/minecraft/world/level/levelgen/WorldOptions;Lnet/minecraft/world/level/levelgen/WorldDimensions;)V"), index = 1)
    private static WorldDimensions fantasy$wrapWorldGenSettings(WorldDimensions original) {
        var dimensions = original.dimensions();
        var saveDimensions = Maps.filterEntries(dimensions, entry -> IsekaiLevelStem.SAVE_PROPERTIES_PREDICATE.test(entry.getValue()));

        return new WorldDimensions(saveDimensions);
    }
}
