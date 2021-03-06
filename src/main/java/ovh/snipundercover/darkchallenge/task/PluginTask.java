package ovh.snipundercover.darkchallenge.task;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.reflections.Reflections;
import ovh.snipundercover.darkchallenge.DarkChallenge;
import ovh.snipundercover.darkchallenge.logging.PluginLogger;

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * An interface for creating recurring Bukkit tasks with ease.
 */
public abstract class PluginTask {
	private static final Map<Class<? extends PluginTask>, PluginTask> TASKS = new Hashtable<>();
	
	private static final PluginLogger LOGGER = PluginLogger.getLogger(PluginTask.class);
	
	final long delay  = 0L;
	final long period = 1L;
	
	static {
		LOGGER.info("Initializing plugin tasks...");
		final Set<Class<? extends PluginTask>> availableTaskClasses =
				new Reflections(PluginTask.class.getPackageName()).getSubTypesOf(PluginTask.class);
		LOGGER.fine("Found {0} task classes.", availableTaskClasses.size());
		
		AtomicInteger count = new AtomicInteger();
		availableTaskClasses.forEach(clazz -> {
			try {
				LOGGER.fine("Attempting to initialize {0}...", clazz.getSimpleName());
				TASKS.put(clazz, clazz.getDeclaredConstructor().newInstance());
				count.getAndIncrement();
				LOGGER.fine("... done");
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
				//should never happen
				LOGGER.log(Level.WARNING,
				           "An error occurred while initializing task %s:".formatted(clazz.getSimpleName()), e
				);
			} catch (NoSuchMethodException e) {
				//should never happen
				LOGGER.log(Level.WARNING,
				           "An error occurred while initializing task {0}: "
						           + "the class does not have an accessible non-params constructor.",
				           clazz.getSimpleName()
				);
			}
		});
		LOGGER.info("Initialized {0}/{1} tasks successfully.",
		            count.get(),
		            availableTaskClasses.size()
		);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends PluginTask> T getTask(Class<T> clazz) {
		return (T) TASKS.get(clazz); //no worries, it's fine
	}
	
	public static void startAll() {
		LOGGER.info("Starting plugin tasks...");
		AtomicInteger count = new AtomicInteger();
		TASKS.values().forEach(task -> {
			try {
				task.start();
				count.getAndIncrement();
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Failed to start task %s:".formatted(task.getClass().getSimpleName()), e);
			}
		});
		LOGGER.info("Started {0}/{1} plugin tasks.",
		            count.get(),
		            TASKS.size()
		);
	}
	
	public static void stopAll() {
		LOGGER.info("Stopping plugin tasks...");
		AtomicInteger count = new AtomicInteger();
		TASKS.values().forEach(task -> {
			try {
				task.stop();
				count.getAndIncrement();
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Failed to stop task %s:".formatted(task.getClass().getSimpleName()), e);
			}
		});
		LOGGER.info("Stopped {0}/{1} plugin tasks.",
		            count.get(),
		            TASKS.size()
		);
	}
	
	//non-static
	@Getter
	protected BukkitTask task;
	
	//override methods
	void init() {
		LOGGER.fine("{0} has no init() implementation, skipping.", this.getClass().getSimpleName());
	}
	
	abstract void run();
	
	public void reload() {
		LOGGER.fine("{0} has no reload() implementation, skipping.", this.getClass().getSimpleName());
	}
	
	void cleanup() {
		LOGGER.fine("{0} has no cleanup() implementation, skipping.", this.getClass().getSimpleName());
	}
	
	//instance methods
	@SuppressWarnings("UnusedReturnValue")
	public BukkitTask start() {
		LOGGER.fine("Starting task {0}...", getClass().getSimpleName());
		init();
		try {
			BukkitTask task = new BukkitRunnable() {
				@Override
				public void run() {
					if (isCancelled()) return;
					PluginTask.this.run();
				}
			}.runTaskTimer(DarkChallenge.getInstance(), this.delay, this.period);
			LOGGER.fine("...done. Task ID: {0}", task.getTaskId());
			return this.task = task;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING,
			           "An error occurred while starting task %s:".formatted(getClass().getSimpleName()),
			           e
			);
			throw e;
		}
	}
	
	public void stop() {
		LOGGER.fine("Stopping task {0}...", getClass().getSimpleName());
		try {
			task.cancel();
			cleanup();
			task = null;
			LOGGER.fine("...done.");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING,
			           "An error occurred while stopping task %s:".formatted(getClass().getSimpleName()),
			           e
			);
			throw e;
		}
	}
}
