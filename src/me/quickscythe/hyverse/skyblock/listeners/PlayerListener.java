package me.quickscythe.hyverse.skyblock.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.quickscythe.hyverse.skyblock.Main;
import me.quickscythe.hyverse.skyblock.utils.Utils;

public class PlayerListener implements Listener {

	public PlayerListener(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().teleport(Utils.getSpawnWorld().getSpawnLocation());
	}
}
