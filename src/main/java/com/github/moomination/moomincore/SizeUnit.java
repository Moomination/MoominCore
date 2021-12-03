package com.github.moomination.moomincore;

public enum SizeUnit {
  BYTE("bytes", 1L),
  KIBIBYTE("kiB", 1024L),
  MEBIBYTE("MiB", 1048576L),
  GIBIBYTE("GiB", 1073741824L),
  TEBIBYTE("TiB", 1099511627776L),
  PEBIBYTE("PiB", 1125899906842624L),
  EXBIBYTE("EiB", 1152921504606846976L);

  private final String unit;
  private final long multiplier;

  SizeUnit(String unit, long multiplier) {
    this.unit = unit;
    this.multiplier = multiplier;
  }

  public static String toString(long value) {
    SizeUnit determined;
    determined = SizeUnit.BYTE;
    for (SizeUnit unit : values()) {
      if (value < unit.multiplier) {
        break;
      }
      determined = unit;
    }
    return determined.format(value);
  }

  @Override
  public String toString() {
    return unit;
  }

  public String format(long value) {
    return String.format("%d %s", value / multiplier, unit);
  }

}
