package com.github.moomination.moomincore.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class DumpFormatArgumentType implements ArgumentType<DumpFormatArgumentType.DumpFormat> {

  @Override
  public DumpFormat parse(StringReader reader) throws CommandSyntaxException {
    return null;
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return builder.suggest("json").suggest("lunar").suggest("fyu").buildFuture();
  }

  @Override
  public Collection<String> getExamples() {
    return ArgumentType.super.getExamples();
  }

  public enum DumpFormat {
    JSON,
    LUNAR,
    FYU,
  }


}
