package net.nielsbwashere.src.SSM;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityBat;
import net.minecraft.server.v1_8_R1.EntityBlaze;
import net.minecraft.server.v1_8_R1.EntityCaveSpider;
import net.minecraft.server.v1_8_R1.EntityChicken;
import net.minecraft.server.v1_8_R1.EntityCow;
import net.minecraft.server.v1_8_R1.EntityCreeper;
import net.minecraft.server.v1_8_R1.EntityEnderDragon;
import net.minecraft.server.v1_8_R1.EntityEnderman;
import net.minecraft.server.v1_8_R1.EntityEndermite;
import net.minecraft.server.v1_8_R1.EntityGhast;
import net.minecraft.server.v1_8_R1.EntityGiantZombie;
import net.minecraft.server.v1_8_R1.EntityGuardian;
import net.minecraft.server.v1_8_R1.EntityHorse;
import net.minecraft.server.v1_8_R1.EntityIronGolem;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.EntityMagmaCube;
import net.minecraft.server.v1_8_R1.EntityMushroomCow;
import net.minecraft.server.v1_8_R1.EntityOcelot;
import net.minecraft.server.v1_8_R1.EntityPig;
import net.minecraft.server.v1_8_R1.EntityPigZombie;
import net.minecraft.server.v1_8_R1.EntityRabbit;
import net.minecraft.server.v1_8_R1.EntitySheep;
import net.minecraft.server.v1_8_R1.EntitySilverfish;
import net.minecraft.server.v1_8_R1.EntitySkeleton;
import net.minecraft.server.v1_8_R1.EntitySlime;
import net.minecraft.server.v1_8_R1.EntitySnowman;
import net.minecraft.server.v1_8_R1.EntitySpider;
import net.minecraft.server.v1_8_R1.EntitySquid;
import net.minecraft.server.v1_8_R1.EntityVillager;
import net.minecraft.server.v1_8_R1.EntityWitch;
import net.minecraft.server.v1_8_R1.EntityWither;
import net.minecraft.server.v1_8_R1.EntityWolf;
import net.minecraft.server.v1_8_R1.EntityZombie;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntityLiving;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
public class Core extends JavaPlugin implements Listener {
	
