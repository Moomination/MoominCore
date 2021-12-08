package com.github.moomination.moomincore.listeners;

import com.github.moomination.moomincore.MoominCore;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Comparator;
import java.util.Set;

public class BossDamageListener implements Listener {

  private static final Set<EntityType> BOSS = Set.of(
    EntityType.WITHER,
    EntityType.ENDER_DRAGON,
    EntityType.ELDER_GUARDIAN
  );
  private static final String KEY_DAMAGES = "damages";

  @EventHandler(priority = EventPriority.MONITOR)
  @SuppressWarnings("unchecked")
  public static void onBossDamagedByEntity(EntityDamageByEntityEvent event) {
    if (BOSS.contains(event.getEntityType())) {
      return;
    }
    Entity boss = event.getEntity();
    Player damager;

    if (event.getDamager() instanceof Player) {
      // プレイヤーの直接攻撃
      damager = (Player) event.getDamager();
    } else if (event.getDamager() instanceof Projectile projectile) {
      ProjectileSource shootBy = projectile.getShooter();
      if (!(shootBy instanceof Player player)) {
        return;
      }
      damager = player;
    } else {
      return;
    }

    Object2IntMap<Player> damages;
    int damage = (int) Math.round(event.getFinalDamage());

    if (!boss.hasMetadata(KEY_DAMAGES)) {
      damages = new Object2IntOpenHashMap<>();
      boss.setMetadata(KEY_DAMAGES, new FixedMetadataValue(MoominCore.getInstance(), damages));
    } else {
      damages = (Object2IntMap<Player>) boss.getMetadata(KEY_DAMAGES).get(0).value();
      if (damages == null) {
        return;
      }
      if (damages.containsKey(damager)) {
        damage += damages.getInt(damager);
      }
    }
    damages.put(damager, damage);
  }

  @EventHandler
  @SuppressWarnings("unchecked")
  public static void onBossSlain(EntityDeathEvent event) {
    if (BOSS.contains(event.getEntityType())) {
      return;
    }
    Entity boss = event.getEntity();
    if (!boss.hasMetadata(KEY_DAMAGES)) {
      return;
    }

    Object2IntMap<Player> damages = (Object2IntMap<Player>) boss.getMetadata(KEY_DAMAGES).get(0).value();
    if (damages == null) {
      return;
    }

    int[] place = { 0 };
    Bukkit.broadcast(Component.text("--- ランキング ---", NamedTextColor.GOLD));
    damages.object2IntEntrySet().stream()
      .sorted(Comparator.comparingInt(Object2IntMap.Entry::getIntValue))
      .map(set -> Component.text((++place[0]) + "位 " + set.getKey().getName() + " (" + set.getIntValue() + " ダメージ)", NamedTextColor.GOLD))
      .forEachOrdered(Bukkit::broadcast);
    boss.getMetadata(KEY_DAMAGES).set(0, null);
    boss.removeMetadata(KEY_DAMAGES, MoominCore.getInstance());
  }

}
