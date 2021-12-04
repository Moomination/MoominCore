package com.github.moomination.moomincore;

import com.github.moomination.moomincore.commands.*;
import com.github.moomination.moomincore.config.Configs;
import com.github.moomination.moomincore.listeners.BossDamageListener;
import com.github.moomination.moomincore.listeners.ChatListener;
import com.github.moomination.moomincore.listeners.PlayerListener;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.Instant;

public final class MoominCore extends JavaPlugin {

  private static MoominCore instance;
  private static Commodore commodore;

  public static Commodore commodore() {
    return commodore;
  }

  @Override
  public void onEnable() {
    Instant start = Instant.now();

    instance = this;

    getLogger().info("Initializing configurations...");
    saveDefaultConfig();
    try {
      Configs.load(this);
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }

    getLogger().info("Initializing permissions...");
    PluginManager pluginManager = getServer().getPluginManager();

    getLogger().info("Initializing commands...");
    commodore = CommodoreProvider.getCommodore(this);
    FactionCommand.register(commodore, this);
    MemoryCommand.register(commodore, this);
    MoominCommand.register(commodore, this);
    PingCommand.register(commodore, this);
    ShogiCommand.register(commodore, this);
    SpawnCommand.register(commodore, this);
    WaypointCommand.register(commodore, this);

    getLogger().info("Initializing events...");
    pluginManager.registerEvents(new ChatListener(), this);
    pluginManager.registerEvents(new PlayerListener(), this);
    pluginManager.registerEvents(new BossDamageListener(), this);

    Instant end = Instant.now();

    getLogger().info("All done. took " + Duration.between(start, end).toMillis() + " millis.");
  }

  @Override
  public void onDisable() {
    instance = null;
    try {
      Configs.save(this);
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
    Configs.free();
  }


  public static MoominCore getInstance() {
    return instance;
  }

}
