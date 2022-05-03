package me.FortiBrine.Taxi.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.FortiBrine.Taxi.main.Main;

public class ItemUtil {
	
	public static ItemStack generate(Main plugin, Player player, String location) {
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(plugin.getConfig().getString("message.item.title").replace("%player", player.getName()).replace("%location", location));
		
		List<String> lore = plugin.getConfig().getStringList("message.item.lore");
		
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, lore.get(i).replace("%player", player.getName()).replace("%location", location));
		}
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}

}
