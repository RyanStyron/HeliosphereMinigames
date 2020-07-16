package mc.rysty.heliosphereminigames;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import mc.rysty.heliosphereminigames.minigames.CaptureTheFlag;
import mc.rysty.heliosphereminigames.minigames.SpawnBuildProtection;
import mc.rysty.heliosphereminigames.minigames.SpawnDamageProtection;
import mc.rysty.heliosphereminigames.queue.QueueCommand;
import mc.rysty.heliosphereminigames.queue.UpdateQueueFile;
import mc.rysty.heliosphereminigames.utils.QueuesFileManager;

public class HelioSphereMinigames extends JavaPlugin {

	public static HelioSphereMinigames plugin;

	public static HelioSphereMinigames getInstance() {
		return plugin;
	}

	PluginManager pm = Bukkit.getPluginManager();
	QueuesFileManager settings = QueuesFileManager.getInstance();

	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		settings.setup(this);

		/** Game-related. */
		pm.registerEvents(new SpawnBuildProtection(), this);
		pm.registerEvents(new SpawnDamageProtection(), this);
		pm.registerEvents(new CaptureTheFlag(), this);
		
		/** Queue-related */
		new QueueCommand(this);
		new UpdateQueueFile(this);

		System.out.println("HS-Minigames enabled");
	}

	public void onDisable() {
		System.out.println("HS-Minigames disabled");
	}
}
