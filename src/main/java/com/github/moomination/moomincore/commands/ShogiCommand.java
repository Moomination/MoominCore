package com.github.moomination.moomincore.commands;

import com.github.moomination.moomincore.command.ArgumentTypes;
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
        .build(plugin),
      Commands.literal("shogi").executes(
        ctx -> shogi(commodore.getBukkitSender(ctx.getSource()))
      ).then(
        Commands.argument("player", ArgumentTypes.player()).executes(
          ctx -> shogi(ArgumentTypes.player(ctx, "player"))
        )
      )
    );
  }

  public static int shogi(CommandSender sender) {
    displayShogiban(sender);
    return 0;
  }

  public static void displayShogiban(CommandSender sender) {
    sender.sendMessage(
      Component.text()
        .append(Component.text("・１２３４５６７８９".replace('　', '＿'))).append(Component.newline())
        .append(Component.text("１香桂銀金王金銀桂香".replace('　', '＿'))).append(Component.newline())
        .append(Component.text("２　飛　　　　　角　".replace('　', '＿'))).append(Component.newline())
        .append(Component.text("３歩歩歩歩歩歩歩歩歩".replace('　', '＿'))).append(Component.newline())
        .append(Component.text("４　　　　　　　　　".replace('　', '＿'))).append(Component.newline())
        .append(Component.text("５　　　　　　　　　".replace('　', '＿'))).append(Component.newline())
        .append(Component.text("６　　　　　　　　　".replace('　', '＿'))).append(Component.newline())
        .append(Component.text("７歩歩歩歩歩歩歩歩歩".replace('　', '＿'))).append(Component.newline())
        .append(Component.text("８　角　　　　　飛　".replace('　', '＿'))).append(Component.newline())
        .append(Component.text("９香桂銀金王金銀桂香".replace('　', '＿')))
    );
  }

}
