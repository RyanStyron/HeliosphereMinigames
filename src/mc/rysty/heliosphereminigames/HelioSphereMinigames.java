package mc.rysty.heliosphereminigames;

import org.bukkit.plugin.java.JavaPlugin;

import mc.rysty.heliosphereminigames.minigames.SpawnProtection;
import mc.rysty.heliosphereminigames.queue.QueueCommand;
import mc.rysty.heliosphereminigames.queue.QueueFileUpdater;
import mc.rysty.heliosphereminigames.queue.QueueScheduler;
import mc.rysty.heliosphereminigames.utils.QueuesFileManager;

public class HelioSphereMinigames extends JavaPlugin {

	private static HelioSphereMinigames plugin;

	public static HelioSphereMinigames getInstance() {
		return plugin;
	}

	private static QueuesFileManager queuesFileManager = QueuesFileManager.getInstance();

	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		queuesFileManager.setup(this);

		/* General */
		new SpawnProtection(this);

		/* Queue-related */
		QueueScheduler.enableScheduler();
		new QueueCommand(this);
		new QueueFileUpdater(this);

		System.out.println("HS-Minigames enabled");
	}

	public void onDisable() {
		System.out.println("HS-Minigames disabled");
	}

	public static QueuesFileManager getQueuesFile() {
		return queuesFileManager;
	}
}
