package com.github.moomination.moomincore.command;

import com.github.moomination.moomincore.MoominCore;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CoordinateArgumentType implements ArgumentType<Coordinate> {

  private static final CoordinateArgumentType INSTANCE = new CoordinateArgumentType();

  public static CoordinateArgumentType coordinate() {
    return INSTANCE;
  }

  @Override
  public Coordinate parse(StringReader reader) throws CommandSyntaxException {
    char c = reader.peek();
    boolean hasPrefix = c != '^' && c != '~';

    if (hasPrefix) reader.expect(c);
    int x = reader.readInt();
    reader.skipWhitespace();
    if (hasPrefix) reader.expect(c);
    int y = reader.readInt();
    reader.skipWhitespace();
    if (hasPrefix) reader.expect(c);
    int z = reader.readInt();

    return switch (c) {
      case '~' -> Coordinate.offsetted(base -> Vec3I.from(base.add(x, y, z)));
      case '^' -> Coordinate.offsetted(base -> Vec3I.from(base.add(new Vector(x, y, z).rotateAroundX(base.getPitch()).rotateAroundY(base.getYaw()))));
      default -> Coordinate.immediate(() -> new Vec3I(x, y, z));
    };
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return CompletableFuture.supplyAsync(() -> {
      builder.suggest("~ ~ ~").suggest("^ ^ ^");
      S source = context.getSource();
      if (MoominCore.commodore().getBukkitSender(source) instanceof Player player) {
        Location location = player.getLocation();
        builder.suggest("%d %d %d".formatted(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
      }
      return builder.build();
    });
  }

  @Override
  public Collection<String> getExamples() {
    return List.of("~ ~ ~");
  }

}
