package com.github.moomination.moomincore.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class NamedArgumentType<T> implements ArgumentType<T> {

  private static final NamedArgumentType<World> WORLD = new NamedArgumentType<>(World::getName, Bukkit::getWorld, () -> Bukkit.getWorlds().stream());
  private static final NamedArgumentType<Player> PLAYER = new NamedArgumentType<>(Player::getName, Bukkit::getPlayer, () -> Bukkit.getOnlinePlayers().stream());

  public static NamedArgumentType<World> world() {
    return WORLD;
  }

  public static NamedArgumentType<Player> player() {
    return PLAYER;
  }

  private final Function<? super T, String> objToName;
  private final Function<String, ? extends T> nameToObj;
  private final Supplier<Stream<? extends T>> all;

  public NamedArgumentType(Function<T, String> objToName, Function<String, T> nameToObj, Supplier<Stream<? extends T>> all) {
    this.objToName = objToName;
    this.nameToObj = nameToObj;
    this.all = all;
  }

  @Override
  public T parse(StringReader reader) throws CommandSyntaxException {
    String name = reader.readQuotedString();
    T world = nameToObj.apply(name);
    if (world == null) {
      throw new SimpleCommandExceptionType(() -> "World " + name + " is not found!").create();
    }
    return world;
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return CompletableFuture.supplyAsync(() -> {
      all.get().map(objToName).forEach(builder::suggest);
      return builder.build();
    });
  }

  @Override
  public Collection<String> getExamples() {
    return List.of("world");
  }

}
