package xyz.nucleoid.isekai.util;

import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.jetbrains.annotations.Nullable;

/**
 * Allows chunk generators other than noise chunk generators to provide custom chunk generator settings.
 */
public interface NoiseGeneratorSettingsProvider {
    @Nullable
    NoiseGeneratorSettings getSettings();
}
