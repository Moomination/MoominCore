package com.github.moomination.moomincore.shogi;

public class Kyousha extends UnitBase {

  @Override
  public boolean isNarigoma() {
    return false;
  }

  @Override
  public Unit naru() {
    return null;
  }

  @Override
  public String displayName() {
    return null;
  }

  @Override
  public boolean canMoveTo(int x, int y) {
    return this.x == x;
  }

}
