package net.nielsbwashere.src.VillagerShop;

import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Core extends JavaPlugin implements Listener {
	String prefix = ChatColor.BLUE + "[" + ChatColor.AQUA + "VillagerShop" + ChatColor.BLUE + "] ";
	Economy e;

	public void onEnable() {
		if (!setupEconomy()) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				List<String> strings = getConfig().getStringList("VillagerShop.Shops");
				if (strings == null)
					return;
				loop: for (String s : strings) {
					if (s == null)
						continue;
					String uuid = getConfig().getString(s + ".UUID");
					Location l = getLocation(s + ".Location");
					if (l == null)
						continue;
					if (uuid == null)
						return;
					for (World w : getServer().getWorlds()) {
						if (w == null)
							continue;
						for (Entity e : w.getEntitiesByClass(Villager.class)) {
							if (e == null)
								continue;
							if (e.getUniqueId().toString().equals(uuid)) {
								e.teleport(l);
								continue loop;
							}
						}
					}
				}
			}
		}, 0, 10);
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player))
			return;
		if (!(e.getEntity() instanceof Villager))
			return;
		List<String> strings = getConfig().getStringList("VillagerShop.Shops");
		if (strings == null)
			return;
		for (String s : strings) {
			String uuid = getConfig().getString(s + ".UUID");
			if (uuid == null)
				return;
			for (Entity en : e.getEntity().getWorld().getEntitiesByClasses(Villager.class)) {
				if (en.getUniqueId().toString().equals(uuid)) {
					e.setCancelled(true);
					return;
				}
			}
		}
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			e = economyProvider.getProvider();
		}
		return (e != null);
	}

	protected Location getLocation(String where) {
		String loc = getConfig().getString(where);
		String[] locs = loc.split(",");
		World w = getServer().getWorld(locs[0]);
		int i = Integer.parseInt(locs[1]), j = Integer.parseInt(locs[2]), k = Integer.parseInt(locs[3]);
		if (w == null || (i == 0 && j == 0 && k == 0))
			return null;
		return new Location(w, i, j, k);
	}

	public void setLocation(String where, Location loc) {
		getConfig().set(where,
				loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
		saveConfig();
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked().getType() != EntityType.VILLAGER)
			return;
		List<String> strings = getConfig().getStringList("VillagerShop.Shops");
		if (strings == null)
			return;
		String shop = "";
		for (String s : strings) {
			if (s == null)
				continue;
			String uuid = getConfig().getString(s + ".UUID");
			if (e.getRightClicked().getUniqueId().toString().equals(uuid)) {
				shop = s;
				break;
			}
		}
		if (shop.equals(""))
			return;
		e.setCancelled(true);
		Inventory i = getInventory("ShopInventories." + shop);
		if (i == null)
			i = Bukkit.createInventory(null, 3 * 9, ChatColor.RESET + shop + " (S)");
		e.getPlayer().openInventory(i);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("CreateShop")) {
			if (!sender.hasPermission("VillagerShop.CreateShop") && !sender.isOp()) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "You are not permitted to use this command!");
				return false;
			}
			if (args.length != 1) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "Incorrect usage: /createShop <name>");
				return false;
			}
			String name = args[0];
			if (name.contains("&"))
				name.replace("&", "§");
			if (shopExists(name)) {
				sender.sendMessage(prefix + ChatColor.DARK_RED
						+ "This shop already exists, if you want to override it, please use /DeleteShop <name> first!");
				return false;
			}
			if (!createShop(name)) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "Couldn't create that shop!");
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "Only players can use this command!");
				return false;
			}
			sender.sendMessage(prefix + ChatColor.GOLD + "Succesfully created a shop!");
			spawnVillager(name, ((Player) sender).getLocation());
			setLocation(name + ".Location", ((Player) sender).getLocation());
		} else if (label.equalsIgnoreCase("DeleteShop")) {
			if (!sender.hasPermission("VillagerShop.DeleteShop") && !sender.isOp()) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "You are not permitted to use this command!");
				return false;
			}
			if (args.length != 1) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "Incorrect usage: /deleteShop <name>");
				return false;
			}
			String name = args[0];
			if (!shopExists(name)) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "This shop doesn't exist!");
				return false;
			}
			if (!deleteShop(name)) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "Couldn't delete that shop!");
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "Only players can use this command!");
				return false;
			}
			sender.sendMessage(prefix + ChatColor.GOLD + "Succesfully deleted that shop!");
			deleteVillager(name, ((Player) sender).getWorld());
			getConfig().set("ShopInventories" + name, null);
			getConfig().set(name, null);
			saveConfig();
		} else if (label.equalsIgnoreCase("AddItem")) {
			if (!sender.hasPermission("VillagerShop.AddItem") && !sender.isOp()) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "You are not permitted to use this command!");
				return false;
			}
			if (args.length != 2) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "Incorrect usage: /addItem <name> <price>");
				return false;
			}
			String name = args[0];
			if (!shopExists(name)) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "This shop doesn't exist!");
				return false;
			}
			double price = 0;
			try {
				price = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "The price you put in isn't a valid price!");
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "Only players can use this command!");
				return false;
			}
			Player p = (Player) sender;
			if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "You must have an actual item in your hand!");
				return false;
			}
			Inventory i = getInventory("ShopInventories." + name);
			if (i == null)
				i = Bukkit.createInventory(null, 3 * 9, ChatColor.RESET + name + " (S)");
			int store = -1, j = -1;
			for (ItemStack is : i.getContents()) {
				j++;
				if (is == null || is.getType() == Material.AIR) {
					store = j;
					break;
				}
			}
			if (store < 0) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "This inventory is full! Please create a new shop!");
				return false;
			}
			ItemStack is = p.getItemInHand().clone();
			ItemMeta im = is.getItemMeta();
			List<String> lore = im.getLore();
			if (lore == null)
				lore = new ArrayList<String>();
			lore.add(ChatColor.GREEN + "Costs " + price + "$!");
			im.setLore(lore);
			is.setItemMeta(im);
			i.setItem(store, is);
			String where = "ShopInventories." + name;
			getConfig().set(where + ".Prices." + store, price);
			saveConfig();
			saveInventory(where, i);
			sender.sendMessage(prefix + ChatColor.GOLD + "Succesfully added that item!");
		} else if (label.equalsIgnoreCase("DeleteItem")) {
			if (!sender.hasPermission("VillagerShop.DeleteItem") && !sender.isOp()) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "You are not permitted to use this command!");
				return false;
			}
			if (args.length != 1) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "Incorrect usage: /deleteItem <shopName>");
				return false;
			}
			String name = args[0];
			if (!shopExists(name)) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "This shop doesn't exist!");
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "Only players can use this command!");
				return false;
			}
			Player p = (Player) sender;
			if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "You must have an actual item in your hand!");
				return false;
			}
			Inventory i = getInventory("ShopInventories." + name);
			if (i == null)
				i = Bukkit.createInventory(null, 3 * 9, ChatColor.RESET + name + " (S)");
			int store = -1, j = -1;
			for (ItemStack is2 : i.getContents()) {
				if (is2 == null || is2.getType() == Material.AIR)
					continue;
				ItemStack is = is2.clone();
				ItemMeta im = is.getItemMeta();
				if (im.hasLore()) {
					List<String> lore = im.getLore();
					List<String> remove = new ArrayList<String>();
					for (String s : lore)
						if (s.startsWith(ChatColor.GREEN + "Costs "))
							remove.add(s);
					for (String s : remove)
						lore.remove(s);
					remove.clear();
					im.setLore(lore);
					is.setItemMeta(im);
				}
				j++;
				if (is.isSimilar(p.getItemInHand())) {
					store = j;
					break;
				}
			}
			if (store == -1) {
				sender.sendMessage(prefix + ChatColor.DARK_RED + "This store doesn't contain such an item!");
				return false;
			}
			i.setItem(store, null);
			String where = "ShopInventories." + name;
			getConfig().set(where + ".Prices." + store, 0);
			saveConfig();
			saveInventory(where, i);
			sender.sendMessage(prefix + ChatColor.GOLD + "Succesfully removed that item!");
		}
		return false;
	}

	private void spawnVillager(String name, Location loc) {
		Villager v = loc.getWorld().spawn(loc, Villager.class);
		v.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Byte.MAX_VALUE));
		v.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, Byte.MAX_VALUE));
		v.setCustomName(name);
		v.setCustomNameVisible(true);
		v.setRemoveWhenFarAway(false);
		getConfig().set(name + ".UUID", v.getUniqueId().toString());
		saveConfig();
	}

	private void deleteVillager(String name, World w) {
		String uuid = getConfig().getString(name + ".UUID");
		if (uuid == null)
			return;
		for (Entity e : w.getEntitiesByClasses(Villager.class)) {
			if (e.getUniqueId().toString().equals(uuid)) {
				e.remove();
				return;
			}
		}
	}

	private void saveInventory(String where, Inventory i) {
		if (i == null)
			return;
		if (i.getTitle() == null)
			return;
		getConfig().set(where + ".Name", i.getTitle());
		getConfig().set(where + ".Size", i.getSize());
		int j = -1;
		for (ItemStack is : i.getContents()) {
			j++;
			if (is != null && is.getType() != Material.AIR)
				getConfig().set(where + "." + j, is);
			else
				getConfig().set(where + "." + j, null);
		}
		saveConfig();
	}

	private Inventory getInventory(String where) {
		String title = getConfig().getString(where + ".Name");
		if (title == null)
			return null;
		int size = getConfig().getInt(where + ".Size");
		ItemStack[] contents = new ItemStack[size];
		for (int i = 0; i < size; i++)
			contents[i] = getConfig().getItemStack(where + "." + i);
		Inventory i = Bukkit.createInventory(null, size, title);
		int j = -1;
		for (ItemStack is : contents) {
			j++;
			if (is != null && is.getType() != Material.AIR)
				i.setItem(j, is);
		}
		return i;
	}

	private boolean deleteShop(String name) {
		List<String> stringlist = getConfig().getStringList("VillagerShop.Shops");
		if (stringlist == null)
			return false;
		if (!stringlist.contains(name))
			return false;
		stringlist.remove(name);
		getConfig().set("VillagerShop.Shops", stringlist);
		saveConfig();
		return true;
	}

	private boolean createShop(String name) {
		List<String> stringlist = getConfig().getStringList("VillagerShop.Shops");
		if (stringlist == null)
			stringlist = new ArrayList<String>();
		if (!stringlist.contains(name))
			stringlist.add(name);
		else
			return false;
		getConfig().set("VillagerShop.Shops", stringlist);
		saveConfig();
		return true;
	}

	private boolean shopExists(String name) {
		List<String> stringlist = getConfig().getStringList("VillagerShop.Shops");
		return stringlist != null ? stringlist.contains(name) : false;
	}

	@EventHandler
	public void disableClicking(InventoryClickEvent e) {
		if (e.getInventory().getTitle() == null || !e.getInventory().getTitle().endsWith(" (S)"))
			return;
		if (e.getRawSlot() > 3 * 9 - 1) {
			e.setCancelled(true);
			return;
		}
		if (e.isShiftClick()) {
			e.setCancelled(true);
			return;
		}
		if (e.getCurrentItem() == null) {
			e.setCancelled(true);
			return;
		}
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
			return;
		double price = getConfig().getDouble("ShopInventories."
				+ e.getInventory().getTitle().replace(" (S)", "").substring(2) + ".Prices." + e.getSlot());
		if (hasEnough(e.getWhoClicked().getName(), price) && price != 0) {
			subtract(e.getWhoClicked().getName(), price);
			((Player) e.getWhoClicked()).sendMessage(prefix + ChatColor.GOLD + "Purchase succesful!");
			ItemStack item = e.getCurrentItem().clone();
			if (item.getItemMeta().hasLore()) {
				ItemMeta im = item.getItemMeta();
				List<String> lore = im.getLore();
				if (lore.contains(ChatColor.GREEN + "Costs " + price + "$!"))
					lore.remove(ChatColor.GREEN + "Costs " + price + "$!");
				im.setLore(lore);
				item.setItemMeta(im);
			}
			add(item, (Player) e.getWhoClicked(), e.getInventory());
		} else
			((Player) e.getWhoClicked())
					.sendMessage(prefix + ChatColor.DARK_RED + "You can't purchase this! Insufficient money!");
		e.setCancelled(true);
	}

	private void subtract(String name, double price) {
		e.withdrawPlayer(name, price);
	}

	private boolean hasEnough(String name, double price) {
		return e.has(name, price);
	}

	private void add(ItemStack item, Player whoClicked, Inventory inv) {
		if (hasSpace(whoClicked, item, inv))
			whoClicked.getInventory().addItem(item);
		else
			whoClicked.getWorld().dropItem(whoClicked.getLocation(), item);
	}

	private boolean hasSpace(Player whoClicked, ItemStack item, Inventory inv) {
		for (ItemStack is : inv.getContents())
			if (is == null || is.getType() == Material.AIR)
				return true;
		int space = 0;
		for (ItemStack is : inv.getContents())
			if (is.isSimilar(item))
				space += is.getMaxStackSize() - is.getAmount();
		return space >= item.getAmount();
	}
}