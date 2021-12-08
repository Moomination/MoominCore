package com.github.moomination.moomincore;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
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

  public static Component gradation(Component component, TextColor color1, TextColor color2) {
    if (!(component instanceof TextComponent text)) {
      return component.color(color1);
    }
    return gradation(text.content(), color1, color2);
  }

  public static TextComponent gradation(String text, TextColor color1, TextColor color2) {
    char[] chars = text.toCharArray();
    TextComponent.Builder builder = Component.text();
    for (int i = 0, len = chars.length; i < len; ++i) {
      builder.append(Component.text(String.valueOf(chars[i]), TextColor.lerp((float) i / len, color1, color2)));
    }
    return builder.build();
  }

  private Phrase() {
  }

}
