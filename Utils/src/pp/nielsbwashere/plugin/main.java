package pp.nielsbwashere.plugin;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
@SuppressWarnings("deprecation")
public class main extends JavaPlugin implements Listener{
	public static Logger logger = Logger.getLogger("Minecraft");
	File f = new File(this.getDataFolder() + "/Data/chat.txt");
	File f2 = new File(this.getDataFolder() + "/Data/commands.txt");	
	DateFormat df1 = new SimpleDateFormat("hh:mm a");
	String time1 = df1.format(new Date());
	DateFormat df2 = new SimpleDateFormat("MM/dd HH:mm:ss");
	String time2 = df2.format(new Date());
	DateFormat month = new SimpleDateFormat("MM");
	String monthtime = month.format(new Date());
	FileWriter fw;
	FileWriter fw2;
	BufferedWriter bufferedWriter;
	BufferedWriter bufferedWriter2;
	BufferedReader reader;
	Random r = new Random();
	public void onDisable() {
		PluginDescriptionFile pdf = this.getDescription();
		logger.info(pdf.getName() + " has been disabled");
		saveConfig();
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bufferedWriter2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void onEnable() {
		PluginDescriptionFile pdf = this.getDescription();
		logger.info(pdf.getName() + " has been enabled");
		saveConfig();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!f2.exists()){
			try {
				f2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
		fw = new FileWriter(this.getDataFolder() + "/Data/chat.txt", true);
	    bufferedWriter = new BufferedWriter(fw);
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
		fw2 = new FileWriter(this.getDataFolder() + "/Data/commands.txt", true);
	    bufferedWriter2 = new BufferedWriter(fw2);
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			reader=new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayer(PlayerEvent e){
		String line = null;
		try{
			line = reader.readLine();
		}catch(Exception e2){
			e2.printStackTrace();
		}
		if(line!=null){
			if(!line.startsWith(monthtime)&&!line.startsWith("0"+ monthtime)&&!line.startsWith("00")&&!line.startsWith(monthtime)){
				f.delete();
				try {
					f.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
				f2.delete();
				try {
					f2.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
			}
		}
	}
	
	@SuppressWarnings("unused")
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		//ignore and unignore
		if(label.equalsIgnoreCase("ignore")){
			if(args.length==1){
				if(sender.hasPermission("tpp.ignore.you")||sender.hasPermission("tpp.ignore.*")||sender.isOp()){
				String whoToIgnore = args[0];
				if(whoToIgnore.equalsIgnoreCase("all")){
					getConfig().set("ignores." + sender.getName() + ".all", true);
					saveConfig();
					sender.sendMessage(ChatColor.GOLD + "You have succesfully ignored " + ChatColor.AQUA + "everyone");
				}else{
					getConfig().set("ignores." + sender.getName() + "." + whoToIgnore, true);
					sender.sendMessage(ChatColor.GOLD + "You have succesfully ignored " + ChatColor.AQUA + whoToIgnore);
					saveConfig();
				}
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "You don't have permissions!");
				}
			}else if(args.length==2){
				if(sender.hasPermission("tpp.ignore.other")||sender.hasPermission("tpp.ignore.*")||sender.isOp()){
					String whoYouWantToIgnoreSomeone = args[0];
					String whoToIgnore = args[1];
					if(whoToIgnore.equalsIgnoreCase("all")){
						getConfig().set("ignores." + whoYouWantToIgnoreSomeone + ".all", true);
						saveConfig();
						sender.sendMessage(ChatColor.GOLD + "You have succesfully made " + whoYouWantToIgnoreSomeone + " ignore " + ChatColor.AQUA + "everyone");
					}else{
						getConfig().set("ignores." + whoYouWantToIgnoreSomeone + "." + whoToIgnore, true);
						saveConfig();
						sender.sendMessage(ChatColor.GOLD + "You have succesfully made " + whoYouWantToIgnoreSomeone + " ignore " + ChatColor.AQUA + whoToIgnore);
					}
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
				}
				}
		}else if(label.equalsIgnoreCase("unignore")){
			if(args.length==1){
				if(sender.hasPermission("tpp.unignore.you")||sender.hasPermission("tpp.unignore.*")||sender.isOp()){
				String whoToIgnore = args[0];
				if(whoToIgnore.equalsIgnoreCase("all")){
					getConfig().set("ignores." + sender.getName() + ".all", false);
					saveConfig();
					sender.sendMessage(ChatColor.GOLD + "You have succesfully unignored " + ChatColor.AQUA + "everyone");
				}else{
					getConfig().set("ignores." + sender.getName() + "." + whoToIgnore, false);
					saveConfig();
					sender.sendMessage(ChatColor.GOLD + "You have succesfully unignored " + ChatColor.AQUA + whoToIgnore);
				}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
			}
			}else if(args.length==2){
				if(sender.hasPermission("tpp.unignore.other")||sender.hasPermission("tpp.unignore.*")||sender.isOp()){
					String whoYouWantToIgnoreSomeone = args[0];
					String whoToIgnore = args[1];
					if(whoToIgnore.equalsIgnoreCase("all")){
						getConfig().set("ignores." + whoYouWantToIgnoreSomeone + ".all", false);
						saveConfig();
						sender.sendMessage(ChatColor.GOLD + "You have succesfully made " + whoYouWantToIgnoreSomeone + " unignore " + ChatColor.AQUA + "everyone");
					}else{
						getConfig().set("ignores." + whoYouWantToIgnoreSomeone + "." + whoToIgnore, false);
						saveConfig();
						sender.sendMessage(ChatColor.GOLD + "You have succesfully made " + whoYouWantToIgnoreSomeone + " unignore " + ChatColor.AQUA + whoToIgnore);
					}
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
				}
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "Wrong usage: </command> <whoToIgnore/all> or </command> <whoToMakeSomeoneIgnore> <whoToIgnore/all>");
				}
			}
		//vanish, unvanish, showplayer, hideplayer
		else if(label.equalsIgnoreCase("vanish")){
			if(sender instanceof Player){
				if(args.length==0){
					if(sender.hasPermission("tpp.vanish.self")||sender.hasPermission("tpp.vanish.*")||sender.isOp()){
				Player p = (Player)sender;
				for(Player pl : getServer().getOnlinePlayers()){
					pl.hidePlayer(p);
				getConfig().set("vanished." + p.getName() + ".to." + pl.getName(), true);
				saveConfig();
				}
				p.sendMessage(ChatColor.GOLD + "You have been vanished");
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
				}
				}else if(args.length==1){
					String playerName = args[0];
					Player p;
					if(sender instanceof Player){ 
						p = (Player)sender;
					}else{
						return false;
					}
					if(getServer().getPlayer(playerName) != null){
						if(sender.hasPermission("tpp.vanish.other")||sender.hasPermission("tpp.vanish.*")||sender.isOp()){
						Player ppl = getServer().getPlayer(playerName);
					for(Player pl : getServer().getOnlinePlayers()){
						pl.hidePlayer(ppl);
					getConfig().set("vanished." + ppl.getName() + ".to." + pl.getName(), true);
					saveConfig();
					}
					p.sendMessage(ChatColor.GOLD + "You have vanished " + playerName);
						}else{
							p.sendMessage(ChatColor.DARK_RED + "You are not permitted to use that command");
						}
				}
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "Wrong usage: </command> or </command> <player>");
				}
			}
		}else if(label.equalsIgnoreCase("unvanish")){
			if(sender instanceof Player){
				if(sender.hasPermission("tpp.unvanish.self")||sender.hasPermission("tpp.unvanish.*")||sender.isOp()){
				if(args.length==0){
				Player p = (Player)sender;
				for(Player pl : getServer().getOnlinePlayers()){
					pl.showPlayer(p);
				getConfig().set("vanished." + p.getName() + ".to." + pl.getName(), false);
				saveConfig();
				}
				p.sendMessage(ChatColor.GOLD + "You have been unvanished");
				}
				}else if(args.length==1){
					String playerName = args[0];
					Player p;
					if(getServer().getPlayer(playerName) != null){
						if(sender instanceof Player){ 
							p = (Player)sender;
						}else{
							return false;
						}
						if(sender.hasPermission("tpp.unvanish.other")||sender.hasPermission("tpp.unvanish.*")||sender.isOp()){
						Player ppl = getServer().getPlayer(playerName);
					for(Player pl : getServer().getOnlinePlayers()){
						pl.showPlayer(ppl);
					getConfig().set("vanished." + ppl.getName() + ".to." + pl.getName(), false);
					saveConfig();
					}
					p.sendMessage(ChatColor.GOLD + "You have unvanished " + playerName);
						}else{
							p.sendMessage(ChatColor.DARK_RED + "You are not permitted to use that command");
						}
				}
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "Wrong usage: </command> or </command> <player>");
				}
			}
		}else if(label.equalsIgnoreCase("hidePlayer")){
			if(args.length==1){
				if(sender instanceof Player){
					if(sender.hasPermission("tpp.hideplayer.self")||sender.hasPermission("tpp.hideplayer.*")||sender.isOp()){
					Player p = (Player)sender;
					String pname = args[0];
					if(getServer().getPlayer(pname)!=null){
						p.hidePlayer(getServer().getPlayer(pname));
					getConfig().set("vanished." + p.getName() + ".to." + getServer().getPlayer(pname).getName(), true);
					saveConfig();
					}
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
					}
				}
			}else if(args.length==2){
				String pname = args[0];
				String ppname = args[1];
				if(getServer().getPlayer(pname)!=null&&getServer().getPlayer(ppname)!=null){
					Player p = getServer().getPlayer(pname);
					Player pp = getServer().getPlayer(ppname);
					if(sender.hasPermission("tpp.hideplayer.other")||sender.hasPermission("tpp.hideplayer.*")||sender.isOp()){
						p.hidePlayer(pp);
					getConfig().set("vanished." + p.getName() + ".to." + pp.getName(), true);
					saveConfig();
						sender.sendMessage(ChatColor.GOLD + "You have hidden " + ppname + " from " + pname);
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "You are not permitted to use that command");
					}
				}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Wrong usage: </command> <player> or </command> <player> <playertohide>");
			}
		}else if(label.equalsIgnoreCase("showPlayer")){
			if(args.length==1){
				if(sender.hasPermission("tpp.showplayer.self")||sender.hasPermission("tpp.showplayer.*")||sender.isOp()){
				if(sender instanceof Player){
					Player p = (Player)sender;
					String pname = args[0];
					if(getServer().getPlayer(pname)!=null){
						p.showPlayer(getServer().getPlayer(pname));
					getConfig().set("vanished." + p.getName() + ".to." + getServer().getPlayer(pname).getName(), false);
					saveConfig();
					}
				}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
			}
			}else if(args.length==2){
				String pname = args[0];
				String ppname = args[1];
				if(getServer().getPlayer(pname)!=null&&getServer().getPlayer(ppname)!=null){
					Player p = getServer().getPlayer(pname);
					Player pp = getServer().getPlayer(ppname);
					if(sender.hasPermission("tpp.showplayer.other")||sender.hasPermission("tpp.showplayer.*")||sender.isOp()){
						p.showPlayer(pp);
					getConfig().set("vanished." + p.getName() + ".to." + pp.getName(), false);
					saveConfig();
						sender.sendMessage(ChatColor.GOLD + "You have shown " + ppname + " from " + pname);
					}else{
						sender.sendMessage(ChatColor.DARK_RED + "You are not permitted to use that command");
					}
				}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Wrong usage: </command> <player> or </command> <player> <playertohide>");
			}
		}
		//change name stuff
		else if(label.equalsIgnoreCase("changename")){
			if(args.length==1){
				if(sender.hasPermission("tpp.changename")||sender.isOp()){
				if(sender instanceof Player){
					((Player) sender).setPlayerListName(args[0]);
					((Player) sender).setDisplayName(args[0]);
					Player p = (Player)sender;
					for(Player pl : getServer().getOnlinePlayers()){
							
					}
					sender.sendMessage(ChatColor.GOLD + "Your name has been changed to " + args[0]);
				}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
			}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Wrong usage: </command> <newname>");
			}
		}
		//sethome, home, spawn and setspawn stuff
		else if(label.equalsIgnoreCase("home")){
			if(args.length==1){
				if(!isNumeric(args[0])){
				sender.sendMessage(ChatColor.DARK_RED + "You can only put in numbers!");
				return true;
				}
				if(!sender.hasPermission("tpp.home." + Integer.valueOf(args[0]))&&!sender.hasPermission("tpp.home.*")){
					sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
					return true;
				}
				int num = Integer.parseInt(args[0]);
				int x = getConfig().getInt(sender.getName() + "." + num + ".x");
				int y = getConfig().getInt(sender.getName() + "." + num + ".y");
				int z = getConfig().getInt(sender.getName() + "." + num + ".z");
				String worldname = getConfig().getString(sender.getName() + "." + num + ".world");
				World world;
				if(worldname!=null){
				world = getServer().getWorld(worldname);
				}else{
					world = null;
				}
				Location l;
				if(x!=0&&y!=0&&z!=0&&world!=null){
					l = new Location(world,x,y,z);
					if(sender instanceof Player){
						((Player) sender).teleport(l);
						sender.sendMessage(ChatColor.GREEN + "You have been teleported to your home!");
					}else{
						sender.sendMessage(ChatColor.RED + "You are not a player so you can't use /home!");
					}
				}else{
					sender.sendMessage(ChatColor.RED + "Your home x, y, z or world is invalid, or you haven't set your home!");
				}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Incorrect usage: </command> <num>");
			}
		}else if(label.equalsIgnoreCase("sethome")){
			if(args.length==1){
				if(sender instanceof Player){
					if(Integer.valueOf(args[0])==null){
						sender.sendMessage(ChatColor.DARK_RED + "You can only put in numbers!");
						return true;
						}
						if(!sender.hasPermission("tpp.sethome." + Integer.valueOf(args[0]))&&!sender.hasPermission("tpp.sethome.*")){
							sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
							return true;
						}
					Player p = (Player)sender;
				int x = p.getLocation().getBlockX();
				int y = p.getLocation().getBlockY();
				int z = p.getLocation().getBlockZ();
				String worldname = p.getWorld().getName();
				int num = Integer.parseInt(args[0]);
				getConfig().set(p.getName() + "." + num + ".x" , x);
				getConfig().set(p.getName() + "." + num + ".y" , y);
				getConfig().set(p.getName() + "." + num + ".z" , z);
				getConfig().set(p.getName() + "." + num + ".world" , worldname);
				if(getConfig().getInt(p.getName() + ".amount")>num){
				getConfig().set(p.getName() + ".amount" , num+1);
				}
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "You have set your home!");
			}else{
				sender.sendMessage(ChatColor.RED + "You are not a player so you can't use /sethome!");
			}
			}else{
				sender.sendMessage(ChatColor.RED + "Incorrect usage: /sethome or /sethome <number>");
			}
		}else if(label.equalsIgnoreCase("setspawn")){
			if(sender instanceof Player){
				if(sender.hasPermission("tpp.setspawn")||sender.isOp()){
				Player p = (Player)sender;
			getConfig().set(p.getWorld().getName() + ".spawnX", p.getLocation().getBlockX());
			getConfig().set(p.getWorld().getName() + ".spawnY", p.getLocation().getBlockY());
			getConfig().set(p.getWorld().getName() + ".spawnZ", p.getLocation().getBlockZ());
			saveConfig();
			sender.sendMessage(ChatColor.GOLD + "Spawn set!");
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
			}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "You are not a player so you can't set the spawn!");
			}
		}else if(label.equalsIgnoreCase("spawn")){
			if(sender instanceof Player){
				if(sender.hasPermission("tpp.spawn")||sender.isOp()){
				Player p = (Player)sender;
			int x = getConfig().getInt(p.getWorld().getName() + ".spawnX", p.getLocation().getBlockX());
			int y = getConfig().getInt(p.getWorld().getName() + ".spawnY", p.getLocation().getBlockY());
			int z = getConfig().getInt(p.getWorld().getName() + ".spawnZ", p.getLocation().getBlockZ());
			Location l;
			if(x!=0&&y!=0&&z!=0){
				l = new Location(p.getWorld(),x,y,z);
				p.teleport(l);
			sender.sendMessage(ChatColor.GOLD + "Warped back to spawn!");
			}else{
				sender.sendMessage(ChatColor.GOLD + "Spawn not set, ask the owner to set a spawn for this world!");
			}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
			}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "You are not a player so you can't go to spawn!");
			}
		}
		//</protect> <x> <y> <z> <x2> <y2> <z2> <name1> <name2> <name3> <pvp> <moving> <destroying> <placement> <interaction>
		//Protect stuff
		else if(label.equalsIgnoreCase("protect")){
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "Consoles can't protect stuff.");
				return true;
			}
			if(args.length != 14 && args.length!=8){
				sender.sendMessage(ChatColor.DARK_RED + "Invalid usage: </command> <x> <y> <z> <x2> <y2> <z2> <name1> <name2> <name3> <pvp> <moving> <destroying> <placement> <interaction>");
				sender.sendMessage(ChatColor.DARK_RED + "<x> <y> <z> <x2> <y2> <z2> can only be numbers");
				sender.sendMessage(ChatColor.DARK_RED + "<pvp> <moving> <destroying> <placement> <interaction> can only be true/false");
				return true;
			}else if(args.length==8){ //     /protect addowner/removeowner <x> <y> <z> <x2> <y2> <z2> <name>
				protectOwner((Player)sender,args);
				return true;
			}
			if(!sender.hasPermission("tpp.protect")){
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
				return true;
			}
			if(!isNumeric(args[0])){
				sender.sendMessage(ChatColor.DARK_RED + "<x> can only be a valid number!");
				return true;
			}
			if(!isNumeric(args[1])){
				sender.sendMessage(ChatColor.DARK_RED + "<y> can only be a valid number!");
				return true;
			}
			if(!isNumeric(args[2])){
				sender.sendMessage(ChatColor.DARK_RED + "<z> can only be a valid number!");
				return true;
			}
			if(!isNumeric(args[3])){
				sender.sendMessage(ChatColor.DARK_RED + "<x2> can only be a valid number!");
				return true;
			}
			if(!isNumeric(args[4])){
				sender.sendMessage(ChatColor.DARK_RED + "<y2> can only be a valid number!");
				return true;
			}
			if(!isNumeric(args[5])){
				sender.sendMessage(ChatColor.DARK_RED + "<z2> can only be a valid number!");
				return true;
			}
			
			int ox = Integer.parseInt(args[0]);
			int oy = Integer.parseInt(args[1]);
			int oz = Integer.parseInt(args[2]);
			int ox2 = Integer.parseInt(args[3]);
			int oy2 = Integer.parseInt(args[4]);
			int oz2 = Integer.parseInt(args[5]);
			String owner1 = args[6];
			String owner2 = args[7];
			String owner3 = args[8];
			Player p = (Player)sender;
			World w = p.getWorld();
			String world = w.getName();
			boolean pvp = true, moving= true, destroying= true, placement= true, interaction= true;
			
			if(args[9].equalsIgnoreCase("false")||args[9].equalsIgnoreCase("true")){
				if(args[9].equalsIgnoreCase("false")){
					pvp = false;
				}else if(args[9].equalsIgnoreCase("true")){
					pvp = true;
				}
			}else{
				p.sendMessage(ChatColor.DARK_RED + "<pvp> can only be true/false");
				return true;
			}
			if(args[10].equalsIgnoreCase("false")||args[10].equalsIgnoreCase("true")){
				if(args[10].equalsIgnoreCase("false")){
					moving = false;
				}else if(args[10].equalsIgnoreCase("true")){
					moving = true;
				}
			}else{
				p.sendMessage(ChatColor.DARK_RED + "<moving> can only be true/false");
				return true;
			}
			if(args[11].equalsIgnoreCase("false")||args[11].equalsIgnoreCase("true")){
				if(args[11].equalsIgnoreCase("false")){
					destroying = false;
				}else if(args[11].equalsIgnoreCase("true")){
					destroying = true;
				}
			}else{
				p.sendMessage(ChatColor.DARK_RED + "<destroying> can only be true/false");
				return true;
			}
			if(args[12].equalsIgnoreCase("false")||args[12].equalsIgnoreCase("true")){
				if(args[12].equalsIgnoreCase("false")){
					placement = false;
				}else if(args[12].equalsIgnoreCase("true")){
					placement = true;
				}
			}else{
				p.sendMessage(ChatColor.DARK_RED + "<placement> can only be true/false");
				return true;
			}
			if(args[13].equalsIgnoreCase("false")||args[13].equalsIgnoreCase("true")){
				if(args[13].equalsIgnoreCase("false")){
					interaction = false;
				}else if(args[13].equalsIgnoreCase("true")){
					interaction = true;
				}
			}else{
				p.sendMessage(ChatColor.DARK_RED + "<interaction> can only be true/false");
				return true;
			}
			int x = 0, y = 0, z = 0, x2 = 0, y2 = 0, z2 = 0;
			if(ox2<ox){
				x=ox2;
				x2=ox;
			}else if(ox2>ox){
				x=ox;
				x2=ox2;
			}
			
			if(oy2<oy){
				y=oy2;
				y2=oy;
			}else if(oy2>oy){
				y=oy;
				y2=oy2;
			}
			
			if(oz2<oz){
				z=oz2;
				z2=oz;
			}else if(oz2>oz){
				z=oz;
				z2=oz2;
			}
			int am = getConfig().getInt("Claim.Amount");
			getConfig().set(am+".p1.x", x);
			getConfig().set(am+".p1.y", y);
			getConfig().set(am+".p1.z", z);
			getConfig().set(am+".p2.x", x2);
			getConfig().set(am+".p2.y", y2);
			getConfig().set(am+".p2.z", z2);
			getConfig().set(am+".owner1", owner1);
			getConfig().set(am+".owner2", owner2);
			getConfig().set(am+".owner3", owner3);
			getConfig().set(am+".pvp", pvp);
			getConfig().set(am+".moving", moving);
			getConfig().set(am+".destroying", destroying);
			getConfig().set(am+".placing", placement);
			getConfig().set(am+".interacting", interaction);
			getConfig().set("Claim.Amount", am+1);
			getConfig().set(am+".world", world);
			saveConfig();
		}
		//Fake join/leave/death for roleplaying and stuff
		else if(label.equalsIgnoreCase("fakejoin")){
			if(args.length==1){
				if(sender.hasPermission("tpp.fakejoin")||sender.isOp()){
				getServer().broadcastMessage(ChatColor.YELLOW + args[0] + " joined the game");
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
			}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Incorrect usage; </command> <name>");
			}
		}else if(label.equalsIgnoreCase("fakeleave")){
			if(args.length==1){
				if(sender.hasPermission("tpp.fakeleave")||sender.isOp()){
				getServer().broadcastMessage(ChatColor.YELLOW + args[0] + " left the game");
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "You don't have the permissions to do that!");
			}
		}else{
				sender.sendMessage(ChatColor.DARK_RED + "Incorrect usage; </command> <name>");
			}
		}else if(label.equalsIgnoreCase("fakedeath")){
			if(args.length==1){
				if(sender.hasPermission("tpp.fakedeath")||sender.isOp()){
				getServer().broadcastMessage(ChatColor.RESET + args[0] + " was killed by magic");
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
			}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Incorrect usage; </command> <name>");
			}
		}
		//stuff for when someone subs/follows
		else if(label.equalsIgnoreCase("getLucky")){
			if(!sender.isOp()&&!sender.hasPermission("tpp.getLucky")){
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
				return true;
			}
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "You are not a player, so you can't get lucky :/");
				return true;
			}
			Player p = (Player)sender;
			int rand = r.nextInt(100);
			if(rand<10){
				int luckyID = getConfig().getInt("Prizes.LuckyID");
				if(p.getWorld().getBlockAt((new Location(p.getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()))).setTypeId(luckyID)){
				p.sendMessage(ChatColor.GOLD + "You just got lucky!");
				}else{
					p.sendMessage(ChatColor.DARK_RED + "You got lucky, but the luckyID is not configured or there was no space for it :/");
				}
			}else if(rand==97||rand==96){
				int pandoraID = getConfig().getInt("Prizes.PandoraID");
				if(p.getWorld().getBlockAt((new Location(p.getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()))).setTypeId(pandoraID)){
					p.sendMessage(ChatColor.GOLD + "You just got extremely lucky!");
					}else{
						p.sendMessage(ChatColor.DARK_RED + "You got extremely lucky, but the pandoraID is not configured or there was no space for it :/");
					}
			}else{
				p.sendMessage(ChatColor.DARK_RED + "You didn't get lucky :/");
			}
		}
		else if(label.equalsIgnoreCase("setLuckyID")){
			if(!sender.isOp()&&!sender.hasPermission("tpp.setLuckyID")){
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
				return true;
			}
			if(args.length!=1){
				sender.sendMessage(ChatColor.DARK_RED + "Incorrect usage: /<command> <id>");
				return true;
			}
			if(Integer.valueOf(args[0])==null){
				sender.sendMessage(ChatColor.DARK_RED + "ID can't be equal to zero!");
				return true;
			}
			int id = Integer.valueOf(args[0]);
			getConfig().set("Prizes.LuckyID", id);
			saveConfig();
			sender.sendMessage(ChatColor.GOLD + "You have set the id of a lucky block!");
		}
		else if(label.equalsIgnoreCase("setPandoraID")){
			if(!sender.isOp()&&!sender.hasPermission("tpp.setPandoraID")){
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
				return true;
			}
			if(args.length!=1){
				sender.sendMessage(ChatColor.DARK_RED + "Incorrect usage: /<command> <id>");
				return true;
			}
			if(Integer.valueOf(args[0])==null){
				sender.sendMessage(ChatColor.DARK_RED + "ID can't be equal to zero!");
				return true;
			}
			int id = Integer.valueOf(args[0]);
			getConfig().set("Prizes.PandoraID", id);
			saveConfig();
			sender.sendMessage(ChatColor.GOLD + "You have set the id of a pandora box!");
		}
		else if(label.equalsIgnoreCase("setLuckyWorld")){
			if(!sender.hasPermission("tpp.setLuckyWorld")){
				if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
				return true;
				}
			}
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "You are not a player so you can't set spawns!");
				return true;
			}
			Player p = (Player)sender;
			sender.sendMessage(ChatColor.GOLD + "You have set the lucky world spawn!");
			getConfig().set("Prizes.Spawn.X", p.getLocation().getBlockX());
			getConfig().set("Prizes.Spawn.Y", p.getLocation().getBlockY());
			getConfig().set("Prizes.Spawn.Z", p.getLocation().getBlockZ());
			getConfig().set("Prizes.Spawn.World", p.getWorld().getName());
			saveConfig();
		}else if(label.equalsIgnoreCase("onFollow")){
				if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
				return true;
				}
			if(args.length!=1){
				return true;
			}
			if(getServer().getPlayer("nielsbwashere")==null){
				getServer().broadcastMessage(ChatColor.GOLD + args[0] + " has followed nielsbwashere on Twitch!");
				return true;
			}
			Player p = getServer().getPlayer("nielsbwashere");
			p.sendMessage(ChatColor.DARK_RED + args[0] + " has followed you on Twitch!");
			Bukkit.dispatchCommand(p, "getLucky");
		}else if(label.equalsIgnoreCase("onDonate")){
				if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
				return true;
				}
			if(args.length!=1){
				return true;
			}
			if(getServer().getPlayer("nielsbwashere")==null){
				getServer().broadcastMessage(ChatColor.GOLD + args[0] + " has donated to nielsbwashere on Twitch!");
				return true;
			}
			Player p = getServer().getPlayer("nielsbwashere");
			p.sendMessage(ChatColor.DARK_RED + args[0] + " has donated on Twitch!");
			for(int i=0;i<10;i++){
			Bukkit.dispatchCommand(p, "getLucky");
			}
		}
		//Fly and god
		else if(label.equalsIgnoreCase("fly")){
			if(args.length!=0){
				sender.sendMessage(ChatColor.DARK_RED + "Incorrect usage: /<command>");
				return true;
			}
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "You are not a player so you can't use /fly");
				return true;
			}
			if(!sender.hasPermission("tpp.fly")){
				if(!sender.isOp()){
					sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
					return true;
				}
			}
			Player p = (Player)sender;
			p.setAllowFlight(true);
			p.setFlying(true);
			p.sendMessage(ChatColor.GOLD + "I believe I can fly, I believe I can touch the sky, I think of it every night and then, swing along and fly away.");
		}
		else if(label.equalsIgnoreCase("god")){
			if(args.length!=0){
				sender.sendMessage(ChatColor.DARK_RED + "Incorrect usage: /<command>");
				return true;
			}
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "You are not a player so you can't use /god");
				return true;
			}
			if(!sender.hasPermission("tpp.god")){
				if(!sender.isOp()){
					sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
					return true;
				}
			}
			Player p = (Player)sender;
			if(!getConfig().getBoolean(p.getName() + ".god")){
			getConfig().set(p.getName() + ".god", true);
			saveConfig();
			p.sendMessage(ChatColor.GOLD + "Those who are invincible are sometimes vincible.");
			}else{
				getConfig().set(p.getName() + ".god", false);
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "Be vincible!");
			}
		}
		//Money, sell, buy
		else if(label.equalsIgnoreCase("money")){
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "You are a console, console's don't have money, stupid!");
				return true;
			}
			if(args.length==0){
				if(!sender.hasPermission("tpp.money.*")&&!sender.hasPermission("tpp.money")&&!sender.isOp()){
					sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
					return true;
				}
			Player p = (Player)sender;
			double pT = getConfig().getInt(p.getName() + ".pT"); //pT is just like a euro; you can have 0.01pT meaning that you have 1 pTC, or you can have 0.1pT meaning that you have 10 pTC
			int pC = getConfig().getInt(p.getName() + ".pC"); //1 pC = 10pT
			int pD = getConfig().getInt(p.getName() + ".pD"); //1 pD = 100pC
			int pPT = getConfig().getInt(p.getName() + ".pPT"); //1 pPT is worth 10 pD meaning 1kpT
			//pPT is not money, although you can use it to get a reward (Like a buff for 360 secs (= 6 min = 0.1 hours))
			//You can sell it to someone that has interest in it; and it will automatically ask 1kpT from the other Player
			//pPT can be used to pay for 'donator' perks, although you would need 1kpPT (1000pPT)
			//Rewards: The more pPT you lay in by a chance game, the less chance you have to win, although you will always win a buff.
			//The level of your buff will be depending on the amount of pPT you betted.
			//you need to pay in some diamonds/gold/emerald/iron/nether stars
			//The chance to win your money back, is 10%, although the chance to double it will be depending on the amount of players that are betting.
			//If there are >2 players online you can bet.
			//The chance to win your money back, is lower if there's a larger gap between the first bet and your bet.
			//if there is a gap between your money you pay, the pPT you bet and the valuables you bet, you'll be able to calculate the chance of winning:
			//(10-gap)%
			//The rewards of winning =
			//the higher amount of pPT you throw in, the better buffs you will get.
			//There is a percent chance (30%) that you won't get the effect from your layer and that it will try to go down the list.
			//For instance; you have used 2pPT to get a buff, BUT you get speed, that is 30% chance
			//but if you have used 3pPT to get a buff, there's a chance of (30-(0.3^2)) for the night vision to appear and a chance of (0.3^2) for the speed appearing.
			//The higher you get (For instance over at 10pPT) the more potion effects you might get (30-(0.3^2)-(0.3^3)-(0.3^4)-(0.3^5)-(0.3^6)-(0.3^7)-(0.3^8)-(0.3^9))
			//The chance formula is: 0.3^(pPT-a) (a = the amount of effects you have to go down)
			//1pPT = night vision
			//2pPT = water breathing
			//3pPT = fire resistance
			//4pPT = speed
			//5pPT = haste
			//6pPT = jump boost
			//7pPT = saturation
			//8pPT = regen
			//9pPT = absorption
			//10pPT = health boost
			//The amount of the potion lasting will depend on the amount of valuables put in;
			//Nether star = 80VP
			//Emerald = 50VP
			//Diamond = 32VP
			//Gold = 8VP
			//Iron = 2VP
			//TVP = (ironputin*ironVP)+(goldputin*goldVP)+(diamondputin*diamondVP)+(emeraldputin*emeraldVP)+(netherstarputin*netherstarVP)
			//Duration = (360)+(TVP*10)
			//The amplifier on potion effects gets calculated this way (moneyPaid/100)
			//The amplifier will depend on the effect you will get and can't get higher than a certain amount.
			//night vision = 1
			//water breathing = 3
			//fire resistance = 1
			//speed = 3
			//haste = 3
			//jump boost = 3
			//saturation = 2
			//regen = 3
			//absorption = 10
			//health boost = 5
			p.sendMessage(ChatColor.GOLD + "You have got " + pT + "pT, " + pC + "pC and " + pD + "pD");
			p.sendMessage(ChatColor.GOLD + "You have also got " + pPT + "pPT");
			}else if(args.length==1){
				String label2 = args[0];
				if(label2.equalsIgnoreCase("info")){
					if(!sender.hasPermission("tpp.money.*")&&!sender.hasPermission("tpp.money.info")&&!sender.isOp()){
						sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
						return true;
					}
					sender.sendMessage(ChatColor.GOLD + "'/money' to look how much money you have");
					sender.sendMessage(ChatColor.GOLD + "'/money sell' to sell items to others for money");
					sender.sendMessage(ChatColor.GOLD + "'/money buy' to buy items from others for money");
					sender.sendMessage(ChatColor.GOLD + "'/money give' to give yourself or others money (Only for OPS)");
					sender.sendMessage(ChatColor.GOLD + "'/money take' to take money from yourself or others (Only for OPS)");
					sender.sendMessage(ChatColor.GOLD + "'/money pay' to pay someone money");
					sender.sendMessage(ChatColor.GOLD + "'/money pPT info' for info about pPTs");
					sender.sendMessage(ChatColor.GOLD + "'/money basic info' for info about the basics");
					sender.sendMessage(ChatColor.GOLD + "'/money sue info' for info about trials");
					sender.sendMessage(ChatColor.GOLD + "'/money job info' for info about jobs");
					sender.sendMessage(ChatColor.GOLD + "'/money bank info' for info about your bank");
					sender.sendMessage(ChatColor.GOLD + "'/money bet info' for info about betting");
				}
				//
				if(label2.equalsIgnoreCase("pPT")){
					if(args.length==0){
						sender.sendMessage(ChatColor.GOLD + "You need more than 0 arguments!");
						return true;
					}
					String label3 = args[1];
					if(label3.equalsIgnoreCase("info")){
						if(!sender.hasPermission("tpp.money.*")&&!sender.hasPermission("tpp.money.pPT.info")&&!sender.hasPermission("tpp.money.pPT.*")&&!sender.isOp()){
							sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
							return true;
						}
						sender.sendMessage(ChatColor.GOLD + "pPT stands for pixelPackTickets and they are not worth ANY money");
						sender.sendMessage(ChatColor.GOLD + "Although, you can sell it for 1kpT = 1000pT");
						sender.sendMessage(ChatColor.GOLD + "pPTs are used to bet, if you bet you will always get potion buffs");
						sender.sendMessage(ChatColor.GOLD + "pPTs are also used to buy donation perks but you need 1kpPT for it");
					}
				}
			}
		}
		//custom armor and stuff
		else if(label.equalsIgnoreCase("specialAbilities")){
			if(!sender.isOp()&&!sender.hasPermission("tpp.specialAbility")){
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
				return true;
			}
			if(args.length==0){
				sender.sendMessage(ChatColor.DARK_RED + "You've got the options to choose between: ");
				sender.sendMessage(ChatColor.DARK_RED + "Coins: pT, pC, pD and pPT");
				sender.sendMessage(ChatColor.DARK_RED + "Armor: health, strength, speed, regen, speed, night vision and water breathing");
				sender.sendMessage(ChatColor.DARK_RED + "Amulet: leaping, protection and luck");
				return true;
			}
		}
		return false;
	}
	private void protectOwner(Player sender, String[] args) {
		if(args[0].equalsIgnoreCase("addowner")){
		if(!sender.hasPermission("tpp.protect.addOwner")&&!sender.hasPermission("tpp.protect.*")){
			sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
			return;
		}
		addOwner(sender,args);
		}else if(args[0].equalsIgnoreCase("removeowner")){
		if(!sender.hasPermission("tpp.protect.removeOwner")&&!sender.hasPermission("tpp.protect.*")){
			sender.sendMessage(ChatColor.DARK_RED + "Permission denied.");
			return;
		}
		removeOwner(sender,args);
		}
	}
	private void removeOwner(Player sender, String[] args) {
		String[] arg = new String[]{args[1],args[2],args[3],args[4],args[5],args[6]};
		if(!checkForNumeric(arg)){
			sender.sendMessage(ChatColor.DARK_RED + "<x> <y> <z> <x2> <y2> and <z2> must be numeric!");
			return;
		}
		int xx = Integer.parseInt(args[1]);
		int yy = Integer.parseInt(args[2]);
		int zz = Integer.parseInt(args[3]);
		int xx2 = Integer.parseInt(args[4]);
		int yy2 = Integer.parseInt(args[5]);
		int zz2 = Integer.parseInt(args[6]);
		String removedOwner = args[7];
		int x = 0, y = 0, z = 0, x2 = 0, y2 = 0, z2 = 0;
		if(xx2<xx){
			x=xx2;
			x2=xx;
		}else if(xx2>xx){
			x=xx;
			x2=xx2;
		}
		
		if(yy2<yy){
			y=yy2;
			y2=yy;
		}else if(yy2>yy){
			y=yy;
			y2=yy2;
		}
		
		if(zz2<zz){
			z=zz2;
			z2=zz;
		}else if(zz2>zz){
			z=zz;
			z2=zz2;
		}
	}
	private void addOwner(Player sender, String[] args) {
		
	}
	private boolean checkForNumeric(String[] arg) {
		for(int o = 0; o<arg.length;o++){
			if(!isNumeric(arg[o])){
				return false;
			}
		}
		return true;
	}
	@SuppressWarnings("unused")
	private String getPlayerLocationString(Player p) {
		return "" + p.getLocation().getBlockX() + "." + p.getLocation().getBlockY() + "." + p.getLocation().getBlockZ() + ".";
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onChat(PlayerChatEvent e){
		e.setCancelled(true);
		if(e.getPlayer() instanceof Player){
			Player p = e.getPlayer();
			for(Player pl : getServer().getOnlinePlayers()){
				boolean ignoresplayer = getConfig().getBoolean("ignores." + pl.getName() + "." + p.getName());
				boolean ignoreeveryone = getConfig().getBoolean("ignores." + pl.getName() + ".all");
				if(!ignoresplayer&&!ignoreeveryone){
					pl.sendMessage(ChatColor.RED + "[" + ChatColor.AQUA + time1 + ChatColor.RED + "]" + ChatColor.RED + "[" + ChatColor.AQUA + p.getDisplayName() + ChatColor.RED + "] " + ChatColor.RESET + e.getMessage());
				}
			}
			try {
				if(!p.hasPermission("tpp.nochatdata")){
					bufferedWriter.write(time2 + ":" + e.getPlayer().getName() + ": " + e.getMessage());
					bufferedWriter.newLine();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[" + ChatColor.AQUA + time2 + ChatColor.RED + "]" + ChatColor.RED + "[" + ChatColor.AQUA + p.getName() + ChatColor.RED + "] " + ChatColor.RESET + e.getMessage());
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onCommand(PlayerCommandPreprocessEvent e){
		try {
			if(!e.getPlayer().hasPermission("tpp.nocommanddata")){
				bufferedWriter2.write(time2 + ":" + e.getPlayer().getName() + " used " + e.getMessage());
				bufferedWriter2.newLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e){
		try {
			if(!e.getPlayer().hasPermission("tpp.nochatdata")){
				bufferedWriter.write(time2 + ":" + e.getPlayer().getName() + " joined.");
				bufferedWriter.newLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(!e.getPlayer().hasPlayedBefore()){
			Player sender = e.getPlayer();
			int spawnx = getConfig().getInt(sender.getWorld().getName() + ".spawnX");
			int spawny = getConfig().getInt(sender.getWorld().getName() + ".spawnY");
			int spawnz = getConfig().getInt(sender.getWorld().getName() + ".spawnZ");
			Location l2;
			if(spawnx!=0&&spawny!=0&&spawnz!=0){
				l2 = new Location(sender.getWorld(),spawnx,spawny,spawnz);
				sender.teleport(l2);
				sender.sendMessage(ChatColor.DARK_RED + "Welcome to the server!");
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Welcome to the server, you weren't teleported to the spawn since the owner didn't set it... oops!");
			}
		}
		
		boolean hasHidden = false;
		if(e.getPlayer() instanceof Player){
			e.setJoinMessage("");
			for(Player p : getServer().getOnlinePlayers()){
				String name1 = p.getName();
				String name2 = e.getPlayer().getName();
			boolean hide = getConfig().getBoolean("vanished." + name2 + ".to." + name1);
			boolean hidesall = getConfig().getBoolean("vanished." + name2 + ".to.all");
			if(hide||hidesall){
			p.hidePlayer(e.getPlayer());	
			hasHidden = true;
			}
			}
		}
		if(hasHidden){
		e.getPlayer().sendMessage(ChatColor.GOLD + "You have been hidden!");
		hasHidden = false;
		}
		e.setJoinMessage("");
		getServer().getConsoleSender().sendMessage(time2 + ":" + e.getPlayer().getName() + " joined.");
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onLeave(PlayerQuitEvent e){
		try {
			if(!e.getPlayer().hasPermission("tpp.nochatdata")){
				bufferedWriter.write(time2 + ":" + e.getPlayer().getName() + " left.");
				bufferedWriter.newLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		e.setQuitMessage("");
	}
	@SuppressWarnings("unused")
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent e){
		int xxx=e.getBlock().getX();
		int yyy=e.getBlock().getY();
		int zzz=e.getBlock().getZ();
		int am = getConfig().getInt("Claim.Amount");
		for(int o=0;o<am;o++){
		int x = getConfig().getInt(o+".p1.x");
		int y = getConfig().getInt(o+".p1.y");
		int z = getConfig().getInt(o+".p1.z");
		int x2 = getConfig().getInt(o+".p2.x");
		int y2 = getConfig().getInt(o+".p2.y");
		int z2 = getConfig().getInt(o+".p2.z");
		if(xxx>x&&xxx<x2){
			if(yyy>y&&yyy<y2){
				if(zzz>z&&zzz<z2){
		String owner = getConfig().getString(o+".owner1");
		String owner2 = getConfig().getString(o+".owner2");
		String owner3 = getConfig().getString(o+".owner3");
		String world = getConfig().getString(o+".world");
		boolean pvp = getConfig().getBoolean(o+".pvp");
		boolean moving = getConfig().getBoolean(o+".moving");
		boolean destroying = getConfig().getBoolean(o+".destroying");
		boolean placing = getConfig().getBoolean(o+".placing");
		boolean interacting = getConfig().getBoolean(o+".interacting");
		if(!(world.equals(e.getPlayer().getWorld().getName()))){
			return;
		}
		if(owner.equals(e.getPlayer().getName())||owner2.equals(e.getPlayer().getName())||owner3.equals(e.getPlayer().getName())){
			
		}else{
			if(destroying&&!e.getPlayer().hasPermission("tpp.claimbypass.break")&&!e.getPlayer().hasPermission("tpp.claimbypass.*")){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You are not permitted to destroy this!");
			double hp = e.getPlayer().getHealth()-7.5D;
			if(hp<0D){
				hp = 0;
			}
			e.getPlayer().setHealth(hp);
			}
			}
		}
			}
		}
		}
	}
	@SuppressWarnings("unused")
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBlockPlaced(BlockPlaceEvent e){
		int xxx=e.getBlock().getX();
		int yyy=e.getBlock().getY();
		int zzz=e.getBlock().getZ();
		int am = getConfig().getInt("Claim.Amount");
		for(int o=0;o<am;o++){
		int x = getConfig().getInt(o+".p1.x");
		int y = getConfig().getInt(o+".p1.y");
		int z = getConfig().getInt(o+".p1.z");
		int x2 = getConfig().getInt(o+".p2.x");
		int y2 = getConfig().getInt(o+".p2.y");
		int z2 = getConfig().getInt(o+".p2.z");
		if(xxx>x&&xxx<x2){
			if(yyy>y&&yyy<y2){
				if(zzz>z&&zzz<z2){
					String owner = getConfig().getString(o+".owner1");
					String owner2 = getConfig().getString(o+".owner2");
					String owner3 = getConfig().getString(o+".owner3");
					String world = getConfig().getString(o+".world");
					boolean pvp = getConfig().getBoolean(o+".pvp");
					boolean moving = getConfig().getBoolean(o+".moving");
					boolean destroying = getConfig().getBoolean(o+".destroying");
					boolean placing = getConfig().getBoolean(o+".placing");
					boolean interacting = getConfig().getBoolean(o+".interacting");
					if(!(world.equals(e.getPlayer().getWorld().getName()))){
						return;
					}
		if(owner.equals(e.getPlayer().getName())||owner2.equals(e.getPlayer().getName())||owner3.equals(e.getPlayer().getName())){
			
		}else{
			if(placing&&!e.getPlayer().hasPermission("tpp.claimbypass.place")&&!e.getPlayer().hasPermission("tpp.claimbypass.*")){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You are not permitted to place this in this area!");
			double hp = e.getPlayer().getHealth()-5.5D;
			if(hp<0D){
				hp = 0;
			}
			e.getPlayer().setHealth(hp);
			}
		}
				}
			}
		}
		}
	}
	@SuppressWarnings("unused")
	@EventHandler(priority=EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e){
		int xxx=e.getPlayer().getLocation().getBlockX();
		int yyy=e.getPlayer().getLocation().getBlockY();
		int zzz=e.getPlayer().getLocation().getBlockZ();
		int am = getConfig().getInt("Claim.Amount");
		for(int o=0;o<am;o++){
		int x = getConfig().getInt(o+".p1.x");
		int y = getConfig().getInt(o+".p1.y");
		int z = getConfig().getInt(o+".p1.z");
		int x2 = getConfig().getInt(o+".p2.x");
		int y2 = getConfig().getInt(o+".p2.y");
		int z2 = getConfig().getInt(o+".p2.z");
		if(xxx>x&&xxx<x2){
			if(yyy>y&&yyy<y2){
				if(zzz>z&&zzz<z2){
					String owner = getConfig().getString(o+".owner1");
					String owner2 = getConfig().getString(o+".owner2");
					String owner3 = getConfig().getString(o+".owner3");
					String world = getConfig().getString(o+".world");
					boolean pvp = getConfig().getBoolean(o+".pvp");
					boolean moving = getConfig().getBoolean(o+".moving");
					boolean destroying = getConfig().getBoolean(o+".destroying");
					boolean placing = getConfig().getBoolean(o+".placing");
					boolean interacting = getConfig().getBoolean(o+".interacting");
					if(!(world.equals(e.getPlayer().getWorld().getName()))){
						return;
					}
		if(owner.equals(e.getPlayer().getName())||owner2.equals(e.getPlayer().getName())||owner3.equals(e.getPlayer().getName())){
			
		}else{
			if(interacting&&!e.getPlayer().hasPermission("tpp.claimbypass.interact")&&!e.getPlayer().hasPermission("tpp.claimbypass.*")){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You are not permitted to interact with this!");
			double hp = e.getPlayer().getHealth()-1.5D;
			if(hp<0D){
				hp = 0;
			}
			e.getPlayer().setHealth(hp);
			}
		}
				}
			}
		}
		}
	}
	
	//TODO: Permissions (in plugin.yml
	//TODO: money
	//TODO: votifier
	//TODO: Simplyfy /protect (Claiming with golden hoe) and /protect addowner, /protect remove owner etc.
	//TODO: /Clan
	//TODO: CUSTOM ARMOR WITH CUSTOM Abilities
	
	@SuppressWarnings("unused")
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPVP(EntityDamageEvent e2){
		
		
		if(!(e2.getEntity() instanceof Player)){
			return;
		}
		Player p = (Player) e2.getEntity();
		if(getConfig().getBoolean(p.getName() + ".god")){
			e2.setCancelled(true);
			if(!(e2 instanceof EntityDamageByEntityEvent))
				return;
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)e2;
			Entity damager = e.getDamager();
			if(!(damager instanceof Player))
				return;
			Player p2 = (Player) e.getDamager();
			p2.sendMessage(ChatColor.DARK_RED + "Hitting gods will always hit you twice as hard!");
			p2.setHealth(p2.getHealth()-(e2.getDamage()*2));
		}
		
		if(!(e2 instanceof EntityDamageByEntityEvent))
			return;
		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)e2;
			Entity damager = e.getDamager();
			if(damager instanceof Player){
				Player pl = (Player)damager;
				int xxx=p.getLocation().getBlockX();
				int yyy=p.getLocation().getBlockY();
				int zzz=p.getLocation().getBlockZ();
				int am = getConfig().getInt("Claim.Amount");
				for(int o=0;o<am;o++){
				int x = getConfig().getInt(o+".p1.x");
				int y = getConfig().getInt(o+".p1.y");
				int z = getConfig().getInt(o+".p1.z");
				int x2 = getConfig().getInt(o+".p2.x");
				int y2 = getConfig().getInt(o+".p2.y");
				int z2 = getConfig().getInt(o+".p2.z");
				if(xxx>x&&xxx<x2){
					if(yyy>y&&yyy<y2){
						if(zzz>z&&zzz<z2){
							String owner = getConfig().getString(o+".owner1");
							String owner2 = getConfig().getString(o+".owner2");
							String owner3 = getConfig().getString(o+".owner3");
							String world = getConfig().getString(o+".world");
							boolean pvp = getConfig().getBoolean(o+".pvp");
							boolean moving = getConfig().getBoolean(o+".moving");
							boolean destroying = getConfig().getBoolean(o+".destroying");
							boolean placing = getConfig().getBoolean(o+".placing");
							boolean interacting = getConfig().getBoolean(o+".interacting");
							if(!(world.equals(p.getWorld().getName()))){
								return;
							}
				if(owner.equals(pl.getName())||owner2.equals(pl.getName())||owner3.equals(pl.getName())){
					
				}else{
					if(interacting&&!p.hasPermission("tpp.claimbypass.pvp")&&!p.hasPermission("tpp.claimbypass.*")){
					e.setCancelled(true);
					pl.sendMessage(ChatColor.RED + "Trying to kill the owner in his area will cause you twice as much damage!!");
					pl.damage(e.getDamage()*2);
					}
				}
						}
					}
				}
				}
				
				
				
				
			}
	}
	@SuppressWarnings("unused")
	@EventHandler(priority=EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent e){
		int xxx=e.getPlayer().getLocation().getBlockX();
		int yyy=e.getPlayer().getLocation().getBlockY();
		int zzz=e.getPlayer().getLocation().getBlockZ();
		int am = getConfig().getInt("Claim.Amount");
		for(int o=0;o<am;o++){
		int x = getConfig().getInt(o+".p1.x");
		int y = getConfig().getInt(o+".p1.y");
		int z = getConfig().getInt(o+".p1.z");
		int x2 = getConfig().getInt(o+".p2.x");
		int y2 = getConfig().getInt(o+".p2.y");
		int z2 = getConfig().getInt(o+".p2.z");
		if(xxx>x&&xxx<x2){
			if(yyy>y&&yyy<y2){
				if(zzz>z&&zzz<z2){
					String owner = getConfig().getString(o+".owner1");
					String owner2 = getConfig().getString(o+".owner2");
					String owner3 = getConfig().getString(o+".owner3");
					String world = getConfig().getString(o+".world");
					boolean pvp = getConfig().getBoolean(o+".pvp");
					boolean moving = getConfig().getBoolean(o+".moving");
					boolean destroying = getConfig().getBoolean(o+".destroying");
					boolean placing = getConfig().getBoolean(o+".placing");
					boolean interacting = getConfig().getBoolean(o+".interacting");
					if(!(world.equals(e.getPlayer().getWorld().getName()))){
						return;
					}
		if(owner.equals(e.getPlayer().getName())||owner2.equals(e.getPlayer().getName())||owner3.equals(e.getPlayer().getName())){
			
		}else{
			if(moving&&!e.getPlayer().hasPermission("tpp.claimbypass.move")&&!e.getPlayer().hasPermission("tpp.claimbypass.*")){
				
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You are not permitted to be here!");
			double hp = e.getPlayer().getHealth()-0.5D;
			if(hp<0D){
				hp = 0;
			}
			if(e.getPlayer().getHealth()!=0D){
			e.getPlayer().setHealth(hp);
			}
			
			
			Player sender = e.getPlayer();
			int homex = getConfig().getInt(sender.getName() + "." + 1 + ".x");
			int homey = getConfig().getInt(sender.getName() + "." + 1 + ".y");
			int homez = getConfig().getInt(sender.getName() + "." + 1 + ".z");
			String worldname = getConfig().getString(sender.getName() + "." + 1 + ".world");
			World world2;
			if(worldname!=null){
			world2 = getServer().getWorld(worldname);
			}else{
				world2 = null;
			}
			Location l;
			if(world2 != null){
				l = new Location(world2,homex,homey,homez);
				sender.teleport(l);
				sender.sendMessage(ChatColor.DARK_RED + "You were not allowed to enter this area, so you have been teleported back to your 1st home!");
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "You don't have your 1st home set, returning to spawn..");
				
				int spawnx = getConfig().getInt(sender.getWorld().getName() + ".spawnX");
				int spawny = getConfig().getInt(sender.getWorld().getName() + ".spawnY");
				int spawnz = getConfig().getInt(sender.getWorld().getName() + ".spawnZ");
				Location l2;
				if(spawnx!=0&&spawny!=0&&spawnz!=0){
					l2 = new Location(sender.getWorld(),spawnx,spawny,spawnz);
					sender.teleport(l2);
					sender.sendMessage(ChatColor.DARK_RED + "You were not allowed to enter this area, so you were teleported to the spawn!");
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "You weren't able to teleport out! Ask the owner if he could add a spawn to this world!");
				}
				
				
			}
			
			
			
			}
		}
				}
			}
		}
		}
	}
	@SuppressWarnings("unused")
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
}
