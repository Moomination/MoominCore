package com.github.moomination.moomincore.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

  @EventHandler
  public void onChat(AsyncPlayerChatEvent event) {
    //    String uuid = event.getPlayer().getUniqueId().toString().replaceAll("-", "");
    //    if (MoominCore.getPrefixes().containsKey(uuid)) {
    //      event.setFormat(ChatColor.RED + "[" + MoominCore.getPrefixes().get(uuid) + "]" + ChatColor.WHITE + "%s: %s");
    //    } else {
    event.setFormat(ChatColor.RED + "[-]" + ChatColor.WHITE + "%s: %s");
    //    }
  }

}
