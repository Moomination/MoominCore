package com.github.moomination.moomincore.event;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MoominSpawnEvent extends Event implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS;
  }

  private final CommandSender teleportBy;
  private final Player teleportee;
  private final Location teleportTo;
  private boolean cancelled;

  public MoominSpawnEvent(CommandSender teleportBy, Player teleportee, Location teleportTo) {
    super(false);
    this.teleportBy = teleportBy;
    this.teleportee = teleportee;
    this.teleportTo = teleportTo;
  }

  public CommandSender teleportBy() {
    return teleportBy;
  }

  public Location teleportTo() {
    return teleportTo;
  }

  public Player teleportee() {
    return teleportee;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

}
