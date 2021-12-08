package com.github.moomination.moomincore.command;

import com.github.moomination.moomincore.Cooldown;
import com.github.moomination.moomincore.config.Configs;
import com.github.moomination.moomincore.config.spawn.Spawn;
import com.github.moomination.moomincore.event.MoominSpawnEvent;
import com.github.moomination.moomincore.internal.commander.ArgumentTypes;
import com.github.moomination.moomincore.internal.commander.Commands;
import com.github.moomination.moomincore.internal.commander.PermissionTest;
import com.github.moomination.moomincore.internal.commander.PluginCommands;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.lucko.commodore.Commodore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Optional;

public class SpawnCommand {

  public static final Cooldown<Player> SPAWN_WAITTIME = new Cooldown<>();

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
        .then(Commands.literal("get")
          .executes(PermissionTest.test(commodore, "moomincore.command.spawn.get",
            ctx -> getSpawn(commodore.getBukkitSender(ctx.getSource()))
          ))
        )
        .then(Commands.literal("reset")
          .executes(PermissionTest.test(commodore, "moomincore.command.spawn.set",
            ctx -> clear(commodore.getBukkitSender(ctx.getSource()))
          ))
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
    if (SPAWN_WAITTIME.contains(teleportee)) {
      throw new SimpleCommandExceptionType(() -> ChatColor.RED + "You are already teleporting!").create();
    }

    Location spawn = spawnLocation(sender).orElseThrow(
      new SimpleCommandExceptionType(() -> ChatColor.RED + "Spawn is not set yet!")::create
    );
    MoominSpawnEvent event = new MoominSpawnEvent(sender, teleportee, spawn);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      throw new SimpleCommandExceptionType(() -> ChatColor.RED + "Teleportation is cancelled!").create();
    }

    int distance = -1;
    if (teleportee.getLocation().getWorld() == spawn.getWorld()) {
      distance = (int) teleportee.getLocation().distance(spawn);
    }

    boolean force = false;
    if (sender != teleportee) {
      //      Component by = Component.text(sender.getName());
      //      if (sender instanceof Player player) {
      //        by = by.hoverEvent(HoverEvent.showEntity(Key.key("minecraft:player"), player.getUniqueId()));
      //      }
      //      teleportee.sendMessage(by.color(NamedTextColor.GOLD).append(Component.text(" is teleporting you")));
      force = true;
    }

    GameMode gamemode;
    if (force || (gamemode = teleportee.getGameMode()) != GameMode.SURVIVAL && gamemode != GameMode.ADVENTURE) {
      teleportee.sendMessage(ChatColor.GOLD + "Teleporting...");
      teleportee.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
      return distance;
    }

    teleportee.sendMessage(ChatColor.GOLD + "Don't move! You will be teleported after 5 seconds...");
    queueSpawn(teleportee, spawn);
    return distance;
  }

  private static void queueSpawn(Player teleportee, Location spawn) {
    SPAWN_WAITTIME.wait(teleportee, 5, 0, 20, () -> {
      teleportee.sendMessage(ChatColor.GOLD + "Teleporting...");
      teleportee.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
    });
  }

  private static int setSpawn(CommandSender sender, Vector vec3, World world) throws CommandSyntaxException {
    Location location = vec3.toLocation(world);
    Configs.spawnConfig().spawn = Spawn.from(location);
    sender.sendMessage(
      spawnPhrase(
        Component.text(
          "Spawn was set to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()
        ),
        location
      )
    );
    return 0;
  }

  private static int clear(CommandSender sender) {
    Configs.spawnConfig().spawn = null;
    sender.sendMessage("Spawn has been cleared.");
    return 0;
  }

  private static int getSpawn(CommandSender sender) {
    spawnLocation(sender).ifPresentOrElse(
      spawn -> sender.sendMessage(spawnPhrase(
        Component.text("Spawn is " + spawn.getBlockX() + ", " + spawn.getBlockY() + ", " + spawn.getBlockZ() + " in " + spawn.getWorld().getName()),
        spawn
      )),
      () -> sender.sendMessage("Spawn is not set yet!")
    );
    return 0;
  }

  private static Optional<Location> spawnLocation(CommandSender sender) {
    Optional<Spawn> optional = Optional.ofNullable(Configs.spawnConfig().spawn);
    if (sender instanceof Player player) {
      return optional.map(Spawn::toLocation)
        .or(() -> Optional.ofNullable(player.getBedSpawnLocation()))
        .or(() -> Optional.of(player.getWorld().getSpawnLocation()));
    }
    return Optional.empty();
  }

  private static ComponentLike spawnPhrase(Component message, Location spawn) {
    String world = spawn.getWorld().getName();
    var x = spawn.getBlockX();
    var y = spawn.getBlockY();
    var z = spawn.getBlockZ();
    return message.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
      Component.text()
        .append(Component.text("World: ", NamedTextColor.GREEN))
        .append(Component.text(world, NamedTextColor.WHITE))
        .append(Component.newline())
        .append(Component.text("X: ", NamedTextColor.GREEN))
        .append(Component.text("" + x, NamedTextColor.WHITE))
        .append(Component.newline())
        .append(Component.text("Y: ", NamedTextColor.GREEN))
        .append(Component.text("" + y, NamedTextColor.WHITE))
        .append(Component.newline())
        .append(Component.text("Z: ", NamedTextColor.GREEN))
        .append(Component.text("" + z, NamedTextColor.WHITE))
        .append(Component.newline())
        .append(Component.text("Click to copy coordinates..."))
        .build()
    )).clickEvent(ClickEvent.suggestCommand(String.format("Spawn is [%d, %d, %d] in '%s'", x, y, z, world)));
  }

}
