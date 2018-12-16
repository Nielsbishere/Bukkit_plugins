package net.nielsbwashere.src.CraftableSpawners;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
@SuppressWarnings("deprecation")
public class Core extends JavaPlugin implements Listener{
@Override
public void onEnable() {
	Bukkit.getPluginManager().registerEvents(this, this);
	ShapedRecipe sr = new ShapedRecipe(new ItemStack(Material.MOB_SPAWNER,1)).shape("aaa","aea","aaa").setIngredient('a', Material.IRON_FENCE).setIngredient('e', Material.DIAMOND_BLOCK);
	Bukkit.addRecipe(sr);
	registerEntityEggs();
	ItemStack is = new ItemStack(Material.EGG,1);
	ItemMeta im = is.getItemMeta();
	im.setDisplayName(ChatColor.RESET + "Capture egg");
	is.setItemMeta(im);
	sr = new ShapedRecipe(is).shape("oao","aea","oao").setIngredient('o', Material.REDSTONE).setIngredient('a', Material.DIAMOND).setIngredient('e', Material.EGG);
	Bukkit.addRecipe(sr);
	is = new ItemStack(Material.GOLD_PICKAXE,1);
	im = is.getItemMeta();
	im.setDisplayName(ChatColor.RESET + "Spawner miner");
	is.setItemMeta(im);
	ShapelessRecipe sl = new ShapelessRecipe(is).addIngredient(Material.GOLD_PICKAXE).addIngredient(Material.REDSTONE_BLOCK).addIngredient(Material.GOLD_BLOCK).addIngredient(4,Material.DIAMOND);
	Bukkit.addRecipe(sl);
}
HashMap<Integer,Integer> eggs = new HashMap<Integer,Integer>();
@EventHandler
public void hit(ProjectileHitEvent e){
	if(!e.getEntity().hasMetadata("Capture"))return;
	final Location l = e.getEntity().getLocation();
	final int id = e.getEntity().getEntityId();
	eggs.put(id,Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
		@Override
		public void run() {
			ItemStack is = new ItemStack(Material.EGG,1);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.RESET + "Capture egg");
			is.setItemMeta(im);
			l.getWorld().dropItem(l, is);
			eggs.remove(id);
		}}, 2));
}
@EventHandler
public void mine(BlockBreakEvent e){
	if(e.getPlayer()==null||e.getPlayer().getItemInHand()==null||e.getPlayer().getItemInHand().getType()!=Material.GOLD_PICKAXE)return;
	ItemStack is = e.getPlayer().getItemInHand();
	if(!is.hasItemMeta()||!is.getItemMeta().hasDisplayName()||!is.getItemMeta().getDisplayName().equals(ChatColor.RESET + "Spawner miner"))return;
	if(e.getBlock()==null)return;
	if(e.getBlock().getType()!=Material.MOB_SPAWNER)e.setCancelled(true);
	else{
		short typeid = ((CreatureSpawner)e.getBlock().getState()).getCreatureType().getTypeId();
		e.getBlock().setType(Material.AIR);
		e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), spawner(typeid));
	}
}
private void registerEntityEggs() {
	for(int i=50;i<102;i++)if(!((i<90&&i>68)||i==63||i==64))registerEgg(i);
}
private void registerEgg(int i) {
	ShapelessRecipe sl = new ShapelessRecipe(spawner(i)).addIngredient(Material.MONSTER_EGG,i).addIngredient(Material.MOB_SPAWNER);
	Bukkit.addRecipe(sl);
}
private ItemStack spawner(int i) {
	ItemStack is = new ItemStack(Material.MOB_SPAWNER,1);
	ItemMeta im = is.getItemMeta();
	im.setLore(Arrays.asList(new String[]{"Id:"+i}));
	is.setItemMeta(im);
	return is;
}
@EventHandler
public void placeSpawner(BlockPlaceEvent e){
	int idd=-1;
	if(e.getItemInHand()==null||e.getItemInHand().getType()!=Material.MOB_SPAWNER||!e.getItemInHand().hasItemMeta()||!e.getItemInHand().getItemMeta().hasLore()||(idd=id(e.getItemInHand().getItemMeta().getLore()))<=0)return;
	CreatureSpawner s = ((CreatureSpawner)e.getBlock().getState());
	if(idd<=0)return;
	s.setCreatureTypeByName(CreatureType.fromId(idd).getName());
}
private int id(List<String> lore) {
	for(String s : lore){
		if(s.startsWith("Id:"))return Integer.parseInt(s.replace("Id:", ""));
	}
	return -1;
}
@EventHandler
public void capture(final EntityDamageByEntityEvent e){
	if(!(e.getDamager()instanceof Egg))return;
	if(!(e.getDamager().hasMetadata("Capture")))return;
	if(e.getEntity().getType()==EntityType.ENDER_DRAGON||e.getEntity().getType()==EntityType.WITHER)return;
	if(((Egg)e.getDamager()).getShooter()==null||!(((Egg)e.getDamager()).getShooter()instanceof Player))return;
	Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
		@Override
		public void run() {
			Bukkit.getScheduler().cancelTask(eggs.get(e.getDamager().getEntityId()));
			eggs.remove(e.getDamager().getEntityId());
			add(Material.MONSTER_EGG,(short)e.getEntity().getType().getTypeId(),(Player)(((Egg)(e.getDamager())).getShooter()));
			e.getEntity().remove();
		}}, 1);
}
@EventHandler
public void turn(ProjectileLaunchEvent e){
	if(!(e.getEntity()instanceof Egg))return;
	if(e.getEntity().getShooter()==null||!(e.getEntity().getShooter() instanceof Player))return;
	Player p = (Player)e.getEntity().getShooter();
	if(p.getItemInHand()==null||!p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RESET+"Capture egg")||p.getItemInHand().getType()!=Material.EGG)return;
	decr(ChatColor.RESET+"Capture egg",Material.EGG,p.getInventory());
	e.getEntity().setMetadata("Capture", new FixedMetadataValue(this, true));
}
private void add(Material monsterEgg, short typeId, Player player) {
	player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(monsterEgg,1,typeId));
}
private void decr(String string, Material m, PlayerInventory inventory) {
	for(int i=0;i<inventory.getSize();i++){
		ItemStack is = inventory.getContents()[i];
		if(is!=null&&is.getType()==m&&is.getItemMeta().hasDisplayName()&&is.getItemMeta().equals(string))dec(i,inventory);
	}
}
private void dec(int i, PlayerInventory inventory) {
	if(inventory.getContents()[i].getAmount()<=1)inventory.setItem(i, null);
	else inventory.getContents()[i].setAmount(inventory.getContents()[i].getAmount()-1);
}
}