	public static final String TAG = ChatColor.BLUE + "[" + ChatColor.RESET + "SuperSmashMobs" + ChatColor.BLUE + "] " + ChatColor.RESET;
	public static Core instance;
	List<PlayerClass> pcl = new ArrayList<PlayerClass>();
	HashMap<Integer,Arena> arenas = new HashMap<Integer,Arena>();
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		instance=this;
		ArenaManager.instantiate();
	}
	@Override
	public void onDisable() {
		for(PlayerClass c : pcl){
		if(c==null)return;
		c.quit();
		}
		saveConfig();
	}
	@EventHandler
	public void onLogout(PlayerQuitEvent e){
		PlayerClass c = PlayerClass.fromPlayer(e.getPlayer());
		if(c==null)return;
		c.quit();
	}
	@EventHandler
	public void onDie(PlayerDeathEvent e){
		if(PlayerClass.fromPlayer(e.getEntity()).ar!=null)e.getEntity().setGameMode(GameMode.SPECTATOR);
	}
	@EventHandler
	public void inventoryClose(InventoryCloseEvent e){
		if(e.getInventory()==null||e.getInventory().getName()==null||e.getInventory().getName().equals(""))return;
		String name = e.getInventory().getName();
		if(!name.startsWith(ChatColor.GOLD +""))return;
		if(name.startsWith(ChatColor.GOLD + "(armor): ")){
			String Class = name.replace(ChatColor.GOLD + "(armor): ", "");
			InventoryHelper.setInventory("Classes."+Class+".Armor", this, e.getInventory());
			e.getPlayer().sendMessage(TAG + ChatColor.GOLD + Class + "'s Armor has been set!");
		}else if(name.startsWith(ChatColor.GOLD + "(inventory): ")){
			String Class = name.replace(ChatColor.GOLD + "(inventory): ", "");
			InventoryHelper.setInventory("Classes."+Class+".Inventory", this, e.getInventory());
			e.getPlayer().sendMessage(TAG + ChatColor.GOLD + Class + "'s Inventory has been set!");
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("SSM")){
			if(args.length==0||args[0].equalsIgnoreCase("list")){
				sender.sendMessage(TAG+ChatColor.DARK_RED + "/SSM <join/leave/create/remove/set/add/activate> ...");
				return false;
			}
			if(args[0].equalsIgnoreCase("create")){
				if(!perm("SSM.Create", sender)){
					sender.sendMessage(TAG+ChatColor.DARK_RED + "Permission denied!");
					return false;
				}
				if(args.length==1||args[1].equalsIgnoreCase("list")){
					sender.sendMessage(TAG+ChatColor.DARK_RED + "/SSM create <arena/class>");
					return false;
				}
				if(args[1].equalsIgnoreCase("arena")){
					if(args.length!=3){
						sender.sendMessage(TAG+ChatColor.DARK_RED + "/SSM create arena <name>");
						return false;
					}
					if(!(sender instanceof Player)){
						sender.sendMessage(TAG+ChatColor.DARK_RED + "Only players can create an arena!");
						return false;
					}
					String arena = args[2];
					int id = ArenaManager.getNext();
					Location l = ((Player)sender).getLocation();
					ArenaManager.register(id,arena,l);
					sender.sendMessage(TAG + ChatColor.GOLD + "Created an area! (" + id + "," + arena + ") with spawn and wait location: " + l.getX() + ", " + l.getY() + " and " + l.getZ());
					sender.sendMessage(TAG + ChatColor.GOLD + "Set spawn and wait location using: " + ChatColor.DARK_PURPLE + "/SSM set <spawn/wait>");
					return false;
				}else if(args[1].equalsIgnoreCase("class")){
					if(args.length!=4){
						sender.sendMessage(TAG+ChatColor.DARK_RED + "/SSM create class <name> <entity id>");
						return false;
					}
					String Class = args[2];
					String Id = args[3];
					int id = 0;
					try{
						id=Integer.parseInt(Id);
					}catch(Exception e){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "Entity id is supposed to be a number!");
						return false;
					}
					DisguiseType dt = null;
					for(DisguiseType d : DisguiseType.values()){
						if(d.id!=(byte)id)continue;
						dt=d;
						break;
					}
					if(dt==null){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "DisguiseCraft doesn't support this mob!");
						return false;
					}
					String disguiseType = dt.toString();
					getConfig().set("Classes."+Class+".DisguiseType", disguiseType);
					InventoryHelper.setInventory("Classes."+Class+".Armor", this, Bukkit.createInventory(null, 9));
					InventoryHelper.setInventory("Classes."+Class+".Inventory", this, Bukkit.createInventory(null, 9*4));
					saveConfig();
					sender.sendMessage(TAG + ChatColor.GOLD + "Class created!");
					sender.sendMessage(TAG + ChatColor.GOLD + "To set the class's inventory use " + ChatColor.DARK_PURPLE + "/SSM set class <armor/inventory>");
					sender.sendMessage(TAG + ChatColor.GOLD + "To add the class to an area use " + ChatColor.DARK_PURPLE + "/SSM add <arenaName> <className>");
					return false;
				}else onCommand(sender, command, label, new String[]{"create"});
			}else if(args[0].equalsIgnoreCase("remove")){
				if(!perm("SSM.Remove", sender)){
					sender.sendMessage(TAG+ChatColor.DARK_RED + "Permission denied!");
					return false;
				}
				if(args.length==1||args[1].equalsIgnoreCase("list")){
					sender.sendMessage(TAG + ChatColor.DARK_RED + "/SSM remove <Class/Arena> <name>");
					return false;
				}
				if(args[1].equalsIgnoreCase("Class")){
					if(args.length!=3){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "/SSM remove class <name>");
						return false;
					}
					PlayerClass.removeClass(args[2]);
					sender.sendMessage(TAG + ChatColor.GOLD + "You have succesfully removed class " + args[2]);
					return false;
				}else if(args[1].equalsIgnoreCase("Arena")){
					if(args.length!=3){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "/SSM remove Arena <name>");
						return false;
					}
					ArenaManager.removeArena(args[2]);
					sender.sendMessage(TAG + ChatColor.GOLD + "You have succesfully removed arena " + args[2]);
					return false;
				}else onCommand(sender,command,label,new String[]{"remove"});
			}else if(args[0].equalsIgnoreCase("activate")){
				if(!perm("SSM.Activate", sender)){
					sender.sendMessage(TAG+ChatColor.DARK_RED + "Permission denied!");
					return false;
				}
				if(args.length!=2){
					sender.sendMessage(TAG + ChatColor.DARK_RED + "/SSM activate <arenaName>");
					return false;
				}
				List<String> arenas = Core.instance.getConfig().getStringList("ArenaList");
				int id = Integer.MIN_VALUE;
				for(String s : arenas){
					if(getConfig().getString("Arenas."+s+".Name").equalsIgnoreCase(args[1])){
						id=Integer.parseInt(s);
						break;
					}
				}
				if(id==Integer.MIN_VALUE){
					sender.sendMessage(TAG + ChatColor.DARK_RED + "That arena doesn't exist!");
					return false;
				}
				Core.instance.addArena(id);
				sender.sendMessage(TAG + ChatColor.GOLD + "You've activated that arena!");
				return false;
			}else if(args[0].equalsIgnoreCase("set")){
				if(!perm("SSM.Set", sender)){
					sender.sendMessage(TAG+ChatColor.DARK_RED + "Permission denied!");
					return false;
				}
				if(args.length==1||args[1].equalsIgnoreCase("list")){
					sender.sendMessage(TAG + ChatColor.DARK_RED + "/SSM set <spawn/wait/hub/class> ...");
					return false;
				}
				if(args[1].equalsIgnoreCase("hub")){
					if(!(sender instanceof Player)){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "Only players can set spawns!");
						return false;
					}
					getConfig().set("MainLobby", RandLoc.fromLocation(((Player)sender).getLocation()).toString());
					saveConfig();
					sender.sendMessage(TAG + ChatColor.GOLD + "You've set the hub spawn!");
					return false;
				}else if(args[1].equalsIgnoreCase("wait")){
					if(!(sender instanceof Player)){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "Only players can set spawns!");
						return false;
					}
					if(args.length!=3){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "/SSM set wait <name>");
						return false;
					}
					List<String> arenas = Core.instance.getConfig().getStringList("ArenaList");
					int id = Integer.MIN_VALUE;
					for(String s : arenas){
						if(getConfig().getString("Arenas."+s+".Name").equalsIgnoreCase(args[2])){
							id=Integer.parseInt(s);
							break;
						}
					}
					if(id==Integer.MIN_VALUE){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "That arena doesn't exist!");
						return false;
					}
					getConfig().set("Arenas."+id+".WaitLoc", RandLoc.fromLocation(((Player)sender).getLocation()).toString());
					saveConfig();
					sender.sendMessage(TAG + ChatColor.GOLD + "You've set the wait spawn!");
					return false;
				}else if(args[1].equalsIgnoreCase("spawn")){
					if(!(sender instanceof Player)){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "Only players can set spawns!");
						return false;
					}
					if(args.length!=6){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "/SSM set spawn <name> <xRadius> <yRadius> <zRadius>");
						return false;
					}
					List<String> arenas = Core.instance.getConfig().getStringList("ArenaList");
					int id = Integer.MIN_VALUE;
					for(String s : arenas){
						if(getConfig().getString("Arenas."+s+".Name").equalsIgnoreCase(args[2])){
							id=Integer.parseInt(s);
							break;
						}
					}
					if(id==Integer.MIN_VALUE){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "That arena doesn't exist!");
						return false;
					}
					int x=Integer.MIN_VALUE,y=Integer.MIN_VALUE,z=Integer.MIN_VALUE;
					try{
						x=Integer.parseInt(args[3]);
						y=Integer.parseInt(args[4]);
						z=Integer.parseInt(args[5]);
					}catch(Exception e){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "xRadius, yRadius and zRadius are meant to be a number!");
						return false;
					}
					if(x==Integer.MIN_VALUE||y==Integer.MIN_VALUE||z==Integer.MIN_VALUE){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "xRadius, yRadius and zRadius are meant to be a number!");
						return false;
					}
					getConfig().set("Arenas."+id+".Spawn", (new RandLoc(((Player)sender).getLocation(), x,y,z)).toString());
					saveConfig();
					sender.sendMessage(TAG + ChatColor.GOLD + "You've set the spawn!");
					return false;
				}else if(args[1].equalsIgnoreCase("class")){
					if(!(sender instanceof Player)){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "Only players can set classes their inventories!");
						return false;
					}
					if(args.length!=4){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "/SSM set class <armor/inventory> <className>");
						return false;
					}
					if(getConfig().get("Classes."+args[3])==null){
						sender.sendMessage(TAG + ChatColor.DARK_RED + "Invalid class!");
						return false;
					}
					if(args[2].equalsIgnoreCase("armor")){
						Inventory i = Bukkit.createInventory(null, 9, ChatColor.GOLD + "(armor): "+ args[3]);
						for(ItemStack is : InventoryHelper.getInventory("Classes."+args[3]+".Armor", getConfig()).getContents()){
							if(is!=null)i.addItem(is);
						}
						((Player)sender).openInventory(i);
						sender.sendMessage(TAG + ChatColor.GOLD + "Put in armor for the player to wear (boots-helmet)");
						return false;
					}else if(args[2].equalsIgnoreCase("inventory")){
						Inventory i = Bukkit.createInventory(null, 4*9, ChatColor.GOLD + "(inventory): "+ args[3]);
						for(ItemStack is : InventoryHelper.getInventory("Classes."+args[3]+".Inventory", getConfig()).getContents()){
							if(is!=null)i.addItem(is);
						}
						((Player)sender).openInventory(i);
						sender.sendMessage(TAG + ChatColor.GOLD + "Put in items for the player to have");
						return false;
					}else onCommand(sender, command, label, new String[]{"set","class"});
				}
				else onCommand(sender, command, label, new String[]{"set"});
			}else if(args[0].equalsIgnoreCase("add")){
				if(!perm("SSM.Add", sender)){
					sender.sendMessage(TAG+ChatColor.DARK_RED + "Permission denied!");
					return false;
				}
				if(args.length!=3){
					sender.sendMessage(TAG + ChatColor.DARK_RED + "/SSM add <arenaName> <className>");
					return false;
				}
				String arena = args[1];
				String Class = args[2];
				ArenaManager.addToArena(arena,Class);
				sender.sendMessage(TAG + ChatColor.GOLD + "Succesfully added " + Class + " to arena " + arena + "!");
				return false;
			}else if(args[0].equalsIgnoreCase("leave")){
				if(!(sender instanceof Player)){
					sender.sendMessage(TAG + ChatColor.DARK_RED + "Only players can join and leave arenas!");
					return false;
				}
				try{
				PlayerClass.fromPlayer((Player)sender).quit();
				}catch(Exception e){
					sender.sendMessage(TAG + ChatColor.DARK_RED + "You don't have an arena to leave!");
					return false;
				}
				sender.sendMessage(TAG + ChatColor.DARK_RED + "Succesfully left the arena!");
				return false;
			}else if(args[0].equalsIgnoreCase("join")){
				if(!(sender instanceof Player)){
					sender.sendMessage(TAG + ChatColor.DARK_RED + "Only players can join and leave arenas!");
					return false;
				}
				if(args.length!=2){
					sender.sendMessage(TAG + ChatColor.DARK_RED + "/SSM join <name>");
					return false;
				}
				ArenaManager.join((Player)sender, args[1]);
				sender.sendMessage(TAG + ChatColor.GOLD + "Welcome to the waiting hub!");
			}
			else onCommand(sender, command, label, new String[0]);
		}
		return false;
	}
	
	private boolean perm(String string, CommandSender s) {
		return s.isOp()||s.hasPermission(string);
	}
	public void teleport(Player p){
		PlayerClass c = PlayerClass.fromPlayer(p);
		if(c==null)return;
		if(c.ar!=null){
			if(c.join())
			c.teleport(c.ar.Id);
		}
	}
	public Arena getArena(int Int) {
		if(!arenas.containsKey(Int))return null;
		return arenas.get(Int);
	}
	public void addArena(int i) {
		if(arenas.containsKey(i))return;
		Arena a = Arena.fromId(i, this);
		a.start();
		arenas.put(i, a);
	}
}
class PlayerClass{
	public Player pl;
	public DisguiseType disg;
	public String Class;
	public Arena ar;
	public PlayerClass(Player p, int id, DisguiseType disguise, String className, Arena arena) {
		pl=p;
		disg=disguise;
		Class=className;
		ar=arena;
		Core.instance.pcl.add(this);
	}
	public void quit() {
		mainLobby();
		if(ar!=null)
		ar.removePlayer(pl);
		Core.instance.getConfig().set(pl.getName() + ".Arena",null);
		Core.instance.saveConfig();
	}
	public void mainLobby(){
		RandLoc rl = RandLoc.fromString(Core.instance.getConfig().getString("MainLobby"),Core.instance);
		pl.teleport(rl.get());
		clear();
		Core.instance.getConfig().set(pl.getName() + ".Class",null);
		Core.instance.saveConfig();
		DisguiseHelper.undisguisePlayer(pl);
	}
	public boolean join(){
		boolean b = ar.addPlayer(pl);
		if(b)pl.teleport(ar.waitLobby.get());
		if(b)pl.setGameMode(GameMode.ADVENTURE);
		return b;
	}
	public void disguise() {
		DisguiseHelper.undisguisePlayer(pl);
		DisguiseHelper.disguisePlayer(pl, disg!=null?disg:DisguiseType.fromString(Core.instance.getConfig().getString("Classes."+Class+".DisguiseType")));
	}
	public void teleport(int arena){
		Arena a = Core.instance.arenas.get(arena);
		if(a==null)return;
		pl.teleport(a.getRandomSpawn());
		clear();
		pl.getInventory().setContents(InventoryHelper.getInventory("Classes." + Class + ".Inventory", Core.instance.getConfig()).getContents());
		int i=-1;
		for(ItemStack is : InventoryHelper.getInventory("Classes." + Class + ".Armor", Core.instance.getConfig()).getContents()){
			i++;
			if(i==0)pl.getEquipment().setBoots(is);
			else if(i==1)pl.getEquipment().setLeggings(is);
			else if(i==2)pl.getEquipment().setChestplate(is);
			else if(i==3)pl.getEquipment().setHelmet(is);
			else break;
		}
		disguise();
	}
	private void clear() {
		pl.getInventory().clear();
		pl.getEquipment().setBoots(null);
		pl.getEquipment().setHelmet(null);
		pl.getEquipment().setChestplate(null);
		pl.getEquipment().setLeggings(null);
	}
	public static PlayerClass fromPlayer(Player p) {
		if(get(p)!=null)return get(p);
		String Class = Core.instance.getConfig().getString(p.getName() + ".Class");
		if(Class==null||Class.equals(""))return null;
		String dt = Core.instance.getConfig().getString("Classes."+Class+".DisguiseType");
		if(dt==null||dt.equals(""))return null;
		DisguiseType dT = DisguiseType.fromString(dt);
		return new PlayerClass(p,dT.id,dT,Class,Core.instance.getArena(Core.instance.getConfig().getInt(p.getName() + ".Arena")));
	}
	private static PlayerClass get(Player p) {
		for(PlayerClass c : Core.instance.pcl){
			if(c.pl.getUniqueId().toString().equals(p.getUniqueId().toString())){
				c.ar = Core.instance.getArena(Core.instance.getConfig().getInt(p.getName() + ".Arena"));
				return c;
			}
		}
		return null;
	}
	public static void removeClass(String string) {
		Core.instance.getConfig().set("Classes."+string, null);
		Core.instance.saveConfig();
		ArenaManager.removeClass(string);
	}
}
class InventoryHelper{
	public static Inventory getInventory(String s, Configuration c){
		if(s==null||s.equals(""))return null;
		int length = c.getInt(s+".Length");
		Inventory inv = Bukkit.createInventory(null, length);
		for(int i=0;i<length;i++){
			ItemStack is = c.getItemStack(s+"."+i);
			if(is==null)continue;
			inv.addItem(is);
		}
		return inv;
	}
	public static void setInventory(String s, JavaPlugin jp, Inventory inv){
		if(s==null||s.equals(""))return;
		jp.getConfig().set(s+".Length", inv.getSize());
		for(int i=0;i<inv.getSize();i++){
			jp.getConfig().set(s+"."+i, inv.getContents()[i]);
		}
		jp.saveConfig();
	}
}
class RandLoc{
	public Location l;
	public double radiusX, radiusY, radiusZ;
	public RandLoc(Location l, double... radius) {
		if(radius.length<3)return;
		this.l=l;
		this.radiusX=radius[0];
		this.radiusX=radius[1];
		this.radiusX=radius[2];
	}
	public Location get(){
		return l.add(rand(radiusX), rand(radiusY), rand(radiusZ));
	}
	private double rand(double r) {
		return (r*Math.random())-(r*Math.random());
	}
	@Override
	public String toString() {
		return l.getWorld().getName()+","+l.getX()+","+l.getY()+","+l.getZ()+","+l.getPitch()+","+l.getYaw()+","+radiusX+","+radiusY+","+radiusZ;
	}
	public static RandLoc fromString(String s, JavaPlugin jp){
		if(s.split(",").length<9)return null;
		Double[] doubles = getDoubles(s.split(","),1);
		World w = jp.getServer().getWorld(s.split(",")[0]);
		if(doubles==null)return null;
		Location l = new Location(w,doubles[0],doubles[1],doubles[2],doubles[4].floatValue(),doubles[3].floatValue());
		return new RandLoc(l,doubles[5],doubles[6],doubles[7]);
	}
	private static Double[] getDoubles(String[] split, int i) {
		List<Double> dlist = new ArrayList<Double>();
		int ii=-1;
		for(String s : split){
			ii++;
			if(ii<i)continue;
			try{
			dlist.add(Double.parseDouble(s));
			}catch(Exception e){
				return null;
			}
		}
		return dlist.toArray(new Double[]{});
	}
	public static RandLoc fromLocation(Location l) {
		return new RandLoc(l,0,0,0);
	}
}
class Arena{
	public static final int MIN_VALUE = 2, MAX_VALUE = 6;
	
