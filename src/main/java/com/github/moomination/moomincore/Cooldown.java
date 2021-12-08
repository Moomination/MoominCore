package com.github.moomination.moomincore;

import org.bukkit.Bukkit;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Cooldown<T> {

  public final Map<T, AtomicInteger> AWAITERS = new WeakHashMap<>();

  public void wait(T t, int count, int delayTicks, int intervalTicks, Runnable onTimeout) {
    AtomicInteger counter = new AtomicInteger(count);
    AWAITERS.put(t, counter);
    Bukkit.getScheduler().runTaskTimer(MoominCore.getInstance(),
      task -> {
        //noinspection NumberEquality
        if (AWAITERS.get(t) != counter) {
          AWAITERS.remove(t);
          task.cancel();
          return;
        }
        if (counter.decrementAndGet() == 0) {
          AWAITERS.remove(t);
          onTimeout.run();
          task.cancel();
        }
      }
      , delayTicks, intervalTicks);
  }

  public boolean contains(T t) {
    return AWAITERS.containsKey(t);
  }

  public int remove(T t) {
    AtomicInteger counter = AWAITERS.remove(t);
    return counter == null ? -1 : counter.getAcquire();
  }

  public int get(T t) {
    AtomicInteger counter = AWAITERS.get(t);
    return counter == null ? -1 : counter.getAcquire();
  }

}
