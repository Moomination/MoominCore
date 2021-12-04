package com.github.moomination.moomincore.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.commodore.Commodore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class Commands {

  public static <S> LiteralArgumentBuilder<S> literal(String literal) {
    return LiteralArgumentBuilder.literal(literal);
  }

  public static <S, T> RequiredArgumentBuilder<S, T> argument(final String name, final ArgumentType<T> type) {
    return RequiredArgumentBuilder.argument(name, type);
  }

  public static Player playerOrException(CommandSender sender) throws CommandSyntaxException {
    if (sender instanceof Player player) {
      return player;
    }
    throw new SimpleCommandExceptionType(() -> "You are not a player!").create();
  }

  public static <S> LiteralCommandNode<S> register(Commodore commodore, Command command, LiteralArgumentBuilder<S> argumentBuilder) {
    LiteralCommandNode<S> node = argumentBuilder.build();
    commodore.register(command, node);
    BukkitToNative.setCustomSuggestionsProvider(node, null);
    return node;
  }

  private Commands() {
  }

}