	public RandLoc spawn, waitLobby;
	public int Id;
	public String Name;
	List<Player> players = new ArrayList<Player>();
	int waitCounter=Integer.MIN_VALUE, countdown = Integer.MIN_VALUE;
	boolean started=false,starting=false,finished=false;
	public Arena(int id, RandLoc wait, String name, RandLoc spawn) {
		this.spawn=spawn;
		Id=id;
		this.waitLobby=wait;
		Name=name;
	}
	public void start() {
		waitCounter = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.instance, new Runnable(){
			@Override
			public void run() {
				if(!started&&players.size()!=0){
					if(!starting){
						countdown=20;
						starting=true;
					}
					for(Player p : players){
						if(countdown>0&&countdown<5)p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
						p.setLevel(countdown);
					}
					if(countdown==0){
						if(players.size()>=Arena.MIN_VALUE){
						for(Player p : players){
							p.sendMessage(Core.TAG + "The game has been started");
							PlayerClass.fromPlayer(p).teleport(Id);
						}
						started=true;
						starting=false;
						countdown=Integer.MIN_VALUE;
						}else{
							for(Player p : players)
								p.sendMessage(Core.TAG + "Not enough players");
							countdown=20;
						}
					}
					countdown--;
				}else if(started){
					if(players.size()==0){
						starting=started=finished=false;
						countdown=Integer.MIN_VALUE;
					}else{
						if(finished){
							starting=started=finished=false;
							countdown=Integer.MIN_VALUE;
							for(Player p : players){
								p.sendMessage(ChatColor.DARK_RED + "The game has ended, restarting!");
								PlayerClass.fromPlayer(p).mainLobby();
							}
						}else{
							if(players.size()==0)finished=true;
							int alive=0;
							for(Player p : players){
								if(p.getGameMode()!=GameMode.SPECTATOR)alive++;
							}
							if(alive<=1)finished=true;
							if(!finished){
								//Game loop TODO:
							}
						}
					}
				}
			}
		}, 0, 20*1);
	}
	public void removePlayer(Player pl) {
		if(players.contains(pl))players.remove(pl);
	}
	public boolean addPlayer(Player pl){
		if(!players.contains(pl)){
			if(players.size()<Arena.MAX_VALUE){
			players.add(pl);
			return true;
			}
			return false;
		}
		return false;
	}
	public static Arena fromId(int i, JavaPlugin jp){
		String name = jp.getConfig().getString("Arenas."+i+".Name");
		RandLoc wait = RandLoc.fromString(jp.getConfig().getString("Arenas."+i+".WaitLoc"),jp);
		RandLoc spawn = RandLoc.fromString(jp.getConfig().getString("Arenas."+i+".Spawn"), jp);
		return new Arena(i,wait,name,spawn);
	}
	public Location getRandomSpawn() {
		if(spawn==null)return null;
		return spawn.get();
	}
}
class ArenaManager{
	public static void instantiate() {
		List<String> arenas = Core.instance.getConfig().getStringList("ArenaList");
		if(arenas==null||arenas.isEmpty())return;
		for(String s : arenas){
			if(s==null||s.equals(""))continue;
			try{
				int i = Integer.parseInt(s);
				Core.instance.addArena(i);
			}catch(Exception e){
				continue;
			}
		}
	}

