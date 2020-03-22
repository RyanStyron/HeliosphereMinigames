package mc.rysty.heliosphereminigames.minigames;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class SpawnDamageProtection implements Listener {

	private String playerWorldName;

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Entity e = event.getEntity();
		playerWorldName = e.getWorld().getName();

		if (playerWorldName.equalsIgnoreCase("MinigamesHub")) {
			if (e.getLocation().distanceSquared(Bukkit.getWorld("MinigamesHub").getSpawnLocation()) <= 250000) {
				if (e instanceof Player) {
					event.setCancelled(true);
				}
			}
		}
		if (playerWorldName.equalsIgnoreCase("Bedwars")) {
			if (e.getLocation().distanceSquared(Bukkit.getWorld("Bedwars").getSpawnLocation()) <= 250000) {
				if (e instanceof Player) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onFallDamage(EntityDamageEvent e) {
		Entity p = e.getEntity();
		playerWorldName = p.getWorld().getName();

		if (playerWorldName.equalsIgnoreCase("MinigamesHub")) {
			if (p.getLocation().distanceSquared(Bukkit.getWorld("MinigamesHub").getSpawnLocation()) <= 250000) {
				e.setCancelled(true);
			}
		}
		if (playerWorldName.equalsIgnoreCase("Bedwars")) {
			if (p.getLocation().distanceSquared(Bukkit.getWorld("Bedwars").getSpawnLocation()) <= 250000) {
				e.setCancelled(true);
			}
		}
	}
}
