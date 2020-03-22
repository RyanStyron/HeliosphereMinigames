package mc.rysty.heliosphereminigames.queue;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import mc.rysty.heliosphereminigames.HelioSphereMinigames;
import mc.rysty.heliosphereminigames.utils.MessageUtils;
import mc.rysty.heliosphereminigames.utils.QueuesFileManager;

public class QueuesFunctions {

	protected static HelioSphereMinigames plugin = HelioSphereMinigames.getInstance();
	protected static FileConfiguration config = plugin.getConfig();
	protected static QueuesFileManager queuesManager = QueuesFileManager.getInstance();
	protected static FileConfiguration queuesFile = queuesManager.getData();

	protected static void createQueue(Player player, String queueName) {
		if (player.hasPermission("hs.minigame.create")) {
			Location location = player.getLocation();
			World playerWorld = location.getWorld();

			// Check if the queue already exists
			if (queuesFile.getString("queues." + queueName) != null) {
				player.sendMessage(MessageUtils.chat(config.getString("queueCommand.queue-already-exists")));
			} else {
				queuesFile.set("queues." + queueName + ".running", true);
				queuesFile.set("queues." + queueName + ".playercount", 0);
				queuesFile.set("queues." + queueName + ".maxplayers", 12);
				queuesFile.set("queues." + queueName + ".playerthreshold", 2);
				queuesFile.set("queues." + queueName + ".queuetag", "in" + queueName.toUpperCase() + "queue");
				queuesFile.set("queues." + queueName + ".gametag", "in" + queueName.toUpperCase() + "game");
				queuesFile.set("queues." + queueName + ".countdown", 10);
				queuesFile.set("queues." + queueName + ".location.world", playerWorld.getName());
				queuesFile.set("queues." + queueName + ".location.x", location.getX());
				queuesFile.set("queues." + queueName + ".location.y", location.getY());
				queuesFile.set("queues." + queueName + ".location.z", location.getZ());
				queuesManager.saveData();

				MessageUtils.message(player,
						config.getString("queueCommand.queue-created").replaceAll("<queue>", queueName));
			}
		} else {
			MessageUtils.message(player, config.getString("no-perm-message"));
		}
	}

	/**
	 * This function sets the given player's queue data to the queue joined, and
	 * sends the players in the current queue a message that they have joined.
	 */
	protected static void addPlayerToQueue(Player player, String queueName) {
		UUID playerId = player.getUniqueId();

		if (queuesFile.getString("players." + playerId + ".currentQueue") == null) {
			if (queuesFile.getString("queues." + queueName) != null) {
				if (queuesFile.getString("queues." + queueName + ".running") == "true") {
					World world = Bukkit.getWorld(queuesFile.getString("queues." + queueName + ".location.world"));
					double locX = queuesFile.getDouble("queues." + queueName + ".location.x");
					double locY = queuesFile.getDouble("queues." + queueName + ".location.y");
					double locZ = queuesFile.getDouble("queues." + queueName + ".location.z");
					Location queueLocation = new Location(world, locX, locY, locZ);
					int playerCount = queuesFile.getInt("queues." + queueName + ".playercount");

					queuesFile.set("queues." + queueName + ".playercount", playerCount + 1);
					queuesFile.set("players." + playerId + ".currentQueue", queueName);
					queuesManager.saveData();

					player.teleport(queueLocation);

					for (Player queuePlayers : Bukkit.getOnlinePlayers()) {
						sendQueueJoinMessage(player, queuePlayers, queueLocation, queueName);
					}
				} else {
					MessageUtils.message(player, config.getString("queueCommand.game-running-error"));
				}
			} else {
				MessageUtils.message(player, config.getString("queueCommand.queue-doesnot-exist"));
			}
		} else {
			MessageUtils.message(player, config.getString("queueCommand.current-queue-error"));
		}
	}

	/**
	 * This function sets the given player's queue data to null, and sends the
	 * players in the current queue a message that they have left.
	 */
	protected static void removePlayerFromQueue(Player player) {
		UUID playerId = player.getUniqueId();
		World playerWorld = player.getWorld();

		if (queuesFile.getString("players." + playerId + ".currentQueue") != null) {
			String playerCurrentQueue = "players." + playerId + ".currentQueue";
			String playerCurrentQueueString = queuesFile.getString(playerCurrentQueue);
			String queueFileString = "queues." + playerCurrentQueueString;
			String queueFilePlayerCount = queueFileString + ".playercount";
			int playerCount = queuesFile.getInt(queueFilePlayerCount);
			World world = Bukkit.getWorld(queuesFile.getString(queueFileString + ".location.world"));
			double locX = queuesFile.getDouble(queueFileString + ".location.x");
			double locY = queuesFile.getDouble(queueFileString + ".location.y");
			double locZ = queuesFile.getDouble(queueFileString + ".location.z");
			Location queueLocation = new Location(world, locX, locY, locZ);

			queuesFile.set(queueFilePlayerCount, playerCount - 1);
			queuesFile.set(playerCurrentQueue, null);
			queuesManager.saveData();

			MessageUtils.message(player, config.getString("queueCommand.player-left-queue"));
			player.teleport(playerWorld.getSpawnLocation());

			for (Player queuePlayers : Bukkit.getOnlinePlayers()) {
				sendQueueLeaveMessage(player, queuePlayers, queueLocation, queueFilePlayerCount,
						playerCurrentQueueString);
			}
		} else {
			MessageUtils.message(player, config.getString("queueCommand.leave-error-message"));
		}
	}

	/**
	 * This function sends the players in the current queue that the given player
	 * has joined.
	 */
	private static void sendQueueJoinMessage(Player joiner, Player queuePlayers, Location queueLocation,
			String queueName) {
		String playerDisplayname = joiner.getDisplayName();
		World world = queuePlayers.getWorld();

		if (queuePlayers.getWorld() == world) {
			if (queuePlayers.getLocation().distanceSquared(queueLocation) <= 225) {
				queuePlayers.sendMessage(MessageUtils.chat(config.getString("queueCommand.joined-queue")
						.replaceAll("<player>", playerDisplayname)
						.replaceAll("<playercount>", queuesFile.getString("queues." + queueName + ".playercount"))
						.replaceAll("<maxplayercount>", queuesFile.getString("queues." + queueName + ".maxplayers"))));
			}
		}
	}

	/**
	 * This function sends the players in the current queue a message that the given
	 * player has left.
	 */
	private static void sendQueueLeaveMessage(Player leaver, Player queuePlayers, Location queueLocation,
			String queueFilePlayerCount, String queueName) {
		String playerDisplayname = leaver.getDisplayName();
		World world = queuePlayers.getWorld();

		if (queuePlayers.getWorld() == world) {
			if (queuePlayers.getLocation().distanceSquared(queueLocation) <= 225) {
				queuePlayers.sendMessage(MessageUtils.chat(config.getString("queueCommand.left-queue")
						.replaceAll("<player>", playerDisplayname).replaceAll("<playercount>",
								queuesFile.getString(queueFilePlayerCount).replaceAll("<maxplayercount>",
										queuesFile.getString("queues." + queueName + ".maxplayers")))));
			}
		}
	}

}
