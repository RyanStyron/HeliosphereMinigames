package mc.rysty.heliosphereminigames.queue;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import mc.rysty.heliosphereminigames.HelioSphereMinigames;
import mc.rysty.heliosphereminigames.utils.MessageUtils;
import mc.rysty.heliosphereminigames.utils.QueuesFileManager;

public class QueueHelper {

	private static QueuesFileManager queuesManager = HelioSphereMinigames.getQueuesFile();
	private static FileConfiguration queuesFile = queuesManager.getData();

	protected static HashMap<String, Boolean> minigameStartingMap = new HashMap<String, Boolean>();

	protected static void createQueue(Player player, String queueName) {
		if (player.hasPermission("hs.minigame.create")) {
			Location location = player.getLocation();

			if (queuesFile.getString("queues." + queueName) != null)
				MessageUtils.configStringMessage(player, "queueCommand.queue-already-exists");
			else {
				queuesFile.set("queues." + queueName + ".running", true);
				queuesFile.set("queues." + queueName + ".playercount", 0);
				queuesFile.set("queues." + queueName + ".maxplayers", 12);
				queuesFile.set("queues." + queueName + ".playerthreshold", 2);
				queuesFile.set("queues." + queueName + ".queuetag", queueName + "Queue");
				queuesFile.set("queues." + queueName + ".gametag", queueName + "Game");
				queuesFile.set("queues." + queueName + ".countdown", 10);
				queuesFile.set("queues." + queueName + ".location.world", location.getWorld().getName());
				queuesFile.set("queues." + queueName + ".location.x", location.getX());
				queuesFile.set("queues." + queueName + ".location.y", location.getY());
				queuesFile.set("queues." + queueName + ".location.z", location.getZ());
				queuesManager.saveData();

				MessageUtils.configStringMessage(player, "queueCommand.queue-created", "<queue>", queueName);
			}
		} else
			MessageUtils.permissionError(player);
	}

	protected static void deleteQueue(Player player, String queueName) {
		if (player.hasPermission("hs.minigame.delete")) {
			if (queuesFile.getString("queues." + queueName) == null)
				MessageUtils.configStringMessage(player, "queueCommand.queue-doesnot-exist", "<queue>", queueName);
			else {
				queuesFile.set("queues." + queueName, null);
				queuesManager.saveData();

				MessageUtils.configStringMessage(player, "queueCommand.queue-deleted", "<queue>", queueName);
			}
		} else
			MessageUtils.permissionError(player);
	}

	protected static void addPlayerToQueue(Player player, String queueName, boolean sendJoinMessage) {
		UUID playerId = player.getUniqueId();

		if (queuesFile.getString("players." + playerId + ".currentQueue") == null) {
			if (queuesFile.getString("queues." + queueName) != null) {
				if (queuesFile.getBoolean("queues." + queueName + ".running")) {
					World world = Bukkit.getWorld(queuesFile.getString("queues." + queueName + ".location.world"));
					double x = queuesFile.getDouble("queues." + queueName + ".location.x");
					double y = queuesFile.getDouble("queues." + queueName + ".location.y");
					double z = queuesFile.getDouble("queues." + queueName + ".location.z");
					Location queueLocation = new Location(world, x, y, z);
					int playerCount = queuesFile.getInt("queues." + queueName + ".playercount");

					if (queuesFile.getInt("queues." + queueName + ".maxplayers") > playerCount + 1) {
						queuesFile.set("queues." + queueName + ".playercount", playerCount + 1);
						queuesFile.set("players." + playerId + ".currentQueue", queueName);
						queuesManager.saveData();

						player.teleport(queueLocation);
						player.getScoreboardTags().add(queuesFile.getString("queues." + queueName + ".queuetag"));

						if (sendJoinMessage)
							sendQueueJoinMessage(player, queueName);

						if (minigameStartingMap.get(queueName) == null)
							minigameStartingMap.put(queueName, false);

						if (!minigameStartingMap.get(queueName))
							if (playerCount + 1 >= queuesFile.getInt("queues." + queueName + ".playerthreshold"))
								minigameStartingMap.put(queueName, true);
					} else
						MessageUtils.configStringMessage(player, "queueCommand.game-running-error");
				} else
					MessageUtils.configStringMessage(player, "queueCommand.game-running-error");
			} else
				MessageUtils.configStringMessage(player, "queueCommand.queue-doesnot-exist", "<queue>", queueName);
		} else
			MessageUtils.configStringMessage(player, "queueCommand.current-queue-error");
	}

	protected static void removePlayerFromQueue(Player player, boolean sendLeaveMessage) {
		UUID playerId = player.getUniqueId();

		if (queuesFile.getString("players." + playerId + ".currentQueue") != null) {
			String playerQueue = queuesFile.getString("players." + playerId + ".currentQueue");
			int queuePlayerCount = queuesFile.getInt("queues." + playerQueue + ".playercount");

			queuesFile.set("queues." + playerQueue + ".playercount", queuePlayerCount - 1);
			queuesFile.set("players." + playerId, null);
			queuesManager.saveData();

			MessageUtils.configStringMessage(player, "queueCommand.player-left-queue");
			player.teleport(player.getWorld().getSpawnLocation());
			player.getScoreboardTags().remove(queuesFile.getString("queues." + playerQueue + ".queuetag"));

			if (sendLeaveMessage)
				sendQueueLeaveMessage(player, playerQueue);

			if (minigameStartingMap.get(playerQueue) == null)
				minigameStartingMap.put(playerQueue, false);

			if (minigameStartingMap.get(playerQueue))
				if (queuePlayerCount - 1 < queuesFile.getInt("queues." + playerQueue + ".playerthreshold"))
					minigameStartingMap.put(playerQueue, false);
		} else
			MessageUtils.configStringMessage(player, "queueCommand.leave-error-message");
	}

	private static void sendQueueJoinMessage(Player joiner, String queue) {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers())
			if (onlinePlayer.getScoreboardTags().contains(queue + "Queue"))
				MessageUtils.configStringMessage(onlinePlayer, "queueCommand.joined-queue", "<player>",
						joiner.getDisplayName(), "<playercount>",
						queuesFile.getString("queues." + queue + ".playercount"), "<maxplayercount>",
						queuesFile.getString("queues." + queue + ".maxplayers"));
	}

	private static void sendQueueLeaveMessage(Player leaver, String queue) {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers())
			if (onlinePlayer.getScoreboardTags().contains(queue + "Queue"))
				MessageUtils.configStringMessage(onlinePlayer, "queueCommand.left-queue", "<player>",
						leaver.getDisplayName(), "<playercount>",
						queuesFile.getString("queues." + queue + ".playercount"), "<maxplayercount>",
						queuesFile.getString("queues." + queue + ".maxplayers"));
	}
}