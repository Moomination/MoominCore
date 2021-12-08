package com.github.moomination.moomincore.command;

import com.github.moomination.moomincore.internal.commander.ArgumentTypes;
import com.github.moomination.moomincore.internal.commander.Commands;
import com.github.moomination.moomincore.internal.commander.PermissionTest;
import com.github.moomination.moomincore.internal.commander.PluginCommands;
import com.github.moomination.moomincore.config.Configs;
import com.github.moomination.moomincore.config.waypoint.Waypoint;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.lucko.commodore.Commodore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.stream.Collectors;

public class WaypointCommand {

  public static void register(Commodore commodore, Plugin plugin) {
    Commands.register(
      commodore,
      PluginCommands.builder()
        .name("waypoint")
        .aliases("waypoints", "wp")
        .description("Server-side waypoint")
        .build(plugin),
      Commands.literal("waypoint")
        .then(Commands.literal("list")
          .executes(PermissionTest.test(commodore, "moomincore.command.waypoint.list",
            ctx -> list(commodore.getBukkitSender(ctx.getSource()), false)
          ))
        )
        .then(Commands.literal("add")
          .then(Commands.argument("name", StringArgumentType.string())
            .executes(PermissionTest.test(commodore, "moomincore.command.waypoint.add",
              ctx -> add(commodore.getBukkitSender(ctx.getSource()), ctx.getArgument("name", String.class))
            ))
            .then(Commands.argument("position", ArgumentTypes.vec3())
              .executes(PermissionTest.test(commodore, "moomincore.command.waypoint.add.positioned",
                ctx -> add(commodore.getBukkitSender(ctx.getSource()), ctx.getArgument("name", String.class), ArgumentTypes.vec3(ctx, "position"))
              ))
              .then(Commands.argument("world", ArgumentTypes.dimension())
                .executes(PermissionTest.test(commodore, "moomincore.command.waypoint.add.positioned",
                  ctx -> addPositioned(commodore.getBukkitSender(ctx.getSource()), ctx.getArgument("name", String.class),
                    ArgumentTypes.vec3(ctx, "position"), ArgumentTypes.dimension(ctx, "world")))
                ))
            )
          )
        )
        .then(Commands.literal("remove")
          .then(Commands.argument("name", StringArgumentType.string())
            .executes(PermissionTest.test(commodore, "moomincore.command.waypoint.remove",
              ctx -> remove(commodore.getBukkitSender(ctx.getSource()), ctx.getArgument("name", String.class))
            ))
          )
        )
    );
  }

  private static int setDisplay(CommandSender sender, String name, boolean display) {
    return 1;
  }

  private static int list(CommandSender sender, boolean raw) {
    final Map<String, Waypoint> waypoints = Configs.waypointsConfig().waypoints;
    int size = waypoints.size();
    if (raw) {
      String json = "{\"timestamp\":" + System.currentTimeMillis() + ",\"values\":[";
      json += waypoints.values()
        .stream()
        .map(w -> "{\"x\":%d,\"y\":%d,\"z\":%d,\"name\":\"%s\",\"world\":\"%s\",\"author\":\"%s\"}".formatted(
          w.x(), w.y(), w.z(),
          w.name().replace("\\", "\\\\"),
          w.world().replace("\\", "\\\\"),
          w.player().replace("\\", "\\\\")
        ))
        .collect(Collectors.joining(","));
      json += "]}";
      sender.sendMessage(Component.text(json));
    } else {
      sender.sendMessage(ChatColor.YELLOW + "====" + ChatColor.WHITE + "Saved Coordinates" + ChatColor.YELLOW + "===========");
      waypoints.values()
        .forEach(waypoint -> sender.sendMessage(locationComponent(
          Component.text()
            .append(Component.text("Name: ", NamedTextColor.AQUA))
            .append(Component.text(waypoint.name(), NamedTextColor.WHITE))
            .append(Component.text(" Pos: ", NamedTextColor.AQUA))
            .append(Component.text(waypoint.x(), NamedTextColor.GRAY))
            .append(Component.text(", ", NamedTextColor.DARK_GRAY))
            .append(Component.text(waypoint.y(), NamedTextColor.GRAY))
            .append(Component.text(", ", NamedTextColor.DARK_GRAY))
            .append(Component.text(waypoint.z(), NamedTextColor.GRAY))
            .append(Component.text(" (", NamedTextColor.DARK_GRAY))
            .append(Component.text(waypoint.player(), NamedTextColor.GRAY))
            .append(Component.text(")", NamedTextColor.DARK_GRAY))
            .build(),
          waypoint
        )));
    }
    return size;
  }

