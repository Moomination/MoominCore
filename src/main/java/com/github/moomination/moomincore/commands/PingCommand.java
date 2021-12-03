package com.github.moomination.moomincore.commands;

import com.github.moomination.moomincore.command.ArgumentTypes;
import com.github.moomination.moomincore.command.Commands;
import com.github.moomination.moomincore.command.PermissionTest;
import com.github.moomination.moomincore.command.PluginCommands;
import me.lucko.commodore.Commodore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PingCommand {

  private static final int GOOD = 40;
  private static final int AVERAGE = 90;
  private static final int POOR = 160;

  public static void register(Commodore commodore, Plugin plugin) {
    commodore.register(
      PluginCommands.builder()
        .name("ping")
        .description("Displays your network latency")
        .permission("moomination.command.ping")
        .build("", plugin),
      Commands.literal("ping")
        .requires(PermissionTest.test(commodore, "moomination.command.ping"))
        .executes(ctx -> ping(commodore.getBukkitSender(ctx.getSource()), Commands.playerOrException(commodore.getBukkitSender(ctx.getSource()))))
        .then(Commands.argument("player", ArgumentTypes.player())
          .requires(PermissionTest.test(commodore, "moomination.command.ping.other"))
          .executes(ctx -> ping(commodore.getBukkitSender(ctx.getSource()), ArgumentTypes.player(ctx, "player")))
        )
    );
  }

  private static int ping(CommandSender sender, Player player) {
    int ping = player.getPing();
    sender.sendMessage(ChatColor.YELLOW + player.getName() + "'s ping: " + colorize(ping) + ping);
    return ping;
  }

  private static ChatColor colorize(long ping) {
    if (ping <= GOOD)
      return ChatColor.GREEN;
    if (ping <= AVERAGE)
      return ChatColor.YELLOW;
    if (ping <= POOR)
      return ChatColor.GOLD;
    return ChatColor.RED;
  }

}
