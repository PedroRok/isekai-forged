package xyz.nucleoid.isekai;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IsekaiWorldAccess {
    void isekai$setTickWhenEmpty(boolean tickWhenEmpty);

    boolean fantasy$shouldTick();
}
