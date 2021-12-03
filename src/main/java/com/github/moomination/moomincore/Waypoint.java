package com.github.moomination.moomincore;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

public record Waypoint(String world, int x, int y, int z, String name, String playerName)
  implements ConfigurationSerializable {

  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("world", world);
    map.put("x", x);
    map.put("y", y);
    map.put("z", z);
    map.put("name", name);
    map.put("player", playerName);
    return map;
  }

  public static Waypoint deserialize(Map<String, Object> data) {
    String world = (String) data.get("world");
    int x = (Integer) data.get("x");
    int y = (Integer) data.get("y");
    int z = (Integer) data.get("z");
    String name = data.get("name").toString();
    String playerName = data.get("player").toString();
    return new Waypoint(world, x, y, z, name, playerName);
  }

}
