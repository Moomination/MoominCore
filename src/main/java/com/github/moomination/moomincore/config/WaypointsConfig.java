package com.github.moomination.moomincore.config;

import com.github.moomination.moomincore.Waypoint;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;

public class WaypointsConfig implements YamlSerializable {

  @SuppressWarnings("unchecked")
  public static WaypointsConfig deserialize(YamlConfiguration data) {
    Map<String, Waypoint> waypoints = (Map<String, Waypoint>) data.get("waypoints");
    if (waypoints == null) waypoints = new Object2ObjectLinkedOpenHashMap<>();
    return new WaypointsConfig(waypoints);
  }

  public final Map<String, Waypoint> waypoints;

  public WaypointsConfig(Map<String, Waypoint> waypoints) {
    this.waypoints = waypoints;
  }

  @Override
  public String id() {
    return "waypoints";
  }

  @Override
  public void serialize(YamlConfiguration yaml) {
    yaml.set("waypoints", waypoints);
  }

}
