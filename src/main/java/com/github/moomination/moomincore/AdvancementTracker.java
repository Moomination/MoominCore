package com.github.moomination.moomincore;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;

public class AdvancementTracker {

  private static final String METADATA_KEY = "unlockedAdvancements";

  private static int numberOfAdvancements;

  public static void reload() {
    Server server = Bukkit.getServer();
    Iterator<Advancement> advancementIterator = server.advancementIterator();
    Collection<? extends Player> players = server.getOnlinePlayers();
    int all = 0;
    Object2IntMap<Player> completed = new Object2IntOpenHashMap<>(players.size());
    while (advancementIterator.hasNext()) {
      ++all;
      Advancement advancement = advancementIterator.next();
      players.forEach(player -> {
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        if (progress.isDone()) {
          completed.put(player, completed.getOrDefault(player, 0) + 1);
        }
      });
    }
    numberOfAdvancements = all;
    players.forEach(player -> setCount(player, completed.getOrDefault(player, 0)));
  }

  public static int numberOfAdvancements() {
    return numberOfAdvancements;
  }

  public static int getCount(Player player) {
    if (player.hasMetadata(METADATA_KEY)) {
      return player.getMetadata(METADATA_KEY).get(0).asInt();
    }
    return 0;
  }

  public static void setCount(Player player, int count) {
    MetadataValue value = new IntMetadataValue(MoominCore.getInstance(), count);
    player.setMetadata(METADATA_KEY, value);
    Component name = colorize(player, player.name(), count);
    player.displayName(name);
    player.playerListName(name);
  }

  public static void addCount(Player player, int count) {
    if (player.hasMetadata(METADATA_KEY)) {
      count += player.getMetadata(METADATA_KEY).get(0).asInt();
    }
    player.setMetadata(METADATA_KEY, new IntMetadataValue(MoominCore.getInstance(), count));
    Component name = colorize(player, player.name(), count);
    player.displayName(name);
    player.playerListName(name);
  }

  public static Component colorize(Player player, Component component, int count) {
    if (numberOfAdvancements == 0) {
      return component.color(NamedTextColor.WHITE);
    }
    int i = (int) ((float) count / numberOfAdvancements * 100);
    if (i < 20) return component.color(NamedTextColor.GRAY);
    if (i < 40) return component.color(NamedTextColor.DARK_GREEN);
    if (i < 60) return component.color(NamedTextColor.DARK_AQUA);
    if (i < 70) return component.color(NamedTextColor.BLUE);
    if (i < 80) return component.color(NamedTextColor.GREEN);
    if (i < 90) return component.color(NamedTextColor.AQUA);
    if (i < 100) return gradient(player, component, TextColor.color(0xd1372c), TextColor.color(0xcc3329));
    return gradient(player, component, NamedTextColor.GOLD, NamedTextColor.YELLOW);
  }

  public static Component gradient(Player player, Component component, TextColor color1, TextColor color2) {
    if (!(component instanceof TextComponent text)) {
      return component.color(color1);
    }
    char[] chars = text.content().toCharArray();
    TextComponent.Builder builder = Component.text();
    for (int i = 0, len = chars.length; i < len; ++i) {
      builder.append(Component.text(String.valueOf(chars[i]), TextColor.lerp((float) i / len, color1, color2)));
    }
    return builder.build();
  }

  private static final class IntMetadataValue extends MetadataValueAdapter {

    private final int value;

    IntMetadataValue(@NotNull Plugin owningPlugin, int value) {
      super(owningPlugin);
      this.value = value;
    }

    @Override
    public @Nullable Object value() {
      return null;
    }

    @Override
    public int asInt() {
      return value;
    }

    @Override
    public void invalidate() {

    }

  }

}
