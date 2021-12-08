package com.github.moomination.moomincore.internal.commander;

import org.bukkit.plugin.Plugin;

public final class Commander {

  public static Commander commander(Plugin plugin) {
    return new Commander(plugin);
  }

  private final Plugin plugin;

  private Commander(Plugin plugin) {
    this.plugin = plugin;
  }

}
