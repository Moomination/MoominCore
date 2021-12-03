package com.github.moomination.moomincore.commands;

import com.github.moomination.moomincore.Waypoint;
import com.github.moomination.moomincore.command.*;
import com.github.moomination.moomincore.config.Configs;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
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

import java.util.Map;
import java.util.stream.Collectors;

public class WaypointCommand {

  public static void register(Commodore commodore, Plugin plugin) {
    commodore.register(
      PluginCommands.builder()
        .name("waypoint")
        .description("Server-side waypoint")
        .permission("moomination.command.waypoint")
        .build("", plugin),
      Commands.literal("waypoint")
        .requires(PermissionTest.test("moomination.command.waypoint"))
        .then(Commands.literal("list")
          .requires(PermissionTest.test("moomination.command.waypoint.list"))
          .executes(ctx -> list(ctx, false))
        )
        .then(Commands.literal("add")
          .requires(PermissionTest.test("moomination.command.waypoint.add"))
          .then(Commands.argument("name", StringArgumentType.string())
            .executes(ctx -> add(ctx, ctx.getArgument("name", String.class)))
            .then(Commands.argument("position", CoordinateArgumentType.coordinate())
              .requires(PermissionTest.test("moomination.command.waypoint.add.positioned"))
              .executes(ctx -> add(ctx, ctx.getArgument("name", String.class), ctx.getArgument("position", Vec3I.class)))
              .then(Commands.argument("world", NamedArgumentType.world())
                .executes(ctx -> addPositioned(ctx, ctx.getArgument("name", String.class), ctx.getArgument("position", Vec3I.class), ctx.getArgument("world", World.class)))
              )
            )
          )
        )
        .then(Commands.literal("edit") // Waypoint Composer v2.0
          .requires(PermissionTest.test("moomination.command.waypoint.edit"))
          .then(Commands.argument("name", StringArgumentType.string())
            .executes(ctx -> add(ctx, ctx.getArgument("name", String.class)))
            .then(Commands.argument("position", CoordinateArgumentType.coordinate())
              .executes(ctx -> add(ctx, ctx.getArgument("name", String.class), ctx.getArgument("position", Vec3I.class)))
              .then(Commands.argument("world", NamedArgumentType.world())
                .executes(ctx -> addPositioned(ctx, ctx.getArgument("name", String.class), ctx.getArgument("position", Vec3I.class), ctx.getArgument("world", World.class)))
              )
            )
          )
        )
        .then(Commands.literal("remove")
          .requires(PermissionTest.test("moomination.command.waypoint.remove"))
          .then(Commands.argument("name", StringArgumentType.string())
            .executes(ctx -> remove(ctx, ctx.getArgument("name", String.class)))
          )
        )
      //        .then(Commands.literal("dump")
      //          .requires(PermissionTest.test("moomination.command.waypoint.dump"))
      //          .then(Commands.argument("format", DumpFormatArgumentType.type()) // json, lunar, fyu, etc...
      //            .executes(ctx -> dumpJson(ctx, true))
      //          )
      //          .then(Commands.literal("auto")
      //            .then(Commands.literal("stop")
      //              .executes(ctx -> watch(ctx, false))
      //            )
      //            .then(Commands.literal("start")
      //              .then(Commands.argument("format", DumpFormatArgumentType.type())
      //                .executes(ctx -> watch(ctx, ctx.getArgument("interval", int.class), true))
      //              )
      //            )
      //          )
      //        )
      //        .then(Commands.literal("gui")
      //          .requires(PermissionTest.test("moomination.command.waypoint.gui"))
      //          .executes(ctx -> list(ctx, true))
      //        )
      //        .then(Commands.literal("marker")
      //          .requires(PermissionTest.test("moomination.command.waypoint.marker"))
      //          .then(Commands.literal("show")
      //            .then(Commands.argument("name", StringArgumentType.string())
      //              .executes(ctx -> setDisplay(ctx, ctx.getArgument("name", String.class), true))
      //            )
      //          )
      //          .then(Commands.literal("hide")
      //            .then(Commands.argument("name", StringArgumentType.string())
      //              .executes(ctx -> setDisplay(ctx, ctx.getArgument("name", String.class), false))
      //            )
      //          )
      //        )
    );
  }

  private static int setDisplay(CommandContext<CommandSource> ctx, String name, boolean display) {
    return 1;
  }

  private static int list(CommandContext<CommandSource> ctx, boolean raw) {
    CommandSender sender = ctx.getSource().sender();

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
          w.playerName().replace("\\", "\\\\")
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
            " (" + waypoint.playerName() + ")",
          waypoint
        )));
    }
    return size;
  }

  private static int add(CommandContext<CommandSource> ctx, String name) throws CommandSyntaxException {
    Location location = Commands.playerOrException(ctx.getSource().sender()).getLocation();
    return addPositioned(ctx, name, Vec3I.from(location), location.getWorld());
  }

  private static int add(CommandContext<CommandSource> ctx, String name, Vec3I position) throws CommandSyntaxException {
    return addPositioned(ctx, name, position, Commands.playerOrException(ctx.getSource().sender()).getWorld());
  }

  private static int addPositioned(CommandContext<CommandSource> ctx, String name, Vec3I position, World world) throws CommandSyntaxException {
    CommandSender sender = ctx.getSource().sender();
    if (Configs.waypointsConfig().waypoints.containsKey(name)) {
      throw new SimpleCommandExceptionType(() -> "\"" + name + "\" already exists").create();
    }
    Waypoint waypoint = new Waypoint(world.getName(), position.x(), position.y(), position.z(), name, sender.getName());
    Configs.waypointsConfig().waypoints.put(name, waypoint);
    Configs.waypointsConfig().save();
    sender.sendMessage(locationComponent(ChatColor.GREEN + "Saved as" + name, waypoint));
    return 1;
  }

  public static int remove(CommandContext<CommandSource> ctx, String name) throws CommandSyntaxException {
    CommandSender sender = ctx.getSource().sender();
    Waypoint waypoint;
    if ((waypoint = Configs.waypointsConfig().waypoints.remove(name)) == null) {
      throw new SimpleCommandExceptionType(() -> "\"" + name + "\" is not found").create();
    }
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
          .append(Component.text(waypoint.world(), TextColor.color(255, 255, 255)))
          .append(Component.newline())
          .append(Component.text("X: ", TextColor.color(0, 255, 0)))
          .append(Component.text(waypoint.x(), TextColor.color(255, 255, 255)))
          .append(Component.newline())
          .append(Component.text("Y: ", TextColor.color(0, 255, 0)))
          .append(Component.text(waypoint.y(), TextColor.color(255, 255, 255)))
          .append(Component.newline())
          .append(Component.text("Z: ", TextColor.color(0, 255, 0)))
          .append(Component.text(waypoint.z(), TextColor.color(255, 255, 25)))
          .append(Component.newline())
          .append(Component.text("Registered by: ", TextColor.color(0, 255, 0)))
          .append(Component.text(waypoint.playerName(), TextColor.color(255, 255, 25)))
          .append(Component.newline())
          .append(Component.newline())
          .append(Component.text("Click to copy location..."))
      ))
      .clickEvent(ClickEvent.copyToClipboard(String.format("%d, %d, %d in %s", waypoint.x(), waypoint.y(), waypoint.z(), waypoint.world())));
  }

}