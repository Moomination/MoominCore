package com.github.moomination.moomincore.commands;

import com.github.moomination.moomincore.command.*;
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
        .requires(PermissionTest.test("moomination.command.ping"))
        .executes(ctx -> ping(ctx.getSource(), Commands.playerOrException(ctx.getSource().sender())))
        .then(Commands.argument("player", NamedArgumentType.player())
          .requires(PermissionTest.test("moomination.command.ping.other"))
          .executes(ctx -> ping(ctx.getSource(), ctx.getArgument("player", Player.class)))
        )
    );
  }

  private static int ping(CommandSource source, Player player) {
    CommandSender sender = source.sender();
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
