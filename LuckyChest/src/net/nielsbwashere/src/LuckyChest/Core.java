package net.nielsbwashere.src.LuckyChest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
public class Core extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("luckyChest")){
			if(!sender.isOp()||!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "Only opped players can use that command!");
				return false;
			}
			if(args.length!=1){
				sender.sendMessage(ChatColor.DARK_RED + "Invalid usage: /luckyChest <name>");
				return false;
			}
			if(getServer().getPlayer(args[0])==null){
				sender.sendMessage(ChatColor.DARK_RED + "That player is not online!");
				return false;
			}
			ItemStack is = new ItemStack(Material.CHEST,1);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.GOLD + "Lucky chest");
			is.setItemMeta(im);
			getServer().getPlayer(args[0]).getInventory().addItem(is);
		}
		return false;
	}
	@EventHandler
	public void onOpen(BlockPlaceEvent e){
		if(e.getItemInHand()==null||e.getItemInHand().getType()!=Material.CHEST)return;
		ItemStack is = e.getItemInHand();
		if(!is.hasItemMeta()||!is.getItemMeta().hasDisplayName())return;
		String name = is.getItemMeta().getDisplayName();
		if(!name.equals(ChatColor.GOLD + "Lucky chest"))return;
		e.getBlock().setType(Material.AIR);
		buildStructure(0,e.getBlock().getLocation());
	}
	private void buildStructure(int i, Location l) {
		if(i==0)net.nielsbwashere.src.Structure2Code.Core.set(this, "1", l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
}