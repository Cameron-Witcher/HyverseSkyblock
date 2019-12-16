package me.quickscythe.hyverse.skyblock.utils.islands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

	public Island(int id, int x, int z, IslandType type, Player owner) {
		this(id,x,z,type,owner.getUniqueId());
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

	protected void deactivate() {
		active = false;
		owner = null;

		IslandManager.saveIsland(this);
		destroy();
	}

	protected void reActivate(Player owner, IslandType type) {
		active = true;
		this.type = type;
		this.owner = owner.getUniqueId();
		IslandManager.saveIsland(this);
		Utils.getSkyblockPlayer(owner.getUniqueId()).addIsland(getID());
		build();
	}

	protected void destroy() {
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
	}

	public void build() {
		

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

	public void destroy_SOFT() {
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
	}

	public void setSpawnLocation(String loc) {
		setSpawnLocation(new Location(Utils.getSkyblockWorld(), Float.parseFloat(loc.split(":")[0]), Float.parseFloat(loc.split(":")[1]), Float.parseFloat(loc.split(":")[2])));
	}
	public void setSpawnLocation(Location loc) {
		spawnLoc = loc;
		IslandManager.saveIsland(this);
	}

}
