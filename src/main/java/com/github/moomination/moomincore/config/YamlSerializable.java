package com.github.moomination.moomincore.config;

import org.bukkit.configuration.file.YamlConfiguration;

public interface YamlSerializable {

  String id();

  void serialize(YamlConfiguration yaml);

  default void save() {
  }

}
