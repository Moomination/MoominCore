package com.github.moomination.moomincore;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

public final class Phrase {

  public static ComponentLike player(Player player, TextComponent.Builder builder) {
    return builder.clickEvent(ClickEvent.suggestCommand("/tell " + player.getName()))
      .hoverEvent(HoverEvent.showEntity(Key.key("player"), player.getUniqueId(), player.teamDisplayName()));
  }

  private Phrase() {
  }

}
