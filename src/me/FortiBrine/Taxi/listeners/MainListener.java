package me.FortiBrine.Taxi.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.FortiBrine.Taxi.main.Main;

public class MainListener implements Listener {

	private Main plugin;
	public MainListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (plugin.request.containsKey(p)) {
			plugin.request.remove(p);
		}
		if (plugin.requestPrice.containsKey(p)) {
			plugin.requestPrice.remove(p);
		}
		if (plugin.requestTime.containsKey(p)) {
			plugin.requestTime.remove(p);
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getClickedInventory()==null) return;
		if (!e.getClickedInventory().equals(plugin.inv)) return;
				
		e.setCancelled(true);
		
	}
	
}
