package com.github.moomination.moomincore.internal.commander;

import org.bukkit.Bukkit;

public final class Naming {

  private static final String SERVER_VERSION;

  static {
    Class<?> server = Bukkit.getServer().getClass();
    if (!server.getSimpleName().equals("CraftServer")) {
      SERVER_VERSION = ".";
    } else if (server.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
      SERVER_VERSION = ".";
    } else {
      String version = server.getName().substring("org.bukkit.craftbukkit".length());
      SERVER_VERSION = version.substring(0, version.length() - "CraftServer".length());
    }
  }

  public static String nms(String className) {
    return "net.minecraft.server" + SERVER_VERSION + className;
  }

  public static Class<?> nmsClass(String className) throws ClassNotFoundException {
    return Class.forName(nms(className));
  }

  public static String obc(String className) {
    return "org.bukkit.craftbukkit" + SERVER_VERSION + className;
  }

  public static Class<?> obcClass(String className) throws ClassNotFoundException {
    return Class.forName(obc(className));
  }

  private Naming() {
  }

}
