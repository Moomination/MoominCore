package com.github.moomination.moomincore.command.interop;

import com.mojang.brigadier.context.CommandContext;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.Method;

public class EntityNativeToBukkit {

  private static final Class<?> ARGUMENT_ENTITY;
  private static final Class<?> ENTITY_PLAYER;

  private static final MutableCallSite GET_PLAYER_CALLSITE = new MutableCallSite(MethodType.methodType(Object.class, CommandContext.class, String.class));
  private static final MethodHandle GET_PLAYER = GET_PLAYER_CALLSITE.dynamicInvoker();
  private static final MutableCallSite GET_BUKKIT_ENTITY_CALLSITE = new MutableCallSite(MethodType.methodType(Player.class, Object.class));
  private static final MethodHandle GET_BUKKIT_ENTITY = GET_BUKKIT_ENTITY_CALLSITE.dynamicInvoker();

  static {
    try {
      ARGUMENT_ENTITY = Class.forName("net.minecraft.commands.arguments.ArgumentEntity");
      Method e = ARGUMENT_ENTITY.getDeclaredMethod("e", CommandContext.class, String.class);
      e.setAccessible(true);
      GET_PLAYER_CALLSITE.setTarget(MethodHandles.lookup().unreflect(e).asType(GET_PLAYER_CALLSITE.type()));

      ENTITY_PLAYER = Class.forName("net.minecraft.server.level.EntityPlayer");
      Method getBukkitEntity = ENTITY_PLAYER.getMethod("getBukkitEntity");
      getBukkitEntity.setAccessible(true);
      GET_BUKKIT_ENTITY_CALLSITE.setTarget(MethodHandles.lookup().unreflect(getBukkitEntity).asType(GET_BUKKIT_ENTITY_CALLSITE.type()));
    } catch (ReflectiveOperationException exception) {
      throw new ExceptionInInitializerError(exception);
    }
  }

  public static Player player(CommandContext<?> ctx, String name) {
    Object player = getPlayer(ctx, name);
    return toBukkit(player);
  }

  private static Object getPlayer(CommandContext<?> ctx, String name) {
    try {
      return GET_PLAYER.invoke(ctx, name);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  private static Player toBukkit(Object player) {
    try {
      return (Player) GET_BUKKIT_ENTITY.invoke(player);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

}
