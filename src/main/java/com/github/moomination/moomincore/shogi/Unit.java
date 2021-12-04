package com.github.moomination.moomincore.shogi;

public interface Unit {

  int x();

  int z();

  void moveTo(int x, int z);

  boolean isNarigoma();

  Unit naru();

  String displayName();

  boolean canMoveTo(int x, int z);

}
