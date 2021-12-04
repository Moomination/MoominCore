package com.github.moomination.moomincore.command.interop;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

public final class Reflections {

  public static void bind(CallSite callSite, MethodHandle target) throws ReflectiveOperationException {
    callSite.setTarget(target.asType(callSite.type()));
  }

  private Reflections() {
  }


}
