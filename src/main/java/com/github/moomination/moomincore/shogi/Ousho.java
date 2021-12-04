package com.github.moomination.moomincore.shogi;

import org.bukkit.ChatColor;

public class Ousho extends UnitBase {

  private final String displayName;

  public Ousho(String displayName) {
    this.displayName = displayName;
  }

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
    return ChatColor.GOLD + displayName;
  }

  @Override
  public boolean canMoveTo(int x, int z) {
    return super.canMoveTo(x, z) && Math.abs(this.x - x) <= 1 && Math.abs(this.z - z) <= 1;
  }

}
