package mc.rysty.heliosphereminigames.minigames;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import mc.rysty.heliosphereminigames.HelioSphereMinigames;

public class SpawnProtection implements Listener {

	public SpawnProtection(HelioSphereMinigames plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof Player)
			if (!canMinigamesBuild((Player) entity))
				event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!canMinigamesBuild(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!canMinigamesBuild(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityPickupItem(EntityPickupItemEvent event) {
		LivingEntity entity = event.getEntity();

		if (entity instanceof Player)
			if (!canMinigamesBuild((Player) entity))
				event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (!canMinigamesBuild(event.getPlayer()))
			event.setCancelled(true);
	}

	private boolean canMinigamesBuild(Player player) {
		World world = player.getWorld();
		GameMode gamemode = player.getGameMode();
		boolean canSpawnBuild = player.hasPermission("hs.spawnbuild");

		if (world.equals(Bukkit.getWorld("Bedwars")))
			if (player.getLocation().distanceSquared(world.getSpawnLocation()) <= 5625)
				if (gamemode != GameMode.CREATIVE || !canSpawnBuild)
					return false;
		return true;
	}
}
