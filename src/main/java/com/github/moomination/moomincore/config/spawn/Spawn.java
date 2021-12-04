package com.github.moomination.moomincore.config.spawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record Spawn(String world, int x, int y, int z)
  implements ConfigurationSerializable {

  public static Spawn from(Location location) {
    return new Spawn(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
  }

  @Override
  public @NotNull Map<String, Object> serialize() {
    return Map.of(
      "world", world,
      "x", x,
      "y", y,
      "z", z
    );
  }

  public static Spawn deserialize(Map<String, Object> data) {
    String world = (String) data.get("world");
    int x = (int) data.get("x");
    int y = (int) data.get("y");
    int z = (int) data.get("z");
    return new Spawn(world, x, y, z);
  }

  public Location toLocation() {
    return new Location(Bukkit.getWorld(world), x, y, z);
  }

}
