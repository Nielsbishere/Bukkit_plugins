package net.nielsbwashere.src.BanItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.ps.PS;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
@SuppressWarnings("deprecation")
public class Core extends JavaPlugin implements Listener{
	WorldGuardPlugin wgp;
	Factions fpl;
		@Override
		public void onEnable() {
			final Core c = this;
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				@Override
				public void run() {
					try{
					wgp = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
					fpl = (Factions)Bukkit.getPluginManager().getPlugin("Factions");
					if(!wgp.isEnabled()||!fpl.isEnabled()){
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Disabling banned items! Factions and/or worldguard weren't installed/enabled!");
						Bukkit.getPluginManager().disablePlugin(c);
					}else{
						Bukkit.getPluginManager().registerEvents(c, c);
					}
					}catch(Exception e){
						Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Disabling banned items! Factions and/or worldguard weren't installed/enabled!");
						e.printStackTrace();
						Bukkit.getPluginManager().disablePlugin(c);
					}
				}},2l);
		}
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if(label.equalsIgnoreCase("banItem")){
				if(!sender.isOp()&&!sender.hasPermission("BanItemFactionWG.Ban")){
					sender.sendMessage(ChatColor.DARK_RED+"You aren't allowed to use this command!");
					return false;
				}
				if(args.length!=0&&args.length!=2){
					sender.sendMessage(ChatColor.DARK_RED + "Invalid use! /<command> <itemID> <metadata/durability> or /<command>");
					return false;
				}
				if(args.length==2){
					int id=0,metadata=0;
					try{
						id=Integer.parseInt(args[0]);
						metadata=Integer.parseInt(args[1]);
					}catch(Exception e){
						sender.sendMessage(ChatColor.DARK_RED + "Id and/or metadata are supposed to be numeric!");
						return false;
					}
					String in = id+":"+metadata;
					List<String> arr = getConfig().getStringList("BannedItems");
					List<String> banned = arr==null||arr.isEmpty()?new ArrayList<String>():arr;
					if(banned.contains(in)){
						sender.sendMessage(ChatColor.DARK_RED + "That item with metadata is already added to the list!");
						return false;
					}
					banned.add(in);
					getConfig().set("BannedItems", banned);
					saveConfig();
					sender.sendMessage(ChatColor.DARK_RED + "Item: " + in + " is now added to the banned item list!");
				}else if(args.length==0){
					if(!(sender instanceof Player)){
						sender.sendMessage(ChatColor.DARK_RED + "The console can only use /<command> <id> <metadata/durability>");
						return false;
					}
					Player p = (Player)sender;
					if(p.getItemInHand()==null||p.getItemInHand().getType()==Material.AIR){
						sender.sendMessage(ChatColor.DARK_RED + "You must have an item in hand to ban it!");
						return false;
					}
					String in = p.getItemInHand().getTypeId()+":"+p.getItemInHand().getDurability();
					List<String> arr = getConfig().getStringList("BannedItems");
					List<String> banned = arr==null||arr.isEmpty()?new ArrayList<String>():arr;
					if(banned.contains(in)){
						sender.sendMessage(ChatColor.DARK_RED + "That item with metadata is already added to the list!");
						return false;
					}
					banned.add(in);
					getConfig().set("BannedItems", banned);
					saveConfig();
					sender.sendMessage(ChatColor.DARK_RED + "Item: " + in + " is now added to the banned item list!");
				}
			}else if(label.equalsIgnoreCase("unbanItem")){
				if(!sender.isOp()&&!sender.hasPermission("BanItemFactionWG.Unban")){
					sender.sendMessage(ChatColor.DARK_RED+"You aren't allowed to use this command!");
					return false;
				}
				if(args.length!=0&&args.length!=2){
					sender.sendMessage(ChatColor.DARK_RED + "Invalid use! /<command> <itemID> <metadata/durability> or /<command>");
					return false;
				}
				if(args.length==2){
					int id=0,metadata=0;
					try{
						id=Integer.parseInt(args[0]);
						metadata=Integer.parseInt(args[1]);
					}catch(Exception e){
						sender.sendMessage(ChatColor.DARK_RED + "Id and/or metadata are supposed to be numeric!");
						return false;
					}
					String in = id+":"+metadata;
					List<String> arr = getConfig().getStringList("BannedItems");
					List<String> banned = arr==null||arr.isEmpty()?new ArrayList<String>():arr;
					if(!banned.contains(in)){
						sender.sendMessage(ChatColor.DARK_RED + "That item with metadata is not even banned!");
						return false;
					}
					banned.remove(in);
					getConfig().set("BannedItems", banned);
					saveConfig();
					sender.sendMessage(ChatColor.DARK_RED + "Item: " + in + " is now removed from the banned item list!");
				}else if(args.length==0){
					if(!(sender instanceof Player)){
						sender.sendMessage(ChatColor.DARK_RED + "The console can only use /<command> <id> <metadata/durability>");
						return false;
					}
					Player p = (Player)sender;
					if(p.getItemInHand()==null||p.getItemInHand().getType()==Material.AIR){
						sender.sendMessage(ChatColor.DARK_RED + "You must have an item in hand to unban it!");
						return false;
					}
					String in = p.getItemInHand().getTypeId()+":"+p.getItemInHand().getDurability();
					List<String> arr = getConfig().getStringList("BannedItems");
					List<String> banned = arr==null||arr.isEmpty()?new ArrayList<String>():arr;
					if(!banned.contains(in)){
						sender.sendMessage(ChatColor.DARK_RED + "That item with metadata is not even banned!");
						return false;
					}
					banned.remove(in);
					getConfig().set("BannedItems", banned);
					saveConfig();
					sender.sendMessage(ChatColor.DARK_RED + "Item: " + in + " is now removed from the banned item list!");
				}
			}
			return false;
		}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent pie){
		Player pl = pie.getPlayer();
		if(pl.getItemInHand()==null||pl.getItemInHand().getType()==Material.AIR)return;
		if(!contains(pl.getItemInHand().getType().getId(),pl.getItemInHand().getDurability()))return;
		if(wgp==null)return;
		GlobalRegionManager grm = wgp.getGlobalRegionManager();
		if(grm==null)return;
		RegionManager rm = grm.load(pl.getWorld());
		if(rm==null)return;
		Map<String,ProtectedRegion> prm = rm.getRegions();
		if(prm==null)return;
		boolean isIn=false;
		for(ProtectedRegion pr : rm.getRegions().values()){
			if(pr.contains(pl.getLocation().getBlockX(), pl.getLocation().getBlockY(), pl.getLocation().getBlockZ())){isIn=true;break;}
		}
		if(!isIn)return;
		pie.setCancelled(true);
		pl.sendMessage(ChatColor.DARK_RED + "You're not allowed to use that here!");
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onInteract2(PlayerInteractEvent pie){
		Player pl = pie.getPlayer();
		if(pl.getItemInHand()==null||pl.getItemInHand().getType()==Material.AIR)return;
		if(!contains(pl.getItemInHand().getType().getId(),pl.getItemInHand().getDurability()))return;
		if(fpl==null)return;
		UPlayer upl = UPlayer.get(pl);
		if(upl==null)return;
		Faction f = upl.getFaction();
		Faction facIn = BoardColls.get().getFactionAt(PS.valueOf(pl.getLocation()));
		if(facIn==null)return;
		if(!(f==facIn||facIn.getName().equals(ChatColor.DARK_GREEN + "Wilderness")||facIn.getName().equals(ChatColor.GREEN + "Wilderness"))){
			pie.setCancelled(true);
			pl.sendMessage(ChatColor.DARK_RED + "You're only allowed to use this in your own faction or the wilderness!");
		}
	}
	private boolean contains(int id, short durability) {
		List<String> banned = getConfig().getStringList("BannedItems");
		for(String s : banned){
			String ID = s.split("\\:")[0];
			String metadata = s.split("\\:")[1];
			int Id=0,Metadata=0;
			try{
				Id=Integer.parseInt(ID);
				Metadata=Short.parseShort(metadata);
			}catch(Exception e){
				getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "There was an invalid banned item in the list!");
				continue;
			}
			if(Id==id&&(Metadata==0||durability==Metadata))return true;
		}
		return false;
	}
}