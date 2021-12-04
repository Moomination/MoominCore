package com.github.moomination.moomincore.shogi;

public abstract class UnitBase implements Unit {

  protected int x;
  protected int z;

  @Override
  public int x() {
    return x;
  }

  @Override
  public int z() {
    return z;
  }

  @Override
  public void moveTo(int x, int z) {
    this.x = x;
    this.z = z;
  }

  @Override
  public boolean canMoveTo(int x, int z) {
    // not moved
    return !(this.x == x && this.z == z);
  }

}
