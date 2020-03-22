package mc.rysty.heliosphereminigames.minigames;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import mc.rysty.heliosphereminigames.HelioSphereMinigames;
import mc.rysty.heliosphereminigames.utils.MessageUtils;

public class SpawnBuildProtection implements Listener {

	private HelioSphereMinigames plugin = HelioSphereMinigames.getInstance();
	private FileConfiguration config = plugin.getConfig();
	private String noPermMessage = MessageUtils.chat(config.getString("no-perm-message"));
	private String noBlockModifyMessage = MessageUtils.chat(config.getString("Spawn.no-block-modify"));
	private String playerWorldName;

	@EventHandler
	public void onBlockInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (e.getClickedBlock() == null) {
			return;
		}
		Block block = e.getClickedBlock();

		if (block.getType() == null) {
			return;
		}
		playerWorldName = p.getWorld().getName();

		if (playerWorldName.equalsIgnoreCase("MinigamesHub")) {
			if (p.getLocation().distanceSquared(Bukkit.getWorld("MinigamesHub").getSpawnLocation()) <= 250000) {
				if (p.getGameMode() != GameMode.CREATIVE) {
					e.setCancelled(true);
				}
			}
		}
		if (playerWorldName.equalsIgnoreCase("Bedwars")) {
			if (p.getLocation().distanceSquared(Bukkit.getWorld("Bedwars").getSpawnLocation()) <= 250000) {
				if (p.getGameMode() != GameMode.CREATIVE) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		boolean spawnBuild = p.hasPermission("hs.build.minigames");
		boolean creative = p.getGameMode().equals(GameMode.CREATIVE);
		playerWorldName = p.getWorld().getName();

		if (playerWorldName.equalsIgnoreCase("MinigamesHub")) {
			if (p.getLocation().distanceSquared(Bukkit.getWorld("MinigamesHub").getSpawnLocation()) <= 250000) {
				if (!creative || !spawnBuild) {
					e.setCancelled(true);
					if (!spawnBuild) {
						return;
					} else {
						p.sendMessage(noBlockModifyMessage);
					}
				}
			}
		}
		if (playerWorldName.equalsIgnoreCase("Bedwars")) {
			if (p.getLocation().distanceSquared(Bukkit.getWorld("Bedwars").getSpawnLocation()) <= 250000) {
				if (!creative || !spawnBuild) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		boolean spawnBuild = p.hasPermission("hs.build.minigames");
		boolean creative = p.getGameMode().equals(GameMode.CREATIVE);
		playerWorldName = p.getWorld().getName();

		if (playerWorldName.equalsIgnoreCase("MinigamesHub")) {
			if (p.getLocation().distanceSquared(Bukkit.getWorld("MinigamesHub").getSpawnLocation()) <= 250000) {
				if (!creative || !spawnBuild) {
					e.setCancelled(true);
					if (!spawnBuild) {
						return;
					} else {
						p.sendMessage(noBlockModifyMessage);
					}
				}
			}
		}
		if (playerWorldName.equalsIgnoreCase("Bedwars")) {
			if (p.getLocation().distanceSquared(Bukkit.getWorld("Bedwars").getSpawnLocation()) <= 250000) {
				if (!creative || !spawnBuild) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPickUp(EntityPickupItemEvent e) {
		LivingEntity p = e.getEntity();
		boolean spawnBuild = p.hasPermission("hs.build.minigames");
		boolean creative = ((HumanEntity) p).getGameMode().equals(GameMode.CREATIVE);
		playerWorldName = p.getWorld().getName();

		if (playerWorldName.equalsIgnoreCase("MinigamesHub")) {
			if (p.getLocation().distanceSquared(Bukkit.getWorld("MinigamesHub").getSpawnLocation()) <= 250000) {
				if (!creative || !spawnBuild) {
					e.setCancelled(true);
				}
			}
		}
		if (playerWorldName.equalsIgnoreCase("Bedwars")) {
			if (p.getLocation().distanceSquared(Bukkit.getWorld("Bedwars").getSpawnLocation()) <= 250000) {
				if (!creative || !spawnBuild) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onBarrierBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		playerWorldName = p.getWorld().getName();

		if (playerWorldName.equalsIgnoreCase("MinigamesHub") || playerWorldName.equalsIgnoreCase("Bedwars")) {
			if (e.getBlock().getType() == Material.BARRIER) {
				if (!p.hasPermission("hs.barrierbreak")) {
					e.setCancelled(true);
					p.sendMessage(noPermMessage);
				}
			}
		}
	}
}
