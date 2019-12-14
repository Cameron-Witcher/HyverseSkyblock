package me.quickscythe.hyverse.skyblock.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;

import me.quickscythe.hyverse.skyblock.Main;
import me.quickscythe.hyverse.skyblock.utils.Utils;
import me.quickscythe.hyverse.skyblock.utils.islands.Island;
import me.quickscythe.hyverse.skyblock.utils.islands.IslandManager;
import me.quickscythe.hyverse.skyblock.utils.islands.IslandType;

public class InventoryListener implements Listener {

	public InventoryListener(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {

		if (e.getWhoClicked().hasMetadata("islandselector")) {
			for (IslandType type : IslandManager.types) {
				if (e.getCurrentItem().getType().equals(type.getGUIItem().getType())) {
					e.getWhoClicked().removeMetadata("islandselector", Main.getPlugin());
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().setMetadata("islandmenu", new FixedMetadataValue(Main.getPlugin(),
							(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()))));
					e.getWhoClicked().openInventory(IslandManager.getIslandMenuGUI((Player) e.getWhoClicked(),
							Integer.parseInt(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()))));

					return;
				}
			}

		}
		if (e.getWhoClicked().hasMetadata("islandtypeselector")) {

			for (IslandType itype : IslandManager.types) {
				if (e.getCurrentItem().getType().equals(itype.getGUIItem().getType())) {
					Island is = IslandManager.nextIsland((Player) e.getWhoClicked(), itype);

					((Player) e.getWhoClicked()).teleport(is.getSpawnLocation());
					e.getWhoClicked().removeMetadata("islandtypeselector", Main.getPlugin());
					e.getWhoClicked().closeInventory();
					break;
				}
			}

			e.setCancelled(true);

		}
		if (e.getWhoClicked().hasMetadata("islandmenu")) {

			if (e.getCurrentItem() == null)
				return;

			if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
				IslandManager.destroyIsland(
						Integer.parseInt("" + e.getWhoClicked().getMetadata("islandmenu").get(0).value()));
				Utils.getSkyblockPlayer(e.getWhoClicked().getUniqueId()).removeIsland(
						Integer.parseInt("" + e.getWhoClicked().getMetadata("islandmenu").get(0).value()));
				e.getWhoClicked().teleport(Utils.getSpawnWorld().getSpawnLocation());
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().removeMetadata("islandmenu", Main.getPlugin());
			}

			if (e.getCurrentItem().getType().equals(Material.GRASS_BLOCK)) {
				IslandManager
						.getIsland(Integer.parseInt("" + e.getWhoClicked().getMetadata("islandmenu").get(0).value()))
						.destroy_SOFT();
				IslandManager
						.getIsland(Integer.parseInt("" + e.getWhoClicked().getMetadata("islandmenu").get(0).value()))
						.build();
				((Player) e.getWhoClicked()).teleport(IslandManager
						.getIsland(Integer.parseInt("" + e.getWhoClicked().getMetadata("islandmenu").get(0).value()))
						.getSpawnLocation());
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().removeMetadata("islandmenu", Main.getPlugin());

			}

			if (e.getCurrentItem().getType().equals(Material.WHEAT_SEEDS)) {
				e.getWhoClicked().setMetadata("islandtypeselector", new FixedMetadataValue(Main.getPlugin(), "true"));
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().openInventory(IslandManager.getIslandTypeSelectorGUI((Player) e.getWhoClicked()));
				e.getWhoClicked().removeMetadata("islandmenu", Main.getPlugin());

			}

			if (e.getCurrentItem().getType().equals(Material.RED_BED)) {
				if (IslandManager
						.getIsland(Integer.parseInt("" + e.getWhoClicked().getMetadata("islandmenu").get(0).value()))
						.getSpawnLocation() == null) {
//					boolean safe = false;
//					int sx = 0;
//					int sy = 0;
//					Coord ipos = IslandManager.getIslandLocation(Integer.parseInt("" + e.getWhoClicked().getMetadata("islandmenu").get(0).value()));
//					sx = ipos.getX()*IslandManager.PLOT_SIZE;
//					sy = ipos.getY()*IslandManager.PLOT_SIZE;
//					int l = 0;
//					while(!safe) {
//						
//					}
				} else {
					e.getWhoClicked().teleport(IslandManager
							.getIsland(
									Integer.parseInt("" + e.getWhoClicked().getMetadata("islandmenu").get(0).value()))
							.getSpawnLocation());
					e.getWhoClicked().removeMetadata("islandmenu", Main.getPlugin());
				}

			}
			e.setCancelled(true);

		}
	}
}
