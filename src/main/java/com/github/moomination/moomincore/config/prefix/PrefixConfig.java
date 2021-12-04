package com.github.moomination.moomincore.config.prefix;

import com.github.moomination.moomincore.config.YamlSerializable;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class PrefixConfig implements YamlSerializable {

  @SuppressWarnings("unchecked")
  public static PrefixConfig deserialize(Map<String, ?> data) {
    Map<String, String> prefixes = (Map<String, String>) data.get("prefixes");
    return new PrefixConfig(prefixes == null ? new Object2ObjectOpenHashMap<>() : prefixes);
  }

  public final Map<String, String> prefixes;

  public PrefixConfig(Map<String, String> prefixes) {
    this.prefixes = prefixes;
  }

  @Override
  public String id() {
    return "prefix";
  }

  public Map<String, Object> serialize() {
    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    builder.put("prefixes", prefixes);
    return builder.build();
  }

}
