package mc.rysty.heliosphereminigames.queue;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import mc.rysty.heliosphereminigames.HelioSphereMinigames;
import mc.rysty.heliosphereminigames.utils.QueuesFileManager;

public class QueueFileUpdater implements Listener {

	private QueuesFileManager queuesManager = QueuesFileManager.getInstance();
	private FileConfiguration queuesFile = queuesManager.getData();

	public QueueFileUpdater(HelioSphereMinigames plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		QueueHelper.removePlayerFromQueue(event.getPlayer(), true);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		checkPlayerInQueue(event.getPlayer());
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		checkPlayerInQueue(event.getPlayer());
	}

	private void checkPlayerInQueue(Player player) {
		UUID playerId = player.getUniqueId();
		Location location = player.getLocation();
		World world = location.getWorld();

		if (queuesFile.getString("players." + playerId + ".currentQueue") != null) {
			String queueName = queuesFile.getString("players." + playerId + ".currentQueue");
			World queueWorld = Bukkit.getWorld(queuesFile.getString("queues." + queueName + ".location.world"));
			double queueX = queuesFile.getDouble("queues." + queueName + ".location.x");
			double queueY = queuesFile.getDouble("queues." + queueName + ".location.y");
			double queueZ = queuesFile.getDouble("queues." + queueName + ".location.z");
			Location queueLocation = new Location(queueWorld, queueX, queueY, queueZ);

			if (world != queueWorld)
				QueueHelper.removePlayerFromQueue(player, true);

			if (world == queueWorld)
				if (location.distanceSquared(queueLocation) > 225)
					QueueHelper.removePlayerFromQueue(player, true);
		}
	}
}
