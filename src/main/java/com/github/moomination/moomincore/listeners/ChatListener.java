package com.github.moomination.moomincore.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

  //  @EventHandler
  public void onChat(AsyncChatEvent event) {
    //    String uuid = event.getPlayer().getUniqueId().toString().replaceAll("-", "");
    //    if (MoominCore.getPrefixes().containsKey(uuid)) {
    //      event.setFormat(ChatColor.RED + "[" + MoominCore.getPrefixes().get(uuid) + "]" + ChatColor.WHITE + "%s: %s");
    //    } else {
    //    event.renderer(ChatColor.RED + "[-]" + ChatColor.WHITE + "%s: %s");
    //    }
  }

}
