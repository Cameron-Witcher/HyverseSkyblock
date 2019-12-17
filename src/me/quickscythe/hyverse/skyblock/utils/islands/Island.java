package me.quickscythe.hyverse.skyblock.utils.islands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.quickscythe.hyverse.skyblock.Main;
import me.quickscythe.hyverse.skyblock.utils.Schematic;
import me.quickscythe.hyverse.skyblock.utils.SkyBlockData;
import me.quickscythe.hyverse.skyblock.utils.SkyblockPlayer;
import me.quickscythe.hyverse.skyblock.utils.Utils;
import me.quickscythe.hyversecore.utils.CoreUtils;

public class Island {
	private int x = 0;
	private int z = 0;
	private int id = 0;
	private IslandType type;
	private boolean active = false;

	private UUID owner = null;
	private Location lgloc = null;
	private List<ItemStack> startingItems = new ArrayList<>();
	private File file;
	private Location spawnLoc = null;
	private Map<UUID, List<ItemStack>> inventories = new HashMap<>();

	public Island(int id, int x, int z, IslandType type, Player owner) {
		this(id, x, z, type, owner.getUniqueId());
//		this.id = id;
//		this.x = x;
//		this.z = z;
//		this.type = type;
//		// TODO DO NOT DO THIS IN ANY RELEASE
//		lgloc = new Location(Utils.getSkyblockWorld(), x, 30, z);
//		active = true;
//		if (owner != null) {
//			
//			this.owner = owner.getUniqueId();
//			Utils.getSkyblockPlayer(this.owner).addIsland(getID());
//		}
//		else
//			active = false;
//		this.file = new File(Main.getPlugin().getDataFolder() + "/islands/" + id + ".yml");
//		if (!file.exists()) {
//			createFiles();
//			createDemoFile();
//		}
//		registerIsland();
	}

	public Island(int id, int x, int z, IslandType type, UUID owner) {
		this.id = id;
		this.x = x;
		this.z = z;
		this.type = type;
		// TODO DO NOT DO THIS IN ANY RELEASE
		lgloc = new Location(Utils.getSkyblockWorld(), x, 30, z);
		active = true;
		this.owner = owner;
		this.file = new File(Main.getPlugin().getDataFolder() + "/islands/" + id + ".yml");
		if (!file.exists()) {
			createFiles();
			createDemoFile();
		}
		registerIsland();
	}

