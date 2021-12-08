package com.github.moomination.moomincore.internal.commander;

import com.github.moomination.moomincore.MoominCore;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class BukkitToNative {

  private static final Class<?> CLASS_VANILLA_COMMAND_WRAPPER;
  private static final Class<?> CLASS_COMMAND_LISTENER_WRAPPER;
  private static final Class<?> CLASS_COMMAND_DISPATCHER;

  private static final MethodHandle GET_LISTENER;
  private static final MethodHandle GET_CONSOLE_FIELD;
  private static final MethodHandle GET_COMMAND_DISPATCHER;
  private static final MethodHandle PERFORM_COMMAND;
  private static final MethodHandle SET_CUSTOM_SUGGESTIONS_PROVIDER;

  static {
    try {
      CLASS_COMMAND_DISPATCHER = Class.forName("net.minecraft.commands.CommandDispatcher");
      CLASS_COMMAND_LISTENER_WRAPPER = Class.forName("net.minecraft.commands.CommandListenerWrapper");
      CLASS_VANILLA_COMMAND_WRAPPER = Naming.obcClass("command.VanillaCommandWrapper");

      Method getListener = CLASS_VANILLA_COMMAND_WRAPPER.getDeclaredMethod("getListener", CommandSender.class);
      getListener.setAccessible(true);
      GET_LISTENER = MethodHandles.lookup().unreflect(getListener);

      Class<?> commodoreImpl = MoominCore.commodore().getClass();
      Field consoleFieldField = commodoreImpl.getDeclaredField("CONSOLE_FIELD");
      consoleFieldField.setAccessible(true);
      Field console = (Field) consoleFieldField.get(null);
      console.setAccessible(true);
      GET_CONSOLE_FIELD = MethodHandles.lookup().unreflectGetter(console);

      Field getCommandDispatcherMethodField = commodoreImpl.getDeclaredField("GET_COMMAND_DISPATCHER_METHOD");
      getCommandDispatcherMethodField.setAccessible(true);
      Method getCommandDispatcher = (Method) getCommandDispatcherMethodField.get(null);
      getCommandDispatcher.setAccessible(true);
      GET_COMMAND_DISPATCHER = MethodHandles.lookup().unreflect(getCommandDispatcher);

      Method performCommand = CLASS_COMMAND_DISPATCHER.getDeclaredMethod("performCommand", CLASS_COMMAND_LISTENER_WRAPPER, String.class, String.class, boolean.class);
      performCommand.setAccessible(true);
      PERFORM_COMMAND = MethodHandles.lookup().unreflect(performCommand);

      Method setCustomSuggestionProvider = commodoreImpl.getDeclaredMethod("setCustomSuggestionProvider", CommandNode.class, SuggestionProvider.class);
      setCustomSuggestionProvider.setAccessible(true);
      SET_CUSTOM_SUGGESTIONS_PROVIDER = MethodHandles.lookup().unreflect(setCustomSuggestionProvider);
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

  public static void setCustomSuggestionsProvider(CommandNode<?> node, SuggestionProvider provider) {
    try {
      SET_CUSTOM_SUGGESTIONS_PROVIDER.invoke(node, provider);
    } catch (Throwable exception) {
      throw new RuntimeException(exception);
    }
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
