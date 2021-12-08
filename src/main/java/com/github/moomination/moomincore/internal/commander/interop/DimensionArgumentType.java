package com.github.moomination.moomincore.internal.commander.interop;

import com.mojang.brigadier.context.CommandContext;
import org.bukkit.World;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.Method;

public final class DimensionArgumentType {

  private static final Class<?> ARGUMENT_DIMENSION;
  private static final Class<?> LEVEL;

  private static final MutableCallSite GET_DIMENSION_CALLSITE = new MutableCallSite(MethodType.methodType(Object.class, CommandContext.class, String.class));
  private static final MethodHandle GET_DIMENSION = GET_DIMENSION_CALLSITE.dynamicInvoker();
  private static final MutableCallSite GET_WORLD_CALLSITE = new MutableCallSite(MethodType.methodType(World.class, Object.class));
  private static final MethodHandle GET_WORLD = GET_WORLD_CALLSITE.dynamicInvoker();

  static {
    try {
      ARGUMENT_DIMENSION = Class.forName("net.minecraft.commands.arguments.ArgumentDimension");
      Method a = ARGUMENT_DIMENSION.getDeclaredMethod("a", CommandContext.class, String.class);
      a.setAccessible(true);
      GET_DIMENSION_CALLSITE.setTarget(MethodHandles.lookup().unreflect(a).asType(GET_DIMENSION_CALLSITE.type()));

      LEVEL = Class.forName("net.minecraft.level.World");
      Method getWorld = LEVEL.getDeclaredMethod("getWorld");
      getWorld.setAccessible(true);
      GET_WORLD_CALLSITE.setTarget(MethodHandles.lookup().unreflect(getWorld).asType(GET_WORLD_CALLSITE.type()));
    } catch (ReflectiveOperationException exception) {
      throw new ExceptionInInitializerError(exception);
    }
  }

  public static World dimension(CommandContext<?> ctx, String name) {
    Object level = getDimension(ctx, name);
    return toBukkit(level);
  }

  private static Object getDimension(CommandContext<?> ctx, String name) {
    try {
      return GET_DIMENSION.invoke(ctx, name);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  private static World toBukkit(Object level) {
    try {
      return (World) GET_WORLD.invoke(level);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

}
