package com.github.moomination.moomincore.listeners;

import com.github.moomination.moomincore.Phrase;
import com.github.moomination.moomincore.config.Configs;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class ChatListener implements Listener {

  private static final Char2ObjectMap<Consumer<TextComponent.Builder>> COLORS
    = new Char2ObjectOpenHashMap<>(22);

  static {
    COLORS.put('0', b -> b.style(Style.style(NamedTextColor.BLACK)));
    COLORS.put('1', b -> b.style(Style.style(NamedTextColor.DARK_BLUE)));
    COLORS.put('2', b -> b.style(Style.style(NamedTextColor.DARK_GREEN)));
    COLORS.put('3', b -> b.style(Style.style(NamedTextColor.DARK_AQUA)));
    COLORS.put('4', b -> b.style(Style.style(NamedTextColor.DARK_RED)));
    COLORS.put('5', b -> b.style(Style.style(NamedTextColor.DARK_PURPLE)));
    COLORS.put('6', b -> b.style(Style.style(NamedTextColor.GOLD)));
    COLORS.put('7', b -> b.style(Style.style(NamedTextColor.GRAY)));
    COLORS.put('8', b -> b.style(Style.style(NamedTextColor.DARK_GRAY)));
    COLORS.put('9', b -> b.style(Style.style(NamedTextColor.BLUE)));
    COLORS.put('a', b -> b.style(Style.style(NamedTextColor.GREEN)));
    COLORS.put('b', b -> b.style(Style.style(NamedTextColor.AQUA)));
    COLORS.put('c', b -> b.style(Style.style(NamedTextColor.RED)));
    COLORS.put('d', b -> b.style(Style.style(NamedTextColor.LIGHT_PURPLE)));
    COLORS.put('e', b -> b.style(Style.style(NamedTextColor.YELLOW)));
    COLORS.put('f', b -> b.style(Style.style(NamedTextColor.WHITE)));
    COLORS.put('k', b -> b.style(Style.style(TextDecoration.OBFUSCATED)));
    COLORS.put('l', b -> b.style(Style.style(TextDecoration.BOLD)));
    COLORS.put('m', b -> b.style(Style.style(TextDecoration.STRIKETHROUGH)));
    COLORS.put('n', b -> b.style(Style.style(TextDecoration.UNDERLINED)));
    COLORS.put('o', b -> b.style(Style.style(TextDecoration.ITALIC)));
    COLORS.put('r', b -> b.resetStyle());
  }

  @EventHandler
  public static void onChat(AsyncChatEvent event) {
    String uuid = event.getPlayer().getUniqueId().toString().replace("-", "");
    String prefix = Configs.prefixConfig().prefixes.getOrDefault(uuid, "-");
    // ChatColor.RED + "[" + prefix + "]" + ChatColor.WHITE + "%s: %s"
    event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) ->
      Component.text()
        .append(Component.text("[", NamedTextColor.GOLD))
        .append(Component.text(prefix, NamedTextColor.RED))
        .append(Component.text("]", NamedTextColor.GOLD))
        .append(Phrase.player(source, sourceDisplayName))
        .append(Component.text(": ", NamedTextColor.WHITE))
        .append(
          Stream.of(
              LegacyComponentSerializer.legacyAmpersand().deserialize(
                  LegacyComponentSerializer.legacySection().serialize(message)
                )
                .replaceText(builder ->
                  Bukkit.getOnlinePlayers().forEach(player ->
                    builder.match(player.getName()).replacement(b -> Phrase.player(player, b))
                  )
                )
            )
            .peek(System.out::println)
            .peek(x -> System.out.println(GsonComponentSerializer.gson().serialize(x)))
            .findFirst().get()
        )
        .build()
    ));
  }

}
