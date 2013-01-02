package net.idea.restnet.c.task;

import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TaskStorageWithNotifier extends TaskStorage<String> {
	protected ScheduledThreadPoolExecutor notificationTimer;
	protected AlertsNotifier notifier;
	
	public TaskStorageWithNotifier(String name, Logger log) {
		super(name, Logger.getLogger(TaskStorageWithNotifier.class.getName()));
		notifier = createAlertsNotifier();
		if (notifier!=null) {
			TimerTask notificationTasks  = new TimerTask() {
				
				@Override
				public void run() {
					try {
						String task = notifier.call();
						logger.log(Level.INFO, task);
					} catch (Exception x) {
						logger.log(Level.SEVERE, "Error launching notifications!", x);
					}
					
				}
			};
			notificationTimer = new ScheduledThreadPoolExecutor(1);
			notificationTimer.scheduleWithFixedDelay(notificationTasks, 0, 12,TimeUnit.HOURS);
		}
	}
	protected abstract AlertsNotifier createAlertsNotifier();

	@Override
	public synchronized void shutdown(long timeout, TimeUnit unit)
			throws Exception {
		if ((notificationTimer!=null)  && !notificationTimer.isShutdown()) {
			notificationTimer.awaitTermination(timeout, unit);
			notificationTimer.shutdown();
		}	
		super.shutdown(timeout, unit);
	}

}
