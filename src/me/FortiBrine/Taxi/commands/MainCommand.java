package me.FortiBrine.Taxi.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.FortiBrine.Taxi.main.Main;
import me.FortiBrine.Taxi.utils.VaultManager;

public class MainCommand implements CommandExecutor {
	
	private List<String> subcommands = new ArrayList<String>();
	
	private Main plugin;
	public MainCommand(Main plugin) {
		this.plugin = plugin;
		this.subcommands.add("list");
		this.subcommands.add("price");
		this.subcommands.add("call");
		this.subcommands.add("yes");
		this.subcommands.add("no");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			/*sender.sendMessage("DEBUG");
			for (Player player : plugin.taxi.keySet()) {
				sender.sendMessage(player.getName()+": "+plugin.taxi.get(player));
			}*/
			
			return true;
		}
		Player player = (Player) sender;
		FileConfiguration config = plugin.getConfig();
		
		if (args.length < 1) {
			List<String> usage = config.getStringList("message.usage");
			
			for (String s : usage) {
				player.sendMessage(s);
			}
			
			return true;
		}
		if (!this.subcommands.contains(args[0].toLowerCase())) {
			List<String> usage = config.getStringList("message.usage");
			
			for (String s : usage) {
				player.sendMessage(s);
			}
			
			return true;
		}
		
		if (args[0].equalsIgnoreCase("call")) {
			
			if (args.length < 2) {
				List<String> usage = config.getStringList("message.usage");
				
				for (String s : usage) {
					player.sendMessage(s);
				}
				
				return true;
			}
			
			if (plugin.taxi.containsKey(player)) {
				player.sendMessage(config.getString("message.alreadyCall"));
				return true;
			}
			
			List<String> arguments = new ArrayList<String>();
			for (String s : args) arguments.add(s);
			arguments.remove(0);
			
			String location = String.join(" ", arguments);
			
			plugin.taxi.put(player, location);
			plugin.reloadInventory();
			player.sendMessage(config.getString("message.call"));

			return true;
		}
		
		if (args[0].equalsIgnoreCase("list")) {
			if (!player.hasPermission(plugin.getDescription().getPermissions().get(0))) {
				player.sendMessage(config.getString("message.noPermissions"));
				return true;
			}
			
			player.closeInventory();
			player.openInventory(plugin.inv);
			return true;
		}
		if (args[0].equalsIgnoreCase("price")) {
			if (args.length < 3) {
				List<String> usage = config.getStringList("message.usage");
				
				for (String s : usage) {
					player.sendMessage(s);
				}
				
				return true;
			}
			
			int price = 0;
			try {
				price = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				player.sendMessage(config.getString("message.numberFormatException"));
				return true;
			}
			
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
			if (!offlinePlayer.isOnline()) {
				player.sendMessage(config.getString("message.noOnline"));
				return true;
			}
			
			Player p = Bukkit.getPlayer(args[1]);
			
			if (p.getLocation().distance(player.getLocation()) > 5) {
				player.sendMessage(config.getString("message.distance"));
				return true;
			}
			
			player.sendMessage(config.getString("message.sendPrice").replace("%player", p.getName()).replace("%money", ""+price));
			p.sendMessage(config.getString("message.sendPricePlayer").replace("%player", player.getName()).replace("%money", ""+price));
			
			plugin.requestTime.put(p, System.currentTimeMillis()+60*1000);
			plugin.request.put(player, p);
			plugin.requestPrice.put(p, price);
			
			return true;
		}
		if (args[0].equalsIgnoreCase("yes")) {
			if (!plugin.requestTime.containsKey(player)) {
				player.sendMessage(config.getString("message.noRequest"));
				return true;
			}
			long time = System.currentTimeMillis();
			long time2 = plugin.requestTime.get(player);
			
			if (time > time2) {
				player.sendMessage(config.getString("message.noRequest"));
				return true;
			}
			int price = plugin.requestPrice.get(player);
			Player p = null;
			for (Player PLAYER : plugin.request.keySet()) {
				if (plugin.request.get(PLAYER) == player) {
					p = PLAYER;
					break;
				}
			}
			
			if (!VaultManager.takeMoney(player, price)) {
				player.sendMessage(config.getString("message.noMoney"));
				return true;
			}
			VaultManager.giveMoney(p, price);
			player.sendMessage(config.getString("message.payRequest").replace("%money", ""+price));
			p.sendMessage(config.getString("message.payRequestPlayer").replace("%money", ""+price));
			
			plugin.taxi.remove(player);
			plugin.request.remove(p);
			plugin.requestPrice.remove(player);
			plugin.requestTime.remove(player);
			plugin.reloadInventory();
			return true;
		}
		if (args[0].equalsIgnoreCase("no")) {
			if (!plugin.requestTime.containsKey(player)) {
				player.sendMessage(config.getString("message.noRequest"));
				return true;
			}
			long time = System.currentTimeMillis();
			long time2 = plugin.requestTime.get(player);
			
			if (time > time2) {
				player.sendMessage(config.getString("message.noRequest"));
				return true;
			}
			int price = plugin.requestPrice.get(player);
			Player p = null;
			for (Player PLAYER : plugin.request.keySet()) {
				if (plugin.request.get(PLAYER) == player) {
					p = PLAYER;
					break;
				}
			}
			
			player.sendMessage(config.getString("message.declineRequest").replace("%money", ""+price));
			p.sendMessage(config.getString("message.declineRequestPlayer").replace("%money", ""+price));
			
			plugin.taxi.remove(player);
			plugin.request.remove(p);
			plugin.requestPrice.remove(player);
			plugin.requestTime.remove(player);
			plugin.reloadInventory();
			return true;
		}
		
		return true;
	}

}
