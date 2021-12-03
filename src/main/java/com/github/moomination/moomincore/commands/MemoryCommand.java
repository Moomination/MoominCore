package com.github.moomination.moomincore.commands;

import com.github.moomination.moomincore.SizeUnit;
import com.github.moomination.moomincore.command.Commands;
import com.github.moomination.moomincore.command.PluginCommands;
import me.lucko.commodore.Commodore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.stream.Stream;

public final class MemoryCommand {

  public static void register(Commodore commodore, Plugin plugin) {
    commodore.register(
      PluginCommands.builder()
        .name("memory")
        .description("Shows about current JVM heap and TPS (tick per second).")
        .permission("moomination.command.memory")
        .build("", plugin),
      Commands.literal("memory")
        .executes(ctx -> showMemory(commodore.getBukkitSender(ctx)))
    );
  }

  private static int showMemory(CommandSender sender) {
    Runtime runtime = Runtime.getRuntime();
    double[] tps = Bukkit.getTPS();

    long maxMemory = runtime.maxMemory();
    long totalMemory = runtime.totalMemory();
    long freeMemory = runtime.freeMemory();
    long usedMemory = totalMemory - freeMemory;

    double totalPercentage = (double) totalMemory / maxMemory * 100;
    double usedPercentage = (double) usedMemory / maxMemory * 100;

    Stream.of(
      String.format("%sHeap Limit: %s%s",
        ChatColor.GOLD,
        ChatColor.GREEN,
        SizeUnit.toString(maxMemory)),
      String.format("%sHeap Allocated: %s%s (%.2f%%)",
        ChatColor.GOLD,
        colorizePercentage(totalPercentage),
        SizeUnit.toString(totalMemory), totalPercentage),
      String.format("%sHeap Free: %s%s",
        ChatColor.GOLD,
        colorizePercentageReversed((double) freeMemory / totalMemory * 100),
        SizeUnit.toString(freeMemory)),
      String.format("%sHeap Used: %s%s (%.2f%%)",
        ChatColor.GOLD,
        colorizePercentage(usedPercentage), SizeUnit.toString(usedMemory), usedPercentage),
      String.format("%sTPS (1m, 5m, 15m): %s%.2f, %s%.2f, %s%.2f",
        ChatColor.GOLD,
        colorizeTPS(tps[0]), tps[0], colorizeTPS(tps[1]), tps[1], colorizeTPS(tps[2]), tps[2])
    ).forEach(sender::sendMessage);
    return (int) usedPercentage;
  }

  private static ChatColor colorizePercentage(double percentage) {
    if (Double.compare(percentage, 30) < 0) {
      return ChatColor.GREEN;
    }
    if (Double.compare(percentage, 80) < 0) {
      return ChatColor.YELLOW;
    }
    return ChatColor.RED;
  }

  // TODO: fix long name
  private static ChatColor colorizePercentageReversed(double percentage) {
    ChatColor r = colorizePercentage(percentage);
    return r == ChatColor.GREEN ? ChatColor.RED : r == ChatColor.RED ? ChatColor.GREEN : r;
  }

  private static ChatColor colorizeTPS(double tps) {
    if (Double.compare(tps, 18) > 0) {
      return ChatColor.GREEN;
    }
    if (Double.compare(tps, 15) > 0) {
      return ChatColor.YELLOW;
    }
    return ChatColor.RED;
  }

}
