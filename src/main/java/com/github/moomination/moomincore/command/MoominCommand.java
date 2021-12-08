package com.github.moomination.moomincore.command;

import com.github.moomination.moomincore.AdvancementTracker;
import com.github.moomination.moomincore.MoominCore;
import com.github.moomination.moomincore.config.Configs;
import com.github.moomination.moomincore.internal.commander.ArgumentTypes;
import com.github.moomination.moomincore.internal.commander.Commands;
import com.github.moomination.moomincore.internal.commander.PermissionTest;
import com.github.moomination.moomincore.internal.commander.PluginCommands;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.lucko.commodore.Commodore;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
              ctx -> saveConfig(commodore.getBukkitSender(ctx.getSource()))
            ))
          )
          .then(Commands.literal("forcereload")
            .executes(PermissionTest.test(commodore, "moomincore.command.moomin",
              ctx -> loadConfig(commodore.getBukkitSender(ctx.getSource()))
            ))
          )
        )
        .then(Commands.literal("unload")
          .then(Commands.argument("plugin", StringArgumentType.string())
            .executes(PermissionTest.test(commodore, "moomincore.command.moomin",
              ctx -> unloadPlugin(commodore.getBukkitSender(ctx.getSource()), StringArgumentType.getString(ctx, "plugin"))
            ))
          )
        )
        .then(Commands.literal("reload")
          .then(Commands.argument("plugin", StringArgumentType.string())
            .executes(PermissionTest.test(commodore, "moomincore.command.moomin",
              ctx -> reloadPlugin(commodore.getBukkitSender(ctx.getSource()), StringArgumentType.getString(ctx, "plugin"))
            ))
          )
        )
        .then(Commands.literal("advancement")
          .then(Commands.literal("count")
            .then(Commands.argument("player", ArgumentTypes.player())
              .executes(PermissionTest.test(commodore, "moomincore.command.moomin",
                ctx -> countAdvancements(commodore.getBukkitSender(ctx.getSource()), ArgumentTypes.player(ctx, "player"))
              ))
            )
          )
          .then(Commands.literal("reload")
            .executes(PermissionTest.test(commodore, "moomincore.command.moomin",
              ctx -> reloadAdvancements(commodore.getBukkitSender(ctx.getSource()))
            ))
          )
        )
        .then(Commands.literal("pvp")
          .then(Commands.literal("on")
            .executes(PermissionTest.test(commodore, "moomincore.command.moomin",
              ctx -> pvp(commodore.getBukkitSender(ctx.getSource()), true)
            ))
          )
          .then(Commands.literal("off")
            .executes(PermissionTest.test(commodore, "moomincore.command.moomin",
              ctx -> pvp(commodore.getBukkitSender(ctx.getSource()), false)
            ))
          )
        )
    );
  }

  private static int saveConfig(CommandSender sender) throws CommandSyntaxException {
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

  private static int loadConfig(CommandSender sender) throws CommandSyntaxException {
    try {
      long start = System.currentTimeMillis();
      Configs.load(MoominCore.getInstance());
      long end = System.currentTimeMillis();
      sender.sendMessage(ChatColor.GREEN + "Took " + (end - start) + " millis");
      return (int) (end - start);
    } catch (IOException exception) {
      throw new SimpleCommandExceptionType(() -> ExceptionUtils.getStackTrace(exception)).create();
    }
  }

  private static int unloadPlugin(CommandSender sender, String pluginName) throws CommandSyntaxException {
    PluginManager pluginManager = Bukkit.getPluginManager();
    Plugin plugin = pluginManager.getPlugin(pluginName);
    if (plugin != null) {
      pluginManager.disablePlugin(plugin);
      sender.sendMessage(ChatColor.GREEN + "Plugin '%s' is disabled".formatted(plugin.getName()));
      return 1;
    }
    throw new SimpleCommandExceptionType(() -> "Plugin '" + pluginName + "' is not installed!").create();
  }

  private static int reloadPlugin(CommandSender sender, String pluginName) throws CommandSyntaxException {
    PluginManager pluginManager = Bukkit.getPluginManager();
    Plugin plugin = pluginManager.getPlugin(pluginName);
    if (plugin != null) {
      pluginManager.disablePlugin(plugin);
      pluginManager.enablePlugin(plugin);
      sender.sendMessage(ChatColor.GREEN + "Plugin '%s' is reloaded!".formatted(plugin.getName()));
      return 1;
    }
    throw new SimpleCommandExceptionType(() -> "Plugin '" + pluginName + "' is not installed!").create();
  }

  private static int countAdvancements(CommandSender sender, Player player) throws CommandSyntaxException {
    int count = AdvancementTracker.getCount(player);
    sender.sendMessage(AdvancementTracker.colorize(player, player.displayName(), AdvancementTracker.getCount(player))
      .append(Component.text("'s Advancements: " + count))
    );
    return count;
  }

  private static int reloadAdvancements(CommandSender sender) throws CommandSyntaxException {
    long start = System.currentTimeMillis();
    AdvancementTracker.reload();
    long end = System.currentTimeMillis();
    sender.sendMessage(ChatColor.GREEN + "Took " + (end - start) + " millis");
    return (int) (end - start);
  }


  private static int pvp(CommandSender sender, boolean enabled) throws CommandSyntaxException {
    Bukkit.getWorlds().forEach(world -> world.setPVP(enabled));
    Bukkit.broadcast(Component.text(ChatColor.YELLOW + "PvP has been " + (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled")));
    return enabled ? 1 : 0;
  }

}
