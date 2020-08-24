package mc.rysty.heliosphereminigames.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import mc.rysty.heliosphereminigames.HelioSphereMinigames;

public class MessageUtils {

	private static HelioSphereMinigames plugin = HelioSphereMinigames.getInstance();
	private static FileConfiguration config = plugin.getConfig();

	public static String convertChatColors(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static void message(CommandSender sender, String message) {
		sender.sendMessage(convertChatColors(message));
	}

	public static void configStringMessage(CommandSender sender, String configString) {
		message(sender, config.getString(configString));
	}

	public static void configStringMessage(CommandSender sender, String configString, String regex,
			String replacement) {
		message(sender, config.getString(configString).replace(regex, replacement));
	}

	public static void configStringMessage(CommandSender sender, String configString, String regex, String replacement,
			String regex2, String replacement2) {
		message(sender, config.getString(configString).replace(regex, replacement).replace(regex2, replacement2));
	}

	public static void configStringMessage(CommandSender sender, String configString, String regex, String replacement,
			String regex2, String replacement2, String regex3, String replacement3) {
		message(sender, config.getString(configString).replace(regex, replacement).replace(regex2, replacement2)
				.replace(regex3, replacement3));
	}

	public static void consoleError() {
		configStringMessage(Bukkit.getConsoleSender(), "console-error-message");
	}

	public static void permissionError(CommandSender sender) {
		configStringMessage(sender, "no-perm-message");
	}

	public static void argumentError(CommandSender sender, String usage) {
		configStringMessage(sender, "argument-error", "<usage>", usage);
	}
}
