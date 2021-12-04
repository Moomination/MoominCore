package com.github.moomination.moomincore.listeners;

import com.github.moomination.moomincore.commands.SpawnCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class PlayerListener implements Listener {

  @EventHandler
  public static void onDeath(PlayerDeathEvent event) {
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
    player.sendMessage(Component.text(
      "✙You died in %d, %d, %d".formatted(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
      NamedTextColor.GOLD
    ));
  }

  @EventHandler(ignoreCancelled = true)
  public static void onRightClick(PlayerInteractEvent event) {
    if (!event.getAction().isRightClick())
      return;
    Player player = event.getPlayer();
    Block clickedBlock = event.getClickedBlock();
    ItemStack item = event.getItem();
    if (clickedBlock == null || clickedBlock.isEmpty()) {
      return;
    }

    if (clickedBlock.getType() == Material.BELL) {
      int base = 0xFEFEFE;
      player.sendMessage(Component.text("ベルたたくな",
        TextColor.color(ThreadLocalRandom.current().nextInt(0xFFFFFF - base) + base)));
      event.setCancelled(true);
      return;
    }

    if (item != null && item.getType().isRecord() && clickedBlock.getType() == Material.JUKEBOX) {
      Location loc = player.getLocation();
      Bukkit.broadcast(Component.text(
        player.getName() + " がレコードを再生しようとしています！場所: %d, %d, %d".formatted(
          loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
        TextColor.color(255, 90, 90)));
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
    if (event.hasChangedBlock() && SpawnCommand.WAITSET.remove(player)) {
      player.sendMessage(ChatColor.RED + "Your teleportation has been cancelled");
    }
  }

  @EventHandler
  public static void onPlayerDamaged(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player player) {
      if (SpawnCommand.WAITSET.remove(player)) {
        player.sendMessage(ChatColor.RED + "Your teleportation has been cancelled");
      }
    }
  }

}
