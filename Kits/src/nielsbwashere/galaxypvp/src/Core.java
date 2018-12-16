package nielsbwashere.galaxypvp.src;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
public class Core extends JavaPlugin implements Listener{
	public static final Inventory waypoints = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Kits");
	public static final ConcurrentHashMap<String, Inventory> waypointCopies = new ConcurrentHashMap<String,Inventory>();
@Override
public void onEnable() {
	getServer().getPluginManager().registerEvents(this, this);
	initializeWaypointsInv();
}
private void initializeWaypointsInv() {
	for(int i=0;i<8;i++){
	String material = getConfig().getString("Kits." + i + ".Item");
	if(material==null)continue;
	String name = getConfig().getString("Kits." + i + ".Name");
	if(name==null)continue;
	ItemStack is = new ItemStack(Material.getMaterial(material),1);
	ItemMeta im = is.getItemMeta();
	im.setDisplayName(ChatColor.GOLD + name);
	is.setItemMeta(im);
	waypoints.addItem(is);
	}
}
@Override
public void onDisable() {
saveWaypoints();
saveConfig();
}
private void saveWaypoints() {
	int i=0;
	getConfig().set("Kits", null);
	for(ItemStack is : waypoints.getContents()){
		if(is==null||is.getType()==Material.AIR)continue;
		getConfig().set("Kits."+i+".Item", is.getType().name());
		getConfig().set("Kits."+i+".Name", is.getItemMeta().getDisplayName().replace(ChatColor.GOLD.toString(), ""));
		i++;
	}
	saveConfig();
}
@EventHandler(priority=EventPriority.LOWEST)
public void onInvSteal(InventoryClickEvent ide){
	if(!ide.getInventory().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Kits"))return;
	if(ide.getCurrentItem()==null||ide.getCurrentItem().getType()==Material.AIR)return;
	String kits = ide.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.GOLD.toString(), "");
	Bukkit.dispatchCommand((Player)ide.getWhoClicked(), "kit " + kits);
	ide.setCancelled(true);
	ide.getWhoClicked().closeInventory();
}
@Override
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if(label.equalsIgnoreCase("kits")){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.DARK_RED + "You're not a player so you can't use /<commands>");
			return false;
		}
		String uuid = ((Player)sender).getUniqueId().toString();
		Inventory inv = waypointCopies.containsKey(uuid)?waypointCopies.get(uuid):null;
		Inventory i = inv!=null?inv:recreate(waypoints);
		if(inv==null)waypointCopies.put(uuid, i);
		((Player)sender).closeInventory();
		((Player)sender).openInventory(i);
	}else if(label.equalsIgnoreCase("addKit")){
		if(!(sender.isOp()||sender.hasPermission("Kits.Add"))){
			sender.sendMessage(ChatColor.DARK_RED + "You don't have permission");
			return false;
		}
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.DARK_RED + "You're not a player!");
			return false;
		}
		if(args.length!=2){
			sender.sendMessage(ChatColor.DARK_RED + "Incorrect usage: /<command> <itemToDisplayName> <kitName>");
			return false;
		}
		Material item = Material.getMaterial(args[0]);
		if(item==null){
			sender.sendMessage(ChatColor.DARK_RED + "That item is not valid!");
			return false;
		}
		String actualName = ChatColor.GOLD + args[1];
		if(isFull(waypoints)){
			sender.sendMessage(ChatColor.DARK_RED + "Please ask the plugin owner to add more slots to the inventory!");
			return false;
		}
		ItemStack is = new ItemStack(item,1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(actualName);
		is.setItemMeta(im);
		waypoints.addItem(is);
		waypointCopies.clear();
		sender.sendMessage(ChatColor.GOLD + "You've successfully added the kit: " + args[1] + ChatColor.RESET + "!");
	}
	return super.onCommand(sender, command, label, args);
}
private boolean isFull(Inventory i) {
	int ii=0;
	for(ItemStack is : i.getContents()){
		if(is!=null&&is.getType()!=Material.AIR)ii++;
	}
	return ii>=9;
}
private Inventory recreate(Inventory i2Copy) {
	Inventory i = Bukkit.createInventory(null, i2Copy.getSize(), i2Copy.getTitle());
	for(ItemStack is : i2Copy.getContents()){
		if(is==null||is.getType()==Material.AIR)continue;
		i.addItem(is);
	}
	return i;
}
}