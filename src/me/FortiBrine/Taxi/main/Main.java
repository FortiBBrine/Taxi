package me.FortiBrine.Taxi.main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import me.FortiBrine.Taxi.commands.MainCommand;
import me.FortiBrine.Taxi.listeners.MainListener;
import me.FortiBrine.Taxi.utils.ItemUtil;
import me.FortiBrine.Taxi.utils.VaultManager;

public class Main extends JavaPlugin {

	public Map<Player, String> taxi = new HashMap<Player, String>();
	public Inventory inv = Bukkit.createInventory(null, 27, this.getConfig().getString("message.title"));
	public Map<Player, Long> requestTime = new HashMap<Player, Long>();
	public Map<Player, Player> request = new HashMap<Player, Player>();
	public Map<Player, Integer> requestPrice = new HashMap<Player, Integer>();
	
	@Override
	public void onEnable() {
		this.getLogger().info("Plugin created by https://vk.com/animationb");
		
		File config = new File(this.getDataFolder()+File.separator+"config.yml");
		if (!config.exists()) {
			this.getConfig().options().copyDefaults(true);
			this.saveDefaultConfig();
		}
		
		VaultManager.init();
		Bukkit.getPluginManager().registerEvents(new MainListener(this), this);
		this.getCommand("taxi").setExecutor(new MainCommand(this));
	}
	
	public void reloadInventory() {
		inv.clear();
		
		for (Player player : this.taxi.keySet()) {
			inv.addItem(ItemUtil.generate(this, player, this.taxi.get(player)));
		}
	}
}
