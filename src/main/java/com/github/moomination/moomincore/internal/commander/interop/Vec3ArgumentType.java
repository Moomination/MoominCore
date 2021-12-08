package com.github.moomination.moomincore.internal.commander.interop;

import com.mojang.brigadier.context.CommandContext;
import org.bukkit.util.Vector;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Vec3ArgumentType {

  private static final Class<?> ARGUMENT_VEC3;
  private static final Class<?> VEC3;

  private static final MutableCallSite GET_VEC3_CALLSITE = new MutableCallSite(MethodType.methodType(Object.class, CommandContext.class, String.class));
  private static final MethodHandle GET_VEC3 = GET_VEC3_CALLSITE.dynamicInvoker();

  private static final MutableCallSite GET_VEC3_X_CALLSITE = new MutableCallSite(MethodType.methodType(double.class, Object.class));
  private static final MethodHandle GET_VEC3_X = GET_VEC3_X_CALLSITE.dynamicInvoker();
  private static final MutableCallSite GET_VEC3_Y_CALLSITE = new MutableCallSite(MethodType.methodType(double.class, Object.class));
  private static final MethodHandle GET_VEC3_Y = GET_VEC3_Y_CALLSITE.dynamicInvoker();
  private static final MutableCallSite GET_VEC3_Z_CALLSITE = new MutableCallSite(MethodType.methodType(double.class, Object.class));
  private static final MethodHandle GET_VEC3_Z = GET_VEC3_Z_CALLSITE.dynamicInvoker();

  static {
    try {
      ARGUMENT_VEC3 = Class.forName("net.minecraft.commands.arguments.coordinates.ArgumentVec3");
      Method a = ARGUMENT_VEC3.getDeclaredMethod("a", CommandContext.class, String.class);
      a.setAccessible(true);
      GET_VEC3_CALLSITE.setTarget(MethodHandles.lookup().unreflect(a).asType(GET_VEC3_CALLSITE.type()));

      VEC3 = Class.forName("net.minecraft.world.phys.Vec3D");
      Field fieldX = VEC3.getDeclaredField("b");
      Field fieldY = VEC3.getDeclaredField("c");
      Field fieldZ = VEC3.getDeclaredField("d");
      GET_VEC3_X_CALLSITE.setTarget(MethodHandles.lookup().unreflectGetter(fieldX).asType(GET_VEC3_X_CALLSITE.type()));
      GET_VEC3_Y_CALLSITE.setTarget(MethodHandles.lookup().unreflectGetter(fieldY).asType(GET_VEC3_Y_CALLSITE.type()));
      GET_VEC3_Z_CALLSITE.setTarget(MethodHandles.lookup().unreflectGetter(fieldZ).asType(GET_VEC3_Z_CALLSITE.type()));
    } catch (ReflectiveOperationException exception) {
      throw new ExceptionInInitializerError(exception);
    }
  }

  public static Vector vec3(CommandContext<?> ctx, String name) {
    Object vec3 = getVec3(ctx, name);
    return new Vector(x(vec3), y(vec3), z(vec3));
  }

  private static Object getVec3(CommandContext<?> ctx, String name) {
    try {
      return GET_VEC3.invoke(ctx, name);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  private static double x(Object vec3) {
    try {
      return (double) GET_VEC3_X.invoke(vec3);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  private static double y(Object vec3) {
    try {
      return (double) GET_VEC3_Y.invoke(vec3);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  private static double z(Object vec3) {
    try {
      return (double) GET_VEC3_Z.invoke(vec3);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

}
