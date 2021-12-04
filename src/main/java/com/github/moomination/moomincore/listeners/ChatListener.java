package com.github.moomination.moomincore.listeners;

import com.github.moomination.moomincore.config.Configs;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

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
        .append(sourceDisplayName.color(NamedTextColor.WHITE))
        .append(Component.text(": ", NamedTextColor.WHITE))
        .append(message.color(NamedTextColor.WHITE))
        .build()
    ));
  }

}