	public static void join(Player sender, String args) {
		List<String> arenas = Core.instance.getConfig().getStringList("ArenaList");
		int i = Integer.MIN_VALUE;
		for(String s : arenas){
			if(Core.instance.arenas.get(Integer.parseInt(s)).Name.equals(args)){
				i=Integer.parseInt(s);
				break;
			}
		}
		Core.instance.getConfig().set(sender.getName() + ".Arena", i);
		if(!ArenaManager.pickDefaultClass(sender,i))return;
		Core.instance.saveConfig();
		PlayerClass.fromPlayer(sender).join();
	}

	private static boolean pickDefaultClass(Player sender, int i) {
		List<String> classes = Core.instance.getConfig().getStringList("Arenas."+i+".Classes");
		if(classes==null||classes.isEmpty())
		return false;
		Core.instance.getConfig().set(sender.getName() + ".Class", classes.get(0));
		return true;
	}

	public static void addToArena(String arena, String Class) {
		int i=Integer.MIN_VALUE;
		List<String> arenas = Core.instance.getConfig().getStringList("ArenaList");
		for(String s : arenas){
			if(Core.instance.getArena(Integer.parseInt(s)).Name.equalsIgnoreCase(arena)){
				i=Integer.parseInt(s);
				break;
			}
		}
		if(i==Integer.MIN_VALUE)return;
		List<String> classes = Core.instance.getConfig().getStringList("Arenas."+i+".Classes");
		if(classes==null||classes.isEmpty()){
			classes = new ArrayList<String>();
		}
		if(!classes.contains(Class))classes.add(Class);
		Core.instance.getConfig().set("Arenas."+i+".Classes",classes);
		Core.instance.saveConfig();
	}

