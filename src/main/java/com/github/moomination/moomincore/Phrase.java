package com.github.moomination.moomincore;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

public final class Phrase {

  public static <C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>> B player(Player player, B builder) {
    return builder
      .hoverEvent(HoverEvent.showEntity(Key.key(Key.MINECRAFT_NAMESPACE, "player"), player.getUniqueId(), player.displayName()))
      .clickEvent(ClickEvent.suggestCommand("/tell " + player.getName()));
  }

  public static Component player(Player player, Component component) {
    return component
      .hoverEvent(HoverEvent.showEntity(Key.key(Key.MINECRAFT_NAMESPACE, "player"), player.getUniqueId(), player.displayName()))
      .clickEvent(ClickEvent.suggestCommand("/tell " + player.getName()));
  }

  private Phrase() {
  }

}
