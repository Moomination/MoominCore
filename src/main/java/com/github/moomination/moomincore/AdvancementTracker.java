package com.github.moomination.moomincore;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.kyori.adventure.text.Component;
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
    Component name = colorize(player.name(), tier(count));
    player.displayName(name);
    player.playerListName(name);
  }

  public static int addCount(Player player, int count) {
    if (player.hasMetadata(METADATA_KEY)) {
      count += player.getMetadata(METADATA_KEY).get(0).asInt();
    }
    player.setMetadata(METADATA_KEY, new IntMetadataValue(MoominCore.getInstance(), count));
    Component name = colorize(player.name(), tier(count));
    player.displayName(name);
    player.playerListName(name);
    return count;
  }

  public static AdvancementTier tier(int count) {
    if (numberOfAdvancements == 0) {
      return AdvancementTier.NONE;
    }
    int i = (int) ((float) count / numberOfAdvancements * 100);
    if (i < 20) return AdvancementTier.NEWBIE;
    if (i < 40) return AdvancementTier.P20;
    if (i < 60) return AdvancementTier.P40;
    if (i < 70) return AdvancementTier.P60;
    if (i < 80) return AdvancementTier.P70;
    if (i < 90) return AdvancementTier.P80;
    if (i < 100) return AdvancementTier.P90;
    return AdvancementTier.MASTER;
  }

  public static Component colorize(Component component, AdvancementTier tier) {
    return switch (tier) {
      case NEWBIE -> component.color(NamedTextColor.GRAY);
      case P20 -> component.color(NamedTextColor.DARK_GREEN);
      case P40 -> component.color(NamedTextColor.DARK_AQUA);
      case P60 -> component.color(NamedTextColor.BLUE);
      case P70 -> component.color(NamedTextColor.GREEN);
      case P80 -> component.color(NamedTextColor.AQUA);
      case P90 -> Phrase.gradation(component, TextColor.color(0xd1372c), TextColor.color(0xcc3329));
      case MASTER -> Phrase.gradation(component, TextColor.color(0xffcc00), NamedTextColor.YELLOW);
      default -> component.color(NamedTextColor.WHITE);
    };
  }

  public enum AdvancementTier {
    NONE(""),
    NEWBIE("Newbie"),
    P20("20%"),
    P40("40%"),
    P60("60%"),
    P70("70%"),
    P80("80%"),
    P90("90%"),
    MASTER("Master");

    private final String displayName;

    AdvancementTier(String displayName) {
      this.displayName = displayName;
    }

    public String displayName() {
      return displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
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
