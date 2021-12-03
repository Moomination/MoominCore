package com.github.moomination.moomincore.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class Configs {

  private static SpawnConfig spawnConfig;
  private static WaypointsConfig waypointsConfig;

  public static SpawnConfig spawnConfig() {
    return spawnConfig;
  }

  public static WaypointsConfig waypointsConfig() {
    return waypointsConfig;
  }

  public static void load(Plugin plugin) throws IOException {
    File dataDir = plugin.getDataFolder();
    spawnConfig = SpawnConfig.deserialize(load(dataDir, "spawn"));
    waypointsConfig = WaypointsConfig.deserialize(load(dataDir, "waypoints"));
  }

  public static void save(Plugin plugin) throws IOException {
    File dataDir = plugin.getDataFolder();
    save(dataDir, spawnConfig);
    save(dataDir, waypointsConfig);
  }

  public static void free() {
    spawnConfig = null;
    waypointsConfig = null;
  }

  private static YamlConfiguration load(File dataDirectory, String name) throws IOException {
    Path configFile = dataDirectory.toPath().resolve(name + ".yml");
    if (Files.notExists(configFile)) {
      return new YamlConfiguration();
    }

    try (BufferedReader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
      return YamlConfiguration.loadConfiguration(reader);
    } catch (NoSuchFileException ignored) {
      return new YamlConfiguration();
    }
  }

  private static void save(File dataDirectory, YamlSerializable config) throws IOException {
    YamlConfiguration configuration = new YamlConfiguration();
    config.serialize(configuration);
    Files.writeString(new File(dataDirectory, config.id() + ".yml").toPath(),
      configuration.saveToString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING);
  }

  private Configs() {
  }

}