	public static void removeClass(String string) {
		List<String> arenas = Core.instance.getConfig().getStringList("ArenaList");
		if(arenas==null||arenas.isEmpty())return;
		for(String s : arenas){
			if(s==null||s.equals(""))continue;
			try{
				int i = Integer.parseInt(s);
				List<String> classes = Core.instance.getConfig().getStringList("Arenas."+i+".Classes");
				if(classes==null||classes.isEmpty())continue;
				if(classes.contains(string))classes.remove(string);
				Core.instance.getConfig().set("Arenas."+i+".Classes",classes);
			}catch(Exception e){
				continue;
			}
		}
		Core.instance.saveConfig();
	}

	public static void removeArena(String string) {
		Arena ar = null;
		int arId = 0;
		List<String> arenas = Core.instance.getConfig().getStringList("ArenaList");
		if(arenas==null||arenas.isEmpty())return;
		for(String s : arenas){
			if(s==null||s.equals(""))continue;
			try{
				int i = Integer.parseInt(s);
				if(Core.instance.arenas.get(i).Name.equals(string)){
					arId=i;
					ar=Core.instance.arenas.get(i);
					break;
				}
			}catch(Exception e){
				continue;
			}
		}
		if(arenas.contains(arId+""))arenas.remove(arId+"");
		Core.instance.getConfig().set("ArenaList",arenas);
		Core.instance.getConfig().set("Arenas."+arId, null);
		Core.instance.saveConfig();
		if(ar.players!=null&&!ar.players.isEmpty()){
			for(Player p : ar.players)PlayerClass.fromPlayer(p).quit();
		}
	}

