package com.github.moomination.moomincore.command;

import java.util.function.Predicate;

public final class PermissionTest {

  public static Predicate<CommandSource> test(String permission) {
    return source -> source.sender().hasPermission(permission);
  }

  private PermissionTest() {
  }

}
