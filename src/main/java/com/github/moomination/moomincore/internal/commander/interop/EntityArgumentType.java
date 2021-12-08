package com.github.moomination.moomincore.internal.commander.interop;

import com.mojang.brigadier.context.CommandContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Collection;

public final class EntityArgumentType {

  private static final Class<?> ARGUMENT_ENTITY;
  private static final Class<?> ENTITY;

  private static final MethodHandle GET_ENTITY;
  private static final MethodHandle GET_ENTITIES;
  private static final MethodHandle GET_PLAYER;
  private static final MethodHandle GET_PLAYERS;
  private static final MethodHandle GET_BUKKIT_ENTITY;

  static {
    try {
      ARGUMENT_ENTITY = Class.forName("net.minecraft.commands.arguments.ArgumentEntity");
      {
        Method getEntity = ARGUMENT_ENTITY.getDeclaredMethod("a", CommandContext.class, String.class);
        getEntity.setAccessible(true);
        GET_ENTITY = MethodHandles.lookup().unreflect(getEntity);
      }

      {
        Method getEntities = ARGUMENT_ENTITY.getDeclaredMethod("b", CommandContext.class, String.class);
        getEntities.setAccessible(true);
        GET_ENTITIES = MethodHandles.lookup().unreflect(getEntities);
      }

      {
        Method getOptionalEntities = ARGUMENT_ENTITY.getDeclaredMethod("a", CommandContext.class, String.class);
        getOptionalEntities.setAccessible(true);
        MethodHandles.lookup().unreflect(getOptionalEntities);
      }

      {
        Method getOptionalPlayers = ARGUMENT_ENTITY.getDeclaredMethod("b", CommandContext.class, String.class);
        getOptionalPlayers.setAccessible(true);
        MethodHandles.lookup().unreflect(getOptionalPlayers);
      }

      {
        Method getPlayer = ARGUMENT_ENTITY.getDeclaredMethod("e", CommandContext.class, String.class);
        getPlayer.setAccessible(true);
        GET_PLAYER = MethodHandles.lookup().unreflect(getPlayer);
      }

      {
        Method getPlayers = ARGUMENT_ENTITY.getDeclaredMethod("f", CommandContext.class, String.class);
        getPlayers.setAccessible(true);
        GET_PLAYERS = MethodHandles.lookup().unreflect(getPlayers);
      }

      {
        ENTITY = Class.forName("net.minecraft.world.entity.Entity");
        Method getBukkitEntity = ENTITY.getMethod("getBukkitEntity");
        getBukkitEntity.setAccessible(true);
        GET_BUKKIT_ENTITY = MethodHandles.lookup().unreflect(getBukkitEntity);
      }
    } catch (ReflectiveOperationException exception) {
      throw new ExceptionInInitializerError(exception);
    }
  }

  public static Player entity(CommandContext<?> ctx, String name) {
    Object player = getEntity(ctx, name);
    return toBukkit(player);
  }

  public static Collection<Entity> entities(CommandContext<?> ctx, String name) {
    return ((Collection<?>) getEntities(ctx, name))
      .stream()
      .map(EntityArgumentType::<Entity>toBukkit)
      .toList();
  }

  public static Player player(CommandContext<?> ctx, String name) {
    Object player = getPlayer(ctx, name);
    return toBukkit(player);
  }

  public static Collection<Player> players(CommandContext<?> ctx, String name) {
    return ((Collection<?>) getPlayers(ctx, name))
      .stream()
      .map(EntityArgumentType::<Player>toBukkit)
      .toList();
  }

  private static Object getEntity(CommandContext<?> ctx, String name) {
    try {
      return GET_ENTITY.invoke(ctx, name);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  private static Object getEntities(CommandContext<?> ctx, String name) {
    try {
      return GET_ENTITIES.invoke(ctx, name);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  private static Object getPlayer(CommandContext<?> ctx, String name) {
    try {
      return GET_PLAYER.invoke(ctx, name);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  private static Object getPlayers(CommandContext<?> ctx, String name) {
    try {
      return GET_PLAYERS.invoke(ctx, name);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T extends Entity> T toBukkit(Object player) {
    try {
      return (T) GET_BUKKIT_ENTITY.invoke(player);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

}
