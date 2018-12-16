package net.nielsbwashere.src.PvPDisconnect;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
public class Core extends JavaPlugin implements Listener{
	String version = "v1.6.4.1.0";
	//TODO: Use worldguard to check if you are in a pvp area before making you go into pvp mode.
	ConcurrentHashMap<String,Integer> PvP = new ConcurrentHashMap<String,Integer>();
	ConcurrentHashMap<Zombie,Inventory> Zombies = new ConcurrentHashMap<Zombie,Inventory>();
	WorldGuardPlugin wgp;
@Override
public void onEnable() {
	final Core c = this;
	Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
		@Override
		public void run() {
			Bukkit.getPluginManager().registerEvents(c, c);
			if(getConfig().getString("Settings.Version")==null||!getConfig().getString("Settings.Version").equals(version)){
				getConfig().set("Settings.Version",version);
				getConfig().set("Settings.PvPInterval",5);
				saveConfig();
			}
			if(Bukkit.getPluginManager().getPlugin("WorldGuard")!=null&&Bukkit.getPluginManager().isPluginEnabled("WorldGuard")){
				wgp = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
			}else{
				Bukkit.getPluginManager().disablePlugin(c);
				return;
			}
		}}, 2);
}
@Override
public void onDisable() {
	saveConfig();
	deleteBodies();
	Bukkit.getScheduler().cancelTasks(this);
}
@EventHandler(priority=EventPriority.LOWEST)
public void onPvp(EntityDamageByEntityEvent e){
	if(!(e.getDamager() instanceof Player)||!(e.getEntity() instanceof Player)||e.isCancelled()||regionFlagPvp(false,e.getDamager())||regionFlagPvp(false,e.getEntity()))return;
	if(!PvP.containsKey(((Player)e.getDamager()).getName()))((Player)e.getDamager()).sendMessage(ChatColor.BLUE + "[" + ChatColor.RESET + "PvPLog" + ChatColor.BLUE + "]" + "You hit " + ((Player)e.getEntity()).getName() + ", And you are now in PvP mode! Wait " + getConfig().getInt("Settings.PvPInterval") + " seconds before login out!");
	if(!PvP.containsKey(((Player)e.getEntity()).getName()))((Player)e.getEntity()).sendMessage(ChatColor.BLUE + "[" + ChatColor.RESET + "PvPLog" + ChatColor.BLUE + "]" + "You were hit by " + ((Player)e.getDamager()).getName() + ", And you are now in PvP mode! Wait " + getConfig().getInt("Settings.PvPInterval") + " seconds before login out!");
	addPvP(((Player)e.getDamager()).getName());
	addPvP(((Player)e.getEntity()).getName());
}
private boolean regionFlagPvp(boolean b, Entity p) {
	if(!(p instanceof Player))return false;
	Player pl = (Player)p;
	Map<String,ProtectedRegion> rm = wgp.getGlobalRegionManager().get(pl.getWorld()).getRegions();
	for(ProtectedRegion pr : rm.values()){
		if(pr.contains(pl.getLocation().getBlockX(),pl.getLocation().getBlockY(),pl.getLocation().getBlockZ())){
			for(Entry<Flag<?>,Object> mf : pr.getFlags().entrySet()){
				if(mf.getKey().getName().equalsIgnoreCase("pvp")&&((boolean)mf.getValue()))return true;
			}
		}
	}
	return false;
}
@EventHandler(priority=EventPriority.LOWEST)
public void onLogout(PlayerQuitEvent e){
	if(!PvP.containsKey(e.getPlayer().getName()))return;
	spawnBody(e.getPlayer().getInventory());
	e.getPlayer().getInventory().clear();
	e.getPlayer().getInventory().setArmorContents(null);
	removePvp(e.getPlayer().getName());
	getServer().broadcastMessage(ChatColor.BLUE + "[" + ChatColor.RESET + "PvPLog" + ChatColor.BLUE + "]"  + e.getPlayer().getName() + " has logged out whilst fighting and left behind his body!");
}
@EventHandler(priority=EventPriority.LOWEST)
public void onLogin(PlayerLoginEvent e){
	Zombie z=null;
	if((z=hasBody(e.getPlayer()))==null)return;
	returnGoods(e.getPlayer(),z);
	getServer().broadcastMessage(ChatColor.BLUE + "[" + ChatColor.RESET + "PvPLog" + ChatColor.BLUE + "]"  + e.getPlayer().getName() + " has logged back in and got back his body!");
}
@EventHandler(priority=EventPriority.LOWEST)
public void onKill(EntityDeathEvent e){
	if(!(e.getEntity() instanceof Zombie))return;
	Zombie z = ((Zombie)e.getEntity());
	if(!z.getCustomName().startsWith(ChatColor.DARK_RED + ""))return;
	if(!Zombies.containsKey(z))return;
	if(z.getEquipment().getArmorContents()!=null&&z.getEquipment().getArmorContents().length>0){
		for(ItemStack is : z.getEquipment().getArmorContents()){
			if(is==null||is.getType()==Material.AIR)continue;
			z.getWorld().dropItemNaturally(z.getLocation(), is);
		}
	}
	if(z.getEquipment().getItemInHand()!=null&&z.getEquipment().getItemInHand().getType()!=Material.AIR){
		z.getWorld().dropItemNaturally(z.getLocation(), z.getEquipment().getItemInHand());
	}
	if(e.getEntity().getKiller()==null)
	getServer().broadcastMessage(ChatColor.BLUE + "[" + ChatColor.RESET + "PvPLog" + ChatColor.BLUE + "]"  + e.getEntity().getCustomName().replaceFirst(ChatColor.DARK_RED+"", "") + "'s body was killed by something because he went offline while fighting");
	else getServer().broadcastMessage(ChatColor.BLUE + "[" + ChatColor.RESET + "PvPLog" + ChatColor.BLUE + "]"  + e.getEntity().getCustomName().replaceFirst(ChatColor.DARK_RED+"", "") + "'s body was slain by " + e.getEntity().getKiller().getName());
	dropItems(z);
	z.getEquipment().clear();
	z.remove();
}
private void returnGoods(Player player, Zombie z) {
	if(z.getEquipment().getArmorContents()!=null&&z.getEquipment().getArmorContents().length>0){
		for(ItemStack is : z.getEquipment().getArmorContents()){
		if(is==null||is.getType()==Material.AIR)continue;
		z.getWorld().dropItemNaturally(z.getLocation(), is);
		}
	}
	if(z.getEquipment().getItemInHand()!=null&&z.getEquipment().getItemInHand().getType()!=Material.AIR){
		player.getInventory().setItemInHand(z.getEquipment().getItemInHand());
	}
	dropItems(z);
	removePvp(player.getName());
}
private void dropItems(Zombie z) {
	for(ItemStack i : getZombieInv(z).getContents()){
		if(i==null||i.getType()==Material.AIR)continue;
		z.getWorld().dropItemNaturally(z.getLocation(), i);
	}
	z.getEquipment().clear();
	z.remove();
	Zombies.remove(z);
}
private Inventory getZombieInv(Zombie z) {
	for(Entry<Zombie,Inventory> zpi : Zombies.entrySet()){
		if(!zpi.getKey().getUniqueId().toString().equals(z.getUniqueId().toString()))continue;
		return zpi.getValue();
	}
	return null;
}
private Zombie hasBody(Player player) {
	Zombie z = null;
	for(Zombie zom : Zombies.keySet()){
		if(!zom.getCustomName().equals(ChatColor.DARK_RED + player.getName()))continue;
		if(zom.isDead()){
			Zombies.remove(zom);
			return null;
		}
		z=zom;
		break;
	}
	return z;
}
private void spawnBody(PlayerInventory inventory) {
	Zombie z = inventory.getHolder().getWorld().spawn(inventory.getHolder().getLocation(), Zombie.class);
	z.setCustomName(ChatColor.DARK_RED + inventory.getHolder().getName());
	z.setCustomNameVisible(true);
	z.setCanPickupItems(false);
	z.setRemoveWhenFarAway(false);
	z.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Byte.MAX_VALUE, false));
	equip(z,inventory);
	Zombies.put(z,copy(inventory));
}
private Inventory copy(PlayerInventory inventory) {
	Inventory i = Bukkit.createInventory(inventory.getHolder(), 36);
	for(ItemStack is : inventory){
		if(is!=null&&is.getType()!=Material.AIR)i.addItem(is);
	}
	return i;
}
@SuppressWarnings("deprecation")
private void equip(Zombie z, PlayerInventory inventory) {
	if(inventory.getArmorContents()!=null&&inventory.getArmorContents().length>0){
	z.getEquipment().setArmorContents(inventory.getArmorContents());
	z.getEquipment().setChestplateDropChance(0f);
	z.getEquipment().setBootsDropChance(0f);
	z.getEquipment().setHelmetDropChance(0f);
	z.getEquipment().setLeggingsDropChance(0f);
	inventory.setArmorContents(new ItemStack[]{});
	}
	if(inventory.getHolder().getItemInHand()!=null&&inventory.getHolder().getItemInHand().getType()!=Material.AIR){
		z.getEquipment().setItemInHand(inventory.getHolder().getItemInHand());
		z.getEquipment().setItemInHandDropChance(0f);
		inventory.remove(inventory.getHeldItemSlot());
	}
}
private void deleteBodies() {
	for(Entry<Zombie, Inventory> zpi : Zombies.entrySet()){
		if(zpi.getKey().isDead())continue;
		zpi.getKey().setHealth(0d);
		for(ItemStack i : zpi.getValue().getContents()){
			if(i==null||i.getType()==Material.AIR)continue;
			zpi.getKey().getWorld().dropItemNaturally(zpi.getKey().getLocation(), i);
		}
	}
}
private int addPvP(final String who){
	removePvp(who);
	int i = Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
		@Override
		public void run() {
			removePvp(who);
			Player p = getServer().getPlayer(who);
			if(p!=null)p.sendMessage(ChatColor.BLUE + "[" + ChatColor.RESET + "PvPLog" + ChatColor.BLUE + "]" + "You are safe to log out again!");
		}}, 20*getConfig().getInt("Settings.PvPInterval"));
	PvP.put(who, i);
	return i;
}
private void removePvp(String who) {
	if(PvP.containsKey(who)){
		Bukkit.getScheduler().cancelTask(PvP.get(who));
		PvP.remove(who);
	}
}
}
