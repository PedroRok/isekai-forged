package xyz.nucleoid.isekai;

import net.minecraft.world.level.dimension.LevelStem;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;

@ApiStatus.Internal
public interface IsekaiLevelStem {
    Predicate<LevelStem> SAVE_PREDICATE = (e) -> ((IsekaiLevelStem) (Object) e).isekaifantasy$getSave();
    Predicate<LevelStem> SAVE_PROPERTIES_PREDICATE = (e) -> ((IsekaiLevelStem) (Object) e).isekai$getSaveProperties();

    void isekai$setSave(boolean value);

    boolean isekaifantasy$getSave();

    void isekai$setSaveProperties(boolean value);

    boolean isekai$getSaveProperties();
}
