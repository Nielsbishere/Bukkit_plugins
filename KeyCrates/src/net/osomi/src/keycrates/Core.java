package net.osomi.src.keycrates;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Core extends JavaPlugin {
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new CoreListener(this), this);
	}

	@Override
	public void onDisable() {
		for (Player p : getServer().getOnlinePlayers())
			p.closeInventory();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("keycrates")) {
			String[] options = new String[] { "key", "crate", "items" };
			String opts = "";
			for (String s : options)
				opts = opts + s + "/";
			opts = opts.substring(0, opts.length() - 1);
			String usage = prefix + "Incorrect usage: /keycrates <" + opts + ">";
			if (args.length == 0) {
				sender.sendMessage(usage);
				return false;
			}
			boolean b = true;
			for (String s : options)
				if (args[0].equalsIgnoreCase(s)) {
					b = false;
				}
			if (b) {
				sender.sendMessage(usage);
				return false;
			}
			if (!sender.hasPermission("keyCrates." + args[0]) && !sender.isOp()) {
				sender.sendMessage(prefix + "You don't have permission to use this command!");
				return false;
			}
			if (args[0].equalsIgnoreCase(options[0])) {
				if (args.length == 1) {
					sender.sendMessage(prefix + "Incorrect usage: /keycrates key <player>");
					return false;
				}
				Player pl = getServer().getPlayer(args[1]);
				if (pl == null) {
					sender.sendMessage(prefix + "That player isn't online!");
					return false;
				}
				ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
				key.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
				ItemMeta im = key.getItemMeta();
				im.setDisplayName(ChatColor.DARK_PURPLE + "Crate key");
				key.setItemMeta(im);
				give(pl, key);
				pl.sendMessage(prefix + ChatColor.GOLD + "You have been given a crate key!");
			} else if (args[0].equalsIgnoreCase(options[1])) {
				if (args.length == 1) {
					sender.sendMessage(prefix + "Incorrect usage: /keycrates crate <id>");
					return false;
				}
				int id;
				try{
					id = Integer.parseInt(args[1]);
				}catch(Exception e){
					sender.sendMessage(prefix + "Incorrect usage: /keycrates crate <id>");
					return false;
				}
				if (!(sender instanceof Player)) {
					sender.sendMessage(prefix + "Only players can set the crate");
					return false;
				}
				boolean valid = false;
				Set<String> str = getConfig().getConfigurationSection("crateItem").getKeys(false);
				for(String s : str){
					try{
						if(Integer.parseInt(s) == id){
							valid = true;
							break;
						}
					}catch(Exception e){}
				}
				if(!valid){
					sender.sendMessage(prefix + "The id you put in is invalid!");
					return false;
				}
				Block chest = ((Player) sender).getTargetBlock((HashSet<Byte>) null, 80);
				Location l = chest.getLocation();
				if (chest.getType() != Material.CHEST) {
					sender.sendMessage(prefix + "You need to look at a chest!");
					return false;
				}
				List<String> chests = getConfig().getStringList("Chests");
				for (String s : chests) {
					if (s.endsWith(l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getWorld().getName())) {
						sender.sendMessage(prefix + "That chest is already added!");
						return false;
					}
				}
				chests.add(id + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getWorld().getName());
				getConfig().set("Chests", chests);
				saveConfig();
				sender.sendMessage(prefix + ChatColor.GOLD + "You have added this chest as a key crate!");
			} else if (args[0].equalsIgnoreCase(options[2])) {
				if (args.length == 1 || (!args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("remove"))) {
					sender.sendMessage(prefix + "Incorrect usage: /keycrates items <add/remove>");
					return false;
				}
				if (args.length < 2 + 3) {
					sender.sendMessage(prefix + "Incorrect usage: /keycrates items add <displayName> <chance> <id>");
					return false;
				}
				if (!(sender instanceof Player)) {
					sender.sendMessage(prefix + "Only players can execute this command!");
					return false;
				}
				Player p = (Player) sender;
				String display = args[2].replace("_", " ");
				double chance;
				try {
					chance = Double.parseDouble(args[3]);
				} catch (Exception e) {
					sender.sendMessage(prefix + "Chance is meant to be a number between 0 and 1");
					return false;
				}
				if (chance < 0 || chance > 1) {
					sender.sendMessage(prefix + "Chance is meant to be a number between 0 and 1");
					return false;
				}
				int id;
				try{
					id = Integer.parseInt(args[4]);
				}catch(Exception e){
					sender.sendMessage(prefix + "Id is meant to be an integer!");
					return false;
				}
				Set<String> str;
				if (args[1].equalsIgnoreCase("add")) {
					ItemStack is = p.getItemInHand();
					if (is == null || is.getType() == Material.AIR) {
						sender.sendMessage(prefix + "You have to be holding an item to add/remove!");
						return false;
					}
					ConfigurationSection cs = getConfig().getConfigurationSection("crateItem." + id);
					str = cs == null ? new HashSet<String>() : cs.getKeys(false);
					String s = str.size() + "";
					str.add(s + "");
					getConfig().set("crateItem." + id + "." + s + ".cmd", "");
					getConfig().set("crateItem." + id + "." + s + ".stack", is);
					getConfig().set("crateItem." + id + "." + s + ".displayName", display);
					getConfig().set("crateItem." + id + "." + s + ".chance", chance);
					getConfig().set("crateItem." + id + "." + s + ".displayId", is.getTypeId());
					getConfig().set("crateItem." + id + "." + s + ".data", (int) is.getDurability());
					saveConfig();
					sender.sendMessage(prefix + ChatColor.GOLD + "Successfully added that item!");
				} else {
					str = getConfig().getConfigurationSection("crateItem." + id).getKeys(false);
					String stri = null;
					for (String s : str) {
						double chance2 = getConfig().getDouble("crateItem." + id + "." + s + ".chance");
						String disp = getConfig().getString("crateItem." + id + "." + s + ".displayName");
						if (chance2 == chance && disp.equalsIgnoreCase(display)) {
							stri = s;
							break;
						}
					}
					if (stri == null) {
						sender.sendMessage(prefix + "That item doesn't even exist!");
						return false;
					}
					getConfig().set("crateItem." + id + "." + stri, null);
					saveConfig();
					sender.sendMessage(prefix + ChatColor.GOLD + "Successfully removed that item!");
				}
			}
			return true;
		}
		return false;
	}

	String prefix = ChatColor.RESET + "[" + ChatColor.AQUA + "VixelCrates" + ChatColor.WHITE + "] "
			+ ChatColor.DARK_RED;

	public String get(String disp, int id) {
		disp = disp.replaceFirst(ChatColor.RESET + "", "");
		Set<String> str = getConfig().getConfigurationSection("crateItem." + id).getKeys(false);
		if (str == null || str.isEmpty())
			return null;
		for (String s : str) {
			String displayName = getConfig().getString("crateItem." + id + "." + s + ".displayName");
			if (displayName == null)
				continue;
			if (!displayName.equals(disp))
				continue;
			return s;
		}
		return null;
	}

	public void give(Player whoClicked, ItemStack is) {
		for (ItemStack i : whoClicked.getInventory().getContents()) {
			if (i == null || i.getType() == Material.AIR) {
				whoClicked.getInventory().addItem(is);
				return;
			}
		}
		whoClicked.getWorld().dropItem(whoClicked.getLocation(), is);
	}

	public Inventory create(boolean accessible, int id) {
		Inventory inv = Bukkit.createInventory(null, 9 * 3,
				accessible ? ChatColor.GOLD + "Crate " + id : ChatColor.RESET + "" + ChatColor.GOLD + "Crate");
		for (int j = 0; j < 2; j++)
			for (int i = 1; i < 10; i++)
				inv.setItem((j != 0 ? j + 1 : 0) * 9 + i - 1,
						i % 2 != 0 ? new ItemStack(Material.WOOL, 1, (short) 10) : new ItemStack(Material.WOOL));
		inv.setItem(1 * 9, new ItemStack(Material.WOOL));
		inv.setItem(2 * 9 - 1, new ItemStack(Material.WOOL));
		ItemStack is = generate(id);
		if (is != null && is.getType() != Material.AIR)
			inv.setItem(1 * 9 + 4, is);
		return inv;
	}

	@SuppressWarnings("deprecation")
	public ItemStack generate(int id) {
		Set<String> str = getConfig().getConfigurationSection("crateItem." + id).getKeys(false);
		if (str == null || str.isEmpty())
			return null;
		ItemStack result = null;
		while (result == null) {
			for (String s : str) {
				double chance = getConfig().getDouble("crateItem." + id + "." + s + ".chance");
				if (chance == 0)
					continue;
				if (Math.random() >= chance)
					continue;
				int displayId = getConfig().getInt("crateItem." + id + "." + s + ".displayId");
				short data = (short) getConfig().getInt("crateItem." + id + "." + s + ".data");
				String displayName = getConfig().getString("crateItem." + id + "." + s + ".displayName");
				ItemStack is = new ItemStack(displayId, 1, data);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.RESET + displayName);
				is.setItemMeta(im);
				return is;
			}
		}
		return null;
	}

	public boolean remove(Player player, ItemStack is) {
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack ist = player.getInventory().getContents()[i];
			if (ist == null)
				continue;
			ItemStack istc = ist.clone();
			istc.setAmount(is.getAmount());
			if (!istc.equals(is))
				continue;
			if (ist.getAmount() <= 1) {
				player.getInventory().clear(i);
				return true;
			} else {
				ist.setAmount(ist.getAmount() - 1);
				player.getInventory().setItem(i, ist);
				return true;
			}
		}
		return false;
	}

	public boolean isCrate(Location location) {
		List<String> chests = getConfig().getStringList("Chests");
		for (String s : chests)
			if (s.endsWith(location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "," + location.getWorld().getName()))
				return true;
		return false;
	}

	public boolean isKey(ItemStack iih) {
		return iih.getType() == Material.TRIPWIRE_HOOK && iih.hasItemMeta() && iih.getItemMeta().hasDisplayName()
				&& iih.getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + "Crate key");
	}
}

