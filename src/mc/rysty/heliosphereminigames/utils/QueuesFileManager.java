package mc.rysty.heliosphereminigames.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class QueuesFileManager {

	private QueuesFileManager() {
	}

	static QueuesFileManager instance = new QueuesFileManager();

	public static QueuesFileManager getInstance() {
		return instance;
	}

	Plugin p;

	FileConfiguration config;
	File cfile;

	FileConfiguration data;
	File dfile;

	public void setup(Plugin p) {
		cfile = new File(p.getDataFolder(), "config.yml");
		config = p.getConfig();

		if (!p.getDataFolder().exists()) {
			p.getDataFolder().mkdir();
		}

		dfile = new File(p.getDataFolder(), "queues.yml");

		if (!dfile.exists()) {
			try {
				dfile.createNewFile();
			} catch (IOException e) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create queues.yml!");
			}
		}

		data = YamlConfiguration.loadConfiguration(dfile);
	}

	public FileConfiguration getData() {
		return data;
	}

	public void saveData() {
		try {
			data.save(dfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save queues.yml!");
		}
	}

	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(dfile);
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void saveConfig() {
		try {
			config.save(cfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml!");
		}
	}

	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(cfile);
	}

	public PluginDescriptionFile getDesc() {
		return p.getDescription();
	}

}
