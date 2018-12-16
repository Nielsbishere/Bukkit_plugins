package net.nielsbwashere.src.MagicBoxes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin implements Listener{
	@Override
	public void onEnable() {
		if(!getConfig().getBoolean("MagicBoxes.Settings.Init")){
			getConfig().set("MagicBoxes.Settings.ChestDespawnDelay", 5*60);
			getConfig().set("MagicBoxes.Settings.Init",true);
			saveConfig();
		}
		Bukkit.getPluginManager().registerEvents(this, this);
		generateChests();
	}
	@Override
	public void onDisable() {
		saveConfig();
		Bukkit.getScheduler().cancelTasks(this);
		removeChests();
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("MagicBoxes")){
			if(!sender.hasPermission("MagicBoxes.Command.Basic")&&!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
				return false;
			}
			if(args.length==0||args[0].equalsIgnoreCase("List")){
				sender.sendMessage(ChatColor.DARK_RED + "/MagicBoxes <list, remove, set, add, clear, reload> <arg1> <arg2> <...>");
				return false;
			}
			if(args[0].equalsIgnoreCase("reload")){
				if(!sender.hasPermission("MagicBoxes.Command.Reload")&&!sender.isOp()){
					sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
					return false;
				}
				Bukkit.getScheduler().cancelTasks(this);
				removeChests();
				generateChests();
				return false;
			}else if(args[0].equalsIgnoreCase("add")){
				if(!sender.hasPermission("MagicBoxes.Command.Add.Basic")&&!sender.isOp()){
					sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
					return false;
				}
				if(args.length==1){
					sender.sendMessage(ChatColor.DARK_RED + "/MagicBoxes add <location/item/chest> <name>");
					return false;
				}
				if(args[1].equalsIgnoreCase("chest")){
					if(!sender.hasPermission("MagicBoxes.Command.Add.Chest")&&!sender.isOp()){
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
						return false;
					}
					if(args.length!=3){
						sender.sendMessage(ChatColor.DARK_RED + "Invalid usage: /MagicBoxes add chest <name>");
						return false;
					}
				addChest(args[2]);
				sender.sendMessage(ChatColor.GOLD + "Added a new chest ("+args[2]+")!");
				sender.sendMessage(ChatColor.GOLD + "Don't forget to set the chest's interval, the items that spawn in it and the locations that it can spawn in.");
				return false;
				}else if(args[1].equalsIgnoreCase("location")){
					if(!sender.hasPermission("MagicBoxes.Command.Add.Location")&&!sender.isOp()){
					sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
					return false;
					}
					if(!(sender instanceof Player)){
						sender.sendMessage(ChatColor.DARK_RED + "You must be a player to add a chest location!");
						return false;
					}
					if(args.length!=3){
						sender.sendMessage(ChatColor.DARK_RED + "Invalid usage: /MagicBoxes add location <name>");
						return false;
					}
					addLoc((Player)sender,args[2]);
					sender.sendMessage(ChatColor.GOLD + "Added a location for chest " + args[2]);
					return false;
				}else if(args[1].equalsIgnoreCase("item")){
					if(!sender.hasPermission("MagicBoxes.Command.Add.Item")&&!sender.isOp()){
					sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
					return false;
					}
					if(!(sender instanceof Player)){
						sender.sendMessage(ChatColor.DARK_RED + "You must be a player to add an item!");
						return false;
					}
					if(args.length!=4){
						sender.sendMessage(ChatColor.DARK_RED + "Invalid usage: /MagicBoxes add item <name> <chance (0 - 10 000)>");
						return false;
					}
					if(addItem((Player)sender,args[2],parse(args[3])))
					sender.sendMessage(ChatColor.GOLD + "Added an item for chest " + args[2]);
					return false;
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "/MagicBoxes add <location/item/chest> <name>");
					return false;
				}
			}else if(args[0].equalsIgnoreCase("set")){
				if(!sender.hasPermission("MagicBoxes.Command.Set.Interval")&&!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
				return false;
				}
				if(args.length<3){
					sender.sendMessage(ChatColor.DARK_RED + "/MagicBoxes set <name> <interval>");
					return false;
				}
				String arg = getConfig().getString("MagicBoxes.Args."+args[1]);
				int i=0;
				try{
					i=Integer.parseInt(args[2]);
				}catch(Exception e){
					sender.sendMessage(ChatColor.DARK_RED + "You can only put in a number as interval argument");
					return false;
				}
				if(arg!=null)
				arg=arg.replace(arg.split("\\'")[0], i+"");
				else arg=i+"'";
				getConfig().set("MagicBoxes.Args."+args[1],arg);
				saveConfig();
				sender.sendMessage(ChatColor.GOLD + "Set the chest ("+args[1]+") it's spawn interval!");
				return false;
			}else if(args[0].equalsIgnoreCase("clear")){
				if(!sender.hasPermission("MagicBoxes.Command.Clear.Basic")&&!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
				return false;
				}
				if(args.length<3){
					sender.sendMessage(ChatColor.DARK_RED + "/MagicBoxes clear <name> <inventory/locations>");
					return false;
				}
				if(args[2].equalsIgnoreCase("inventory")){
					if(!sender.hasPermission("MagicBoxes.Command.Clear.Inventory")&&!sender.isOp()){
					sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
					return false;
					}
					getConfig().set("MagicBoxes.Args."+args[1],getConfig().getString("MagicBoxes.Args."+args[1]).replace("MagicBoxes.Args."+args[1].split("\\'")[1], ""));
					sender.sendMessage(ChatColor.GOLD + "You've cleared the inventory of chest " + args[1]);
				}else if(args[2].equalsIgnoreCase("locations")){
					if(!sender.hasPermission("MagicBoxes.Command.Clear.Locations")&&!sender.isOp()){
					sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
					return false;
					}
					getConfig().set("MagicBoxes."+args[1]+".Locations",null);
					sender.sendMessage(ChatColor.GOLD + "Chest " + args[1] + " can't spawn anymore!");
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "/MagicBoxes clear <name> <inventory/locations>");
					return false;
				}
				saveConfig();
				return false;
			}else if(args[0].equalsIgnoreCase("remove")){
				if(!sender.hasPermission("MagicBoxes.Command.Remove.Chest")&&!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
				return false;
				}
				if(args.length<2){
					sender.sendMessage(ChatColor.DARK_RED + "/MagicBoxes remove <name>");
					return false;
				}
					getConfig().set("MagicBoxes.Args."+args[1], null);
					getConfig().set("MagicBoxes."+args[1], null);
					saveConfig();
					sender.sendMessage(ChatColor.GOLD + "Chest " + args[1] + " is now removed!");
				return false;
			}
			else{
				sender.sendMessage(ChatColor.DARK_RED + "/MagicBoxes <list, remove, set, add, clear> <arg1> <arg2> <...>");
				return false;
			}
		}
		return false;
	}
	private void removeChests() {
		for(String s : getConfig().getStringList("MagicBoxes.ChestTypes")){
				for(String st : getConfig().getStringList("MagicBoxes."+s+".Locations")){
					World w = getServer().getWorld(st.split(",")[0]);
					if(w==null)continue;
					Location l = new Location(w,parse(st.split(",")[1]),257,parse(st.split(",")[2]));
					while(l.getBlockY()>2){
						if(w.getBlockAt(l).hasMetadata("IsMagicChest")&&w.getBlockAt(l).getState() instanceof Chest){
							((Chest)w.getBlockAt(l).getState()).getBlockInventory().clear();
							w.getBlockAt(l).setType(Material.AIR);
						}
						l.add(0,-1,0);
					}
				}
		}
	}
	private boolean addItem(Player sender, String string, int i) {
		String args = getConfig().getString("MagicBoxes.Args."+string);
		if(!args.endsWith("\"")&&!args.endsWith("'"))args=args+"\"";
		args=args+itemAsString(sender)+","+i;
		if(args.endsWith("ERROR")){
			sender.sendMessage(ChatColor.DARK_RED + "Couldn't add that item to the chest!");
			return false;
		}
		getConfig().set("MagicBoxes.Args."+string,args);
		saveConfig();
		return true;
	}
	private String itemAsString(Player sender) {
		if(sender.getItemInHand()==null||sender.getItemInHand().getType()==Material.AIR){
			return "ERROR";
		}
		ItemStack is = sender.getItemInHand();
		return is.getType().name()+","+is.getAmount()+","+is.getDurability()+","+(is.hasItemMeta()&&is.getItemMeta().hasDisplayName()?is.getItemMeta().getDisplayName():"false")+","+(is.hasItemMeta()&&is.getItemMeta().hasLore()?lore(is.getItemMeta().getLore()):"false")+","+(is.hasItemMeta()&&is.getItemMeta().hasEnchants()?enchants(is.getItemMeta().getEnchants()):"false");
	}
	private String lore(List<String> lore) {String s="";boolean isFirst=true;for(String st : lore){if(!isFirst)s=s+"|";s=s+st;isFirst=false;}return s;}
	private String enchants(Map<Enchantment,Integer> ench) {String s="";boolean isFirst=true;for(Entry<Enchantment,Integer> e : ench.entrySet()){if(!isFirst)s=s+"|";s=s+e.getKey().getName()+"."+e.getValue();isFirst=false;}return s;}
	private void addLoc(Player sender, String string) {
		List<String> strl = getConfig().getStringList("MagicBoxes." + string + ".Locations");
		String s = sender.getWorld().getName()+","+sender.getLocation().getBlockX()+","+sender.getLocation().getBlockZ();
		if(strl.contains(s))return;
		strl.add(s);
		getConfig().set("MagicBoxes." + string + ".Locations",strl);
		saveConfig();
	}
	private void addChest(String string) {
		List<String> strl = getConfig().getStringList("MagicBoxes.ChestTypes");
		if(strl.contains(string))return;
		strl.add(string);
		getConfig().set("MagicBoxes.ChestTypes",strl);
		saveConfig();
	}
	public void generateChests(){
		List<String> cst = getConfig().getStringList("MagicBoxes.ChestTypes");
		if(cst==null||cst.isEmpty())return;
		for(String s : cst){
			List<String> strl = getConfig().getStringList("MagicBoxes."+s+".Locations");
			if(strl==null||strl.isEmpty())continue;
			String st = null;
			while(st==null){
				for(String str : strl){
					if(Math.random()>=0.1)continue;
					st=str;
					break;
				}
			}
			World w = getServer().getWorld(st.split(",")[0]);
			if(w==null)continue;
			final String args = getConfig().getString("MagicBoxes.Args."+s);
			final Location l = new Location(w,parse(st.split(",")[1]),257,parse(st.split(",")[2]));
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
				@Override
				public void run() {
					spawnChest(l,args);
				}}, 0, 20*parse(args.split("\\'")[0]));
		}
	}
	public void spawnChest(Location l, String s){
		@SuppressWarnings("deprecation")
		FallingBlock fb = l.getWorld().spawnFallingBlock(l, Material.CHEST, (byte)0);
		fb.setMetadata("chestMeta", new FixedMetadataValue(this, s));
		System.out.println(s + " at " + fb.getLocation().toVector());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onFallingBlock(final EntityChangeBlockEvent event){
		if(event.getEntity()==null||!event.getEntity().hasMetadata("chestMeta")||event.getEntityType()!=EntityType.FALLING_BLOCK)return;
		String s = event.getEntity().getMetadata("chestMeta").get(0).asString();
		createChest(s,event.getEntity().getLocation());
		event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
		final int i = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			int j=0;
			@Override
			public void run() {
				j++;
				spawnRandomFirework(event.getEntity().getLocation().add(0,1,0),j<4?j:3);
			}}, 0, 10);
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run() {
				Bukkit.getScheduler().cancelTask(i);
			}},20*7);
		event.setCancelled(true);
	}
	private void spawnRandomFirework(Location l, int j) {
		Firework fw = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        Random r = new Random();  
        int rt = r.nextInt(4) + 1;
        Type type = Type.BALL;      
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;
        Color c1 = Color.fromRGB((int)(255*Math.random()), (int)(255*Math.random()), (int)(255*Math.random()));
        Color c2 = Color.fromRGB((int)(255*Math.random()), (int)(255*Math.random()), (int)(255*Math.random()));
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
        fwm.addEffect(effect);
        int rp = j;
        fwm.setPower(rp);
        fw.setFireworkMeta(fwm);  
	}
	private void createChest(String s, final Location location) {
		location.getWorld().getBlockAt(location).setType(Material.CHEST);
		Chest c = (Chest)location.getWorld().getBlockAt(location).getState();
		c.setMetadata("IsMagicChest", new FixedMetadataValue(this, true));
		genInv(c.getBlockInventory(),s);
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run() {
				if(location.getWorld().getBlockAt(location).getType()==Material.CHEST){
					((Chest)location.getWorld().getBlockAt(location).getState()).getBlockInventory().clear();
					location.getWorld().getBlockAt(location).setType(Material.AIR);
				}
			}}, 20*getConfig().getInt("MagicBoxes.Settings.ChestDespawnDelay"));
	}
	private void genInv(Inventory inv, String s) {
		inv.clear();
		String[] items = s.split("\\'")[1].split("\\\"");
		for(String st : items){
			String[] str = st.split(",");
			ItemStack is = new ItemStack(Material.getMaterial(str[0]), parse(str[1]), (short)parse(str[2]));
			if(!str[5].equals("false"))is.addEnchantments(getEnch(str[5]));
			ItemMeta im = is.getItemMeta();
			if(!str[3].equals("false"))im.setDisplayName(str[3]);
			if(!str[4].equals("false"))im.setLore(getLore(str[4]));
			is.setItemMeta(im);
			int chance = parse(str[6]);
			if(Math.random()>chance/10000.0)continue;
			inv.addItem(is);
		}
	}
	private Map<Enchantment, Integer> getEnch(String string) {
		Map<Enchantment,Integer> map = new HashMap<Enchantment,Integer>();
		for(String s : string.split("\\|")){
			if(!map.containsKey(Enchantment.getByName(s.split("\\.")[0])))map.put(Enchantment.getByName(s.split("\\.")[0]), parse(s.split("\\.")[1]));
		}
		return map;
	}
	private List<String> getLore(String string) {
		return Arrays.asList(string.split("\\|"));
	}
	private int parse(String string) {
		for(char c : string.toCharArray()){
			if((c<48||c>57)&&c!='-')return 0;
		}
		return Integer.parseInt(string);
	}
}
