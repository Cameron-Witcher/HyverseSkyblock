package me.quickscythe.hyverse.skyblock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import me.quickscythe.hyverse.skyblock.commands.IslandCommand;
import me.quickscythe.hyverse.skyblock.listeners.InventoryListener;
import me.quickscythe.hyverse.skyblock.utils.islands.IslandManager;


public class Main extends JavaPlugin {
	
	private static Main plugin;
	
	
	
	int attempt = 1;
	int maxattempts = 3;

	public void onEnable() {
		if(!getServer().getPluginManager().isPluginEnabled("HyverseCore") || !getServer().getPluginManager().isPluginEnabled("VoidGenerator")){
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l" + getDescription().getName() + " &f>&7 Can't find HyverseCore. Trying again.. Attempt " + attempt + " out of " + maxattempts));
			attempt+=1;
			if(attempt==maxattempts+1){
				Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l" + getDescription().getName() + " &f>&7 Couldn't find HyverseCore after " + maxattempts + " tries. Plugin will not load."));
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			Bukkit.getScheduler().runTaskLater(this, new Runnable(){

				@Override
				public void run() {
					onEnable();
				}
				
			}, 20*3);
			return;
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l" + getDescription().getName() + " &f>&7 Found HyverseCore! Loading plugin.."));
		}
		plugin = this;
		
		IslandManager.registerIslands();
		
		
		new InventoryListener(this);
		new IslandCommand("island", this);
		
		
		
		
		
		
		
	}
	
	
	
	public static Main getPlugin(){
		return plugin;
	}
}