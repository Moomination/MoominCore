package com.github.moomination.moomincore.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class Commands {

  public static LiteralArgumentBuilder<CommandSource> literal(String literal) {
    return LiteralArgumentBuilder.literal(literal);
  }

  public static <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
    return RequiredArgumentBuilder.argument(name, type);
  }

  public static Player playerOrException(CommandSender sender) throws CommandSyntaxException {
    if (sender instanceof Player player) {
      return player;
    }
    throw new SimpleCommandExceptionType(() -> "You are not a player!").create();
  }

  private Commands() {
  }

}
