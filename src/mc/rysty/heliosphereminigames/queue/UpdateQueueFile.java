package mc.rysty.heliosphereminigames.queue;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import mc.rysty.heliosphereminigames.HelioSphereMinigames;
import mc.rysty.heliosphereminigames.utils.QueuesFileManager;

public class UpdateQueueFile implements Listener {

	private QueuesFileManager queuesManager = QueuesFileManager.getInstance();
	private FileConfiguration queuesFile = queuesManager.getData();

	public UpdateQueueFile(HelioSphereMinigames plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		QueuesFunctions.removePlayerFromQueue(player);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		UUID playerId = player.getUniqueId();

		if (queuesFile.getString("players." + playerId + ".currentQueue") != null) {
			World playerWorld = player.getWorld();
			Location playerLocation = player.getLocation();
			String queueName = queuesFile.getString("players." + playerId + ".currentQueue");
			World queueWorld = Bukkit.getWorld(queuesFile.getString("queues." + queueName + ".location.world"));
			double queueX = queuesFile.getDouble("queues." + queueName + ".location.x");
			double queueY = queuesFile.getDouble("queues." + queueName + ".location.y");
			double queueZ = queuesFile.getDouble("queues." + queueName + ".location.z");
			Location queueLocation = new Location(queueWorld, queueX, queueY, queueZ);

			if (queueWorld == playerWorld)
				if (playerLocation.distanceSquared(queueLocation) > 225)
					QueuesFunctions.removePlayerFromQueue(player);
		}
	}
}
