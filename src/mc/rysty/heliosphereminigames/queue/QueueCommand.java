package mc.rysty.heliosphereminigames.queue;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import mc.rysty.heliosphereminigames.HelioSphereMinigames;
import mc.rysty.heliosphereminigames.utils.MessageUtils;

public class QueueCommand implements CommandExecutor, TabCompleter {

	private FileConfiguration queuesFile = HelioSphereMinigames.getQueuesFile().getData();

	public QueueCommand(HelioSphereMinigames plugin) {
		plugin.getCommand("queue").setExecutor(this);
		plugin.getCommand("queue").setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("queue")) {
			/*
			 * Permissions are checked upfront here as the player will be made a temporary
			 * operator when actually running commands regarding queues, which eliminates
			 * tedious permission checks.
			 */
			if (sender.hasPermission("hs.minigame.queue")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					String argumentOne = "";
					String argumentTwo = "";

					if (args.length > 0)
						argumentOne = args[0].toLowerCase();

					if (argumentOne.equals("join") || argumentOne.equals("leave") || argumentOne.equals("create")
							|| argumentOne.equals("delete")) {
						if (argumentOne.equals("leave"))
							QueueHelper.removePlayerFromQueue(player, true);
						else if (args.length == 2) {
							argumentTwo = args[1].toLowerCase();

							if (argumentOne.equals("create"))
								QueueHelper.createQueue(player, argumentTwo);
							else if (argumentOne.equals("delete"))
								QueueHelper.deleteQueue(player, argumentTwo);
							else if (argumentOne.equals("join"))
								QueueHelper.addPlayerToQueue(player, argumentTwo, true);
						} else
							MessageUtils.argumentError(sender, "/queue " + argumentOne + " <queue>");
					} else
						MessageUtils.argumentError(sender, "/queue <join|leave|create|delete>");
				} else
					MessageUtils.consoleError();
			} else
				MessageUtils.permissionError(sender);
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission("hs.minigame.queue")) {
			List<String> firstCompletions = new ArrayList<>();
			List<String> secondCompletions = new ArrayList<>();

			firstCompletions.add("join");
			firstCompletions.add("leave");
			firstCompletions.add("create");
			firstCompletions.add("delete");

			if (args.length == 1)
				return firstCompletions;
			else if (args.length == 2) {
				for (String queues : queuesFile.getConfigurationSection("queues").getKeys(false))
					secondCompletions.add(queues);

				if (!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("leave"))
					return secondCompletions;
			}
		}
		return null;
	}
}