  private static int add(CommandSender sender, String name) throws CommandSyntaxException {
    Location location = Commands.playerOrException(sender).getLocation();
    return addPositioned(sender, name, location.toVector(), location.getWorld());
  }

  private static int add(CommandSender sender, String name, Vector position) throws CommandSyntaxException {
    return addPositioned(sender, name, position, Commands.playerOrException(sender).getWorld());
  }

  private static int addPositioned(CommandSender sender, String name, Vector position, World world) throws CommandSyntaxException {
    if (Configs.waypointsConfig().waypoints.containsKey(name)) {
      throw new SimpleCommandExceptionType(() -> "\"" + name + "\" already exists").create();
    }
    Waypoint waypoint = new Waypoint(world.getName(), position.getBlockX(), position.getBlockY(), position.getBlockZ(), name, sender.getName());
    Configs.waypointsConfig().waypoints.put(name, waypoint);
    Configs.waypointsConfig().save();
    sender.sendMessage(locationComponent(Component.text("Saved as '" + name + "'", NamedTextColor.GREEN), waypoint));
    return 1;
  }

  public static int remove(CommandSender sender, String name) throws CommandSyntaxException {
    Waypoint waypoint;


    if ((waypoint = Configs.waypointsConfig().waypoints.get(name)) == null) {
      throw new SimpleCommandExceptionType(() -> "Waypoint \"" + name + "\" is not found").create();
    }

    if (!sender.hasPermission("moomincore.command.waypoint.remove.other")
      && sender.getName().equals(waypoint.player())) {
      throw new SimpleCommandExceptionType(() -> "Waypoint \"" + name + "\" is not yours").create();
    }

    Configs.waypointsConfig().waypoints.remove(name, waypoint);
    Configs.waypointsConfig().save();
    sender.sendMessage(locationComponent(Component.text("\"" + name + "\" has been removed", NamedTextColor.GREEN), waypoint));
    return 1;
  }

  private static ComponentLike locationComponent(Component message, Waypoint waypoint) {
    return message.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
      Component.text(waypoint.name())
        .append(Component.newline())
        .append(Component.text("World: ", NamedTextColor.GREEN))
        .append(Component.text(waypoint.world(), NamedTextColor.WHITE))
        .append(Component.newline())
        .append(Component.text("X: ", NamedTextColor.GREEN))
        .append(Component.text("" + waypoint.x(), NamedTextColor.WHITE))
        .append(Component.newline())
        .append(Component.text("Y: ", NamedTextColor.GREEN))
        .append(Component.text("" + waypoint.y(), NamedTextColor.WHITE))
        .append(Component.newline())
        .append(Component.text("Z: ", NamedTextColor.GREEN))
        .append(Component.text("" + waypoint.z(), NamedTextColor.WHITE))
        .append(Component.newline())
        .append(Component.text("Registered by: ", NamedTextColor.GREEN))
        .append(Component.text(waypoint.player(), NamedTextColor.WHITE))
        .append(Component.newline())
        .append(Component.text("Click to copy coordinates..."))
    )).clickEvent(ClickEvent.suggestCommand(String.format("Waypoint '%s' [%d, %d, %d] in '%s'", waypoint.name(), waypoint.x(), waypoint.y(), waypoint.z(), waypoint.world())));
  }

}
