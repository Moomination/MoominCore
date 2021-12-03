package com.github.moomination.moomincore.config;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SpawnConfig implements YamlSerializable {

  public static SpawnConfig deserialize(Map<String, ?> data) {
    Location spawn = (Location) data.get("spawn");
    return new SpawnConfig(spawn);
  }

  public @Nullable Location spawn;
  public int fee;

  public SpawnConfig() {
  }

  @Override
  public String id() {
    return "spawn";
  }

  public SpawnConfig(Location spawn) {
    this.spawn = spawn;
  }

  public Map<String, Object> serialize() {
    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    if (spawn != null) builder.put("spawn", spawn);
    builder.put("fee", fee);
    return builder.build();
  }

}
