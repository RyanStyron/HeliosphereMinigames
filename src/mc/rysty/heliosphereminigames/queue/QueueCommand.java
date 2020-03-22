package mc.rysty.heliosphereminigames.queue;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import mc.rysty.heliosphereminigames.HelioSphereMinigames;
import mc.rysty.heliosphereminigames.utils.MessageUtils;

public class QueueCommand implements CommandExecutor {

	private HelioSphereMinigames plugin = HelioSphereMinigames.getInstance();
	private FileConfiguration config = plugin.getConfig();

	public QueueCommand(HelioSphereMinigames plugin) {
		plugin.getCommand("queue").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("queue")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;

				if (args.length == 0) {
					player.sendMessage(MessageUtils.chat(config.getString("queueCommand.argument-error")));
				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("leave")
							|| args[0].equalsIgnoreCase("join")) {
						if (args[0].equalsIgnoreCase("create")) {
							if (player.hasPermission("hs.minigame.create")) {
								MessageUtils.message(player, config.getString("queueCommand.create-argument-error"));
							} else {
								MessageUtils.message(player, config.getString("no-perm-message"));
							}
						} else if (args[0].equalsIgnoreCase("join")) {
							MessageUtils.message(player, config.getString("queueCommand.join-argument-error"));
						} else if (args[0].equalsIgnoreCase("leave")) {
							QueuesFunctions.removePlayerFromQueue(player);
						}
					} else {
						MessageUtils.message(player, config.getString("queueCommand.argument-provided-error"));
					}
				} else if (args.length == 2) {
					String queueName = args[1].toLowerCase();

					if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("leave")
							|| args[0].equalsIgnoreCase("join")) {
						if (args[0].equalsIgnoreCase("create")) {
							QueuesFunctions.createQueue(player, queueName);
						} else if (args[0].equalsIgnoreCase("join")) {
							QueuesFunctions.addPlayerToQueue(player, queueName);
						} else if (args[0].equalsIgnoreCase("leave")) {
							MessageUtils.message(player, config.getString("queueCommand.leave-argument-error"));
						}
					} else {
						MessageUtils.message(player, config.getString("queueCommand.argument-provided-error"));
					}
				}
			} else {
				MessageUtils.message(sender, config.getString("console-error-message"));
			}
		}
		return false;
	}
}