	public static void register(int id, String arena, Location l) {
		List<String> arenas = Core.instance.getConfig().getStringList("ArenaList");
		arenas=arenas==null?new ArrayList<String>():arenas;
		if(arenas.isEmpty())arenas.add(id+"");
		else if(!arenas.contains(id+""))arenas.add(id+"");
		Core.instance.getConfig().set("ArenaList", arenas);
		Core.instance.getConfig().set("Arenas."+id+".Name",arena);
		Core.instance.getConfig().set("Arenas."+id+".WaitLoc",RandLoc.fromLocation(l).toString());
		Core.instance.getConfig().set("Arenas."+id+".Spawn",RandLoc.fromLocation(l).toString());
		Core.instance.saveConfig();
	}

	public static int getNext() {
		List<String> arenas = Core.instance.getConfig().getStringList("ArenaList");
		int j=1;
		if(arenas==null||arenas.isEmpty())return 1;
		for(String s : arenas){
			if(s==null||s.equals(""))continue;
			try{
				int i = Integer.parseInt(s);
				if(i>j)j=i;
			}catch(Exception e){
				continue;
			}
		}
		return j;
	}
}
class DisguiseHelper{
	@SuppressWarnings("deprecation")
	public static void disguisePlayer(Player p, DisguiseType dt){
		if(dt==null)return;
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(p.getEntityId());
		PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(dt.instantiate(p));
		for(Player pl : p.getServer().getOnlinePlayers()){
			if(p.getName().equals(pl.getName()))continue;
			((CraftPlayer)pl).getHandle().playerConnection.sendPacket(destroy);
			((CraftPlayer)pl).getHandle().playerConnection.sendPacket(spawn);
		}
	}
	@SuppressWarnings("deprecation")
	public static void undisguisePlayer(Player pl) {
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(pl.getEntityId());
		PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)pl).getHandle());
		for(Player p : pl.getServer().getOnlinePlayers()){
			if(p.getName().equals(pl.getName()))continue;
			((CraftPlayer)p).getHandle().playerConnection.sendPacket(destroy);
			((CraftPlayer)p).getHandle().playerConnection.sendPacket(spawn);
		}
	}
}
class DisguiseType{
	public byte id,type;
	public DisguiseType(byte entityId, byte type) {
		this.id=entityId;
		this.type=type;
	}
	public EntityLiving instantiate(Player p) {
		if(id==50)return set(new EntityCreeper(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==51)return set(new EntitySkeleton(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==52)return set(new EntitySpider(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==53)return set(new EntityGiantZombie(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==54)return set(new EntityZombie(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==55)return set(new EntitySlime(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==56)return set(new EntityGhast(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==57)return set(new EntityPigZombie(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==58)return set(new EntityEnderman(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==59)return set(new EntityCaveSpider(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==60)return set(new EntitySilverfish(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==61)return set(new EntityBlaze(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==62)return set(new EntityMagmaCube(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==63)return set(new EntityEnderDragon(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==64)return set(new EntityWither(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==66)return set(new EntityWitch(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==67)return set(new EntityEndermite(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==68)return set(new EntityGuardian(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==120)return set(new EntityVillager(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==65)return set(new EntityBat(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==90)return set(new EntityPig(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==91)return set(new EntitySheep(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==92)return set(new EntityCow(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==93)return set(new EntityChicken(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==94)return set(new EntitySquid(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==95)return set(new EntityWolf(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==96)return set(new EntityMushroomCow(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==97)return set(new EntitySnowman(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==98)return set(new EntityOcelot(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==99)return set(new EntityIronGolem(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==100)return set(new EntityHorse(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		if(id==101)return set(new EntityRabbit(((CraftWorld)p.getWorld()).getHandle()),p.getLocation(),p);
		return null;
	}
	private EntityLiving set(EntityLiving e, Location l, Player p) {
		if(e instanceof EntitySkeleton)((EntitySkeleton)e).setSkeletonType(type);
		if(e instanceof EntityZombie)((EntityZombie)e).setVillager(type==1);
		e.locX=l.getX();
		e.locY=l.getY();
		e.locZ=l.getZ();
		int id = ((CraftPlayer)p).getHandle().getId();
		try{
		Field f = Entity.class.getDeclaredField("id");
		f.setAccessible(true);
		f.set(e, id);
		return e;
		}catch(Exception ex){
			return null;
		}
	}
	public static List<DisguiseType> values() {
		List<DisguiseType> dt = new ArrayList<DisguiseType>();
		boolean doubleCountSkel = false;
		boolean doubleCountZom = false;
		for(byte i : new Byte[]{50,51,51,52,53,54,55,56,57,58,59,60,61,62,63,64,66,67,68,120,65,91,92,93,94,95,96,97,98,99,100,101}){
			dt.add(new DisguiseType(i,(byte) (doubleCountSkel&&i==51?1:doubleCountZom&&i==54?1:0)));
			if(i==51)doubleCountSkel=true;
			if(i==54)doubleCountZom=true;
		}
		return dt;
	}
	@Override
	public String toString() {
		return "id&"+id+"*type&"+type;
	}
	public static final DisguiseType fromString(String s) {
		return new DisguiseType(Byte.parseByte(s.split("\\*")[0].split("\\&")[1]),Byte.parseByte(s.split("\\*")[1].split("\\&")[1]));
	}
}