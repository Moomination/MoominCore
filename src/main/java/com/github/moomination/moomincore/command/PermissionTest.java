package com.github.moomination.moomincore.command;

import me.lucko.commodore.Commodore;

import java.util.function.Predicate;

public final class PermissionTest {

  public static <T> Predicate<T> test(Commodore commodore, String permission) {
    return ctx -> commodore.getBukkitSender(ctx).hasPermission(permission);
  }

  private PermissionTest() {
  }

}
