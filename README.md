# Isekai
Isekai is a port to the neoforge loader of the [Fantasy](https://github.com/NucleoidMC/fantasy) library
that allows for dimensions to be created and destroyed at runtime on the server.

It supports both temporary dimensions which do not get saved,
and persistent dimensions which can be safely used across server restarts.

## Using

### Adding to Gradle
To add Isekai to your Gradle project, add the Jitpack Maven repository and Isekai dependency.
`ISEKAI_VERSION` should be replaced with the latest version from [Jitpack](https://jitpack.io/#nertzhuldev/isekai).
```gradle
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  // ...
  modImplementation 'com.github.nertzhuldev:isekai:ISEKAI_VERSION'
}
```

### Creating Runtime Dimensions
All access to Isekai's APIs goes through the `Isekai` object, which can be acquired given a `MinecraftServer` instance.

```java
Isekai isekai = Isekai.get(server);
// ...
```

All dimensions created with Isekai must be set up through a `RuntimeWorldConfig`.
This specifies how the dimension should be created, involving a dimension type, seed, chunk generator, and so on.

For example, we could create a config like such:
```java
RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
    .setDimensionType(BuiltinDimensionTypes.OVERWORLD)
    .setDifficulty(Difficulty.HARD)
    .setGameRule(GameRules.RULE_DAYLIGHT, false)
    .setGenerator(server.overworld().getChunkSource().getGenerator())
    .setSeed(1234L);
```

Values such as difficulty, game rules, and weather can all be configured per-world. 

#### Creating a temporary dimension
Once we have a runtime world config, creating a temporary dimension is simple:
```java
RuntimeWorldHandle worldHandle = isekai.openTemporaryWorld(worldConfig);

// set a block in our created temporary world!
ServerLevel world = worldHandle.asWorld();
world.setBlockState(BlockPos.ZERO, Blocks.STONE.defaultBlockState());

// we don't need the world anymore, delete it!
worldHandle.delete();
```
Explicit deletion is not strictly required for temporary worlds: they will be automatically cleaned up when the server exits.
However, it is generally a good idea to delete old worlds if they're not in use anymore.

#### Creating a persistent dimension 
Persistent dimensions work along very similar lines to temporary dimensions:

```java
RuntimeWorldHandle worldHandle = isekai.getOrOpenPersistentWorld(ResourceLocation.fromNamespaceAndPath("foo", "bar"), config);

// set a block in our created persistent world!
ServerLevel world = worldHandle.asWorld();
world.setBlockState(BlockPos.ZERO, Blocks.STONE.defaultBlockState());
```

The main difference involves the addition of an `ResourceLocation` parameter which much be specified to name your dimension uniquely.

Another **very important note** with persistent dimensions is that `getOrOpenPersistentWorld` must be called to re-initialize
the dimension after a game restart! Isekai will not restore the dimension by itself- it only makes sure that the world data
sticks around. This means, if you have a custom persistent dimension, you need to keep track of it and all its needed
data such that it can be reconstructed by calling `getOrOpenPersistentWorld` again with the same ResourceLocation.
