package me.quickscythe.hyverse.skyblock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import me.quickscythe.hyverse.skyblock.Main;
import me.quickscythe.hyverse.skyblock.utils.SkyblockPlayer;
import me.quickscythe.hyverse.skyblock.utils.Utils;
import me.quickscythe.hyverse.skyblock.utils.islands.IslandManager;
import me.quickscythe.hyversecore.utils.CoreUtils;

public class IslandCommand implements CommandExecutor {

	public IslandCommand(String cmd, Main plugin) {
		plugin.getCommand(cmd).setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = ((Player) sender);
			SkyblockPlayer pl = Utils.getSkyblockPlayer(player.getUniqueId());
			if (args.length == 0) {
				if (pl.getIslands().size() == 0) {
					player.setMetadata("islandtypeselector", new FixedMetadataValue(Main.getPlugin(), "true"));
					player.openInventory(IslandManager.getIslandTypeSelectorGUI(player));
				}
				if (pl.getIslands().size() == 1) {
					player.setMetadata("islandmenu", new FixedMetadataValue(Main.getPlugin(), "1"));
					player.openInventory(IslandManager.getIslandMenuGUI(player, (int)pl.getIslands().toArray()[0]));
				}
				if(pl.getIslands().size() > 1) {
					player.setMetadata("islandselector", new FixedMetadataValue(Main.getPlugin(), "true"));
					player.openInventory(IslandManager.getIslandSelectorGUI(player, pl.getIslands()));
				}

			}

//			IslandManager.nextIsland(player, IslandManager.types.get(0)).build();

		} else {
			sender.sendMessage(CoreUtils.colorize("&eSkyblock &7>&f Below is a list of admin commands:"));
		}
		return true;
	}
}
