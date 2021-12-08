package com.github.moomination.moomincore.internal.commander;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.lucko.commodore.Commodore;

import java.util.function.Predicate;

public final class PermissionTest {

  public static <T> Predicate<T> test(Commodore commodore, String permission) {
    return src -> commodore.getBukkitSender(src).hasPermission(permission);
  }

  public static <S> Command<S> test(Commodore commodore, String permission, Command<S> delegation) {
    return ctx -> {
      if (!commodore.getBukkitSender(ctx.getSource()).hasPermission(permission))
        throw new SimpleCommandExceptionType(() -> "You do not have permission!").create();
      return delegation.run(ctx);
    };
  }

  private PermissionTest() {
  }

}
