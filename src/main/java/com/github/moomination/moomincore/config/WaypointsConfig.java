package com.github.moomination.moomincore.config;

import com.github.moomination.moomincore.Waypoint;

import java.util.LinkedHashMap;
import java.util.Map;

public class WaypointsConfig implements YamlSerializable {

  @SuppressWarnings("unchecked")
  public static WaypointsConfig deserialize(Map<String, ?> data) {
    Map<String, Waypoint> waypoints = new LinkedHashMap<>(data.size());
    data.forEach((k, v) -> waypoints.put(k, Waypoint.deserialize((Map<String, Object>) v)));
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
  public Map<String, ?> serialize() {
    Map<String, Map<String, Object>> serialized = new LinkedHashMap<>();
    waypoints.forEach((k, v) -> serialized.put(k, v.serialize()));
    return serialized;
  }

}
