package com.github.moomination.moomincore.commands;

import com.github.moomination.moomincore.command.ArgumentTypes;
import com.github.moomination.moomincore.command.Commands;
import com.github.moomination.moomincore.command.PermissionTest;
import com.github.moomination.moomincore.command.PluginCommands;
import com.github.moomination.moomincore.config.Configs;
import com.github.moomination.moomincore.config.spawn.Spawn;
import com.github.moomination.moomincore.event.MoominSpawnEvent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.lucko.commodore.Commodore;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Optional;

public class SpawnCommand {

  public static void register(Commodore commodore, Plugin plugin) {
    Commands.register(
      commodore,
      PluginCommands.builder()
        .name("spawn")
        .description("Teleport to spawn point")
        .build(plugin),
      Commands.literal("spawn")
        .then(Commands.literal("teleport")
          .executes(PermissionTest.test(commodore, "moomincore.command.spawn.teleport",
            ctx -> respawn(commodore.getBukkitSender(ctx.getSource()), Commands.playerOrException(commodore.getBukkitSender(ctx.getSource())))
          ))
          .then(Commands.argument("player", ArgumentTypes.player())
            .executes(PermissionTest.test(commodore, "moomincore.command.spawn.teleport.other",
              ctx -> respawn(commodore.getBukkitSender(ctx.getSource()), ArgumentTypes.player(ctx, "player"))
            ))
          )
        )
        .then(Commands.literal("set")
          .executes(PermissionTest.test(commodore, "moomincore.command.spawn.set",
            ctx -> setSpawn(commodore.getBukkitSender(ctx.getSource()), Commands.playerOrException(commodore.getBukkitSender(ctx.getSource())).getLocation().toVector(),
              Commands.playerOrException(commodore.getBukkitSender(ctx.getSource())).getWorld())
          ))
          .then(Commands.argument("location", ArgumentTypes.vec3())
            .executes(
              PermissionTest.test(commodore, "moomincore.command.spawn.set",
                ctx -> setSpawn(commodore.getBukkitSender(ctx.getSource()), ArgumentTypes.vec3(ctx, "location"),
                  Commands.playerOrException(commodore.getBukkitSender(ctx.getSource())).getWorld())
              ))
            .then(Commands.argument("dimension", ArgumentTypes.dimension())
              .executes(PermissionTest.test(commodore, "moomincore.command.spawn.set",
                ctx -> setSpawn(commodore.getBukkitSender(ctx.getSource()), ArgumentTypes.vec3(ctx, "location"),
                  ArgumentTypes.dimension(ctx, "dimension"))
              ))
            )
          )
        )
        .requires(PermissionTest.test(commodore, "moomincore.command.spawn.teleport"))
        .executes(ctx -> respawn(commodore.getBukkitSender(ctx.getSource()), Commands.playerOrException(commodore.getBukkitSender(ctx.getSource()))))
    );
  }

  private static int respawn(CommandSender sender, Player teleportee) throws CommandSyntaxException {
    Location spawn = Optional.ofNullable(Configs.spawnConfig().spawn)
      .map(Spawn::toLocation)
      .or(() -> Optional.ofNullable(teleportee.getBedSpawnLocation()))
      .orElseGet(teleportee.getWorld()::getSpawnLocation);
    MoominSpawnEvent event = new MoominSpawnEvent(sender, teleportee, spawn);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      throw new SimpleCommandExceptionType(() -> ChatColor.RED + "Teleport is cancelled!").create();
    }

    sender.sendMessage(ChatColor.GOLD + "Teleporting...");
    if (sender != teleportee) {
      Component by = Component.text(sender.getName());
      if (sender instanceof Player player) {
        by = by.hoverEvent(HoverEvent.showEntity(Key.key("minecraft:player"), player.getUniqueId()));
      }
      teleportee.sendMessage(Component.text(ChatColor.GOLD + "You are teleporting by ").append(by).append(Component.text("...")));
    }
    int distance = (int) teleportee.getLocation().distance(spawn);
    teleportee.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
    return distance;
  }

  private static int setSpawn(CommandSender sender, Vector vec3, World world) throws CommandSyntaxException {
    Location location = vec3.toLocation(world);
    Configs.spawnConfig().spawn = Spawn.from(location);
    sender.sendMessage("Spawn was set to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
    return 1;
  }

}
