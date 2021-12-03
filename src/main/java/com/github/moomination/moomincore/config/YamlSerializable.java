package com.github.moomination.moomincore.config;

import java.util.Map;

public interface YamlSerializable {

  String id();

  Map<String, ?> serialize();

  default void save() {
  }

}
