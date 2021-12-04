package com.github.moomination.moomincore.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {

  @EventHandler
  public static void onPlantBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    Block plant = block.getRelative(BlockFace.UP);
    if (!plant.isEmpty() && block.getType() == Material.FARMLAND) {
      event.setCancelled(true);
    }
  }

}
