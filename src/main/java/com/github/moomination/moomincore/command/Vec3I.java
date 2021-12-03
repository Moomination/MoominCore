package com.github.moomination.moomincore.command;

import org.bukkit.Location;
import org.bukkit.World;

public record Vec3I(int x, int y, int z) {

  public static Vec3I from(Location location) {
    return new Vec3I(location.getBlockX(), location.getBlockY(), location.getBlockZ());
  }

  public Location toLocation(float yaw, float pitch, World world) {
    return new Location(world, x, y, z, yaw, pitch);
  }

}
