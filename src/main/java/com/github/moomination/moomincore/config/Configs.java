package com.github.moomination.moomincore.config;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map;

public final class Configs {

  private static final Yaml YAML = new Yaml();

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

  @SuppressWarnings("unchecked")
  private static Map<String, ?> load(File dataDirectory, String name) throws IOException {
    Path configFile = dataDirectory.toPath().resolve(name + ".yml");
    if (Files.notExists(configFile)) {
      return Collections.emptyMap();
    }

    try (BufferedReader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
      Map<String, ?> map = (Map<String, ?>) YAML.loadAs(reader, Map.class);
      return map == null ? Collections.emptyMap() : map;
    } catch (NoSuchFileException ignored) {
      return Collections.emptyMap();
    }
  }

  private static void save(File dataDirectory, YamlSerializable config) throws IOException {
    try (Writer writer = Files.newBufferedWriter(new File(dataDirectory, config.id() + ".yml").toPath(),
      StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
      YAML.dump(config.serialize(), writer);
    }
  }

  private Configs() {
  }

}
