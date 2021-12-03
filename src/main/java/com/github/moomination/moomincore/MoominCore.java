package com.github.moomination.moomincore;

import com.github.moomination.moomincore.commands.*;
import com.github.moomination.moomincore.config.Configs;
import com.github.moomination.moomincore.listeners.BossDamageListener;
import com.github.moomination.moomincore.listeners.ChatListener;
import com.github.moomination.moomincore.listeners.PlayerDeathListener;
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

    getLogger().info("Initializing commands...");
    commodore = CommodoreProvider.getCommodore(this);
    MemoryCommand.register(commodore, this);
    PingCommand.register(commodore, this);
    SpawnCommand.register(commodore, this);
    WaypointCommand.register(commodore, this);
    MoominCommand.register(commodore, this);

    getLogger().info("Initializing events...");
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new ChatListener(), this);
    pluginManager.registerEvents(new PlayerDeathListener(), this);
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
