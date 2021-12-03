package com.github.moomination.moomincore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

public record Spawn(String world, int x, int y, int z)
  implements ConfigurationSerializable {

  public static Spawn from(Location location) {
    return new Spawn(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
  }

  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("world", world);
    map.put("x", x);
    map.put("y", y);
    map.put("z", z);
    return map;
  }

  public static Spawn deserialize(Map<String, Object> data) {
    String world = (String) data.get("world");
    int x = (Integer) data.get("x");
    int y = (Integer) data.get("y");
    int z = (Integer) data.get("z");
    return new Spawn(world, x, y, z);
  }

  public Location toLocation() {
    return new Location(Bukkit.getWorld(world), x, y, z);
  }

}
