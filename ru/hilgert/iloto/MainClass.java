package ru.hilgert.iloto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin {

	private static Random random = new Random();

	static Map<String, ItemStack> items = new HashMap<String, ItemStack>();
	static FileConfiguration config;

	public void onEnable() {
		config = getConfig();

		initConfig();
		reloadItems();
		run();
		Bukkit.getLogger().info("[iLoto] enabled!");
		super.onEnable();
	}

	private void initConfig() {
		config.addDefault(
				"bcast",
				"&6Игрок: &3{PLAYER}&6 получил награду: &3{ITEM_NAME}[x{ITEM_AMOUNT}]&6 в ежеминутной лотерее");
		config.addDefault("items", new String[] { "1:64:&7Камень" });
		config.addDefault("requiredPlayers", 1);
		config.addDefault("time", 60);
		config.options().copyDefaults(true);
		saveConfig();
	}

	public void onDisable() {
		items.clear();
		Bukkit.getLogger().info("[iLoto] [DEBUG] items list cleared!");
		Bukkit.getLogger().info("[iLoto] disabled!");
		super.onDisable();
	}

	public void run() {

		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {

			public void run() {

				if (getRandomPlayer() != null) {
					Player p = getRandomPlayer();
					ItemStack item = getRandomItem();
					p.getInventory().addItem(item);
					Bukkit.broadcastMessage(ChatColor
							.translateAlternateColorCodes(
									'&',
									config.getString("bcast")
											.replace("{PLAYER}", p.getName())
											.replace("{ITEM_NAME}",
													getItemName(item))
											.replace("{ITEM_AMOUNT}",
													item.getAmount() + "")));

				}

			}

		}, config.getInt("time") * 20, config.getInt("time") * 20);

	}

	public static void reloadItems() {
		Bukkit.getLogger().info("[iLoto] [DEBUG] items list creating!");
		items.clear();
		for (String s : config.getStringList("items")) {
			String[] itemInfo = s.split(":");
			if (itemInfo.length >= 3) {
				items.put(
						itemInfo[2],
						new ItemStack(Integer.parseInt(itemInfo[0]), Integer
								.parseInt(itemInfo[1])));
			} else {
				Bukkit.getLogger().severe(
						"[iLoto] Unknown item: " + s
								+ ". use: itemID:itemAmount:itemName");
			}
		}
		Bukkit.getLogger().info("[iLoto] [DEBUG] items list created!");
	}

	private ItemStack getRandomItem() {
		List<String> keys = new ArrayList<String>(items.keySet());
		String randomItemKey = keys.get(random.nextInt(keys.size()));
		return items.get(randomItemKey);
	}

	private Player getRandomPlayer() {
		int players = Bukkit.getOnlinePlayers().length;
		if (players >= config.getInt("requiredPlayers")) {
			return Bukkit.getOnlinePlayers()[random.nextInt(players)];
		} else {
			return null;
		}
	}

	private static String getItemName(ItemStack item) {
		for (Object o : items.keySet()) {
			if (items.get(o).equals(item)) {
				return o.toString();
			}
		}
		return null;
	}

}