class CoreListener implements Listener {
	Core c;

	public CoreListener(Core c) {
		this.c = c;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getInventory().getTitle() == null)
			return;
		if (e.getInventory().getTitle().equals(ChatColor.RESET + "" + ChatColor.GOLD + "Crate"))
			e.setCancelled(true);
		else if (e.getInventory().getTitle().startsWith(ChatColor.GOLD + "Crate")) {
			if (e.getRawSlot() > 9 * 3)
				return;
			if (e.getCurrentItem() == null)
				return;
			if (e.getCurrentItem().getType() == Material.WOOL) {
				e.setCancelled(true);
				return;
			}
			if (e.isShiftClick()) {
				e.setCancelled(true);
				Player p = (Player) e.getWhoClicked();
				p.sendMessage(c.prefix + "You can't shift click a reward!");
				return;
			}
			Player p = (Player) e.getWhoClicked();
			if (!e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName())
				return;
				
			int id;
			try{
				id = Integer.parseInt(e.getInventory().getTitle().split(" ")[1]);
			}catch(Exception exc){
				System.out.println(c.prefix + "Tried to access an invalid crate!");
				return;
			}
			
			String s = c.get(e.getCurrentItem().getItemMeta().getDisplayName(), id);
			e.setCurrentItem(null);
			p.closeInventory();
			if (s == null || s.equals(""))
				return;
			String command = c.getConfig().getString("crateItem." + id + "." + s + ".cmd");
			ItemStack is = c.getConfig().getItemStack("crateItem." + id + "." + s + ".stack");
			if (command != null && !command.equals(""))
				for(String cmmnd : command.split("@#@"))
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmmnd.replace("{player}", p.getName()));
			if (is != null && is.getType() != Material.AIR)
				c.give(p, is);
			p.sendMessage(c.prefix + ChatColor.GOLD + "You got a(n) "
					+ c.getConfig().getString("crateItem." + id + "." + s + ".displayName") + " as reward!");
		}
	}

	List<Player> closable = new ArrayList<Player>();

	@EventHandler
	public void onClick(InventoryCloseEvent e) {
		if (e.getInventory().getTitle() == null)
			return;
		if (e.getInventory().getTitle().startsWith(ChatColor.GOLD + "Crate")) {
			Player p = (Player) e.getPlayer();
			ItemStack ist = e.getInventory().getItem(9 * 1 + 4);
			if (ist == null || !ist.hasItemMeta() || !ist.getItemMeta().hasDisplayName())
				return;
			
			int id;
			try{
				id = Integer.parseInt(e.getInventory().getTitle().split(" ")[1]);
			}catch(Exception exc){
				System.out.println(c.prefix + "Tried to close an invalid crate!");
				return;
			}
			
			String s = c.get(ist.getItemMeta().getDisplayName(), id);
			if (s == null || s.equals(""))
				return;
			String command = c.getConfig().getString("crateItem." + id + "." + s + ".cmd");
			ItemStack is = c.getConfig().getItemStack("crateItem." + id + "." + s + ".stack");
			if (command != null && !command.equals(""))
				for(String cmmnd : command.split("@#@"))
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmmnd.replace("{player}", p.getName()));
			if (is != null && is.getType() != Material.AIR)
				c.give(p, is);
			p.sendMessage(c.prefix + ChatColor.GOLD + "You got a(n) "
					+ c.getConfig().getString("crateItem." + id + "." + s + ".displayName") + " as reward!");
		}
		final Player p = (Player) e.getPlayer();
		if (closable.contains(p)) {
			closable.remove(p);
			return;
		}
		if (e.getInventory().getTitle().endsWith(ChatColor.RESET + "" + ChatColor.GOLD + "Crate")) {
			final Inventory i = e.getInventory();
			Bukkit.getScheduler().scheduleSyncDelayedTask(c, new Runnable() {
				@Override
				public void run() {
					p.openInventory(i);
				}
			}, 1);
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e){
		if(e.getItemInHand() == null || !c.isKey(e.getItemInHand()))
			return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onKey(final PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if (e.getClickedBlock().getType() != Material.CHEST || !c.isCrate(e.getClickedBlock().getLocation()))
			return;
		ItemStack iih = e.getPlayer().getItemInHand();
		if (iih == null || !c.isKey(iih)) {
			Player p = (Player) e.getPlayer();
			Vector v = p.getLocation().subtract(e.getClickedBlock().getLocation()).toVector().normalize();
			e.getPlayer().setVelocity(v.multiply(1.1));
			e.getPlayer().sendMessage(c.prefix + "You need to use a crate key to open this! Use /vote to get one!");
			e.setCancelled(true);
			return;
		}
		int id = 0;
		boolean valid = false;
		for(String s : c.getConfig().getStringList("Chests")){
			if(s.endsWith(e.getClickedBlock().getLocation().getBlockX() + "," + e.getClickedBlock().getLocation().getBlockY() + "," + e.getClickedBlock().getLocation().getBlockZ() + "," + e.getClickedBlock().getWorld().getName())){
				try{
					id = Integer.parseInt(s.split(",")[0]);
				}catch(Exception exc){
					System.out.println(c.prefix + "A chest didn't start with an id! Aborting!");
					e.setCancelled(true);
					return;
				}
				valid = true;
			}
		}
		if(!valid){
			System.out.print(c.prefix + "Tried to access an invalid chest!");
			e.setCancelled(true);
			return;
		}
		if(!(e.getPlayer().isOp() || e.getPlayer().hasPermission("KeyCrates.open." + id))){
			e.getPlayer().sendMessage(c.prefix + "You don't have permission to open the chest!");
			e.setCancelled(true);
			return;
		}
			
		ItemStack is = new ItemStack(Material.TRIPWIRE_HOOK, 1);
		is.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.DARK_PURPLE + "Crate key");
		is.setItemMeta(im);
		
		int maxKeys = c.getConfig().getInt("keys." + id);
		if(maxKeys == 0)maxKeys = 1;
		
		for(int i = 0; i < maxKeys; i++){
			if(!c.remove(e.getPlayer(), is)){
				for(int j = 0; j < i; j++)
					e.getPlayer().getInventory().addItem(is);

				e.getPlayer().sendMessage(c.prefix + "You don't have enough keys!");
				e.setCancelled(true);
				return;
			}
		}
		
		final int fid = id;
		final int som = Bukkit.getScheduler().scheduleSyncRepeatingTask(c, new Runnable() {
			int j = 0;
			Inventory currInv = null;
			ItemStack result = null;

			@Override
			public void run() {
				boolean b = false;
				if (j == 8 + 4 * 2) {
					b = true;
				} else if (j > 8 + 4 * 2) {
					return;
				}
				if (j >= 8) {
					if (result == null) {
						result = c.generate(fid);
						currInv.setItem(1 * 9 + 4, result);
					} else {
						ItemStack x1 = currInv.getItem(1 * 9 + 3);
						ItemStack x2 = currInv.getItem(1 * 9 + 5);
						if ((x1 == null || x1.getType() == Material.AIR)
								|| (x2 == null || x2.getType() == Material.AIR)) {
							currInv.setItem(1 * 9 + 3, new ItemStack(Material.GOLD_INGOT));
							currInv.setItem(1 * 9 + 5, new ItemStack(Material.GOLD_INGOT));
						} else {
							currInv.setItem(1 * 9 + 3, null);
							currInv.setItem(1 * 9 + 5, null);
						}
						if (b) {
							currInv = c.create(b, fid);
							currInv.setItem(1 * 9 + 4, result);
							closable.add(e.getPlayer());
							e.getPlayer().closeInventory();
							e.getPlayer().openInventory(currInv);
						}
					}
				} else {
					if (j == 0)
						e.getPlayer().closeInventory();
					if (currInv == null) {
						currInv = c.create(b, fid);
						e.getPlayer().openInventory(currInv);
					} else {
						currInv.setItem(1 * 9 + 4, c.generate(fid));
					}
				}
				j++;
			}
		}, 0, 5);
		Bukkit.getScheduler().scheduleSyncDelayedTask(c, new Runnable() {
			@Override
			public void run() {
				Bukkit.getScheduler().cancelTask(som);
			}
		}, 20 * 10);
		e.setCancelled(true);
	}
}