package com.github.moomination.moomincore.commands;

import com.github.moomination.moomincore.command.ArgumentTypes;
import com.github.moomination.moomincore.command.Commands;
import com.github.moomination.moomincore.command.PermissionTest;
import com.github.moomination.moomincore.command.PluginCommands;
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
import net.kyori.adventure.text.format.TextColor;
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
        .permission("moomincore.command.waypoint")
        .build(plugin),
      Commands.literal("waypoint")
        .requires(PermissionTest.test(commodore, "moomincore.command.waypoint"))
        .then(Commands.literal("list")
          .requires(PermissionTest.test(commodore, "moomincore.command.waypoint.list"))
          .executes(ctx -> list(commodore.getBukkitSender(ctx.getSource()), false))
        )
        .then(Commands.literal("add")
          .requires(PermissionTest.test(commodore, "moomincore.command.waypoint.add"))
          .then(Commands.argument("name", StringArgumentType.string())
            .executes(ctx -> add(commodore.getBukkitSender(ctx.getSource()), ctx.getArgument("name", String.class)))
            .then(Commands.argument("position", ArgumentTypes.vec3())
              .requires(PermissionTest.test(commodore, "moomincore.command.waypoint.add.positioned"))
              .executes(ctx -> add(commodore.getBukkitSender(ctx.getSource()), ctx.getArgument("name", String.class), ArgumentTypes.vec3(ctx, "position")))
              .then(Commands.argument("world", ArgumentTypes.dimension())
                .executes(ctx -> addPositioned(commodore.getBukkitSender(ctx.getSource()), ctx.getArgument("name", String.class),
                  ArgumentTypes.vec3(ctx, "position"), ArgumentTypes.dimension(ctx, "world")))
              )
            )
          )
        )
        .then(Commands.literal("remove")
          .requires(PermissionTest.test(commodore, "moomincore.command.waypoint.remove"))
          .then(Commands.argument("name", StringArgumentType.string())
            .executes(ctx -> remove(commodore.getBukkitSender(ctx.getSource()), ctx.getArgument("name", String.class)))
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
          ChatColor.AQUA + "Name: " +
            ChatColor.WHITE + waypoint.name() +
            ChatColor.AQUA + " Pos: " +
            ChatColor.WHITE + waypoint.x() +
            ChatColor.GRAY + ", " +
            ChatColor.WHITE + waypoint.y() +
            ChatColor.GRAY + ", " +
            ChatColor.WHITE + waypoint.z() +
            ChatColor.DARK_GRAY +
            " (" + waypoint.player() + ")",
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
    sender.sendMessage(locationComponent(ChatColor.GREEN + "Saved as '" + name + "'", waypoint));
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
    sender.sendMessage(locationComponent(ChatColor.GREEN + "\"" + name + "\" has been removed", waypoint));
    return 1;
  }

  private static ComponentLike locationComponent(String message, Waypoint waypoint) {
    return Component.text(message)
      .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
        Component.text(waypoint.name())
          .append(Component.newline())
          .append(Component.text("World: ", TextColor.color(0, 255, 0)))
          .append(Component.text(waypoint.world(), TextColor.color(255, 255, 0)))
          .append(Component.newline())
          .append(Component.text("X: ", TextColor.color(0, 255, 0)))
          .append(Component.text(waypoint.x(), TextColor.color(255, 255, 0)))
          .append(Component.newline())
          .append(Component.text("Y: ", TextColor.color(0, 255, 0)))
          .append(Component.text(waypoint.y(), TextColor.color(255, 255, 0)))
          .append(Component.newline())
          .append(Component.text("Z: ", TextColor.color(0, 255, 0)))
          .append(Component.text(waypoint.z(), TextColor.color(255, 255, 0)))
          .append(Component.newline())
          .append(Component.text("Registered by: ", TextColor.color(0, 255, 0)))
          .append(Component.text(waypoint.player(), TextColor.color(255, 255, 0)))
          .append(Component.newline())
          .append(Component.newline())
          .append(Component.text("Click to copy location..."))
      ))
      .clickEvent(ClickEvent.copyToClipboard(String.format("%d, %d, %d in %s", waypoint.x(), waypoint.y(), waypoint.z(), waypoint.world())));
  }

}
