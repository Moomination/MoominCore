package com.github.moomination.moomincore.config.waypoint;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record Waypoint(String world, int x, int y, int z, String name, String player)
  implements ConfigurationSerializable {

  @Override
  public @NotNull Map<String, Object> serialize() {
    return Map.of(
      "world", world,
      "x", x,
      "y", y,
      "z", z,
      "name", name,
      "player", player
    );
  }

  public static Waypoint deserialize(Map<String, Object> data) {
    String world = (String) data.get("world");
    int x = (int) data.get("x");
    int y = (int) data.get("y");
    int z = (int) data.get("z");
    String name = data.get("name").toString();
    String playerName = data.get("player").toString();
    return new Waypoint(world, x, y, z, name, playerName);
  }

}
