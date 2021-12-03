package com.github.moomination.moomincore.commands;

import com.github.moomination.moomincore.command.*;
import com.github.moomination.moomincore.config.Configs;
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

import java.util.Objects;
import java.util.stream.Stream;

public class SpawnCommand {

  public static void register(Commodore commodore, Plugin plugin) {
    commodore.register(
      PluginCommands.builder()
        .name("spawn")
        .description("Teleport to spawn point")
        .permission("moomination.command.spawn")
        .build("", plugin),
      Commands.literal("spawn")
        .then(
          Commands.literal("teleport")
            .requires(PermissionTest.test(commodore, "moomination.command.spawn.teleport"))
            .executes(ctx -> respawn(commodore.getBukkitSender(ctx), Commands.playerOrException(commodore.getBukkitSender(ctx))))
            .then(Commands.argument("player", NamedArgumentType.player())
              .executes(ctx -> respawn(commodore.getBukkitSender(ctx), ctx.getArgument("player", Player.class)))
            )
        )
        .then(
          Commands.literal("set")
            .requires(PermissionTest.test(commodore, "moomination.command.spawn.set"))
            .then(Commands.argument("location", CoordinateArgumentType.coordinate())
              .executes(ctx -> setSpawn(commodore.getBukkitSender(ctx), ctx.getArgument("location", Coordinate.class),
                Commands.playerOrException(commodore.getBukkitSender(ctx)).getWorld()))
              .then(Commands.argument("world", NamedArgumentType.world())
                .executes(ctx -> setSpawn(commodore.getBukkitSender(ctx), ctx.getArgument("location", Coordinate.class),
                  ctx.getArgument("world", World.class)))
              )
            )
        )
        .requires(PermissionTest.test(commodore, "moomination.command.spawn.teleport"))
        .executes(ctx -> respawn(commodore.getBukkitSender(ctx), Commands.playerOrException(commodore.getBukkitSender(ctx))))
    );
  }

  private static int respawn(CommandSender sender, Player teleportee) throws CommandSyntaxException {
    final Location spawn = Stream
      .of(Configs.spawnConfig().spawn, teleportee.getBedSpawnLocation(), teleportee.getWorld().getSpawnLocation())
      .filter(Objects::nonNull)
      .findFirst()
      .orElseGet(teleportee::getLocation);
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

  private static int setSpawn(CommandSender sender, Coordinate coordinate, World world) throws CommandSyntaxException {
    Location location;
    if (coordinate.offsetted()) {
      location = coordinate.toVec3I(Commands.playerOrException(sender).getLocation()).toLocation(0, 0, world);
    } else {
      location = coordinate.toVec3I(null).toLocation(0, 0, world);
    }

    Configs.spawnConfig().spawn = location;
    sender.sendMessage("Spawn was set to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
    return 1;
  }

}
