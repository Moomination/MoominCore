package com.github.moomination.moomincore.commands;

import com.github.moomination.moomincore.command.Commands;
import com.github.moomination.moomincore.command.PluginCommands;
import me.lucko.commodore.Commodore;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ShogiCommand {

  public static void register(Commodore commodore, Plugin plugin) {
    Commands.register(
      commodore,
      PluginCommands.builder()
        .name("shogi")
        .description("Shogi!")
        .permission("moomincore.command.shogi")
        .build(plugin),
      Commands.literal("shogi").executes(ctx -> shogi(commodore.getBukkitSender(ctx.getSource())))
    );
  }

  public static int shogi(CommandSender sender) {
    displayShogiban(sender);
    return 0;
  }

  public static void displayShogiban(CommandSender sender) {
    Component.text()
      .append(Component.text("・１２３４５６７８９")).append(Component.newline())
      .append(Component.text("１香桂銀金王金銀桂香")).append(Component.newline())
      .append(Component.text("２　飛　　　　　角　")).append(Component.newline())
      .append(Component.text("３歩歩歩歩歩歩歩歩歩")).append(Component.newline())
      .append(Component.text("４　　　　　　　　　")).append(Component.newline())
      .append(Component.text("５　　　　　　　　　")).append(Component.newline())
      .append(Component.text("６　　　　　　　　　")).append(Component.newline())
      .append(Component.text("７歩歩歩歩歩歩歩歩歩")).append(Component.newline())
      .append(Component.text("８　角　　　　　飛　")).append(Component.newline())
      .append(Component.text("９香桂銀金王金銀桂香"));
  }

}
