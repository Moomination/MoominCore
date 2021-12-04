package com.github.moomination.moomincore.commands;

import com.github.moomination.moomincore.MoominCore;
import com.github.moomination.moomincore.command.Commands;
import com.github.moomination.moomincore.command.PermissionTest;
import com.github.moomination.moomincore.command.PluginCommands;
import com.github.moomination.moomincore.config.Configs;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.lucko.commodore.Commodore;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.IOException;

public class MoominCommand {

  public static void register(Commodore commodore, Plugin plugin) {
    Commands.register(
      commodore,
      PluginCommands.builder()
        .name("moomin")
        .description("Adjusts MoominCore")
        .build(plugin),
      Commands.literal("moomin")
        .then(Commands.literal("config")
          .then(Commands.literal("flush")
            .executes(PermissionTest.test(commodore, "moomincore.command.moomin",
              ctx -> flush(commodore.getBukkitSender(ctx.getSource()))
            ))
          )
        )
        .then(Commands.literal("unload")
          .then(Commands.argument("plugin", StringArgumentType.string())
            .executes(PermissionTest.test(commodore, "moomincore.command.moomin",
              ctx -> unload(commodore.getBukkitSender(ctx.getSource()), StringArgumentType.getString(ctx, "plugin"))
            ))
          )
        )
    );
  }

  private static int flush(CommandSender sender) throws CommandSyntaxException {
    try {
      long start = System.currentTimeMillis();
      Configs.save(MoominCore.getInstance());
      long end = System.currentTimeMillis();
      sender.sendMessage(ChatColor.GREEN + "Took " + (end - start) + " millis");
      return (int) (end - start);
    } catch (IOException exception) {
      throw new SimpleCommandExceptionType(() -> ExceptionUtils.getStackTrace(exception)).create();
    }
  }

  private static int unload(CommandSender sender, String pluginName) throws CommandSyntaxException {
    PluginManager pluginManager = Bukkit.getPluginManager();
    Plugin plugin = pluginManager.getPlugin(pluginName);
    if (plugin != null) {
      pluginManager.disablePlugin(plugin);
      sender.sendMessage(ChatColor.GREEN + "Plugin '%s' is disabled".formatted(plugin.getName()));
      return 1;
    }
    throw new SimpleCommandExceptionType(() -> "Plugin '" + pluginName + "' is not installed!").create();
  }

}
