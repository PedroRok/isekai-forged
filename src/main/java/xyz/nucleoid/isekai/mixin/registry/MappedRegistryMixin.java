package xyz.nucleoid.isekai.mixin.registry;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.isekai.RemoveFromRegistry;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> implements RemoveFromRegistry<T>, WritableRegistry<T> {
    @Unique private static final Logger isekai$LOGGER = LogUtils.getLogger();

    @Shadow @Final private Map<T, Holder.Reference<T>> byValue;
    @Shadow @Final private Map<ResourceLocation, Holder.Reference<T>> byLocation;
    @Shadow @Final private Map<ResourceKey<T>, Holder.Reference<T>> byKey;
    @Shadow @Final private Map<ResourceKey<T>, Lifecycle> lifecycles;
    @Shadow @Final private ObjectList<Holder.Reference<T>> byId;
    @Shadow @Final private Object2IntMap<T> toId;
    @Shadow @Final ResourceKey<? extends Registry<T>> key;
    @Shadow private boolean frozen;

    @Override
    public boolean isekai$remove(T entry) {
        var holder = this.byValue.get(entry);
        int rawId = this.toId.removeInt(entry);
        if (rawId == -1) {
            return false;
        }

        try {
            this.byKey.remove(holder.key());
            this.byLocation.remove(holder.key().registry());
            this.byValue.remove(entry);
            this.byId.set(rawId, null);
            this.lifecycles.remove(this.key);

            return true;
        } catch (Throwable e) {
            isekai$LOGGER.error("Could not remove entry", e);
            return false;
        }
    }

    @Override
    public boolean isekai$remove(ResourceLocation key) {
        var entry = this.byLocation.get(key);
        return entry != null && entry.isBound() && this.isekai$remove(entry.value());
    }

    @Override
    public void fantasy$setFrozen(boolean value) {
        this.frozen = value;
    }

    @Override
    public boolean fantasy$isFrozen() {
        return this.frozen;
    }

    //@ModifyReturnValue(method = "", at = @At("RETURN"))
    //public Stream<Holder.Reference<T>> fixEntryStream(Stream<Holder.Reference<T>> original) {
    //    return original.filter(Objects::nonNull);
    //}


}
