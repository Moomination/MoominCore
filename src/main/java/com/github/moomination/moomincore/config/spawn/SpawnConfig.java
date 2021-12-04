package com.github.moomination.moomincore.config.spawn;

import com.github.moomination.moomincore.config.YamlSerializable;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SpawnConfig implements YamlSerializable {

  @SuppressWarnings("unchecked")
  public static SpawnConfig deserialize(Map<String, ?> data) {
    Map<String, Object> spawnMap = (Map<String, Object>) data.get("spawn");
    Spawn spawn = spawnMap == null ? null : Spawn.deserialize(spawnMap);
    return new SpawnConfig(spawn);
  }

  public @Nullable Spawn spawn;
  public int fee;

  public SpawnConfig() {
  }

  @Override
  public String id() {
    return "spawn";
  }

  public SpawnConfig(Spawn spawn) {
    this.spawn = spawn;
  }

  public Map<String, Object> serialize() {
    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    if (spawn != null) builder.put("spawn", spawn.serialize());
    builder.put("fee", fee);
    return builder.build();
  }

}
