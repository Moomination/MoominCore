package com.github.moomination.moomincore.listeners;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

  @EventHandler(ignoreCancelled = true)
  public void onRightClick(PlayerInteractEvent event) {
    if (!event.getAction().isRightClick())
      return;
    Player player = event.getPlayer();
    Block clickedBlock = event.getClickedBlock();
    ItemStack item = event.getItem();
    if (clickedBlock == null || clickedBlock.isEmpty())
      return;
    if (item != null && item.getType().isRecord() && clickedBlock.getType() == Material.JUKEBOX) {
      Location loc = player.getLocation();
      Bukkit.broadcast(Component.text(
        (player.getUniqueId().toString().replace("-", "").equals("f5ae87af5139453888500d5938529e18")
          ? "" : "たぶん") +
          "VIPTEACHERがレコードを再生しようとしています！場所: %d, %d, %d".formatted(
            loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
        TextColor.color(255, 90, 90)).hoverEvent(
        HoverEvent.showEntity(HoverEvent.ShowEntity.of(Key.key("minecraft:player"), player.getUniqueId()))));
      event.setCancelled(true);
      return;
    }
    if (
      clickedBlock.getType() != Material.BAMBOO &&
        clickedBlock.getType() != Material.SWEET_BERRY_BUSH &&
        clickedBlock.getType() != Material.SWEET_BERRIES &&
        clickedBlock.getBlockData() instanceof Ageable ageable) {
      if (isAgeableMature(ageable)) {
        World world = clickedBlock.getWorld();
        Location location = clickedBlock.getLocation();
        for (var drop : clickedBlock.getDrops(player.getActiveItem()))
          world.dropItem(location, drop);
        ageable.setAge(0);
        world.playSound(location, Sound.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1, 1);
        world.spawnParticle(Particle.COMPOSTER, location, 20);
        clickedBlock.setBlockData(ageable);
      }
    }

  }

  public static boolean isAgeableMature(Ageable ageable) {
    return ageable.getAge() == ageable.getMaximumAge()
      && ageable.getAge() != 0;
  }

}
