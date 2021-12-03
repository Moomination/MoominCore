package com.github.moomination.moomincore.config;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

public class SpawnConfig implements YamlSerializable {

  public static SpawnConfig deserialize(YamlConfiguration yaml) {
    Location spawn = (Location) yaml.get("spawn");
    return new SpawnConfig(spawn);
  }

  public @Nullable Location spawn;

  public SpawnConfig() {
  }

  @Override
  public String id() {
    return "spawn";
  }

  public SpawnConfig(Location spawn) {
    this.spawn = spawn;
  }

  public void serialize(YamlConfiguration yaml) {
    yaml.set("spawn", spawn);
  }

}
