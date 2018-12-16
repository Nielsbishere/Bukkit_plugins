package net.nielsbwashere.src.GUIAwayOnDestroy;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class Core extends JavaPlugin implements Listener{
	String prefix = ChatColor.BLUE + "[" + ChatColor.AQUA + "InventoryGlitch" + ChatColor.BLUE + "] ";
@Override
public void onEnable() {
	Bukkit.getPluginManager().registerEvents(this, this);
	if(!getConfig().getBoolean("Settings.isInitialized")){
		getConfig().set("Settings.isInitialized",true);
		getConfig().set("Settings.itemsThatCanRemoveBlocks",Arrays.asList("10720","10729"));
		saveConfig();
	}
}
@EventHandler
public void onDestroy(PlayerInteractEvent e){
	if(!e.hasBlock())return;
	if(!canRemoveBlock(e.getPlayer().getItemInHand()))return;
	prevent(e.getClickedBlock());
}
private boolean canRemoveBlock(ItemStack itemInHand) {
	List<String> items = getConfig().getStringList("Settings.itemsThatCanRemoveBlocks");
	for(String s : items)if(itemInHand!=null&&s.equals(itemInHand.getTypeId()+""))return true;
	return false;
}
@EventHandler
public void onDestroy(BlockDamageEvent e){
	prevent(e.getBlock());
}
private void prevent(Block b) {
	if(!(b.getState() instanceof InventoryHolder))return;
	if(!isBanned(b))return;
	Location l2 = b.getLocation();
	for(Player p : getServer().getOnlinePlayers()){
		Location l = p.getLocation();
		double distance = Math.sqrt(Math.pow(l.getX()-l2.getX(),2)+Math.pow(l.getY()-l2.getY(),2)+Math.pow(l.getZ()-l2.getZ(),2));
		if(distance<=15)p.closeInventory();
	}
}
private boolean isBanned(Block block) {
	List<String> strings = getConfig().getStringList("GlitchedInventories");
	int i = block.getTypeId();
	for(String s : strings)if(s.equals(i+""))return true;
	return false;
}
@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if(label.equalsIgnoreCase("addGlitchingInventory")){
		if(!sender.hasPermission("InventoryGlitch.addGlitchingInventory")&&!sender.isOp()){
			sender.sendMessage(prefix + ChatColor.DARK_RED + "You don't have permission to use this command!");
			return false;
		}
		if(!(sender instanceof Player)){
			sender.sendMessage(prefix + ChatColor.DARK_RED + "Only players can use this command!");
			return false;
		}
		Player p = (Player)sender;
		if(p.getItemInHand()==null||p.getItemInHand().getType()==Material.AIR){
			sender.sendMessage(prefix + ChatColor.DARK_RED + "You must have something in hand!");
			return false;
		}
		if(!p.getItemInHand().getType().isBlock()){
			sender.sendMessage(prefix + ChatColor.DARK_RED + "You can only ban blocks!");
			return false;
		}
		int i = p.getItemInHand().getTypeId();
		List<String> strings = getConfig().getStringList("GlitchedInventories");
		boolean contains=false;
		for(String s : strings)if(s.equals(i+""))contains=true;
		if(contains){
			sender.sendMessage(prefix + ChatColor.DARK_RED + "That block inventory is already told to be glitched!");
			return false;
		}
		strings.add(i+"");
		getConfig().set("GlitchedInventories",strings);
		saveConfig();
		sender.sendMessage(prefix + ChatColor.GOLD + "Added the block to the glitched inventories list!");
	}else if(label.equalsIgnoreCase("removeGlitchingInventory")){
		if(!sender.hasPermission("InventoryGlitch.removeGlitchingInventory")&&!sender.isOp()){
			sender.sendMessage(prefix + ChatColor.DARK_RED + "You don't have permission to use this command!");
			return false;
		}
		if(!(sender instanceof Player)){
			sender.sendMessage(prefix + ChatColor.DARK_RED + "Only players can use this command!");
			return false;
		}
		Player p = (Player)sender;
		if(p.getItemInHand()==null||p.getItemInHand().getType()==Material.AIR){
			sender.sendMessage(prefix + ChatColor.DARK_RED + "You must have something in hand!");
			return false;
		}
		if(!p.getItemInHand().getType().isBlock()){
			sender.sendMessage(prefix + ChatColor.DARK_RED + "You can only unban blocks!");
			return false;
		}
		int i = p.getItemInHand().getTypeId();
		List<String> strings = getConfig().getStringList("GlitchedInventories");
		boolean contains=false;
		for(String s : strings)if(s.equals(i+""))contains=true;
		if(!contains){
			sender.sendMessage(prefix + ChatColor.DARK_RED + "That block inventory is not even told to be glitched!");
			return false;
		}
		strings.remove(i+"");
		getConfig().set("GlitchedInventories",strings);
		saveConfig();
		sender.sendMessage(prefix + ChatColor.GOLD + "Removed the block from the glitched inventories list!");
	}
	return false;
	}
}
