package net.goa.src;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
@SuppressWarnings({ "deprecation" })
public class Main extends JavaPlugin implements Listener{
	public static ConcurrentHashMap<Location,ChestProperties> chests = new ConcurrentHashMap<Location,ChestProperties>();
	public static ConcurrentHashMap<String,Inventory> bodies = new ConcurrentHashMap<String,Inventory>();
	public NPCRegistry npcr = null;
	@Override
	public void onEnable() {
		loadChests();
		loadBodies();
		addRecipes();
		getServer().getPluginManager().registerEvents(this, this);
		getConfig().set("Bodies",null);
		saveConfig();
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run() {
				if(getServer().getPluginManager().getPlugin("Citizens")==null)getServer().getPluginManager().disablePlugin(getServer().getPluginManager().getPlugin("GOAPlugin"));
				else{
					NPCRegistry npcr = CitizensAPI.getNPCRegistry();
					((Main)getServer().getPluginManager().getPlugin("GOAPlugin")).npcr=npcr;
				}
			}}, 1L);
	}
	private void addRecipes() {
		ItemStack is = new ItemStack(Material.TNT,1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "TNT +1 Power");
		is.setItemMeta(im);
		ShapedRecipe recipe = new ShapedRecipe(is);
		recipe.shape(" b ","bab"," b ");
		recipe.setIngredient('b', Material.SULPHUR);
		recipe.setIngredient('a', Material.TNT);
		Bukkit.addRecipe(recipe);
		 is = new ItemStack(Material.TNT,1);
		 im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "TNT +1 Fuse");
		is.setItemMeta(im);
		recipe = new ShapedRecipe(is);
		recipe.shape(" b ","bab"," b ");
		recipe.setIngredient('b', Material.PAPER);
		recipe.setIngredient('a', Material.TNT);
		Bukkit.addRecipe(recipe);
	}
	@EventHandler
	private void onTNT(BlockPlaceEvent e){
		if(!e.canBuild()){
			e.setCancelled(true);
			return;
		}
		if(e.getBlockPlaced().getType()!=Material.TNT||!e.getItemInHand().hasItemMeta()||!e.getItemInHand().getItemMeta().hasLore()){
			return;
		}
			int lvl = getConfig().getInt(e.getPlayer().getUniqueId().toString() + ".ExperienceLevel");
			ItemMeta im = e.getItemInHand().getItemMeta();
			int calculated = calculatedXP((Integer.parseInt(im.getLore().get(0).replace(ChatColor.DARK_RED + "Blast power: ",""))+1),(Integer.parseInt(im.getLore().get(1).replace(ChatColor.DARK_RED + "Fuse: ",""))));
			if(calculated>lvl&&e.getPlayer().getGameMode()!=GameMode.CREATIVE){
				if(calculated<=136)e.getPlayer().sendMessage(ChatColor.DARK_RED + " this is too high level for you, you are only level " + lvl + " whilst you require level " + calculated);
				else e.getPlayer().sendMessage(ChatColor.DARK_RED + "You do not have the knowledge of what this can do, better stay away from it!");
				e.setCancelled(true);
				return;
			}else if(calculated>lvl&&e.getPlayer().getGameMode()==GameMode.CREATIVE&&e.getPlayer().isOp()){
				e.getPlayer().sendMessage(ChatColor.DARK_RED + "This should require level " + calculated + " whilst you are level " + lvl);
			}
		e.setCancelled(true);
		decreaseItemStack(e.getItemInHand(), e.getPlayer().getInventory().getHeldItemSlot(), e.getPlayer());
		TNTPrimed tntp = e.getBlock().getWorld().spawn(e.getBlock().getLocation(), TNTPrimed.class);
		tntp.setYield((float)Integer.parseInt(e.getItemInHand().getItemMeta().getLore().get(0).replace(ChatColor.DARK_RED + "Blast power: ", "")));
		tntp.setFuseTicks(Integer.parseInt(e.getItemInHand().getItemMeta().getLore().get(1).replace(ChatColor.DARK_RED + "Fuse: ", "")));
	}
	private int calculatedXP(int blastPower, int fuse) {
		return (int) (fuse+(Math.pow(blastPower%2+2, 1+((int)(blastPower/6))*3)));
	}
	@EventHandler
	private void changeTNT(PrepareItemCraftEvent e){
		if(e.getRecipe().getResult().getType()==Material.TNT&&!e.getRecipe().getResult().hasItemMeta()){
			ItemStack is = new ItemStack(Material.TNT,1);
			ItemMeta im = is.getItemMeta();
			List<String> string = new ArrayList<String>();
			string.add(ChatColor.DARK_RED + "Blast power: 0");
			string.add(ChatColor.DARK_RED + "Fuse: 0");
			im.setLore(string);
			im.setDisplayName(ChatColor.GOLD + "TNT");
			is.setItemMeta(im);
			e.getInventory().setResult(is);
		}
		if(e.getRecipe().getResult().hasItemMeta()&&e.getRecipe().getResult().getItemMeta().hasDisplayName()&&e.getRecipe().getResult().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RESET + "TNT +1 Power")){
			if(e.getInventory().getContents()[5].getType()!=Material.TNT){
			for(HumanEntity he : e.getViewers()){
				he.closeInventory();
			}
			}
			if(e.getInventory().getContents()[5].hasItemMeta()&&e.getInventory().getContents()[5].getItemMeta().hasLore()){
				ItemStack is = e.getInventory().getContents()[5].clone();
				is.setAmount(1);
				ItemMeta im = is.getItemMeta();
				List<String> string = new ArrayList<String>();
				string.add(ChatColor.DARK_RED + "Blast power: " + (Integer.parseInt(im.getLore().get(0).replace(ChatColor.DARK_RED + "Blast power: ",""))+1));
				string.add(ChatColor.DARK_RED + "Fuse: " + (Integer.parseInt(im.getLore().get(1).replace(ChatColor.DARK_RED + "Fuse: ",""))));
				im.setLore(string);
				im.setDisplayName(ChatColor.GOLD + "TNT");
				is.setItemMeta(im);
				e.getInventory().setResult(is);
			}else{
				ItemStack is = e.getInventory().getContents()[5].clone();
				is.setAmount(1);
				ItemMeta im = is.getItemMeta();
				List<String> string = new ArrayList<String>();
				string.add(ChatColor.DARK_RED + "Blast power: 1");
				string.add(ChatColor.DARK_RED + "Fuse: 0");
				im.setLore(string);
				im.setDisplayName(ChatColor.GOLD + "TNT");
				is.setItemMeta(im);
				e.getInventory().setResult(is);
			}
		}else if(e.getRecipe().getResult().hasItemMeta()&&e.getRecipe().getResult().getItemMeta().hasDisplayName()&&e.getRecipe().getResult().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RESET + "TNT +1 Fuse")){
				if(e.getInventory().getContents()[5].getType()!=Material.TNT){
				for(HumanEntity he : e.getViewers()){
					he.closeInventory();
				}
				}
				if(e.getInventory().getContents()[5].hasItemMeta()&&e.getInventory().getContents()[5].getItemMeta().hasLore()&&e.getInventory().getContents()[5].getItemMeta().getLore()!=null){
					ItemStack is = e.getInventory().getContents()[5].clone();
					is.setAmount(1);
					ItemMeta im = is.getItemMeta();
					List<String> string = new ArrayList<String>();
					string.add(ChatColor.DARK_RED + "Blast power: " + (Integer.parseInt(im.getLore().get(0).replace(ChatColor.DARK_RED + "Blast power: ",""))));
					string.add(ChatColor.DARK_RED + "Fuse: " + (Integer.parseInt(im.getLore().get(1).replace(ChatColor.DARK_RED + "Fuse: ",""))+1));
					im.setLore(string);
					im.setDisplayName(ChatColor.GOLD + "TNT");
					is.setItemMeta(im);
					e.getInventory().setResult(is);
				}else{
					ItemStack is = e.getInventory().getContents()[5].clone();
					is.setAmount(1);
					ItemMeta im = is.getItemMeta();
					List<String> string = new ArrayList<String>();
					string.add(ChatColor.DARK_RED + "Blast power: 0");
					string.add(ChatColor.DARK_RED + "Fuse: 1");
					im.setLore(string);
					im.setDisplayName(ChatColor.GOLD + "TNT");
					is.setItemMeta(im);
					e.getInventory().setResult(is);
				}
			}
	}
	private void loadBodies() {
		for(String s : getConfig().getStringList("Bodies")){
			String[] st = s.split("'");
			Inventory i = generateBackpackFor(st[0],4);
			String owner = st[1];
			bodies.put(owner,i);
		}
	}
	private Inventory generateBackpackFor(String string, int rows) {
		Inventory i = Bukkit.createInventory(null, rows*9,"Backpack");
		String[] split = string.split("\\|");
		for(String s : split){
			String[] ssplit = s.split(",");
			if(ssplit.length!=6)continue;
			Material m = Material.getMaterial(ssplit[0]);
			int amount = Integer.parseInt(ssplit[1]);
			short data = Short.parseShort(ssplit[2]);
			ItemStack is = new ItemStack(m,amount,data);
			String[] stsplit = ssplit[3].split("\\{");
			if(stsplit.length<1)continue;
			if(stsplit[0].equalsIgnoreCase("true")){
				ItemMeta im = is.getItemMeta();
				String[] strsplit = stsplit[1].split("\\.");
				String[] display = strsplit[0].split("\\[");
				String[] lore = strsplit[1].split("\\[");
				if(display[0].equalsIgnoreCase("true")){
					String name = display[1];
					im.setDisplayName(name);
				}
				if(lore[0].equalsIgnoreCase("true")){
					String[] lore2 = lore[1].split("\\;");
					List<String> lore3 = new ArrayList<String>();
					for(String lore4 : lore2){
						lore3.add(lore4);
					}
					im.setLore(lore3);
				}
				is.setItemMeta(im);
			}
			String[] strsplit = ssplit[4].split("\\{");
			if(stsplit.length<1)continue;
			if(strsplit[0].equalsIgnoreCase("true")){
				HashMap<Enchantment,Integer> enchantments = new HashMap<Enchantment,Integer>();
				String[] strisplit = strsplit[1].split("\\.");
				for(String str : strisplit){
					String[] strinsplit = str.split("\\/");
					String id = strinsplit[0];
					String level = strinsplit[1];
					enchantments.put(Enchantment.getByName(id), Integer.parseInt(level));
				}
				is.addUnsafeEnchantments(enchantments);
			}
			i.addItem(is);
		}
	return i;
	}
	public void loadChests(){
		for(String s : getConfig().getStringList("Chests")){
			String[] split = s.split(",");
			Location l = new Location(getServer().getWorld(split[3]),Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]));
			ChestProperties cp = new ChestProperties(split[4], Integer.parseInt(split[6]),split[5]);
			chests.put(l, cp);
		}
		for(Entry<Location,ChestProperties> l : chests.entrySet()){
			World w = getServer().getWorld(l.getKey().getWorld().getName());
			if(w==null)continue;
			Block b = w.getBlockAt(l.getKey());
			if(b.getType()!=Material.CHEST)continue;
			Inventory i = ((Chest)b.getState()).getBlockInventory();
			if(i==null)continue;
			i.clear();
			generateChestFor(l.getKey(),getChestByParentAndBelongingVillage(l.getValue().parent,l.getValue().belongingvillage),i,b,w,l.getValue().parent);
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(label.equalsIgnoreCase("createChest")){
			if(args.length!=2){
				sender.sendMessage(ChatColor.DARK_RED + "ERROR! You can only create a parent from a village!");
				return super.onCommand(sender, command, label, args);
			}
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "ERROR! You must be a player!");
				return super.onCommand(sender, command, label, args);
			}
			if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "ERROR! You must be opped!");
				return super.onCommand(sender, command, label, args);
			}
			if(((Player)sender).getTargetBlock(null, 90).getType()!=Material.CHEST){
				sender.sendMessage(ChatColor.DARK_RED + "ERROR! You are not looking at a chest!");
				return super.onCommand(sender, command, label, args);
			}
			getConfig().set(args[1] + "." + args[0], chestToString(((Chest)((Player)sender).getTargetBlock(null, 90).getState()).getBlockInventory(),args[1]));
			saveConfig();
			sender.sendMessage(ChatColor.GOLD + "You have succesfully added the parent!: " + args[1] + "." + args[0] + " With code: ");
			sender.sendMessage(ChatColor.DARK_PURPLE + chestToString(((Chest)((Player)sender).getTargetBlock(null, 90).getState()).getBlockInventory(),args[1]));
		}else if(label.equalsIgnoreCase("addChest")){
			if(args.length!=3){
				sender.sendMessage(ChatColor.DARK_RED + "ERROR! You forgot the two arguments; parent, village, stealValue");
				return super.onCommand(sender, command, label, args);
			}
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "ERROR! You must be a player!");
				return super.onCommand(sender, command, label, args);
			}
			if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "ERROR! You must be opped!");
				return super.onCommand(sender, command, label, args);
			}
			if(((Player)sender).getTargetBlock(null, 90).getType()!=Material.CHEST){
				sender.sendMessage(ChatColor.DARK_RED + "ERROR! You are not looking at a chest!");
				return super.onCommand(sender, command, label, args);
			}
			ChestProperties cp = new ChestProperties(args[0], Integer.parseInt(args[2]),args[1]);
			chests.put(((Player)sender).getTargetBlock(null, 90).getLocation(), cp);
			sender.sendMessage(ChatColor.GOLD + "Added chest to parent: " + args[0] + " at village " + args[1] + " with steal-value: " + Integer.parseInt(args[2]));
		}else if(label.equalsIgnoreCase("reloadChests")){
			if(!sender.isOp())return super.onCommand(sender, command, label, args);
			saveChests();
			chests.clear();
			loadChests();
			sender.sendMessage(ChatColor.GOLD + "All chests are reloaded!");
		}else if(label.equalsIgnoreCase("newVillage")){
			if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied!");
				return super.onCommand(sender, command, label, args);
			}
			if(args.length!=5){
				sender.sendMessage(ChatColor.DARK_RED + "Correct usage: /newVillage <name> <MaxAcceptableStolenGoods> <Passive/Agressive(0/1)> [GroupToBeAgressiveTowards1,2,3,4,etc.] [GroupToBeNiceTowards,1,2,3,etc.]");
				sender.sendMessage(ChatColor.DARK_RED + "Example: /newVillage Nubisvillage1 25 0 Tenebrae,Auxiliator Nubis");
				sender.sendMessage(ChatColor.DARK_RED + "Creates a Nubisvillage which accepts a maximum of 25 stolen goods, is passive towards [Aedifex,Inane,Facies] until they steal enough");
				sender.sendMessage(ChatColor.DARK_RED + "Accepts it if someone from Nubis steals something and immediately goes after [Tenbrae,Auxiliator]");
				//Auxiliator (helper,balance), Tenebrae (Night,Darkness,Hell), Aedifex (Light,Day,World), Nubis (Skies,Flying), Inane (Neutrality,Void), Facies (Nature,Peace)
				return super.onCommand(sender, command, label, args);
			}
			String villageName = args[0];
			int maxStolenGoods = Integer.parseInt(args[1]);
			boolean isAggressive = args[2].equalsIgnoreCase("1")?true:args[2].equalsIgnoreCase("0")?false:null;
			String[] aggressiveTowards = args[3].split(",");
			String[] passiveTowards = args[4].split(",");
			List<String> aggressive = new ArrayList<String>();
			List<String> passive = new ArrayList<String>();
			for(String agg : aggressiveTowards){
				aggressive.add(agg);
			}
			for(String pass : passiveTowards){
				passive.add(pass);
			}
			getConfig().set("Village."+villageName+".Goods",maxStolenGoods);
			getConfig().set("Village."+villageName+".Passive",!isAggressive);
			getConfig().set("Village."+villageName+".PassiveList",passive);
			getConfig().set("Village."+villageName+".AggressiveList",aggressive);
			List<String> villages = getConfig().getStringList("Villages");
			villages.add(villageName);
			getConfig().set("Villages", villages);
			saveConfig();
			sender.sendMessage(ChatColor.GOLD + "You've added a village! Village: " + villageName + " with maxStolenGoods: " + maxStolenGoods + " and the village is " + (isAggressive?"NOT":"") + " passive.");
			sender.sendMessage(ChatColor.GOLD + "On its passive/accept list are:");
			for(String st : passive){
				sender.sendMessage(ChatColor.GOLD + st);
			}
			sender.sendMessage(ChatColor.GOLD + "and on its aggressive/fight list are:");
			for(String st : aggressive){
				sender.sendMessage(ChatColor.GOLD + st);
			}
		}else if(label.equalsIgnoreCase("setSpawn")){
			if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied!");
				return super.onCommand(sender, command, label, args);
			}
			if(sender instanceof Player){
				Player p = (Player)sender;
				String st = p.getLocation().getX() + "," + p.getLocation().getY() + "," + p.getLocation().getZ() + "," + p.getLocation().getWorld().getName() + "," + p.getLocation().getYaw() + "," + p.getLocation().getPitch();
				getConfig().set("Info.Spawn", st);
				saveConfig();
				p.sendMessage(ChatColor.DARK_RED + "You have set the spawn!");
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Only ingame-players can use this command");
				return super.onCommand(sender, command, label, args);
			}
		}else if(label.equalsIgnoreCase("village")){
			if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied!");
				return super.onCommand(sender, command, label, args);
			}
			if(sender instanceof Player){
				Player p = (Player)sender;
				if(args.length<1||(!args[0].equalsIgnoreCase("region")&&!args[0].equalsIgnoreCase("setregion"))){
				p.sendMessage(ChatColor.DARK_RED + "You must specify what to do!");
				p.sendMessage(ChatColor.DARK_RED + "setRegion or region");
				return super.onCommand(sender, command, label, args);
				}else if(args[0].equalsIgnoreCase("region")){
					if(args.length<2||!args[1].equalsIgnoreCase("name")&&!args[1].equalsIgnoreCase("showsName")&&!args[1].equalsIgnoreCase("popup")){
						p.sendMessage(ChatColor.DARK_RED + "You need to specify: name, showsName, popup and mobSpawning");
						return super.onCommand(sender, command, label, args);
					}else if(args[1].equalsIgnoreCase("name")){
						if(args.length!=5){
							p.sendMessage(ChatColor.DARK_RED + "Incorrect usage: /village region name <Village> <Title> <Subtitle>");
							return super.onCommand(sender, command, label, args);
						}
						getConfig().set("Village." + args[2] + ".Welcome.Title", args[3]);
						getConfig().set("Village." + args[2] + ".Welcome.Subtitle", args[4]);
						saveConfig();
						p.sendMessage(ChatColor.GOLD + "You've set village's title to: " + args[3] + " and subtitle: " + args[4]);
						return super.onCommand(sender, command, label, args);
					}else if(args[1].equalsIgnoreCase("showsName")){
						if(args.length!=4){
							p.sendMessage(ChatColor.DARK_RED + "Incorrect usage: /village region showsName <Village> <true/false>");
							return super.onCommand(sender, command, label, args);
						}
						getConfig().set("Village." + args[2] + ".Welcome.Allowed", args[3].equalsIgnoreCase("true")?true:args[3].equalsIgnoreCase("false")?false:null);
						saveConfig();
						p.sendMessage(ChatColor.GOLD + "You've set village's show-name to: " + (args[3].equalsIgnoreCase("true")?true:args[3].equalsIgnoreCase("false")?false:null));
						return super.onCommand(sender, command, label, args);
					}else if(args[1].equalsIgnoreCase("popup")){
						if(args.length!=6){
							p.sendMessage(ChatColor.DARK_RED + "Incorrect usage: /village region popup <Village> <FadeInTicks> <DelayTicks> <FadeOutTicks>");
							return super.onCommand(sender, command, label, args);
						}
						int delay=0,fadeIn=0,fadeOut=0;
						try{
							delay=Integer.parseInt(args[3]);
							fadeIn=Integer.parseInt(args[4]);
							fadeOut=Integer.parseInt(args[5]);
						}catch(Exception e){
							p.sendMessage(ChatColor.DARK_RED + "Delay, fadeIn and fadeOut are in FULL ticks which mean only numbers (20 ticks=1 second)");
							return super.onCommand(sender, command, label, args);
						}
						getConfig().set("Village." + args[2] + ".Popup.Delay",delay);
						getConfig().set("Village." + args[2] + ".Popup.FadeIn",fadeIn);
						getConfig().set("Village." + args[2] + ".Popup.FadeOut",fadeOut);
						saveConfig();
						p.sendMessage(ChatColor.GOLD + "You've set village's popup delay to: " + delay + " it's fadeIn delay to: " + fadeIn + " and it's fadeOut delay to: " + fadeOut);
						return super.onCommand(sender, command, label, args);
					}else if(args[1].equalsIgnoreCase("mobSpawning")){
						if(args.length!=4){
							p.sendMessage(ChatColor.DARK_RED + "Incorrect usage: /village region mobSpawning <Village> <0/1,false/true,deny/allow,0b,1b,b0,b1>");
							return super.onCommand(sender, command, label, args);
						}
						boolean allowMobSpawning=true;
						try{
							allowMobSpawning=(args[3].contains("0")||args[3].contains("false")||args[3].contains("deny")||args[3].contains("0b")||args[3].contains("b0"))?false:(args[3].contains("1")||args[3].contains("true")||args[3].contains("allow")||args[3].contains("1b")||args[3].contains("b1"))?true:null;
						}catch(Exception e){
							p.sendMessage(ChatColor.DARK_RED + "Delay, fadeIn and fadeOut are in FULL ticks which mean only numbers (20 ticks=1 second)");
							return super.onCommand(sender, command, label, args);
						}
						getConfig().set("Village." + args[2] + ".Mobs",allowMobSpawning);
						saveConfig();
						p.sendMessage(ChatColor.GOLD + "You've set 'allowmobspawning' in village" + args[2] + " to " + allowMobSpawning);
						return super.onCommand(sender, command, label, args);
					}
				}else if(args[0].equalsIgnoreCase("setregion")){
					if(args.length!=8){
						p.sendMessage(ChatColor.DARK_RED + "Invalid usage: /village setRegion <village> <x1> <y1> <z1> <x2> <y2> <z2>");
						return super.onCommand(sender, command, label, args);
					}
					if(getConfig().getInt("Village." + args[1] + ".Goods")==0){
						p.sendMessage(ChatColor.DARK_RED + "Village doesn't exist!");
						return super.onCommand(sender, command, label, args);
					}
					int x1=0,y1=0,z1=0,x2=0,y2=0,z2=0;
					int minX,minY,minZ,maxX,maxY,maxZ;
					try{
						x1=Integer.parseInt(args[2]);
						y1=Integer.parseInt(args[3]);
						z1=Integer.parseInt(args[4]);
						x2=Integer.parseInt(args[5]);
						y2=Integer.parseInt(args[6]);
						z2=Integer.parseInt(args[7]);
						maxX=x1<x2?x2:x1;
						maxY=y1<y2?y2:y1;
						maxZ=z1<z2?z2:z1;
						minX=x1>x2?x2:x1;
						minY=y1>y2?y2:y1;
						minZ=z1>z2?z2:z1;
						String zone = minX + "," + minY + "," + minZ + "," + maxX + "," + maxY + "," + maxZ;
						getConfig().set("Village." + args[1] + ".Zone", zone);
						saveConfig();
						p.sendMessage(ChatColor.GOLD +"Successfully set the zone of village: " + args[1]);
						return super.onCommand(sender, command, label, args);
					}catch(Exception e){
						p.sendMessage(ChatColor.DARK_RED + "the arguments can only be <x1> <y1> <z1> <x2> <y2> <z2> and no letters! only numbers!");
					}
				}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "Only ingame-players can use this command");
				return super.onCommand(sender, command, label, args);
			}
		}
		return super.onCommand(sender, command, label, args);
	}
	@EventHandler
    public void onMove(PlayerMoveEvent event){
		if(event.getFrom().getBlockX()==event.getTo().getBlockX()&&event.getFrom().getBlockY()==event.getTo().getBlockY()&&event.getFrom().getBlockZ()==event.getTo().getBlockZ())return;
		String to = isVillage(event.getTo());
		String from = isVillage(event.getFrom());
		if(from==to)return;
		if(!getConfig().getBoolean("Village." + to + ".Welcome.Allowed"))return;
		sendTitle(event.getPlayer(), getConfig().getInt("Village." + to + ".Popup.FadeIn")==0?50:getConfig().getInt("Village." + to + ".Popup.FadeIn"), getConfig().getInt("Village." + to + ".Popup.Delay")==0?60:getConfig().getInt("Village." + to + ".Popup.Delay"), getConfig().getInt("Village." + to + ".Popup.FadeOut")==0?30:getConfig().getInt("Village." + to + ".Popup.FadeOut"), getConfig().getString("Village." + to + ".Welcome.Title"), getConfig().getString("Village." + to + ".Welcome.Subtitle"));
	}
	private String isVillage(Location l) {
		List<String> villages = getConfig().getStringList("Villages");
		for(String st : villages){
			String[] zone = getConfig().getString("Village." + st + ".Zone").split(",");
			int minX,minY,minZ,maxX,maxY,maxZ;
			try{
				minX = Integer.parseInt(zone[0]);
				if(l.getX()<minX)continue;
				minY = Integer.parseInt(zone[1]);
				if(l.getY()<minY)continue;
				minZ = Integer.parseInt(zone[2]);
				if(l.getZ()<minZ)continue;
				maxX = Integer.parseInt(zone[3]);
				if(l.getX()>maxX)continue;
				maxY = Integer.parseInt(zone[4]);
				if(l.getY()>maxY)continue;
				maxZ = Integer.parseInt(zone[5]);
				if(l.getZ()>maxZ)continue;
				return st;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}
	public void teleportToSpawn(Player p){
		String[] st = getConfig().getString("Info.Spawn").split(",");
		double x = Double.parseDouble(st[0]);
		double y = Double.parseDouble(st[1]);
		double z = Double.parseDouble(st[2]);
		World w = getServer().getWorld(st[3]);
		float yaw = Float.parseFloat(st[4]);
		float pitch = Float.parseFloat(st[5]);
		if(x==0&&y==0&&z==0||w==null)return;
		Location l = new Location(w,x,y,z,yaw,pitch);
		p.teleport(l);
	}
	private String getChestByParentAndBelongingVillage(String parent,String belongingvillage) {
		return getConfig().getString(belongingvillage + "." + parent);
	}
	private String backpackAsString(String owner,Inventory i) {
		String s = "";
		boolean isFirst = true;
		for(ItemStack is : i.getContents()){
			if(is==null||is.getType()==null||is.getType()==Material.AIR){
				continue;
			}
			if(isFirst) s = s + is.getType().name().replaceFirst("'", "") + "," + is.getAmount() + "," + is.getDurability() + ",";
			else  s = s + "|" + is.getType().name().replaceFirst("'", "") + "," + is.getAmount() + "," + is.getDurability() + ",";
			s = s + getItemMetaToString(is) + "," + getEnchantmentsToString(is) + ",0.5";
			isFirst=false;
		}
		s=s+"'"+owner.replace("'","");
		return s;
	}
	@Override
	public void onDisable() {
		saveChests();
		saveBodies();
	}
	private void saveBodies() {
		List<String> Bodies = new ArrayList<String>();
		for(Entry<String,Inventory> entry : bodies.entrySet()){
			Bodies.add(backpackAsString(entry.getKey(), entry.getValue()));
		}
		getConfig().set("Bodies", Bodies);
		saveConfig();
	}
	private void saveChests(){
		List<String> chest = new ArrayList<String>();
		for(Entry<Location,ChestProperties> lcp : chests.entrySet()){
			chest.add(lcp.getKey().getBlockX() + "," + lcp.getKey().getBlockY() + "," + lcp.getKey().getBlockZ() + "," + lcp.getKey().getWorld().getName() + "," + lcp.getValue().parent + "," + lcp.getValue().belongingvillage + "," + lcp.getValue().value);
		}
		getConfig().set("Chests", chest);
		saveConfig();
	}
	private String chestToString(Inventory i, String belongingVillage){
		String s = "";
		boolean isFirst = true;
		for(ItemStack is : i.getContents()){
			if(is==null||is.getType()==null||is.getType()==Material.AIR){
				continue;
			}
			if(isFirst) s = s + is.getType().name().replaceFirst("'", "") + "," + is.getAmount() + "," + is.getDurability() + ",";
			else  s = s + "|" + is.getType().name().replaceFirst("'", "") + "," + is.getAmount() + "," + is.getDurability() + ",";
			s = s + getItemMetaToString(is) + "," + getEnchantmentsToString(is) + ",0.5";
			isFirst=false;
		}
		s=s+"'"+20*60*5+"'" + belongingVillage + "'5";
		return s;
	}
	private String getEnchantmentsToString(ItemStack is) {
		String s="true{";
		if(is.getEnchantments()==null||is.getEnchantments().isEmpty())return "false";
		boolean isFirst = true;
		for(Entry<Enchantment,Integer> ench : is.getEnchantments().entrySet()){
			if(isFirst) s=s+ench.getKey().getName()+"/"+ench.getValue();
			else s=s+"."+ench.getKey().getName()+"/"+ench.getValue();
			isFirst=false;
		}
		return s;
	}
	private String getItemMetaToString(ItemStack is) {
		String s = "true{";
		if(!is.hasItemMeta()||(is.getItemMeta().getDisplayName()==null&&is.getItemMeta().getLore()==null))return"false";
		ItemMeta im = is.getItemMeta();
		boolean hasDisplayName = im.hasDisplayName();
		boolean hasLore = im.hasLore();
		if(hasDisplayName) s = s + "true[" + im.getDisplayName() + ".";
		else s = s + "false.";
		if(hasLore){
			s = s + "true[";
			boolean isFirst = true;
			for(String st : im.getLore()){
				if(isFirst)s=s+st;
				else s = s + ";" + st;
				isFirst=false;
			}
		}
		else s = s + "false";
		return s;
	}
	private void generateChestFor(Location key, String value, Inventory i,Block b, World w,String parent) {
		//Decode this:
		//DIAMOND_SWORD,1,0,true{true[AwesomeSword.true[This is the Sword;Made by Notch himself!; Made to kill Herobrine,true{DAMAGE_ALL/10.FIRE_ASPECT/10.LOOTING/10.DURABILITY/10,0.0001'6000'Notchville'5
		//Material,amount,data,itemMeta{displayName[name.lore[lore1;lore2;lore3;etc,enchanted{enchantment/level.enchantment/level.etc|(Other items)'RefillInTicks'BelongingVillage'value
		//Value: The amount of value this chest is to the belonging village; if you pickup 1 item they would get 1*value disliking added to their dislike list (They will dislike you the more you steal)
		//If you steal more than 20 value (In Notchville that would be 4 things out of the said chest) the people from the village will start to aggro on you! (Can be changed in the config!)
		//The more you steal the more strength they get towards you! (value/maxValue*normalDamage) so if you can steal 20 max (Notchville) and you stole 40 things; they would hit you twice as hard then normal. if value/maxValue < 1 they won't hit you!
		chests.remove(key);
		String[] props = value.split("'");
		String inventory = props[0];
		int refillInTicks = Integer.parseInt(props[1]);
		String belongingVillage = props[2];
		int stealValue = Integer.parseInt(props[3]);
		for(int in=0;in<i.getSize();in++){
			if(Math.random()>=0.5)continue;
		String[] split = inventory.split("\\|");
		Outerloop: for(;;){
		for(String s : split){
			String[] ssplit = s.split(",");
			if(ssplit.length!=6)continue;
			double chance = Double.parseDouble(ssplit[5]);
			if(Math.random()>chance)continue;
			Material m = Material.getMaterial(ssplit[0]);
			int amount = Integer.parseInt(ssplit[1]);
			short data = Short.parseShort(ssplit[2]);
			ItemStack is = new ItemStack(m,amount,data);
			String[] stsplit = ssplit[3].split("\\{");
			if(stsplit.length<1)continue;
			if(stsplit[0].equalsIgnoreCase("true")){
				ItemMeta im = is.getItemMeta();
				String[] strsplit = stsplit[1].split("\\.");
				String[] display = strsplit[0].split("\\[");
				String[] lore = strsplit[1].split("\\[");
				if(display[0].equalsIgnoreCase("true")){
					String name = display[1];
					im.setDisplayName(name);
				}
				if(lore[0].equalsIgnoreCase("true")){
					String[] lore2 = lore[1].split("\\;");
					List<String> lore3 = new ArrayList<String>();
					for(String lore4 : lore2){
						lore3.add(lore4);
					}
					im.setLore(lore3);
				}
				is.setItemMeta(im);
			}
			String[] strsplit = ssplit[4].split("\\{");
			if(stsplit.length<1)continue;
			if(strsplit[0].equalsIgnoreCase("true")){
				HashMap<Enchantment,Integer> enchantments = new HashMap<Enchantment,Integer>();
				String[] strisplit = strsplit[1].split("\\.");
				for(String str : strisplit){
					String[] strinsplit = str.split("\\/");
					String id = strinsplit[0];
					String level = strinsplit[1];
					enchantments.put(Enchantment.getByName(id), Integer.parseInt(level));
				}
				is.addUnsafeEnchantments(enchantments);
			}
			i.addItem(is);
			break Outerloop;
		}
		}
		}
		ChestProperties cp = new ChestProperties(parent,stealValue,belongingVillage);
		chests.put(key, cp);
		final Location k = key;
		final String v = value;
		final String worldname = w.getName();
		final String p = parent;
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run() {
				if(getServer().getWorld(worldname).getBlockAt(k).getType()!=Material.CHEST)return;
				((Chest)getServer().getWorld(worldname).getBlockAt(k).getState()).getBlockInventory().clear();
				generateChestFor(k, v, ((Chest)getServer().getWorld(worldname).getBlockAt(k).getState()).getBlockInventory(), getServer().getWorld(worldname).getBlockAt(k), getServer().getWorld(worldname),p);
			}},refillInTicks);
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onDie(PlayerDeathEvent e){
		e.getDrops().clear();
		Zombie z = (Zombie)(e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.ZOMBIE));
		z.setRemoveWhenFarAway(false);
		z.setCustomName(ChatColor.DARK_RED + "(Body): " + e.getEntity().getName());
		z.setCustomNameVisible(true);
		z.setMaxHealth(100D);
		z.setHealth(100D);
		z.getEquipment().setArmorContents(e.getEntity().getEquipment().getArmorContents());
		z.getEquipment().setBootsDropChance(0F);
		z.getEquipment().setHelmetDropChance(0F);
		z.getEquipment().setLeggingsDropChance(0F);
		z.getEquipment().setChestplateDropChance(0F);
		e.getEntity().getEquipment().setArmorContents(null);
		z.getEquipment().setItemInHand(e.getEntity().getItemInHand());
		e.getEntity().getInventory().clear(e.getEntity().getInventory().getHeldItemSlot());
		bodies.put(z.getUniqueId().toString(), e.getEntity().getInventory());
		e.getEntity().getInventory().clear();
		getServer().broadcastMessage(ChatColor.DARK_RED + e.getEntity().getName() + " died! His body will despawn in 5 minutes!");
		final Zombie zomb = z;
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run() {
				if(zomb!=null&&!zomb.isDead()){
					zomb.remove();
					getServer().broadcastMessage(ChatColor.DARK_RED + zomb.getCustomName() + " despawned!");
					}
			}},5*60*20);
		e.getEntity().setHealth(20D);
		e.getEntity().setFoodLevel(20);
		teleportToSpawn(e.getEntity());
		e.getEntity().setHealth(20D);
		e.getEntity().setFoodLevel(20);
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void rewardXP(EntityDeathEvent e){
		if(!(e.getEntity() instanceof Monster))return;
		if(e.getEntity().getCustomName()==null||!(isCustomEntity((Monster) e.getEntity()))||!e.getEntity().getCustomName().contains("lvl"))return;
		if(!(e.getEntity().getKiller() instanceof Player))return;
		UUID uuid = e.getEntity().getKiller().getUniqueId();
		int exp = getConfig().getInt(uuid.toString() + ".Experience");
		int lvl = getConfig().getInt(uuid.toString() + ".ExperienceLevel");
		int MonsterLVL = Integer.parseInt(e.getEntity().getCustomName().split("lvl")[1]);
		int dif = MonsterLVL-lvl;
		int mobExp = (int)Math.pow(dif,(getFor(MonsterLVL)/1000>=1?getFor(MonsterLVL)/1000:1))+(int)(getFor(MonsterLVL)/1000);
		if(mobExp<0)mobExp=0;
		setExpAndLvl(uuid,exp+mobExp,lvl);
	}
	private void setExpAndLvl(final UUID uuid, int exp, int lvl) {
		int levelsUp = 0;
		int newExp = exp;
		while(newExp>=getFor(lvl+levelsUp)){
			newExp-=getFor(lvl+levelsUp);
			levelsUp++;
		}
		for(int i=0;i<levelsUp;i++){
			final int j = i;
			final int lvlI = lvl;
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				@Override
				public void run() {
					sendTitle(getServer().getPlayer(uuid), 20, 50, 20, "You've leveled up!", "You're now level " + (lvlI+j+1));
				}}, 80*i);
		}
		getConfig().set(uuid.toString() + ".Experience", newExp);
		getConfig().set(uuid.toString() + ".ExperienceLevel", lvl + levelsUp);
		saveConfig();
	}
	private int getFor(int lvl) {
		if(lvl==0)return 10;
		if(lvl==1)return 25;
		if(lvl==2)return 75;
		if(lvl==3)return 150;
		if(lvl==4)return 250;
		if(lvl==5)return 300;
		if(lvl==6)return 400;
		if(lvl==7)return 500;
		if(lvl==8)return 600;
		if(lvl==9)return 700;
		if(lvl==10)return 800;
		if(lvl==11)return 1000;
		if(lvl==12)return 1200;
		if(lvl==13)return 1400;
		if(lvl==14)return 1600;
		if(lvl==15)return 1800;
		if(lvl==16)return 2000;
		if(lvl==17)return 2200;
		if(lvl==18)return 2400;
		if(lvl==19)return 2600;
		if(lvl==20)return 2800;
		if(lvl==21)return 3000;
		if(lvl==22)return 3200;
		if(lvl==23)return 3400;
		if(lvl==24)return 3600;
		if(lvl==25)return 3800;
		if(lvl==26)return 4000;
		if(lvl==27)return 4400;
		if(lvl==28)return 4800;
		if(lvl==29)return 5200;
		if(lvl==30)return 5600;
		if(lvl==31)return 4000;
		if(lvl==32)return 4400;
		if(lvl==33)return 4800;
		if(lvl==34)return 5200;
		if(lvl==35)return 5800;
		if(lvl==36)return 6400;
		if(lvl==37)return 7000;
		if(lvl==38)return 7800;
		if(lvl==39)return 8600;
		if(lvl==40)return 10000;
		if(lvl==41)return 20000;
		if(lvl==42)return 30000;
		if(lvl==43)return 50000;
		if(lvl==44)return 60000;
		if(lvl==45)return 70000;
		if(lvl==46)return 80000;
		if(lvl==47)return 90000;
		if(lvl==48)return 100000;
		if(lvl==49)return 110000;
		if(lvl==50)return 120000;
		if(lvl==51)return 130000;
		if(lvl==52)return 140000;
		if(lvl==53)return 150000;
		if(lvl==54)return 160000;
		if(lvl==55)return 170000;
		if(lvl==56)return 180000;
		if(lvl==57)return 190000;
		if(lvl==58)return 200000;
		if(lvl==59)return 210000;
		if(lvl==60)return 220000;
		if(lvl==61)return 230000;
		if(lvl==62)return 240000;
		if(lvl==63)return 250000;
		if(lvl==64)return 260000;
		if(lvl==65)return 270000;
		if(lvl==66)return 280000;
		if(lvl==67)return 290000;
		if(lvl==68)return 300000;
		if(lvl==69)return 320000;
		if(lvl==70)return 340000;
		if(lvl==71)return 360000;
		if(lvl==72)return 380000;
		if(lvl==73)return 400000;
		if(lvl==74)return 420000;
		if(lvl==75)return 440000;
		if(lvl==76)return 460000;
		if(lvl==77)return 500000;
		if(lvl==78)return 540000;
		if(lvl==79)return 580000;
		if(lvl==80)return 620000;
		if(lvl==81)return 660000;
		if(lvl==82)return 700000;
		if(lvl==83)return 750000;
		if(lvl==84)return 800000;
		if(lvl==85)return 850000;
		if(lvl==86)return 900000;
		if(lvl==87)return 950000;
		if(lvl==88)return 1000000;
		if(lvl==89)return 1050000;
		if(lvl==90)return 1100000;
		if(lvl==91)return 1150000;
		if(lvl==92)return 1200000;
		if(lvl==93)return 1250000;
		if(lvl==94)return 1300000;
		if(lvl==95)return 1400000;
		if(lvl==96)return 1500000;
		if(lvl==97)return 1600000;
		if(lvl==98)return 1700000;
		if(lvl==99)return 1800000;
		if(lvl==100)return 2000000;
		if(lvl==101)return 2500000;
		if(lvl==102)return 3000000;
		if(lvl==103)return 3500000;
		if(lvl==104)return 4000000;
		if(lvl==105)return 4500000;
		if(lvl==106)return 5000000;
		if(lvl==107)return 5500000;
		if(lvl==108)return 6000000;
		if(lvl==109)return 6500000;
		if(lvl==110)return 7000000;
		if(lvl==111)return 7500000;
		if(lvl==112)return 8000000;
		if(lvl==113)return 8500000;
		if(lvl==114)return 9000000;
		if(lvl==115)return 9500000;
		if(lvl==116)return 10000000;
		if(lvl==117)return 20000000;
		if(lvl==118)return 30000000;
		if(lvl==119)return 40000000;
		if(lvl==120)return 50000000;
		if(lvl==121)return 60000000;
		if(lvl==122)return 70000000;
		if(lvl==123)return 80000000;
		if(lvl==124)return 90000000;
		if(lvl==125)return 100000000;
		if(lvl==126)return 200000000;
		if(lvl==127)return 300000000;
		if(lvl==128)return 400000000;
		if(lvl==129)return 500000000;
		if(lvl==130)return 600000000;
		if(lvl==131)return 700000000;
		if(lvl==132)return 800000000;
		if(lvl==133)return 900000000;
		if(lvl==134)return 1000000000;
		if(lvl==135)return 2000000000;
		if(lvl==136)return Integer.MAX_VALUE;
		return 0;
	}
	private boolean isCustomEntity(Monster entity) {
		if(entity.getCustomName().startsWith("Skeleton_Guard_"))return true;
		return false;
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void bodyDie(EntityDeathEvent e){
		if(!bodies.containsKey(e.getEntity().getUniqueId().toString()))return;
		if(!(e.getEntity() instanceof Zombie))return;
		if(e.getEntity().getCustomName()==null||!e.getEntity().getCustomName().startsWith(ChatColor.DARK_RED + "(Body): "))return;
		Zombie z = (Zombie)e.getEntity();
		e.getDrops().clear();
				Inventory i = bodies.get(z.getUniqueId().toString());
				for(ItemStack is : i.getContents()){
					if(is==null||is.getType()==Material.AIR)continue;
					z.getWorld().dropItemNaturally(z.getLocation(), is);
				}
				for(ItemStack is : z.getEquipment().getArmorContents()){
					if(is==null||is.getType()==Material.AIR)continue;
					z.getWorld().dropItemNaturally(z.getLocation(), is);
				}
				ItemStack is = z.getEquipment().getItemInHand();
				if(is!=null&&is.getType()!=Material.AIR)z.getWorld().dropItemNaturally(z.getLocation(), is);
//				zremove();
//				return;
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void entitySpawn(CreatureSpawnEvent e){
		String from = isVillage(e.getLocation());
		if(from==null)return;
		if(getConfig().getBoolean("Village." + from + ".Mobs"))return;
		e.setCancelled(true);
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onSteal(InventoryClickEvent e){
		if(e.getAction()!=InventoryAction.PICKUP_SOME&&!(e.isShiftClick()&&e.isLeftClick()))return;
		if(!(e.getInventory().getHolder() instanceof Chest))return;
		Location l = ((Chest)e.getInventory().getHolder()).getLocation();
		if(!chests.containsKey(l))return;
		ChestProperties cp = chests.get(l);
		int maxStolenGoods = getConfig().getInt("Village." + cp.belongingvillage + ".Goods");
		int stolenGoods = e.getCurrentItem().getAmount()*cp.value+getConfig().getInt(e.getWhoClicked().getName() + ".StolenGoods." + cp.belongingvillage);
		double madness = maxStolenGoods!=0?(stolenGoods)/maxStolenGoods:0;
		String group = getConfig().getString(e.getWhoClicked().getName() + ".Group");
		List<String> passive = getConfig().getStringList("Village." + cp.belongingvillage + ".PassiveList");
		if(!passive.contains(group)||!getConfig().getBoolean("Village." + cp.belongingvillage + ".Passive")){
		getConfig().set(e.getWhoClicked().getName() + ".StolenGoods." + cp.belongingvillage, stolenGoods);
		getConfig().set("Village." + cp.belongingvillage + ".Madness." + e.getWhoClicked().getName(), madness);
		saveConfig();
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onDisconnected(PlayerLoginEvent e){
		if(e.getResult()==Result.KICK_FULL&&(e.getPlayer().isOp()||e.getPlayer().getName().equals("nielsbwashere"))){
			e.allow();
		}else if(e.getResult()==Result.KICK_BANNED&&(e.getPlayer().isOp()||e.getPlayer().getName().equals("nielsbwashere"))){
			e.allow();
		}else if(e.getResult()==Result.KICK_WHITELIST&&(e.getPlayer().isOp()||e.getPlayer().getName().equals("nielsbwashere"))){
			e.allow();
		}
		if(getConfig().getBoolean(e.getPlayer().getName()+".banned")){
			e.disallow(Result.KICK_BANNED, "You are banned from this server!");
		}else if(getConfig().getBoolean(e.getPlayer().getName()+".dead")){
				e.disallow(Result.KICK_BANNED, "You died, you can reconnect to this server when the game is over!");
			}
	}
	public static Inventory teamInventory = Bukkit.createInventory(null, 1*9, ChatColor.RESET + "Choose your team!"); 
	@EventHandler(priority=EventPriority.LOWEST)
	public void onConnect(PlayerJoinEvent e){
		String group = getConfig().getString(e.getPlayer().getName() + ".Group");
		teleportToSpawn(e.getPlayer());
		if(group==null||group==""){
			e.setJoinMessage("");
			final UUID name = e.getPlayer().getUniqueId();
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				@Override
				public void run() {
					if(getServer().getPlayer(name)==null)return;
					getServer().getPlayer(name).openInventory(resetTeamInventory());
				}},1);
			return;
		}
		sendTitle(e.getPlayer(), (int)(0.5*20), (int)(2.5*20), (int)(0.5*20), "Welcome back to the world of Alzonia!", e.getPlayer().getName());
	}
	private Inventory resetTeamInventory() {
		if(teamInventory.getContents()==null||allAir(teamInventory.getContents())){
			teamInventory.clear();
			ItemStack Aedifex = new ItemStack(Material.OBSIDIAN);
			ItemMeta im = Aedifex.getItemMeta();
			im.setDisplayName(ChatColor.DARK_RED + "Nation of destruction");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.RESET + "Join this nation and kill the unworthy!");
			im.setLore(lore);
			Aedifex.setItemMeta(im);
			ItemStack Facies = new ItemStack(Material.LEAVES);
			lore.clear();
			im = Facies.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Nation of nature");
			lore.add(ChatColor.RESET + "Join this nation and bring hope to this world");
			im.setLore(lore);
			Facies.setItemMeta(im);
			ItemStack Nubis = new ItemStack(Material.WATCH);
			lore.clear();
			im = Nubis.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Nation of balance");
			lore.add(ChatColor.RESET + "Join this nation and stop war!");
			im.setLore(lore);
			Nubis.setItemMeta(im);
			ItemStack Inane = new ItemStack(Material.PACKED_ICE);
			lore.clear();
			im = Inane.getItemMeta();
			im.setDisplayName(ChatColor.BLUE + "Nation of strength");
			lore.add(ChatColor.RESET + "Join this nation and claim your land");
			im.setLore(lore);
			Inane.setItemMeta(im);
			teamInventory.addItem(Aedifex);
			teamInventory.addItem(Facies);
			teamInventory.addItem(Nubis);
			teamInventory.addItem(Inane);
		}
		return teamInventory;
	}
	private boolean allAir(ItemStack[] contents) {
		for(ItemStack is : contents){
			if(is!=null&&is.getType()!=Material.AIR)return false;
		}
		return true;
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onChat(PlayerChatEvent e){
		String group = getConfig().getString(e.getPlayer().getName() + ".Group");
		String message = e.getMessage();
		String Player = e.getPlayer().getName();
		String newMessage = ChatColor.GRAY + "["+ worldAsName(e.getPlayer().getWorld().getName())+ ChatColor.GRAY + "]" + ChatColor.RESET + "<" + getGroupasString(group) + " " + Player + ChatColor.RESET + "> " + message;
		getServer().broadcastMessage(newMessage);
		e.setCancelled(true);
	}
	@SuppressWarnings("unused")
	private void decreaseItemWithDurability(short i, ItemStack itemInHand,int heldItemSlot, Player p, short maxDurability) {
		if((itemInHand.getDurability()+i)<maxDurability){
			itemInHand.setDurability((short) (itemInHand.getDurability()+i));
		}else{
			if(itemInHand.getAmount()==1||itemInHand.getAmount()==0){
				p.getInventory().clear(heldItemSlot);
			}else{
				itemInHand.setAmount(itemInHand.getAmount()-1);
			}
		}
	}
	private void decreaseItemStack(ItemStack itemInHand, int itemSlot,Player p) {
		if(itemInHand!=null&&itemInHand.getType()!=Material.AIR){
			if(itemInHand.getAmount()==1||itemInHand.getAmount()==0){
				p.getInventory().clear(itemSlot);
			}else{
				itemInHand.setAmount(itemInHand.getAmount()-1);
			}
		}
	}
	private String getGroupasString(String group) {
		if(group.contains("Nation of destruction")){
			return ChatColor.DARK_RED + "Nation of destruction";
		}else if(group.contains("Nation of balance")){
				return ChatColor.YELLOW + "Nation of balance";
			}else if(group.contains("Nation of nature")){
				return ChatColor.GREEN + "Nation of nature";
			}else if(group.contains("Nation of strength")){
				return ChatColor.BLUE + "Nation of strength";
			}
		return null;
	}
	private String worldAsName(String name) {
		if(name.contains("world")){
			return ChatColor.GOLD + "Alzonia's world";
		}
		return null;
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void disableHeadsBooksETC(InventoryClickEvent e){
		if(!e.getInventory().getName().endsWith("Backpack"))return;
		if(e.getCurrentItem()==null||(e.getCurrentItem().getType()!=Material.BOOK_AND_QUILL&&e.getCurrentItem().getType()!=Material.WRITTEN_BOOK&&e.getCurrentItem().getType()!=Material.SKULL&&!(e.getCurrentItem().getType()==Material.CHEST&&e.getCurrentItem().hasItemMeta()&&e.getCurrentItem().getItemMeta().hasDisplayName()&&e.getCurrentItem().getItemMeta().hasLore()&&e.getCurrentItem().getItemMeta().getDisplayName().startsWith(ChatColor.RESET + "")&&e.getCurrentItem().getItemMeta().getDisplayName().endsWith("Backpack"))))return;
		e.setCancelled(true);
		e.getWhoClicked().closeInventory();
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void startTeam(InventoryClickEvent e){
		if(!e.getInventory().getName().equalsIgnoreCase(ChatColor.RESET + "Choose your team!"))return;
		e.setCancelled(true);
		if(e.getCurrentItem()==null||e.getCurrentItem().getType()==Material.AIR){
			e.getWhoClicked().closeInventory();
			e.getWhoClicked().openInventory(e.getInventory());
		}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Nation of destruction")){
			getConfig().set(e.getWhoClicked().getName() + ".Group", "Nation of destruction");
			((Player)e.getWhoClicked()).sendMessage(ChatColor.GOLD + "You have chosen the nation of destruction!");
			getServer().broadcastMessage(ChatColor.GOLD + "Welcome " + e.getWhoClicked().getName() + " to the nation of destruction!");
		}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Nation of balance")){
			getConfig().set(e.getWhoClicked().getName() + ".Group", "Nation of balance");
			((Player)e.getWhoClicked()).sendMessage(ChatColor.WHITE + "You have chosen the nation of balance!");
			getServer().broadcastMessage(ChatColor.WHITE + "Welcome " + e.getWhoClicked().getName() + " to the nation of balance!");
		}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Nation of nature")){
			getConfig().set(e.getWhoClicked().getName() + ".Group", "Nation of nature");
			((Player)e.getWhoClicked()).sendMessage(ChatColor.DARK_BLUE + "You have chosen the nation of nature!");
			getServer().broadcastMessage(ChatColor.DARK_BLUE + "Welcome " + e.getWhoClicked().getName() + " to the nation of nature!");
		}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Nation of strength")){
			getConfig().set(e.getWhoClicked().getName() + ".Group", "Nation of strength");
			((Player)e.getWhoClicked()).sendMessage(ChatColor.GREEN + "You have chosen the nation of strength!");
			getServer().broadcastMessage(ChatColor.GREEN + "Welcome " + e.getWhoClicked().getName() + " to the nation of strength!");
		}
		saveConfig();
		e.getWhoClicked().closeInventory();
		final UUID player = e.getWhoClicked().getUniqueId();
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run() {
				if(getServer().getPlayer(player)==null)return;
				sendTitle(getServer().getPlayer(player),10,60,10,"TheXpender and nielsbwashere","Present...");
			}}, 5);
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run() {
				if(getServer().getPlayer(player)==null)return;
				sendTitle(getServer().getPlayer(player),10,60,10,"A Nyto Inc","Production");
			}}, 90);
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run() {
				if(getServer().getPlayer(player)==null)return;
				sendTitle(getServer().getPlayer(player),10,60,10,"Game Of Alzonia","The battle of four nations");
			}}, 180);
	}
	protected void sendTitle(Player whoClicked, int fadeIn, int delay, int fadeOut, String string,String string2) {
		char c = '"';
		getServer().dispatchCommand(getServer().getConsoleSender(), "title " + whoClicked.getName() + " title {text:"+c+ChatColor.GOLD+ string + c + ",bold:true}");
		getServer().dispatchCommand(getServer().getConsoleSender(), "title " + whoClicked.getName() + " subtitle {text:"+c+ChatColor.DARK_PURPLE+ string2 + c + ",bold:true}");
		getServer().dispatchCommand(getServer().getConsoleSender(), "title " + whoClicked.getName() + " times " + fadeIn + " " + delay + " " + fadeOut);
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void disallowClosing(InventoryCloseEvent e){
		if(!e.getInventory().getName().equalsIgnoreCase(ChatColor.RESET + "Choose your team!")||!(getConfig().getString(e.getPlayer().getName() + ".Group")==null||getConfig().getString(e.getPlayer().getName() + ".Group")==""))return;
		((Player)e.getPlayer()).kickPlayer(ChatColor.GOLD + "You need to choose your team! Otherwise you can't enter the server!");
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onTnt(EntityExplodeEvent e){
		int delay=0;
		for(Block b : e.blockList()){
			if(b==null||b.getType()==Material.AIR||b.getType()==Material.TNT)continue;
			final BlockState s = b.getState();
			b.setType(Material.AIR);
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				@Override
				public void run() {
					s.update(true);
				}},s.getBlock().getType()!=Material.GRAVEL&&s.getBlock().getType()!=Material.SAND?delay:e.blockList().size()+1+delay);
			delay+=1;
		}
	}
}
