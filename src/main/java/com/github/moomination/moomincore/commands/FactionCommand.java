package com.github.moomination.moomincore.commands;

import com.github.moomination.moomincore.command.Commands;
import com.github.moomination.moomincore.command.PermissionTest;
import com.github.moomination.moomincore.command.PluginCommands;
import com.github.moomination.moomincore.config.Configs;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.lucko.commodore.Commodore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FactionCommand {

  public static void register(Commodore commodore, Plugin plugin) {
    Commands.register(
      commodore,
      PluginCommands.builder()
        .name("faction")
        .aliases("f", "fac")
        .description("(not) Faction")
        .build(plugin),
      Commands.literal("faction")
        .then(Commands.literal("join")
          .then(Commands.argument("name", StringArgumentType.string())
            .executes(PermissionTest.test(commodore, "moomincore.command.faction.join",
              ctx -> join(Commands.playerOrException(commodore.getBukkitSender(ctx.getSource())), StringArgumentType.getString(ctx, "name"))
            ))
          )
        )
        .then(Commands.literal("leave")
          .executes(PermissionTest.test(commodore, "moomincore.command.faction.leave",
            ctx -> leave(Commands.playerOrException(commodore.getBukkitSender(ctx.getSource())))
          ))
        )
        .then(Commands.literal("members")
          .then(
            Commands.argument("name", StringArgumentType.string())
              .executes(PermissionTest.test(commodore, "moomincore.command.faction.list",
                ctx -> list(commodore.getBukkitSender(ctx.getSource()), StringArgumentType.getString(ctx, "name"))
              ))
          )
        )
    );
  }

  private static int join(Player sender, String faction) throws CommandSyntaxException {
    if (faction.length() > 16) {
      throw new SimpleCommandExceptionType(() -> "Faction names cannot be longer than 16 characters").create();
    }
    String old = Configs.prefixConfig().prefixes.put(sender.getUniqueId().toString().replace("-", ""), faction);
    if (old != null) {
      sender.sendMessage(ChatColor.GRAY + old + " → " + faction);
    } else {
      sender.sendMessage(ChatColor.GRAY + "Joined " + faction);
    }
    return 1;
  }

  private static int leave(Player sender) throws CommandSyntaxException {
    if (Configs.prefixConfig().prefixes.remove(sender.getUniqueId().toString().replace("-", "")) != null) {
      sender.sendMessage(ChatColor.GRAY + "You are no longer in the faction");
    } else {
      sender.sendMessage(ChatColor.GRAY + "Nothing to do");
    }
    return 1;
  }

  private static int list(CommandSender sender, String faction) throws CommandSyntaxException {
    sender.sendMessage("List of '" + faction + "'");
    @NotNull OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
    Map<String, OfflinePlayer> uuidToOffline = new HashMap<>(offlinePlayers.length);
    for (OfflinePlayer offlinePlayer : offlinePlayers) {
      uuidToOffline.put(offlinePlayer.getUniqueId().toString().replace("-", ""), offlinePlayer);
    }

    return (int) Configs.prefixConfig().prefixes.entrySet().stream()
      .filter(entry -> entry.getValue().equals(faction))
      .map(Map.Entry::getKey)
      .peek(uuid -> {
        OfflinePlayer player = uuidToOffline.get(uuid);
        if (player == null) return;
        String name = player.getName();
        sender.sendMessage("- " + (name == null ? uuid : name));
      })
      .count();
  }

}
