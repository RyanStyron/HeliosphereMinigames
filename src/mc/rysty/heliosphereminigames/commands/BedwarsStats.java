package mc.rysty.heliosphereminigames.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

import mc.rysty.heliosphereminigames.HelioSphereMinigames;
import mc.rysty.heliosphereminigames.utils.MessageUtils;

public class BedwarsStats implements CommandExecutor {

	private HelioSphereMinigames plugin = HelioSphereMinigames.getInstance();
	private FileConfiguration config = plugin.getConfig();

	public BedwarsStats(HelioSphereMinigames plugin) {
		plugin.getCommand("bedwarsstats").setExecutor(this);
	}

	private int games = 0;
	private int wins = 0;
	private int losses = 0;
	private int kills = 0;
	private int deaths = 0;
	private int bedsBroken = 0;
	private int bedwarsScore = 0;
	private double wlr = 0;
	private double kdr = 0;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("bedwarsstats")) {
			Player target = null;

			if (args.length > 0)
				if (sender.hasPermission("hs.minigames.stats.others"))
					target = Bukkit.getPlayer(args[0]);
				else
					MessageUtils.message(sender, config.getString("no-perm-message"));
			else if (sender instanceof Player)
				target = (Player) sender;

			if (target == null) {
				MessageUtils.message(sender, config.getString("player-offline-message"));
			} else {
				try {
					PluginManager pluginManager = Bukkit.getPluginManager();
					UUID targetId = target.getUniqueId();

					if (pluginManager.getPlugin("BedWars") != null) {
						PlayerStatistic statistics = Main.getPlayerStatisticsManager().loadStatistic(targetId);
						games = statistics.getGames();
						wins = statistics.getWins();
						losses = statistics.getLoses();
						wlr = (wins / losses != 0 ? wins / losses : 0);
						kills = statistics.getKills();
						deaths = statistics.getDeaths();
						bedsBroken = statistics.getDestroyedBeds();
						bedwarsScore = statistics.getScore();
						kdr = (kills / deaths != 0 ? kills / deaths : 0);

						sendBedwarsStatistics(sender, target);
					}
				} catch (Exception exception) {
					MessageUtils.message(sender, ChatColor.RED
							+ "An error occurred when running this command. Please contact an adminstrator and alert them of this issue.");
					exception.printStackTrace();
				}
			}
		}
		return false;
	}

	private void sendBedwarsStatistics(CommandSender sender, Player target) {
		ChatColor gold = ChatColor.GOLD;
		ChatColor yellow = ChatColor.YELLOW;
		ChatColor darkAqua = ChatColor.DARK_AQUA;
		ChatColor green = ChatColor.GREEN;
		ChatColor red = ChatColor.RED;

		MessageUtils.message(sender,
				darkAqua + "==" + gold + "Bedwars Statistics: " + yellow + target.getDisplayName() + darkAqua + "==");
		MessageUtils.message(sender, gold + "Score: " + yellow + bedwarsScore);
		MessageUtils.message(sender, gold + "Games: " + yellow + games);
		MessageUtils.message(sender, gold + "Wins: " + green + wins);
		MessageUtils.message(sender, gold + "Losses: " + red + losses);
		MessageUtils.message(sender, gold + "Kills: " + green + kills);
		MessageUtils.message(sender, gold + "Deaths: " + red + deaths);
		MessageUtils.message(sender, gold + "Beds Broken: " + green + bedsBroken);
		MessageUtils.message(sender, gold + "Wins/Losses: " + yellow + wlr);
		MessageUtils.message(sender, gold + "Kills/Deaths: " + yellow + kdr);
	}
}
