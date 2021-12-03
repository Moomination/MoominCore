package com.github.moomination.moomincore.commands;

import com.github.moomination.moomincore.MoominCore;
import com.github.moomination.moomincore.command.Commands;
import com.github.moomination.moomincore.command.PluginCommands;
import com.github.moomination.moomincore.config.Configs;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.lucko.commodore.Commodore;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class MoominCommand {

  public static void register(Commodore commodore, Plugin plugin) {
    commodore.register(
      PluginCommands.builder()
        .name("moomin")
        .description("Adjusts MoominCore")
        .permission("moomination.command.moomin")
        .build("", plugin),
      Commands.literal("moomin")
        .then(Commands.literal("config")
          .then(Commands.literal("flush")
            .executes(ctx -> flush())
          )
        )
    );
  }

  private static int flush() throws CommandSyntaxException {
    try {
      long start = System.currentTimeMillis();
      Configs.save(MoominCore.getInstance());
      long end = System.currentTimeMillis();
      return (int) (end - start);
    } catch (IOException exception) {
      throw new SimpleCommandExceptionType(() -> ExceptionUtils.getStackTrace(exception)).create();
    }
  }

}
