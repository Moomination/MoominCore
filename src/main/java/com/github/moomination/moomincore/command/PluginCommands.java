package com.github.moomination.moomincore.command;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class PluginCommands {

  private static final MethodHandle GET_COMMAND_MAP;
  private static final MethodHandle CONSTRUCTOR_PLUGIN_COMMAND;

  static {
    try {
      Field field = SimplePluginManager.class.getDeclaredField("commandMap");
      field.setAccessible(true);
      GET_COMMAND_MAP = MethodHandles.lookup().unreflectGetter(field);

      Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
      constructor.setAccessible(true);
      CONSTRUCTOR_PLUGIN_COMMAND = MethodHandles.lookup().unreflectConstructor(constructor);
    } catch (ReflectiveOperationException exception) {
      throw new ExceptionInInitializerError(exception);
    }
  }

  public static PluginCommandBuilder builder() {
    return new SimplePluginCommandBuilder();
  }

  public static PluginCommand register(String prefix, Plugin plugin, String name, String description, List<String> aliases, String permission) {
    Objects.requireNonNull(plugin);
    CommandMap commandMap = getCommandMap();
    Objects.requireNonNull(name);
    Validate.isTrue(!name.contains(":"));
    PluginCommand command = newPluginCommand(name, plugin);
    command.setPermission(permission);
    command.setDescription(description);
    command.setAliases(aliases);
    commandMap.register(prefix, command);
    return command;
  }

  private static CommandMap getCommandMap() {
    PluginManager pluginManager = Bukkit.getPluginManager();
    if (!(pluginManager instanceof SimplePluginManager)) {
      throw new IllegalStateException("PluginManager instance is not SimplePluginManager");
    }
    try {
      return (CommandMap) GET_COMMAND_MAP.invoke(pluginManager);
    } catch (Throwable exception) {
      throw new RuntimeException(exception);
    }
  }

  private static PluginCommand newPluginCommand(String name, Plugin plugin) {
    try {
      return (PluginCommand) CONSTRUCTOR_PLUGIN_COMMAND.invoke(name, plugin);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  private PluginCommands() {
  }

  public interface PluginCommandBuilder {

    PluginCommandBuilder name(String name);

    default PluginCommandBuilder aliases(String... aliases) {
      return aliases(Arrays.asList(Objects.requireNonNull(aliases)));
    }

    PluginCommandBuilder aliases(List<String> aliases);

    PluginCommandBuilder description(String description);

    PluginCommandBuilder permission(String permission);

    PluginCommand build(String prefix, Plugin plugin);

  }

  private static final class SimplePluginCommandBuilder implements PluginCommandBuilder {

    private String name;
    private List<String> aliases = Collections.emptyList();
    private String description;
    private String permission;

    @Override
    public PluginCommandBuilder name(String name) {
      this.name = Objects.requireNonNull(name);
      return this;
    }

    @Override
    public PluginCommandBuilder aliases(List<String> aliases) {
      this.aliases = Objects.requireNonNull(aliases);
      return this;
    }

    @Override
    public PluginCommandBuilder description(String description) {
      this.description = description;
      return this;
    }

    @Override
    public PluginCommandBuilder permission(String permission) {
      this.permission = permission;
      return this;
    }

    public PluginCommand build(String prefix, Plugin plugin) {
      return register(prefix, plugin, name, description, aliases, permission);
    }

  }

}
