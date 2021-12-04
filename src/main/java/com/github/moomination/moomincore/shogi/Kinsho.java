package com.github.moomination.moomincore.shogi;

public class Kinsho extends UnitBase {

  @Override
  public boolean isNarigoma() {
    return true;
  }

  @Override
  public Unit naru() {
    return this;
  }

  @Override
  public String displayName() {
    return "金";
  }

  @Override
  public boolean canMoveTo(int x, int z) {
    if (!super.canMoveTo(x, z)) {
      return false;
    }

    // OOO
    // OTO
    // XOX
    // backward
    if (z - this.z == -1) {
      return this.x == x;
    }
    return this.x - 1 <= x && x <= this.x + 1;
  }

}
