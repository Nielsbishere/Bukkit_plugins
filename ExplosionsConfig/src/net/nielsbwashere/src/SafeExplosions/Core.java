package net.nielsbwashere.src.SafeExplosions;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
public class Core extends JavaPlugin implements Listener{
	String version = "1.0";
	@Override
	public void onEnable() {
		String version = getConfig().getString("Info.Version");
		if(version==null||version==""||!version.equals(this.version)){
			reset("ALL");
		}
		Bukkit.getPluginManager().registerEvents(this, this);
		if(getConfig().getBoolean("AntiTNT.PlaceRestricted")){
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
				@Override
				public void run() {
					for(Player p : getServer().getOnlinePlayers()){
						int prevPlaced = getConfig().getInt("AntiTNT.list." + p.getName());
						int toSubtract = getConfig().getInt("AntiTNT.SubtractTNT");
						if(prevPlaced>0){
							getConfig().set("AntiTNT.list." + p.getName(),prevPlaced-toSubtract<0?0:prevPlaced-toSubtract);
							saveConfig();
						}
					}
					timer=0;
				}},0, 20*getConfig().getInt("AntiTNT.PlaceRestrictionInterval"));
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
				@Override
				public void run() {
					timer++;
				}}, 0, 20);
		}
	}
	public int timer=0;
	private void reset(String type) {
		getConfig().set("Info.Version",version);
		if(type.equalsIgnoreCase("ALL")||type.equalsIgnoreCase("AntiExplosions")){
		getConfig().set("AntiExplosions.Disabled", false);
		getConfig().set("AntiExplosions.AllNonEntitiesDisabled", false);
		getConfig().set("AntiExplosions.AllBlacklisted", false);
		getConfig().set("AntiExplosions.Whitelist", new ArrayList<String>());
		getConfig().set("AntiExplosions.Blacklist", new ArrayList<String>());
		}
		if(type.equalsIgnoreCase("ALL")||type.equalsIgnoreCase("AntiTNT")){
		getConfig().set("AntiTNT.PlaceRestricted", false);
		getConfig().set("AntiTNT.PlaceRestriction", 25);
		getConfig().set("AntiTNT.PlaceRestrictionInterval", 10);
		getConfig().set("AntiTNT.SubtractTNT",1);
		}
		saveConfig();
	}
	@Override
	public void onDisable() {
		saveConfig();
		Bukkit.getScheduler().cancelTasks(this);
		timer=0;
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onTntPlace(BlockPlaceEvent e){
		if(e.getBlock().getType()!=Material.TNT)return;
		if(!getConfig().getBoolean("AntiTNT.PlaceRestricted"))return;
		if(e.getPlayer()==null){
			e.setCancelled(true);
			return;
		}
		int prevPlaced = getConfig().getInt("AntiTNT.list." + e.getPlayer().getName());
		if(prevPlaced>=getConfig().getInt("AntiTNT.PlaceRestriction")){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "You've placed too much TNT! Try again in "+ (getConfig().getInt("AntiTNT.PlaceRestrictionInterval")-timer) + " seconds!");
			return;
		}
		getConfig().set("AntiTNT.list." + e.getPlayer().getName(),prevPlaced+1);
		saveConfig();
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.LOWEST)
	public void onExplode(EntityExplodeEvent e){
		System.out.println("Boom");
		if(getConfig().getBoolean("AntiExplosions.Disabled"))return;
		if(getConfig().getBoolean("AntiExplosions.AllNonEntitiesDisabled")&&e.getEntity()==null){
			e.blockList().clear();
			e.setYield(0f);
			return;
		}
		if(e.getEntity()==null)return;
		List<String> exploding = getConfig().getStringList("AntiExplosions.ExplodingEntities");
		List<String> actual = exploding==null||exploding.isEmpty()?new ArrayList<String>():exploding;
		if(!actual.contains(""+e.getEntityType().getTypeId()+","+e.getEntityType().name())){
			actual.add(""+e.getEntityType().getTypeId()+","+e.getEntityType().name());
			getConfig().set("AntiExplosions.ExplodingEntities", actual);
			saveConfig();
			System.out.println("There's been a new entity added to the exploding entities list! " + e.getEntityType().getTypeId()+","+e.getEntityType().name());
		}
		if(isBlacklisted(e.getEntityType().getTypeId())&&!isWhitelisted(e.getEntityType().getTypeId())){
			e.blockList().clear();
			e.setYield(0f);
		}
	}
	private boolean isWhitelisted(int entityId) {
		return getConfig().getStringList("AntiExplosions.Whitelist").contains(""+entityId);
	}
	private boolean isBlacklisted(int entityId) {
		return getConfig().getBoolean("AntiExplosions.AllBlacklisted")||getConfig().getStringList("AntiExplosions.Blacklist").contains(""+entityId);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("AntiExplosions")){
			if(!sender.isOp()&&!sender.hasPermission("AntiExplosions.MainCommand")){
				sender.sendMessage(ChatColor.DARK_RED + "You don't have access to that command!");
				return false;
			}
			if(args.length==0||args[0].equalsIgnoreCase("list")){
				sender.sendMessage(ChatColor.DARK_RED + "All commands: /AntiExplosions <reset/disable/enable/blacklist/whitelist/list/removeFrom> <arguments>");
				return false;
			}
			if(args[0].equalsIgnoreCase("reset")&&(sender.hasPermission("AntiExplosions.Reset")||sender.isOp())){
				reset("AntiExplosions");
				sender.sendMessage(ChatColor.DARK_RED + "AntiExplosions config reset!");
				return false;
			}
			else if(args[0].equalsIgnoreCase("disable")&&(sender.hasPermission("AntiExplosions.Disable")||sender.isOp())){
				getConfig().set("AntiExplosions.Disabled", true);
				saveConfig();
				sender.sendMessage(ChatColor.DARK_RED + "Your world explosions won't be limited by us anymore!");
				return false;
			}
			else if(args[0].equalsIgnoreCase("enable")&&(sender.hasPermission("AntiExplosions.Enable")||sender.isOp())){
				getConfig().set("AntiExplosions.Disabled", false);
				saveConfig();
				sender.sendMessage(ChatColor.DARK_RED + "Your world explosions will be watched from now on!");
				return false;
			}
			else if(args[0].equalsIgnoreCase("blacklist")&&(sender.hasPermission("AntiExplosions.Blacklist")||sender.isOp())){
				if(args.length!=2){
					sender.sendMessage(ChatColor.DARK_RED + "You can only use /AntiExplosions blacklist <ALL/NonEntity/(entity_id)>!");
					return false;
				}
				if(args[1].equalsIgnoreCase("ALL")){
					getConfig().set("AntiExplosions.AllBlacklisted", true);
					saveConfig();
					sender.sendMessage(ChatColor.DARK_RED + "You have blacklisted all entity explosions!");
					return false;
				}else if(args[1].equalsIgnoreCase("NonEntity")){
					getConfig().set("AntiExplosions.AllNonEntitiesDisabled", true);
					saveConfig();
					sender.sendMessage(ChatColor.DARK_RED + "You have blacklisted all non-entity explosions!");
					return false;
				}else{
					for(char c : args[1].toCharArray()){
						if(c<48||c>57){
							sender.sendMessage(ChatColor.DARK_RED + "You can only put in /AntiExplosion blacklist <ALL/NonEntity/(entity_id)>");
							sender.sendMessage(ChatColor.DARK_RED + "And entity_id was not nummeric!");
							return false;
						}
					}
					int id = Integer.parseInt(args[1]);
					List<String> blacklist = getConfig().getStringList("AntiExplosions.Blacklist");
					List<String> actual = blacklist==null||blacklist.isEmpty()?new ArrayList<String>():blacklist;
					if(!actual.contains(id+"")){
						actual.add(id+"");
						getConfig().set("AntiExplosions.Blacklist", actual);
						saveConfig();
						sender.sendMessage(ChatColor.DARK_RED + "You've added this entity explosion to the blacklist!");
						return false;
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "This entity explosion was already in the blacklist!");
						return false;
					}
				}
			}
			else if(args[0].equalsIgnoreCase("whitelist")&&(sender.hasPermission("AntiExplosions.Whitelist")||sender.isOp())){
				if(args.length!=2){
					sender.sendMessage(ChatColor.DARK_RED + "You can only use /AntiExplosions whitelist <ALL/NonEntity/(entity_id)>!");
					return false;
				}
				if(args[1].equalsIgnoreCase("ALL")){
					getConfig().set("AntiExplosions.AllBlacklisted", false);
					saveConfig();
					sender.sendMessage(ChatColor.DARK_RED + "You have unblacklisted all entity explosions!");
					return false;
				}else if(args[1].equalsIgnoreCase("NonEntity")){
					getConfig().set("AntiExplosions.AllNonEntitiesDisabled", false);
					saveConfig();
					sender.sendMessage(ChatColor.DARK_RED + "You have unblacklisted all non-entity explosions!");
					return false;
				}else{
					for(char c : args[1].toCharArray()){
						if(c<48||c>57){
							sender.sendMessage(ChatColor.DARK_RED + "You can only put in /AntiExplosion whitelist <ALL/NonEntity/(entity_id)>");
							sender.sendMessage(ChatColor.DARK_RED + "And entity_id was not nummeric!");
							return false;
						}
					}
					int id = Integer.parseInt(args[1]);
					List<String> blacklist = getConfig().getStringList("AntiExplosions.Whitelist");
					List<String> actual = blacklist==null||blacklist.isEmpty()?new ArrayList<String>():blacklist;
					if(!actual.contains(id+"")){
						actual.add(id+"");
						getConfig().set("AntiExplosions.Whitelist", actual);
						saveConfig();
						sender.sendMessage(ChatColor.DARK_RED + "You've added this entity explosion to the whitelist!");
						return false;
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "This entity explosion was already in the whitelist!");
						return false;
					}
				}
			}
			else if(args[0].equalsIgnoreCase("removeFrom")&&(sender.hasPermission("AntiExplosions.removeFrom")||sender.isOp())){
				if(args.length!=3){
					sender.sendMessage(ChatColor.DARK_RED + "You can only use /AntiExplosions removeFrom <whitelist/blacklist/all> <ALL/NonEntity/(entity_id)>!");
					return false;
				}
				if(args[1].equalsIgnoreCase("whitelist")){
					if(args[2].equalsIgnoreCase("ALL")||args[2].equalsIgnoreCase("NonEntity")){
						sender.sendMessage(ChatColor.DARK_RED + "You can't use ALL or NonEntity as second argument! You can only use it with removeFrom blacklist!");
						return false;
					}else{
						for(char c : args[2].toCharArray()){
							if(c<48||c>57){
								sender.sendMessage(ChatColor.DARK_RED + "The entity_id was not nummeric!");
								return false;
							}
						}
						int id = Integer.parseInt(args[2]);
						List<String> blacklist = getConfig().getStringList("AntiExplosions.Whitelist");
						List<String> actual = blacklist==null||blacklist.isEmpty()?new ArrayList<String>():blacklist;
						if(actual.contains(id+"")){
							actual.remove(id+"");
							getConfig().set("AntiExplosions.Whitelist", actual);
							saveConfig();
							sender.sendMessage(ChatColor.DARK_RED + "You have removed " + id + " from the whitelist!");
							return false;
						}else{
							sender.sendMessage(ChatColor.DARK_RED + "That entity was not even on the whitelist!");
							return false;
						}
					}
				}else if(args[1].equalsIgnoreCase("blacklist")){
					if(args[2].equalsIgnoreCase("ALL")){
						getConfig().set("AntiExplosions.AllBlacklisted", false);
						saveConfig();
						sender.sendMessage(ChatColor.DARK_RED + "You have unblacklisted all entity explosions!");
						return false;
					}else if(args[2].equalsIgnoreCase("NonEntity")){
						getConfig().set("AntiExplosions.AllNonEntitiesDisabled", false);
						saveConfig();
						sender.sendMessage(ChatColor.DARK_RED + "You have unblacklisted all non-entity explosions!");
						return false;
					}
					else{
						for(char c : args[2].toCharArray()){
							if(c<48||c>57){
								sender.sendMessage(ChatColor.DARK_RED + "The entity_id was not nummeric!");
								return false;
							}
						}
						int id = Integer.parseInt(args[2]);
						List<String> blacklist = getConfig().getStringList("AntiExplosions.Blacklist");
						List<String> actual = blacklist==null||blacklist.isEmpty()?new ArrayList<String>():blacklist;
						if(actual.contains(id+"")){
							actual.remove(id+"");
							getConfig().set("AntiExplosions.Blacklist", actual);
							saveConfig();
							sender.sendMessage(ChatColor.DARK_RED + "You have removed " + id + " from the blacklist!");
							return false;
						}else{
							sender.sendMessage(ChatColor.DARK_RED + "That entity was not even on the blacklist!");
							return false;
						}
					}
				}else if(args[1].equalsIgnoreCase("all")){
					if(args[2].equalsIgnoreCase("ALL")){
						getConfig().set("AntiExplosions.AllBlacklisted", false);
						saveConfig();
						sender.sendMessage(ChatColor.DARK_RED + "You have unblacklisted all entity explosions!");
						return false;
					}else if(args[2].equalsIgnoreCase("NonEntity")){
						getConfig().set("AntiExplosions.AllNonEntitiesDisabled", false);
						saveConfig();
						sender.sendMessage(ChatColor.DARK_RED + "You have unblacklisted all non-entity explosions!");
						return false;
					}else{
						for(char c : args[2].toCharArray()){
							if(c<48||c>57){
								sender.sendMessage(ChatColor.DARK_RED + "The entity_id was not nummeric!");
								return false;
							}
						}
						int id = Integer.parseInt(args[2]);
						List<String> blacklist = getConfig().getStringList("AntiExplosions.Blacklist");
						List<String> actual = blacklist==null||blacklist.isEmpty()?new ArrayList<String>():blacklist;
						if(actual.contains(id+"")){
							actual.remove(id+"");
							getConfig().set("AntiExplosions.Blacklist", actual);
							saveConfig();
							sender.sendMessage(ChatColor.DARK_RED + "You have removed " + id + " from the blacklist!");
						}
						blacklist = getConfig().getStringList("AntiExplosions.Whitelist");
						actual = blacklist==null||blacklist.isEmpty()?new ArrayList<String>():blacklist;
						if(actual.contains(id+"")){
							actual.remove(id+"");
							getConfig().set("AntiExplosions.Whitelist", actual);
							saveConfig();
							sender.sendMessage(ChatColor.DARK_RED + "You have removed " + id + " from the whitelist!");
						}
					}
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "You can only use /AntiExplosions removeFrom <whitelist/blacklist/all> <ALL/NonEntity/(entity_id)>!");
					return false;
				}
			}
			else{
				sender.sendMessage(ChatColor.DARK_RED + "You either didn't have permission for that command, or it didn't exist, try using /AntiExplosions list and look through the commands before you run it!");
				return false;
			}	
		}else if(label.equalsIgnoreCase("AntiTNT")){
			if(!sender.isOp()&&!sender.hasPermission("AntiTNT.MainCommand")){
				sender.sendMessage(ChatColor.DARK_RED + "You don't have access to that command!");
				return false;
			}
			if(args.length==0||args[0].equalsIgnoreCase("list")){
				sender.sendMessage(ChatColor.DARK_RED + "All commands: /AntiTNT <reset/disable/enable/setInterval/setRestriction/setSubtractAmount/list> <arguments>");
				return false;
			}
			if(args[0].equalsIgnoreCase("reset")&&(sender.hasPermission("AntiTNT.Reset")||sender.isOp())){
				reset("AntiTNT");
				sender.sendMessage(ChatColor.DARK_RED + "AntiTNT config reset!");
				return false;
			}else if(args[0].equalsIgnoreCase("disable")&&(sender.hasPermission("AntiTNT.Disable")||sender.isOp())){
				getConfig().set("AntiTNT.PlaceRestricted", false);
				saveConfig();
				sender.sendMessage(ChatColor.DARK_RED + "Your TNT placing will now be watched by AntiTNT!");
				return false;
			}else if(args[0].equalsIgnoreCase("enable")&&(sender.hasPermission("AntiTNT.Enable")||sender.isOp())){
				getConfig().set("AntiTNT.PlaceRestricted", true);
				saveConfig();
				sender.sendMessage(ChatColor.DARK_RED + "You are now free to place as much TNT as you want!");
				return false;
			}else if(args[0].equalsIgnoreCase("setInterval")&&(sender.hasPermission("AntiTNT.setInterval")||sender.isOp())){
				if(args.length!=2){
					sender.sendMessage(ChatColor.DARK_RED + "You must use /AntiTNT <setInterval> <intervalInSecs>");
					return false;
				}
				for(char c : args[1].toCharArray()){
					if(c<48||c>57){
						sender.sendMessage(ChatColor.DARK_RED + "The interval must be nummeric!");
						return false;
					}
				}
				int interval = Integer.parseInt(args[1]);
				getConfig().set("AntiTNT.PlaceRestrictionInterval", interval);
				saveConfig();
				sender.sendMessage(ChatColor.DARK_RED + "People can now place +1 TNT every " + args[1] + " seconds!");
				return false;
			}else if(args[0].equalsIgnoreCase("setRestriction")&&(sender.hasPermission("AntiTNT.setRestriction")||sender.isOp())){
				if(args.length!=2){
					sender.sendMessage(ChatColor.DARK_RED + "You must use /AntiTNT <setRestriction> <amountOfTNT>");
					return false;
				}
				for(char c : args[1].toCharArray()){
					if(c<48||c>57){
						sender.sendMessage(ChatColor.DARK_RED + "The amount of TNT must be nummeric!");
						return false;
					}
				}
				int interval = Integer.parseInt(args[1]);
				getConfig().set("AntiTNT.PlaceRestriction", interval);
				saveConfig();
				sender.sendMessage(ChatColor.DARK_RED + "People can now place a max of " + interval + " TNT!");
				return false;
			}else if(args[0].equalsIgnoreCase("setSubtractAmount")&&(sender.hasPermission("AntiTNT.setSubtractAmount")||sender.isOp())){
				if(args.length!=2){
					sender.sendMessage(ChatColor.DARK_RED + "You must use /AntiTNT <setSubtractAmount> <amountOfTNT>");
					return false;
				}
				for(char c : args[1].toCharArray()){
					if(c<48||c>57){
						sender.sendMessage(ChatColor.DARK_RED + "The amount of TNT must be nummeric!");
						return false;
					}
				}
				int interval = Integer.parseInt(args[1]);
				getConfig().set("AntiTNT.SubtractTNT", interval);
				saveConfig();
				sender.sendMessage(ChatColor.DARK_RED + "People can now get back " + interval + " TNT per time period!");
				return false;
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "All commands: /AntiTNT <reset/disable/enable/setInterval/setRestriction/list> <arguments>");
				return false;
			}
		} 
		return false;
	}
}