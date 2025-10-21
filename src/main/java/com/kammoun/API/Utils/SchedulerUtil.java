package com.kammoun.API.Utils;

/*USE THIS FOR FOLIA SUPPORT !*/
public final class SchedulerUtil {
/*
	private static boolean isFolia;

	static {
		try {
			Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
			isFolia = true;

		} catch (final ClassNotFoundException e) {
			isFolia = false;
		}
	}

	public static void run(Runnable runnable) {
		if (isFolia)
			Bukkit.getGlobalRegionScheduler()
					.execute(Main.getInstance(), runnable);

		else
			Bukkit.getScheduler().runTask(Main.getInstance(), runnable);
	}

  public static Task runAsync(Runnable runnable) {
    if (isFolia)
      return new Task(Bukkit.getAsyncScheduler()
        .runNow(Main.getInstance(), t -> runnable.run()));
    else
      return new Task(Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), runnable));
  }

  public static Task runAsyncLater(Runnable runnable, long delayTicks) {
    if (isFolia)
      return new Task(Bukkit.getAsyncScheduler()
        .runDelayed(Main.getInstance(), t -> runnable.run(), delayTicks, TimeUnit.MILLISECONDS));
    else
      return new Task(Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), runnable, delayTicks));
  }

	public static Task runLater(Runnable runnable, long delayTicks) {
		if (isFolia)
			return new Task(Bukkit.getGlobalRegionScheduler()
					.runDelayed(Main.getInstance(), t -> runnable.run(), delayTicks));

		else
			return new Task(Bukkit.getScheduler().runTaskLater(Main.getInstance(), runnable, delayTicks));
	}

	public static Task runTimer(Runnable runnable, long delayTicks, long periodTicks) {
		if (isFolia)
			return new Task(Bukkit.getGlobalRegionScheduler()
					.runAtFixedRate(Main.getInstance(), t -> runnable.run(), delayTicks < 1 ? 1 : delayTicks, periodTicks));

		else
			return new Task(Bukkit.getScheduler().runTaskTimer(Main.getInstance(), runnable, delayTicks, periodTicks));
	}

	public static boolean isFolia() {
		return isFolia;
	}

	public static class Task {

		private Object foliaTask;
		private BukkitTask bukkitTask;

		Task(Object foliaTask) {
			this.foliaTask = foliaTask;
		}

		Task(BukkitTask bukkitTask) {
			this.bukkitTask = bukkitTask;
		}

		public void cancel() {
			if (foliaTask != null)
				((ScheduledTask) foliaTask).cancel();
			else
				bukkitTask.cancel();
		}
	}*/
}


