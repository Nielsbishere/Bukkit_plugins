package net.chests.src;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
public class Main extends JavaPlugin implements Listener{
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		reloadChests();
	}
	private void reloadChests() {
		for(String s : getConfig().getStringList("Chests.Backups")){
			String[] st = s.split("\\}");
			World w = getServer().getWorld(st[0]);
			int x = parse(st[1]);
			int y = parse(st[2]);
			int z = parse(st[3]);
			Location l = new Location(w,x,y,z);
			String newInventory = st[4];
			if(w==null||(x==0&&y==0&&z==0))continue;
			if(w.getBlockAt(l).getType()!=Material.CHEST)continue;
			Chest c = (Chest)w.getBlockAt(l).getState();
			List<String> newItems = new ArrayList<String>();
			for(String str : newInventory.split("\\{")){
				newItems.add(str);
			}
			addItems(c.getBlockInventory(),newItems);
		}
	}
	public void onDisable() {
		saveConfig();
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("addChest")){
			if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "[Chest Reload]: You can't add chests!");
				return true;
			}
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "[Chest Reload]: You can't add chests, since you should be looking directly at them!");
				return true;
			}
			Player p = (Player)sender;
			Block b = p.getTargetBlock(null, 100);
			if(b.getType()!=Material.CHEST){
				sender.sendMessage(ChatColor.DARK_RED + "[Chest Reload]: You must be looking directly at the chest!");
				return true;
			}
			saveAllItems(((Chest)b.getState()).getBlockInventory(), b.getLocation());
			sender.sendMessage(ChatColor.DARK_RED + "[Chest Reload]: You have saved this chest!");
		}else if(label.equalsIgnoreCase("reloadChests")){
			if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "[Chest Reload]: You can't reload chests!");
				return true;
			}
			reloadChests();
			sender.sendMessage(ChatColor.DARK_RED + "[Chest Reload]: You have reloaded all chests!");
			getServer().broadcastMessage(ChatColor.DARK_RED + "[Chest Reload]: " + sender.getName() + " has reloaded all chests!");
		}
		return false;
	}
	public void saveAllItems(Inventory i, Location l){
		List<String> contents = new ArrayList<String>();
		for(ItemStack is : i.getContents()){
			saveItem(contents,is);
		}
		boolean isFirst=true;
		String s = "";
		for(String st : contents){
			if(isFirst){
				s=st;
				isFirst=false;
			}
			else s=s+"{"+st;
		}
		s=l.getWorld().getName()+"}"+l.getBlockX()+"}"+l.getBlockY()+"}"+l.getBlockZ()+"}"+s;
		List<String> allChests = getConfig().getStringList("Chests.Backups");
		List<String> newAllChests = new ArrayList<String>();
		newAllChests.add(s);
		for(String st : allChests){
			if(!st.startsWith(l.getWorld().getName()+"}"+l.getBlockX()+"}"+l.getBlockY()+"}"+l.getBlockZ()+"}")){
				newAllChests.add(st);
			}
		}
		getConfig().set("Chests.Backups",newAllChests);
		saveConfig();
	}
	public void saveItem(List<String> allItems, ItemStack is){
		if(is==null||is.getType()==Material.AIR)return;
		String material = is.getType().name();
		int amount = is.getAmount();
		short damage = is.getDurability();
		ItemMeta im = is.getItemMeta();
		boolean hasItemMeta = is.hasItemMeta()&&(im.hasDisplayName()||im.hasLore());
		boolean hasDisplayName = im.hasDisplayName();
		boolean hasLore = im.hasLore();
		boolean isEnchanted = im.hasEnchants();
		String itemStackToString = material + "," + amount + ","+damage+",";
		if(hasItemMeta)itemStackToString=itemStackToString+"true("+hasDisplayName+"["+im.getDisplayName()+"]"+hasLore+"["+getStringListAsString(im.getLore())+",";
		else itemStackToString=itemStackToString+"false,";
		if(isEnchanted)itemStackToString=itemStackToString+"true("+allEnchantments(im.getEnchants());
		else itemStackToString=itemStackToString+"false";
		boolean hasTwin=false;
		for(String s : allItems){
			if(itemStackToString==s){
				hasTwin=true;
				break;
			}
		}
		if(!hasTwin){
			allItems.add(itemStackToString);
		}
	}
	public void addItems(Inventory chest, List<String> chestItems){
		chest.clear();
		for(String s : chestItems){
			chest.addItem(readItem(s));
		}
	}
	public ItemStack readItem(String s){
		String[] segment = s.split(",");
		Material m = Material.getMaterial(segment[0]);
		int amount = parse(segment[1]);
		short damage = (short)parse(segment[2]);
		ItemStack is = new ItemStack(m,amount,damage);
		boolean hasItemMeta = segment[3].split("\\(")[0].equalsIgnoreCase("true");
		if(hasItemMeta)parseDisplayAndLore(is,segment[3].split("\\(")[1]);
		boolean hasEnchant = segment[4].split("\\(")[0].equalsIgnoreCase("true");
		if(hasEnchant)parseEnchants(is,segment[4].split("\\(")[1]);
		return is;
	}
	private void parseEnchants(ItemStack is, String string) {
		ItemMeta im = is.getItemMeta();
		String[] s = string.split("\\|");
		for(String st : s){
			String[] nameAndLvl = st.split("\\.");
			Enchantment e = Enchantment.getByName(nameAndLvl[0]);
			if(im.hasEnchant(e))continue;
			im.addEnchant(e, parse(nameAndLvl[1]), true);
		}
		is.setItemMeta(im);
	}
	private void parseDisplayAndLore(ItemStack is, String string) {
		ItemMeta im = is.getItemMeta();
		String displayName = string.split("\\]")[0];
		String lore = string.split("\\]")[1];
		boolean hasDisplayName = displayName.split("\\[")[0].equalsIgnoreCase("true");
		boolean hasLore = lore.split("\\[")[0].equalsIgnoreCase("true");
		if(hasDisplayName)im.setDisplayName(displayName.split("\\[")[1]);
		if(hasLore)im.setLore(parseStringListFromString(lore.split("\\[")[1]));
		is.setItemMeta(im);
	}
	private List<String> parseStringListFromString(String string) {
		List<String> lore = new ArrayList<String>();
		String[] allLore = string.split("\\|");
		for(String s : allLore){
			lore.add(s);
		}
		return lore;
	}
	private int parse(String string) {
		boolean onlyHasNumbers = true;
		for(char c : string.toCharArray()){
			if(c!='0'&&c!='1'&&c!='2'&&c!='3'&&c!='4'&&c!='5'&&c!='6'&&c!='7'&&c!='8'&&c!='9'){
				onlyHasNumbers=false;
				break;
			}
		}
		if(!onlyHasNumbers)return 0;
		return Integer.parseInt(string);
	}
	private String allEnchantments(Map<Enchantment, Integer> enchants) {
		if(enchants==null)return "";
		String s = "";
		boolean isFirst=true;
		for(Entry<Enchantment,Integer> EI : enchants.entrySet()){
			if(isFirst){
				s=EI.getKey().getName()+"."+EI.getValue();
				isFirst=false;
			}
			else s=s+"|"+EI.getKey().getName()+"."+EI.getValue();
		}
		return s;
	}
	private String getStringListAsString(List<String> lore) {
		if(lore==null)return "";
		String s = "";
		boolean isFirst=true;
		for(String st : lore){
			if(isFirst){
				s=st;
				isFirst=false;
			}
			else s=s+"|"+st;
		}
		return s;
	}
}