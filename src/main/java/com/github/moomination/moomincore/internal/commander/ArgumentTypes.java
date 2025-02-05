package com.github.moomination.moomincore.internal.commander;

import com.github.moomination.moomincore.internal.commander.interop.DimensionArgumentType;
import com.github.moomination.moomincore.internal.commander.interop.EntityArgumentType;
import com.github.moomination.moomincore.internal.commander.interop.Vec3ArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.commodore.MinecraftArgumentTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.util.Collection;

public final class ArgumentTypes {

  private static final ArgumentType<?> ENTITY;
  private static final ArgumentType<?> ENTITIES;
  private static final ArgumentType<?> PLAYER;
  private static final ArgumentType<?> PLAYERS;
  private static final ArgumentType<?> VEC3;
  private static final ArgumentType<?> DIMENSION;

  static {
    try {
      Class<? extends ArgumentType<?>> entityArgumentType = MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft("entity"));
      Constructor<? extends ArgumentType<?>> entityArgumentTypeConstructor = entityArgumentType.getDeclaredConstructor(boolean.class, boolean.class);
      entityArgumentTypeConstructor.setAccessible(true);
      ENTITY = entityArgumentTypeConstructor.newInstance(true, false);
      ENTITIES = entityArgumentTypeConstructor.newInstance(false, false);
      PLAYER = entityArgumentTypeConstructor.newInstance(true, true);
      PLAYERS = entityArgumentTypeConstructor.newInstance(false, true);

      Class<? extends ArgumentType<?>> vec3ArgumentType = MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft("vec3"));
      Constructor<? extends ArgumentType<?>> vec3ArgumentTypeConstructor = vec3ArgumentType.getDeclaredConstructor(boolean.class);
      vec3ArgumentTypeConstructor.setAccessible(true);
      VEC3 = vec3ArgumentTypeConstructor.newInstance(true);

      DIMENSION = MinecraftArgumentTypes.getByKey(NamespacedKey.minecraft("dimension"));
    } catch (ReflectiveOperationException exception) {
      throw new ExceptionInInitializerError(exception);
    }
  }

  public static ArgumentType<?> player() {
    return PLAYER;
  }

  public static ArgumentType<?> players() {
    return PLAYERS;
  }

  public static ArgumentType<?> entity() {
    return ENTITY;
  }

  public static ArgumentType<?> entities() {
    return ENTITIES;
  }

  public static ArgumentType<?> vec3() {
    return VEC3;
  }

  public static ArgumentType<?> dimension() {
    return DIMENSION;
  }

  private ArgumentTypes() {
  }

  public static Player player(CommandContext<?> ctx, String argumentName) {
    return EntityArgumentType.player(ctx, argumentName);
  }

  public static Collection<Player> players(CommandContext<?> ctx, String argumentName) {
    return EntityArgumentType.players(ctx, argumentName);
  }

  public static Vector vec3(CommandContext<?> ctx, String argumentName) {
    return Vec3ArgumentType.vec3(ctx, argumentName);
  }

  public static World dimension(CommandContext<?> ctx, String argumentName) {
    return DimensionArgumentType.dimension(ctx, argumentName);
  }

}
