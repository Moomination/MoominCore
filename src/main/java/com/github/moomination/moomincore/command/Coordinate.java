package com.github.moomination.moomincore.command;

import org.bukkit.Location;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Coordinate {

  static Coordinate offsetted(Function<Location, Vec3I> toVec3I) {
    return new Coordinate() {
      @Override
      public Vec3I toVec3I(Location base) {
        return toVec3I.apply(base);
      }

      @Override
      public boolean offsetted() {
        return true;
      }
    };
  }

  static Coordinate immediate(Supplier<Vec3I> toVec3I) {
    return new Coordinate() {
      @Override
      public Vec3I toVec3I(Location base) {
        return toVec3I.get();
      }

      @Override
      public boolean offsetted() {
        return false;
      }
    };
  }

  Vec3I toVec3I(Location base);

  boolean offsetted();

}
