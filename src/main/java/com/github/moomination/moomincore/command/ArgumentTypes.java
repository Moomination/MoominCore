package com.github.moomination.moomincore.command;

import com.mojang.brigadier.arguments.ArgumentType;
import me.lucko.commodore.MinecraftArgumentTypes;
import org.bukkit.NamespacedKey;

public final class ArgumentTypes {

  public static ArgumentType<?> PLAYER = MinecraftArgumentTypes.getByKey(NamespacedKey.minecraft("player"));
  public static ArgumentType<?> PLAYERS = MinecraftArgumentTypes.getByKey(NamespacedKey.minecraft("players"));
  public static ArgumentType<?> ENTITY = MinecraftArgumentTypes.getByKey(NamespacedKey.minecraft("entity"));
  public static ArgumentType<?> ENTITIES = MinecraftArgumentTypes.getByKey(NamespacedKey.minecraft("entities"));

  private ArgumentTypes() {
  }

}
