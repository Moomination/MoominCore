package com.github.moomination.moomincore.shogi;

import org.bukkit.ChatColor;

public class Narikin extends Kinsho {

  private final String displayName;

  public Narikin(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String displayName() {
    return ChatColor.RED + displayName;
  }

}
