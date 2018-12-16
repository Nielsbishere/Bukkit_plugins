package net.osomi.src.HungerGames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin implements Listener{
	public static final String prefix = ChatColor.AQUA + "[" + ChatColor.RESET + "Hunger games" + ChatColor.AQUA + "]" + ChatColor.DARK_RED;
	@Override
	public void onEnable() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run() {
				if(getConfig().get("ChestInventories")!=null){
					for(int i=0;i<getConfig().getConfigurationSection("ChestInventories").getKeys(false).size();i++){
						Location[] ll = LocationHelper.getLocations(Core.getPlugin(Core.class), "ChestInventories." + i + ".Location", getConfig());
						if(ll==null||ll.length==0)continue;
						for(Location l : ll){
							if(l==null)continue;
							Inventory inv = InventoryHelper.getInventory(Core.getPlugin(Core.class), "ChestInventories." + i, getConfig());
							if(l.getBlock().getType()!=Material.CHEST)continue;
							Chest c = (Chest)l.getBlock().getState();
							c.getInventory().setContents(inv.getContents());
						}
					}
				}
			}}, 0, 20*60*10);
		Bukkit.getPluginManager().registerEvents(this, this);
		Lobby.init();
	}
	@Override
	public void onDisable() {
		Lobby.stop();
	}
	@EventHandler
	public void onLogout(PlayerQuitEvent e){
		Lobby.checkout(e.getPlayer());
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if(Lobby.check(e.getPlayer())){
			if(e.getBlock().getType()==Material.LEAVES||e.getBlock().getType()==Material.LEAVES_2){
				Lobby l = Lobby.get(e.getPlayer());
				if(l.isStarted){
					l.brokenLeaves.add(e.getBlock().getState());
					e.setCancelled(true);
					e.getBlock().setType(Material.AIR);
				}
				else e.setCancelled(true);
			}else e.setCancelled(true);
		}
	}
}

class Lobby{
	public static final List<Lobby> lobbyList = new ArrayList<Lobby>();

	public static boolean check(Player p){
		return get(p)!=null;
	}
	
	public static Lobby get(Player p) {
		for(Lobby l : lobbyList)if(l.playerList.contains(p))return l;
		return null;
	}

	public static void checkout(Player p){
		get(p).quit(p);
	}
	
	public static void stop() {
		for(Lobby l : lobbyList)l.kickPlayers();
	}
	
	public static void init(){
		//TODO: Initializing the lobbyList
	}
	
	public static void enter(Player p){
		for(Lobby l : lobbyList)if(l.onEnter(p))return;
		p.sendMessage(Core.prefix + "You can't join any arena's! They are all filled up!");
	}

	List<Player> playerList = new ArrayList<Player>();
	protected List<BlockState> brokenLeaves = new ArrayList<BlockState>();
	
	protected boolean isStarted = false;
	int minPlayers = 12;
	int maxPlayers = 24;
	final int sCountDown = 30*20;
	int countDown = sCountDown;
	
	Location lobbySpawn = Bukkit.getWorlds().get(0).getSpawnLocation(); 	//TODO: Spawn location
	
	public boolean onEnter(Player p){
		if(isStarted||playerList.size()>=maxPlayers)return false;
		playerList.add(p);
		p.getInventory().clear();
		p.teleport(lobbySpawn);
		return true;
	}
	public void quit(Player p){
		kickPlayer(p, true);
	}
	
	public void kickPlayers() {
		for(Player p : playerList)kickPlayer(p, false);
		playerList.clear();
	}
	private void kickPlayer(Player p, boolean clear) {
		p.getInventory().clear();
		p.teleport(p.getWorld().getSpawnLocation());
		p.sendMessage(Core.prefix + "You have been kicked out of this Lobby, because the server was either reloading or you logged out!");
		if(clear){
			playerList.remove(p);
			if(isStarted){
				if(playerList.size()==0)restart();
			}else{
				if(playerList.size()==0)restart();
				else if(playerList.size()<minPlayers){
					countDown = sCountDown;
					for(Player pl : playerList)pl.sendMessage(Core.prefix + "The timer has been reset, because there aren't enough players!");
				}
			}
		}
	}
	private void restart(){
		countDown = sCountDown;
		isStarted = false;
		for(BlockState bs : brokenLeaves)bs.update();
	}
}

class InventoryHelper{
    public static Inventory getInventory(JavaPlugin plugin, String s, Configuration c){
        if(s==null||s.equals("")||c.get(s) == null)return null;
        ConfigurationSection cf = c.getConfigurationSection(s);
        int length= cf.getKeys(false).size();
        Inventory inv = Bukkit.createInventory(null, length);
        int j = 0;
        for(int i=0;i<length;i++){
            double chance = c.getDouble(s + "." + i + ".Chance");
            if(chance>1||chance<0)continue;
            if(chance>=Math.random())continue;
            ItemStack is = c.getItemStack(s+"." + i + ".ItemStack");
            if(is==null)continue;
            inv.setItem(getRandomAvailable(inv), is);
            j++;
            if(!getAvailable(inv)||j>=length/3.0)break;
        }
        return inv;
    }
	private static int[] getAvailableSlots(Inventory inv) {
		List<Integer> iList = new ArrayList<Integer>();
		for(int i=0;i<inv.getSize();i++)if(inv.getItem(i)==null||inv.getItem(i).getType()==Material.AIR)iList.add(i);
		int[] li = new int[iList.size()];
		for(int i=0;i<li.length;i++)li[i] = iList.get(i);
		return li;
	}
	private static int getRandomAvailable(Inventory inv) {
		int[] availableSlots = getAvailableSlots(inv);
		return availableSlots[(int) (availableSlots.length*Math.random())];
	}
	private static boolean getAvailable(Inventory inv) {
		return getAvailableSlots(inv).length > 0;
	}
	public static void setInventory(JavaPlugin plugin, String s, Configuration fc, Inventory inv){
        if(s==null||s.equals(""))return;
        for(int i=0;i<inv.getSize();i++){
            fc.set(s+"."+i+".ItemStack", inv.getContents()[i]);
            fc.set(s + "." + i + ".Chance", 1.0);
        }
        plugin.saveConfig();
    }
}

class LocationHelper{

	public static Location getLocation(JavaPlugin jp, String s, Configuration config) {
		String sworld = config.getString(s + ".World");
		if(sworld==null||sworld.equals("")||jp.getServer().getWorld(sworld)==null)return null;
		double x = config.getDouble(s + ".x");
		double y = config.getDouble(s + ".y");
		double z = config.getDouble(s + ".z");
		float yaw = (float) config.getDouble(s + ".yaw");
		float pitch = (float) config.getDouble(s + ".pitch");
		return new Location(jp.getServer().getWorld(sworld), x, y, z, yaw, pitch);
	}
	public static Location[] getLocations(JavaPlugin jp, String s, Configuration config){
		Location[] l = new Location[config.getConfigurationSection(s).getKeys(false).size()];
		for(int i=0;i<l.length;i++)l[i] = getLocation(jp, s + "." + i, config);
		return l;
	}
}