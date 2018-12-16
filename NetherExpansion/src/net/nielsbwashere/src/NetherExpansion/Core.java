package net.nielsbwashere.src.NetherExpansion;
import java.util.Arrays;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
public class Core extends JavaPlugin implements Listener{
@Override
public void onEnable() {
	Bukkit.getPluginManager().registerEvents(this, this);
	FurnaceRecipe fr = new FurnaceRecipe(name(Material.IRON_INGOT,1,0,ChatColor.RESET + "Nether ingot", null), (name(Material.QUARTZ_ORE,1,0,ChatColor.RESET+"Nether ore",null)).getData());
	Bukkit.addRecipe(fr);
	ShapedRecipe sr = new ShapedRecipe(name(Material.STICK,1,0,ChatColor.RESET+"Nether stick",null)).shape("a","a").setIngredient('a', Material.NETHERRACK);
	Bukkit.addRecipe(sr);
	sr = new ShapedRecipe(name(Material.WOOD_PICKAXE,1,0,ChatColor.RESET + "Nether Quartz Pickaxe",null)).shape("aaa"," b "," b ").setIngredient('a', Material.QUARTZ_BLOCK).setIngredient('b', (name(Material.STICK,1,0,ChatColor.RESET+"Nether stick",null).getData()));
	Bukkit.addRecipe(sr);
	sr = new ShapedRecipe(name(Material.STONE_PICKAXE,1,0,ChatColor.RESET + "Nether Crystal Pickaxe",null)).shape("aaa"," b "," b ").setIngredient('a', (name(Material.QUARTZ,1,0,ChatColor.RESET + "Nether crystal",null)).getData()).setIngredient('b', (name(Material.STICK,1,0,ChatColor.RESET+"Nether stick",null).getData()));
	Bukkit.addRecipe(sr);
	sr = new ShapedRecipe(name(Material.IRON_PICKAXE,1,0,ChatColor.RESET + "Nether Gem Pickaxe",null)).shape("aaa"," b "," b ").setIngredient('a', (name(Material.QUARTZ,1,0,ChatColor.RESET + "Nether gem",null)).getData()).setIngredient('b', (name(Material.STICK,1,0,ChatColor.RESET+"Nether stick",null).getData()));
	Bukkit.addRecipe(sr);
	sr = new ShapedRecipe(name(Material.DIAMOND_PICKAXE,1,0,ChatColor.RESET + "Nether Pickaxe",null)).shape("aaa"," b "," b ").setIngredient('a', (name(Material.IRON_INGOT,1,0,ChatColor.RESET + "Nether ingot",null)).getData()).setIngredient('b', (name(Material.STICK,1,0,ChatColor.RESET+"Nether stick",null).getData()));
	Bukkit.addRecipe(sr);
	sr = new ShapedRecipe(name(Material.BOW,1,0,ChatColor.RESET+"Wither Bow",null)).shape(" ab","acb", " ab").setIngredient('a', (name(Material.STICK,1,0,ChatColor.RESET+"Nether stick",null).getData())).setIngredient('b', Material.STRING).setIngredient('c', (name(Material.SKULL,1,1,null,null)).getData());
	Bukkit.addRecipe(sr);
	sr = new ShapedRecipe(name(Material.BOW,1,0,ChatColor.RESET+"Ghast Bow",null)).shape("dab","acb", "dab").setIngredient('a', (name(Material.STICK,1,0,ChatColor.RESET+"Nether stick",null).getData())).setIngredient('b', Material.STRING).setIngredient('c', Material.GOLD_BLOCK).setIngredient('d', Material.GHAST_TEAR);
	Bukkit.addRecipe(sr);
	addArmor(name(Material.QUARTZ_BLOCK,1,0,null,null),"Quartz");
	addArmor((name(Material.QUARTZ,1,0,ChatColor.RESET + "Nether crystal",null)), "Nether Crystal");
	addArmor((name(Material.QUARTZ,1,0,ChatColor.RESET + "Nether gem",null)), "Nether Gem");
	addArmor((name(Material.IRON_INGOT,1,0,ChatColor.RESET + "Nether ingot",null)),"Nether Ingot");
}
private void addArmor(ItemStack itemStack, String armorStart) {
	ShapedRecipe sr = new ShapedRecipe(name(Material.DIAMOND_CHESTPLATE,1,0,ChatColor.RESET + armorStart + " Chestplate",null)).shape("A A","AAA","AAA").setIngredient('A', itemStack.getData());Bukkit.addRecipe(sr);
	sr = new ShapedRecipe(name(Material.DIAMOND_LEGGINGS,1,0,ChatColor.RESET + armorStart + " Leggings",null)).shape("AAA","A A","A A").setIngredient('A', itemStack.getData());Bukkit.addRecipe(sr);
	sr = new ShapedRecipe(name(Material.DIAMOND_BOOTS,1,0,ChatColor.RESET + armorStart + " Boots",null)).shape("A A","A A").setIngredient('A', itemStack.getData());Bukkit.addRecipe(sr);
	sr = new ShapedRecipe(name(Material.DIAMOND_HELMET,1,0,ChatColor.RESET + armorStart + " Helmet",null)).shape("A A","A A").setIngredient('A', itemStack.getData());Bukkit.addRecipe(sr);
}
@EventHandler
public void onShoot(EntityShootBowEvent e){
	if (e.getEntity() instanceof Player && hasName(((Player)e.getEntity()).getItemInHand())) {
		String s = ((Player)e.getEntity()).getItemInHand().getItemMeta().getDisplayName();
		if(s.equals(ChatColor.RESET + "Wither Bow")){
		Player p = (Player) e.getEntity();
		e.setCancelled(true);
		p.launchProjectile(WitherSkull.class).setVelocity(e.getProjectile().getVelocity());
		}else if(s.equals(ChatColor.RESET + "Ghast Bow")){
			Player p = (Player) e.getEntity();
			e.setCancelled(true);
			p.launchProjectile(Fireball.class).setVelocity(e.getProjectile().getVelocity());
		}
	}
}
private ItemStack name(Material m, int i, int j, String disp, String[] lore, Ench... enchantlist) {
	ItemStack is = new ItemStack(m,i,(short)j);
	if(enchantlist!=null)for(Ench e : enchantlist)is.addUnsafeEnchantment(e.e, e.level);
	if(disp==null&&lore==null)return is;
	ItemMeta im = is.getItemMeta();
	if(disp!=null)im.setDisplayName(disp);
	if(lore!=null)im.setLore(Arrays.asList(lore));
	is.setItemMeta(im);
	return is;
}
@EventHandler
public void onBlockBreak(BlockBreakEvent e){
	if(e.isCancelled())return;
	if(e.getBlock().getType()==Material.QUARTZ_ORE){
		if(Math.random()<=0.1/100){
			e.getBlock().setType(Material.AIR);
			add(e.getPlayer(),name(Material.QUARTZ_ORE,1,0,ChatColor.RESET + "Nether ore",null));
			return;
		}
		if(Math.random()<=1.0/100){
			e.getBlock().setType(Material.AIR);
			add(e.getPlayer(),name(Material.QUARTZ,1,0,ChatColor.RESET + "Nether gem",null));
			return;
		}
		if(Math.random()<=10.0/100){
			e.getBlock().setType(Material.AIR);
			add(e.getPlayer(),name(Material.QUARTZ,1,0,ChatColor.RESET + "Nether crystal",null));
			return;
		}
		return;
	}
	if(!hasName(e.getPlayer().getItemInHand()))return;
	String s = e.getPlayer().getItemInHand().getItemMeta().getDisplayName();
	if(s.equals(ChatColor.RESET+"Nether Quartz Pickaxe"))smeltBlock(e.getBlock(), e.getPlayer().getItemInHand());
	else if(s.equals(ChatColor.RESET + "Nether Crystal Pickaxe")){
		for(int i=-1;i<2;i++){
			for(int j=-1;j<2;j++){
				for(int k=-1;k<2;k++){
					smeltBlock(e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(i,j,k)), e.getPlayer().getItemInHand());
				}
			}
		}
	}
	else if(s.equals(ChatColor.RESET + "Nether Gem Pickaxe")||s.equals(ChatColor.RESET + "Nether Pickaxe")){
		for(int i=-2;i<3;i++){
			for(int j=-2;j<3;j++){
				for(int k=-2;k<3;k++){
					smeltBlock(e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().add(i,j,k)), e.getPlayer().getItemInHand());
				}
			}
		}
		if(s.equals(ChatColor.RESET + "Nether Pickaxe"))e.getPlayer().getItemInHand().setDurability((short)0);
	}
}
private void add(Player whoClicked, ItemStack itemStack) {
	if(!canHold(whoClicked,itemStack))whoClicked.getWorld().dropItem(whoClicked.getLocation(), itemStack);
	else whoClicked.getInventory().addItem(itemStack);
}
private boolean canHold(Player whoClicked, ItemStack itemStack) {
	Inventory i = whoClicked.getInventory();
	for(ItemStack is : i.getContents())if(is==null||is.getType()==Material.AIR||(is.isSimilar(itemStack)&&is.getAmount()+itemStack.getAmount()<=itemStack.getMaxStackSize()))return true;
	if(getTotalToStack(itemStack,i)>=itemStack.getAmount())return true;
	return false;
}
private int getTotalToStack(ItemStack itemStack, Inventory i) {
	int j=0;
	for(ItemStack is : i.getContents())if(is.isSimilar(itemStack))j+=is.getAmount();
	return j;
}
public void smeltBlock(Block b, ItemStack is ){
	FurnaceRecipe fr;
	if((fr=getSmelt(b))!=null){
		b.setType(Material.AIR);
		int fortune = getFortune(is);
		ItemStack clone = fr.getResult().clone();
		clone.setAmount(clone.getAmount()*fortune);
		b.getWorld().dropItem(b.getLocation(), clone);
	}else b.breakNaturally();
}

private int getFortune(ItemStack is) {
	return is!=null&&is.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)?is.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS):0;
}

private FurnaceRecipe getSmelt(Block b) {
	for(Iterator<Recipe> ir = Bukkit.recipeIterator();ir.hasNext();){
		Recipe r = ir.next();
		if(!(r instanceof FurnaceRecipe))continue;
		FurnaceRecipe fr = (FurnaceRecipe)r;
		ItemStack input = fr.getInput();
		if(input.getType()==b.getType()&&!hasName(input)&&!hasLore(input))return fr;
	}
	return null;
}

private boolean hasName(ItemStack input) {
	return input!=null&&input.hasItemMeta()&&input.getItemMeta().hasDisplayName();
}
private boolean hasLore(ItemStack input) {
	return input!=null&&input.hasItemMeta()&&input.getItemMeta().hasLore();
}
}
class Ench{
	public Enchantment e;
	public int level;
	public Ench(Enchantment ench, int lvl) {
		level=lvl;
		e=ench;
	}
}