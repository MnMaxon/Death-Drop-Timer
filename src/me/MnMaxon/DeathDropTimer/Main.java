package me.MnMaxon.DeathDropTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;

public final class Main extends JavaPlugin implements Listener {
	public static String dataFolder;
	public static Main plugin;
	public static Map<Item, Integer> items = new HashMap<Item, Integer>();
	public static boolean running = false;

	@Override
	public void onEnable() {
		plugin = this;
		dataFolder = this.getDataFolder().getAbsolutePath();
		setupConfig();
		Config.Load(dataFolder + "/Data");
		getServer().getPluginManager().registerEvents(this, this);
	}

	public YamlConfiguration setupConfig() {
		cfgSetter("Time_Q_Drop", true);
		cfgSetter("Time", 120);
		cfgSetter("OnlyArmorDrops", false);
		cfgSetter("OnlySoupDrops", false);
		return Config.Load(dataFolder + "/Config.yml");
	}

	public void cfgSetter(String path, Object value) {
		YamlConfiguration cfg = Config.Load(dataFolder + "/Config.yml");
		if (cfg.get(path) == null) {
			cfg.set(path, value);
			Config.Save(cfg, dataFolder + "/Config.yml");
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player p = (Player) sender;
		if (args.length == 0 || args[0].equals("help")) {
			displayHelp(p);
		} else if (args[0].equals("set") && args.length == 2) {
			try {
				Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				displayHelp(p);
				return false;
			}
			YamlConfiguration cfg = setupConfig();
			cfg.set("Time", Integer.parseInt(args[1]));
			Config.Save(cfg, dataFolder + "/Config.yml");
		}
		return false;
	}

	private void displayHelp(Player p) {
		p.sendMessage(ChatColor.GOLD + "===== " + ChatColor.DARK_PURPLE + "Death Drop Timer Help" + ChatColor.GOLD
				+ " =====");
		p.sendMessage(ChatColor.DARK_AQUA + "/DDT" + ChatColor.DARK_PURPLE + " - Displays Help");
		p.sendMessage(ChatColor.DARK_AQUA + "/DDT Set <TIME>" + ChatColor.DARK_PURPLE + " - Sets item despawn time");
	}

	public void runner() {
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				ArrayList<Item> toRemove = new ArrayList<Item>();
				for (Map.Entry<Item, Integer> entry : items.entrySet()) {
					entry.setValue(entry.getValue() - 1);
					if (entry.getValue() <= 0) {
						toRemove.add(entry.getKey());
					}
				}
				for (Item i : toRemove) {
					items.remove(i);
					i.remove();
				}
				if (items.isEmpty()) {
					running = false;
				} else
					runner();
			}
		}, 20L);
	}

	@EventHandler
	public void onPickUp(PlayerPickupItemEvent e) {
		if (items.containsKey(e.getItem()))
			items.remove(e.getItem());
	}

	@EventHandler
	public void onDespawn(ItemDespawnEvent e) {
		if (items.containsKey(e.getEntity()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		YamlConfiguration cfg = setupConfig();
		if (cfg.getBoolean("Time_Q_Drop")) {
			items.put(e.getItemDrop(), cfg.getInt("Time"));
			if (!running) {
				running = true;
				runner();
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		YamlConfiguration cfg = setupConfig();
		List<ItemStack> toRemove = new ArrayList<ItemStack>();
		if (cfg.getBoolean("OnlyArmorDrops") && cfg.getBoolean("OnlySoupDrops")) {
			for (ItemStack i : e.getDrops())
				if (!(i.getType().equals(Material.LEATHER_BOOTS) || i.getType().equals(Material.LEATHER_CHESTPLATE)
						|| i.getType().equals(Material.LEATHER_HELMET) || i.getType().equals(Material.LEATHER_LEGGINGS)
						|| i.getType().equals(Material.IRON_BOOTS) || i.getType().equals(Material.IRON_CHESTPLATE)
						|| i.getType().equals(Material.IRON_HELMET) || i.getType().equals(Material.IRON_LEGGINGS)
						|| i.getType().equals(Material.GOLD_BOOTS) || i.getType().equals(Material.GOLD_CHESTPLATE)
						|| i.getType().equals(Material.GOLD_HELMET) || i.getType().equals(Material.GOLD_LEGGINGS)
						|| i.getType().equals(Material.DIAMOND_BOOTS)
						|| i.getType().equals(Material.DIAMOND_CHESTPLATE)
						|| i.getType().equals(Material.DIAMOND_HELMET) || i.getType().equals(Material.DIAMOND_LEGGINGS)
						|| i.getType().equals(Material.CHAINMAIL_BOOTS)
						|| i.getType().equals(Material.CHAINMAIL_CHESTPLATE)
						|| i.getType().equals(Material.CHAINMAIL_HELMET)
						|| i.getType().equals(Material.CHAINMAIL_LEGGINGS) || i.getType()
						.equals(Material.MUSHROOM_SOUP)))
					toRemove.add(i);
		} else if (cfg.getBoolean("OnlyArmorDrops")) {
			for (ItemStack i : e.getDrops())
				if (!(i.getType().equals(Material.LEATHER_BOOTS) || i.getType().equals(Material.LEATHER_CHESTPLATE)
						|| i.getType().equals(Material.LEATHER_HELMET) || i.getType().equals(Material.LEATHER_LEGGINGS)
						|| i.getType().equals(Material.IRON_BOOTS) || i.getType().equals(Material.IRON_CHESTPLATE)
						|| i.getType().equals(Material.IRON_HELMET) || i.getType().equals(Material.IRON_LEGGINGS)
						|| i.getType().equals(Material.GOLD_BOOTS) || i.getType().equals(Material.GOLD_CHESTPLATE)
						|| i.getType().equals(Material.GOLD_HELMET) || i.getType().equals(Material.GOLD_LEGGINGS)
						|| i.getType().equals(Material.DIAMOND_BOOTS)
						|| i.getType().equals(Material.DIAMOND_CHESTPLATE)
						|| i.getType().equals(Material.DIAMOND_HELMET) || i.getType().equals(Material.DIAMOND_LEGGINGS)
						|| i.getType().equals(Material.CHAINMAIL_BOOTS)
						|| i.getType().equals(Material.CHAINMAIL_CHESTPLATE)
						|| i.getType().equals(Material.CHAINMAIL_HELMET) || i.getType().equals(
						Material.CHAINMAIL_LEGGINGS)))
					toRemove.add(i);
		} else if (cfg.getBoolean("OnlySoupDrops")) {
			for (ItemStack i : e.getDrops())
				if (!(i.getType().equals(Material.MUSHROOM_SOUP)))
					toRemove.add(i);
		}
		if (!toRemove.isEmpty())
			for (ItemStack item : toRemove)
				e.getDrops().remove(item);
		for (ItemStack i : e.getDrops())
			items.put(e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), i), cfg.getInt("Time"));
		e.getDrops().clear();
		if (!running) {
			running = true;
			runner();
		}
	}
}