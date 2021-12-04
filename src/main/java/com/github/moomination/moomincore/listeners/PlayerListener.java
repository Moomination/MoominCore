package com.github.moomination.moomincore.listeners;

import com.github.moomination.moomincore.commands.SpawnCommand;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {


  @EventHandler
  public void onDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    Location location = player.getLocation();
    int killCount = player.getStatistic(Statistic.PLAYER_KILLS);
    int deaths = player.getStatistic(Statistic.DEATHS) + 1;
    String deathMessage = "";
    String cause = ChatColor.stripColor(event.getDeathMessage().replace(player.getName(), "").trim());
    if (player.getKiller() != null && cause.contains(player.getKiller().getDisplayName())) {
      int killerKillCount = player.getKiller().getStatistic(Statistic.PLAYER_KILLS);
      cause = cause.replace(player.getKiller().getDisplayName(), "").trim();
      deathMessage = " " + ChatColor.RED + player.getKiller().getDisplayName() + ChatColor.DARK_RED + "[" + killerKillCount + "]";
      ItemStack itemInMainHand = player.getKiller().getInventory().getItemInMainHand();
      if (itemInMainHand.getType() != Material.AIR) {
        String weaponName;
        if (itemInMainHand.getItemMeta().hasDisplayName()) {
          String displayName = itemInMainHand.getItemMeta().getDisplayName();
          if (cause.contains(displayName)) {
            cause = cause.substring(0, cause.length() - 10 - displayName.length());
          }
          weaponName = displayName;
        } else {
          weaponName = WordUtils.capitalize(itemInMainHand.getType().name().replace("_", " ").toLowerCase());
        }
        deathMessage = deathMessage + ChatColor.YELLOW + " using " + ChatColor.RED + weaponName + ChatColor.YELLOW;
      }
    }
    deathMessage = ChatColor.RED + player.getDisplayName() + ChatColor.DARK_RED + "[" + deaths + "] " + ChatColor.YELLOW + cause + deathMessage;
    event.setDeathMessage(deathMessage + ".");
    String finalDeathMessage = deathMessage;
    location.getWorld().strikeLightningEffect(location);
  }

  @EventHandler(ignoreCancelled = true)
  public void onRightClick(PlayerInteractEvent event) {
    if (!event.getAction().isRightClick())
      return;
    Player player = event.getPlayer();
    Block clickedBlock = event.getClickedBlock();
    ItemStack item = event.getItem();
    if (clickedBlock == null || clickedBlock.isEmpty()) {
      return;
    }

    if (clickedBlock.getType() == Material.BELL) {
      player.sendMessage(Component.text("ベルたたくな"));
      event.setCancelled(true);
      return;
    }

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
        player.swingMainHand();
      }
    }

  }

  public static boolean isAgeableMature(Ageable ageable) {
    return ageable.getAge() == ageable.getMaximumAge()
      && ageable.getAge() != 0;
  }

  @EventHandler
  public static void onPlayerMoved(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    if (event.hasChangedPosition() && SpawnCommand.WAITSET.remove(player)) {
      player.sendMessage(ChatColor.RED + "Your teleportation has been cancelled");
    }
  }

}
