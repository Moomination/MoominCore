package com.github.moomination.moomincore.command;

import com.github.moomination.moomincore.MoominCore;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class BukkitToNative {

  private static final Class<?> CLASS_VANILLA_COMMAND_WRAPPER;
  private static final Class<?> CLASS_DEDICATED_SERVER;
  private static final Class<?> CLASS_COMMAND_LISTENER_WRAPPER;
  private static final Class<?> CLASS_COMMAND_DISPATCHER;

  private static final MutableCallSite GET_LISTENER_CALLSITE;
  private static final MethodHandle GET_LISTENER;
  private static final MutableCallSite GET_CONSOLE_FIELD_CALLSITE;
  private static final MethodHandle GET_CONSOLE_FIELD;
  private static final MutableCallSite GET_COMMAND_DISPATCHER_CALLSITE;
  private static final MethodHandle GET_COMMAND_DISPATCHER;
  private static final MutableCallSite PERFORM_COMMAND_CALLSITE;
  private static final MethodHandle PERFORM_COMMAND;

  static {
    try {
      CLASS_COMMAND_DISPATCHER = Class.forName("net.minecraft.commands.CommandDispatcher");
      CLASS_DEDICATED_SERVER = Class.forName("net.minecraft.server.dedicated.DedicatedServer");
      CLASS_COMMAND_LISTENER_WRAPPER = Class.forName("net.minecraft.commands.CommandListenerWrapper");
      CLASS_VANILLA_COMMAND_WRAPPER = Naming.obcClass("command.VanillaCommandWrapper");

      GET_LISTENER_CALLSITE = new MutableCallSite(MethodType.methodType(CLASS_COMMAND_LISTENER_WRAPPER, CommandSender.class));
      GET_LISTENER = GET_LISTENER_CALLSITE.dynamicInvoker();
      GET_CONSOLE_FIELD_CALLSITE = new MutableCallSite(MethodType.methodType(CLASS_DEDICATED_SERVER, Server.class));
      GET_CONSOLE_FIELD = GET_CONSOLE_FIELD_CALLSITE.dynamicInvoker();
      GET_COMMAND_DISPATCHER_CALLSITE = new MutableCallSite(MethodType.methodType(CLASS_COMMAND_DISPATCHER, CLASS_DEDICATED_SERVER));
      GET_COMMAND_DISPATCHER = GET_COMMAND_DISPATCHER_CALLSITE.dynamicInvoker();
      PERFORM_COMMAND_CALLSITE = new MutableCallSite(MethodType.methodType(int.class, CLASS_COMMAND_DISPATCHER, CLASS_COMMAND_LISTENER_WRAPPER, String.class, String.class, boolean.class));
      PERFORM_COMMAND = PERFORM_COMMAND_CALLSITE.dynamicInvoker();

      Method getListener = CLASS_VANILLA_COMMAND_WRAPPER.getDeclaredMethod("getListener", CommandSender.class);
      getListener.setAccessible(true);
      GET_LISTENER_CALLSITE.setTarget(MethodHandles.lookup().unreflect(getListener).asType(GET_LISTENER_CALLSITE.type()));

      Class<?> commodoreImpl = MoominCore.commodore().getClass();
      Field consoleFieldField = commodoreImpl.getDeclaredField("CONSOLE_FIELD");
      consoleFieldField.setAccessible(true);
      Field console = (Field) consoleFieldField.get(null);
      console.setAccessible(true);
      GET_CONSOLE_FIELD_CALLSITE.setTarget(MethodHandles.lookup().unreflectGetter(console).asType(GET_CONSOLE_FIELD_CALLSITE.type()));

      Field getCommandDispatcherMethodField = commodoreImpl.getDeclaredField("GET_COMMAND_DISPATCHER_METHOD");
      getCommandDispatcherMethodField.setAccessible(true);
      Method getCommandDispatcher = (Method) getCommandDispatcherMethodField.get(null);
      getCommandDispatcher.setAccessible(true);
      GET_COMMAND_DISPATCHER_CALLSITE.setTarget(MethodHandles.lookup().unreflect(getCommandDispatcher).asType(GET_COMMAND_DISPATCHER_CALLSITE.type()));

      Method performCommand = CLASS_COMMAND_DISPATCHER.getDeclaredMethod("performCommand", CLASS_COMMAND_LISTENER_WRAPPER, String.class, String.class, boolean.class);
      performCommand.setAccessible(true);
      PERFORM_COMMAND_CALLSITE.setTarget(MethodHandles.lookup().unreflect(performCommand).asType(PERFORM_COMMAND_CALLSITE.type()));
    } catch (ReflectiveOperationException exception) {
      throw new ExceptionInInitializerError(exception);
    }
  }

  public static String joinArguments(String[] args, String name) {
    return name + (args.length > 0 ? " " + String.join(" ", args) : "");
  }

  public static CommandExecutor executor() {
    Object dispatcher = dispatcher();
    return (sender, command, label, args) -> {
      try {
        Object wrapper = getListener(sender);
        PERFORM_COMMAND.invoke(dispatcher, wrapper, joinArguments(args, command.getName()), joinArguments(args, label), true);
      } catch (Throwable throwable) {
        throw new CommandException("", throwable);
      }
      return true;
    };
  }

  private static Object dispatcher() {
    try {
      Object dedicatedServer = GET_CONSOLE_FIELD.invoke(Bukkit.getServer());
      return GET_COMMAND_DISPATCHER.invoke(dedicatedServer);
    } catch (Throwable exception) {
      throw new RuntimeException(exception);
    }
  }

  public static Object getListener(CommandSender sender) {
    try {
      return GET_LISTENER.invoke(sender);
    } catch (Throwable exception) {
      throw new RuntimeException(exception);
    }
  }

  private BukkitToNative() {
  }

}
