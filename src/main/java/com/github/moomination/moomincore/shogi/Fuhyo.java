package com.github.moomination.moomincore.shogi;

import org.bukkit.ChatColor;

public class Fuhyo extends UnitBase {

  @Override
  public boolean isNarigoma() {
    return false;
  }

  @Override
  public Unit naru() {
    return new Narikin(ChatColor.RED + "と");
  }

  @Override
  public String displayName() {
    return "歩";
  }

  @Override
  public boolean canMoveTo(int x, int z) {
    // XOX
    // XTX
    // XXX

    // forward only
    return this.x == x && (z - this.z) == 1;
  }

}
