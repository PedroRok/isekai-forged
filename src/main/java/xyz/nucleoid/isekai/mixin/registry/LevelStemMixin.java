package xyz.nucleoid.isekai.mixin.registry;

import net.minecraft.world.level.dimension.LevelStem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.nucleoid.isekai.IsekaiLevelStem;

@Mixin(LevelStem.class)
public class LevelStemMixin implements IsekaiLevelStem {
    @Unique
    private boolean isekai$save = true;
    @Unique
    private boolean isekai$saveProperties = true;

    @Override
    public void isekai$setSave(boolean value) {
        this.isekai$save = value;
    }

    @Override
    public boolean isekaifantasy$getSave() {
        return this.isekai$save;
    }

    @Override
    public void isekai$setSaveProperties(boolean value) {
        this.isekai$saveProperties = value;
    }

    @Override
    public boolean isekai$getSaveProperties() {
        return this.isekai$saveProperties;
    }
}
