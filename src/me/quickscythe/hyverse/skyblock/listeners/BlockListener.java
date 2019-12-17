package me.quickscythe.hyverse.skyblock.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

import me.quickscythe.hyverse.skyblock.Main;

public class BlockListener implements Listener {

	public BlockListener(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onBlockGenerate(BlockFormEvent e) {
		e.getBlock().setType(Material.STONE);
	}
}