	public Island leave(Player player) {
		inventories.get(player.getUniqueId()).clear();
		player.sendMessage(CoreUtils.colorize("&eSkyblock &7>&f You have left Island " + id));
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null)
				continue;
			if (!item.getType().equals(Material.AIR)) {
				inventories.get(player.getUniqueId()).add(item);
			}
		}
		CoreUtils.debug("Saved inv on leave. id: " + id + " Size: " + inventories.get(player.getUniqueId()).size());
		save();
		return this;
	}

	public Island join(Player player) {
		if (!inventories.containsKey(player.getUniqueId()))
			inventories.put(player.getUniqueId(), new ArrayList<ItemStack>());
		SkyblockPlayer pl = Utils.getSkyblockPlayer(player.getUniqueId());

		if (!pl.getIsland().equals(id + "")) {

			if (!pl.getIsland().equals(""))
				IslandManager.getIsland(Integer.parseInt(pl.getIsland())).leave(player);
			player.sendMessage(CoreUtils.colorize("&eSkyblock &7>&f Joining Island " + id));
			player.getInventory().clear();

			if (inventories.get(player.getUniqueId()).size() == 0) {
				givePlayerStartingItems(player);

			} else {
				returnPlayerItems(player);
			}

			pl.setIsland(getID() + "");
			Utils.getWorldBorderAPI().setBorder(player, IslandManager.PLOT_SIZE,
					getLocation_LG().clone().add((IslandManager.PLOT_SIZE / 2), 0, (IslandManager.PLOT_SIZE / 2)));
		}
		player.sendMessage(CoreUtils.colorize("&eSkyblock &7>&f Teleporting to Island: " + id));
		player.teleport(spawnLoc);
		return this;

	}

	private void returnPlayerItems(Player player) {
		for (ItemStack i : inventories.get(player.getUniqueId())) {
			if (i == null) {
				continue;
			}
			player.getInventory().addItem(i.clone());
		}

	}

	private void givePlayerStartingItems(Player player) {
		player.sendMessage(CoreUtils.colorize("&aHere, you look like you could use a little help."));
		for (ItemStack i : startingItems) {
			CoreUtils.debug("2-1");
			if (i == null)
				continue;
			player.getInventory().addItem(i.clone());
		}
	}

	public UUID getOwner() {
		return owner;
	}

	public void registerIsland() {
		try {
			FileConfiguration fc = YamlConfiguration.loadConfiguration(this.file);
			if (fc.isSet("Items")) {
				for (String s : fc.getStringList("Items")) {
					if (s.contains("CustomItem")) {

						startingItems.add(CoreUtils.getItem(s.split(":")[1]));
					} else {
						startingItems.add(CoreUtils.decryptItemStack(s));
					}
				}
			}

		} catch (NullPointerException e) {
			createDemoFile();
		}
		IslandManager.saveIsland(this);

	}

	public void createFiles() {
		new File(Main.getPlugin().getDataFolder() + "/islands").mkdir();
	}

	public void createDemoFile() {
		createFiles();
		File demo = new File(Main.getPlugin().getDataFolder() + "/islands/" + id + ".yml");

		try {
			demo.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileConfiguration fc = YamlConfiguration.loadConfiguration(demo);
		List<String> sl = new ArrayList<>();
		sl.add("ICE:0:0");
		sl.add("LAVA_BUCKET:0:0");
		sl.add("BUCKET:0:0");
//		sl.add("WHEAT_SEEDS");
		sl.add("CARROT:0:0");
		sl.add("POTATO:0:0");
		sl.add("SUGAR_CANE:0:0");

		fc.set("Items", sl);

		for (String s : sl) {
			if (s.contains("CustomItem")) {

				startingItems.add(CoreUtils.getItem(s.split(":")[1]));
			} else {
				startingItems.add(CoreUtils.decryptItemStack(s));
			}
		}

		try {
			fc.save(demo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public IslandType getType() {
		return type;
	}

	public Location getLocation_LG() {
		return lgloc;
	}

	public boolean isActive() {
		return active;
	}

	public Island regen() {
		destroy().build();
		for(UUID uid : inventories.keySet()) {
			if(Bukkit.getPlayer(uid) == null) continue;
			if(Utils.getSkyblockPlayer(uid).getIsland().equals(id+"")) {
				join(Bukkit.getPlayer(uid));
			}
		}
		return this;
	}

	protected Island deactivate() {
		Utils.getSkyblockPlayer(owner).removeIsland(id);
		Utils.saveSkyblockPlayer(owner);
		active = false;
		

		for(UUID uid : inventories.keySet()) {
			if(Bukkit.getPlayer(uid) == null) continue;
			if(Utils.getSkyblockPlayer(uid).getIsland().equals(id+"")) {
				leave(Bukkit.getPlayer(uid));
				Bukkit.getPlayer(uid).teleport(Utils.getSpawnWorld().getSpawnLocation());
			}
		}
		owner = null;
		
		IslandManager.saveIsland(this);
		destroy();
		return this;
	}

	protected Island reActivate(Player owner, IslandType type) {
		active = true;
		this.type = type;
		this.owner = owner.getUniqueId();
		IslandManager.saveIsland(this);
		Utils.getSkyblockPlayer(owner.getUniqueId()).addIsland(getID());
		build();
		return this;
	}

	public Island destroy() {
		for (Entry<UUID, List<ItemStack>> entry : inventories.entrySet()) {
			entry.getValue().clear();

		}
		Schematic schem = Utils.loadSchematic(type.getFileName());

		for (int x = 0; x < schem.getWidth(); ++x) {
			for (int y = 0; y < schem.getHeight(); ++y) {
				for (int z = 0; z < schem.getLength(); ++z) {
					Block block = new Location(Utils.getSkyblockWorld(), this.x + x, y + IslandManager.getHeight(),
							this.z + z).getBlock();
					block.setType(Material.AIR);
				}
			}
		}
		return this;
	}

	public Island build() {

//		getLocation_LG().getBlock().setType(Material.RED_WOOL);

		Schematic schem = Utils.loadSchematic(type.getFileName());

		for (int x = 0; x < schem.getWidth(); ++x) {
			for (int y = 0; y < schem.getHeight(); ++y) {
				for (int z = 0; z < schem.getLength(); ++z) {
					int index = (y * schem.getLength() + z) * schem.getWidth() + x;
					Block block = new Location(Utils.getSkyblockWorld(), this.x + x, y + IslandManager.getHeight(),
							this.z + z).getBlock();
					if (((SkyBlockData) (schem.getData().get(schem.getBlocks().toArray()[index]))).getMaterial()
							.equals(Material.AIR))
						continue;
					if (((SkyBlockData) (schem.getData().get(schem.getBlocks().toArray()[index]))).getMaterial()
							.equals(Material.RED_WOOL)) {
						this.spawnLoc = block.getLocation();
						continue;
					}
					block.setType(
							((SkyBlockData) (schem.getData().get(schem.getBlocks().toArray()[index]))).getMaterial());

				}
			}
		}
		save();
		return this;

	}

	private void save() {
		IslandManager.saveIsland(this);
	}

	public Location getSpawnLocation() {
		return spawnLoc;
	}

	public int getID() {
		return id;
	}

	public Island destroy_SOFT() {
		Schematic schem = Utils.loadSchematic(type.getFileName());

		for (int x = 0; x < schem.getWidth(); ++x) {
			for (int y = 0; y < schem.getHeight(); ++y) {
				for (int z = 0; z < schem.getLength(); ++z) {
					Block block = new Location(Utils.getSkyblockWorld(), this.x + x, y + IslandManager.getHeight(),
							this.z + z).getBlock();
					block.setType(Material.AIR);
				}
			}
		}
		return this;
	}

	public void setSpawnLocation(String loc) {
		setSpawnLocation(new Location(Utils.getSkyblockWorld(), Float.parseFloat(loc.split(":")[0]),
				Float.parseFloat(loc.split(":")[1]), Float.parseFloat(loc.split(":")[2])));
	}

	public void setSpawnLocation(Location loc) {
		spawnLoc = loc;
		IslandManager.saveIsland(this);
	}

	public Map<UUID, List<ItemStack>> getInventories() {
		return inventories;
	}

//	public void addInventory(UUID uid, Inventory inv) {
//		inventories.put(uid, inv);
//	}

}
