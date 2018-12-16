package net.nielsbwashere.src.ProperBackpacks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
public class Core extends JavaPlugin implements Listener{
	
	
	ShapedRecipe backpack, craftingBackpack, enderBackpack, fastpack;
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.addRecipe((backpack=new ShapedRecipe(new ItemStack(Material.CHEST,1)).shape("CAC","ABA","CAC").setIngredient('A', Material.GOLD_INGOT).setIngredient('B', Material.CHEST).setIngredient('C', Material.DIAMOND)));
		Bukkit.addRecipe((craftingBackpack=new ShapedRecipe(new ItemStack(Material.WORKBENCH,1)).shape("CAC","ABA","CAC").setIngredient('A', Material.GOLD_INGOT).setIngredient('B', Material.WORKBENCH).setIngredient('C', Material.DIAMOND)));
		Bukkit.addRecipe((enderBackpack=new ShapedRecipe(new ItemStack(Material.ENDER_CHEST,1)).shape("CAC","ABA","CAC").setIngredient('A', Material.GOLD_INGOT).setIngredient('B', Material.ENDER_CHEST).setIngredient('C', Material.DIAMOND)));
		Bukkit.addRecipe((fastpack=new ShapedRecipe(new ItemStack(Material.CHEST,1)).shape("ABC","EDC","DDD").setIngredient('A', Material.WORKBENCH).setIngredient('B', Material.CHEST).setIngredient('D', Material.GOLD_BLOCK).setIngredient('C', Material.DIAMOND_BLOCK).setIngredient('E', Material.ENDER_CHEST)));
	}
	@EventHandler
	public void onCraft(PrepareItemCraftEvent e){
		if(!(e.getRecipe() instanceof ShapedRecipe))return;
		ShapedRecipe r = (ShapedRecipe) e.getRecipe();
		if(equal(r,backpack))e.getInventory().setItem(0, getBackpack());
		if(equal(r,craftingBackpack))e.getInventory().setItem(0, getCraftBackpack());
		if(equal(r,enderBackpack))e.getInventory().setItem(0, getEnderBackpack());
		if(equal(r,fastpack))e.getInventory().setItem(0, getFastPack());
	}
	@EventHandler
	public void onCraft(CraftItemEvent e){
		if(!(e.getRecipe() instanceof ShapedRecipe))return;
		ShapedRecipe r = (ShapedRecipe) e.getRecipe();
		if(equal(r,backpack))e.getInventory().setItem(0, getBackpackFinal());
		if(equal(r,fastpack))e.getInventory().setItem(0, getFastpackFinal());
	}
	public ItemStack getBackpackFinal(){
		String s = asND(getConfig().getInt("ProperBackpacks.Backpacks"));
		ItemStack is = new ItemStack(Material.CHEST,1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Backpack");
		im.setLore(new ArrayList<String>(Arrays.asList(new String[]{ChatColor.DARK_RED + "Backpack id: " + s})));
		is.setItemMeta(im);
		getConfig().set("ProperBackpacks.Backpacks", getConfig().getInt("ProperBackpacks.Backpacks")+1);
		saveConfig();
		return is;
	}
	public ItemStack getFastpackFinal(){
		String s = asND(getConfig().getInt("ProperBackpacks.Backpacks"));
		ItemStack is = new ItemStack(Material.CHEST,1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Fastpack");
		im.setLore(new ArrayList<String>(Arrays.asList(new String[]{ChatColor.DARK_RED + "Backpack id: " + s})));
		is.setItemMeta(im);
		getConfig().set("ProperBackpacks.Backpacks", getConfig().getInt("ProperBackpacks.Backpacks")+1);
		saveConfig();
		return is;
	}
	private boolean equal(ShapedRecipe shape, ShapedRecipe shape2) {
		ItemStack[] is1 = getFromShape(shape);
		ItemStack[] is2 = getFromShape(shape2);
		if(is1.length!=is2.length)return false;
		for(int i=0;i<is1.length;i++)
			if(!is1[i].isSimilar(is2[i]))return false;
		return true;
	}
	private ItemStack[] getFromShape(ShapedRecipe shape) {
		List<ItemStack> is = new ArrayList<ItemStack>();
		String[] s = shape.getShape();
		for(String st : s){
			for(char c : st.toCharArray()){
				is.add(shape.getIngredientMap().get(c));
			}
		}
		return is.toArray(new ItemStack[]{});
	}
	private ItemStack getCraftBackpack() {
		ItemStack is = new ItemStack(Material.WORKBENCH,1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Crafting backpack");
		is.setItemMeta(im);
		return is;
	}
	private ItemStack getFastPack() {
		ItemStack is = new ItemStack(Material.CHEST,1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Fastpack");
		is.setItemMeta(im);
		return is;
	}
	private ItemStack getEnderBackpack() {
		ItemStack is = new ItemStack(Material.ENDER_CHEST,1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Ender backpack");
		is.setItemMeta(im);
		return is;
	}
	public ItemStack getBackpack(){
		ItemStack is = new ItemStack(Material.CHEST,1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Backpack");
		is.setItemMeta(im);
		return is;
	}
	
	public static String asND(int i){
		String s = "";
		if(i==0)return "#";
		while(i!=0){
			int j = i%64;
			i=i/64;
			s=(j==0?'#':j>=1&&j<=26?(char)('a'-1+j):j>26&&j<=52?(char)('A'+(j-27)):j>52&&j!=63?(char)('0'+(j-53)):'*')+s;
		}
		return s;
	}
	@EventHandler
	public void close(InventoryCloseEvent e){
		if(e.getInventory()==null||e.getInventory().getName()==null||e.getInventory().getName().equals(""))return;
		String name = e.getInventory().getName();
		if(name.startsWith(ChatColor.GOLD+"")&&name.endsWith("'s backpack")){
			getConfig().set(e.getPlayer().getName() + ".Backpack", null);
			for(int i=0;i<9*4;i++){
				getConfig().set(getId(e.getPlayer().getItemInHand()) + ".Backpack." + i, e.getInventory().getContents()[i]);
			}
			saveConfig();
		}
	}
	private String getId(ItemStack itemInHand) {
		if(!itemInHand.hasItemMeta()||!itemInHand.getItemMeta().hasLore())
		return null;
		String tag = null;
		for(String s : itemInHand.getItemMeta().getLore()){
			if(s.startsWith(ChatColor.DARK_RED + "Backpack id: ")){
				tag=s;
				break;
			}
		}
		if(tag==null)return null;
		return tag.replace("Backpack id: ", "");
	}
	@EventHandler
	public void onPlace(PlayerInteractEvent e){
		if(e.getAction()!=Action.RIGHT_CLICK_AIR&&e.getAction()!=Action.RIGHT_CLICK_BLOCK&&e.getAction()!=Action.LEFT_CLICK_AIR&&e.getAction()!=Action.LEFT_CLICK_BLOCK)return;
		if(e.getItem()==null||e.getItem().getType()==Material.AIR||!e.getItem().hasItemMeta()||!e.getItem().getItemMeta().hasDisplayName())return;
		if(getEnderBackpack().getItemMeta().getDisplayName().equals(e.getItem().getItemMeta().getDisplayName())){
			e.getPlayer().openInventory(e.getPlayer().getEnderChest());
			e.setCancelled(true);
		}else if(getBackpack().getItemMeta().getDisplayName().equals(e.getItem().getItemMeta().getDisplayName())){
			e.getPlayer().openInventory(getBackpack(e.getPlayer()));
			e.setCancelled(true);
		}else if(getCraftBackpack().getItemMeta().getDisplayName().equals(e.getItem().getItemMeta().getDisplayName())){
			e.getPlayer().openWorkbench(null,true);
			e.setCancelled(true);
		}else if(getFastPack().getItemMeta().getDisplayName().equals(e.getItem().getItemMeta().getDisplayName())){
			if(e.getPlayer().isSneaking()&&!(e.getAction()==Action.LEFT_CLICK_AIR||e.getAction()==Action.LEFT_CLICK_BLOCK))
				e.getPlayer().openInventory(e.getPlayer().getEnderChest());
			else if(!(e.getAction()==Action.LEFT_CLICK_AIR||e.getAction()==Action.LEFT_CLICK_BLOCK)) e.getPlayer().openInventory(getBackpack(e.getPlayer()));
			if(e.getAction()==Action.LEFT_CLICK_AIR||e.getAction()==Action.LEFT_CLICK_BLOCK)
				e.getPlayer().openWorkbench(null,true);
			e.setCancelled(true);
		}
	}
	private Inventory getBackpack(Player player) {
		return getInventory(getId(player.getItemInHand()) + ".Backpack", player.getName());
	}
	private Inventory getInventory(String string, String name) {
		Inventory inv = Bukkit.createInventory(null, 9*4, ChatColor.GOLD + name + "'s backpack");
		for(int i=0;i<9*4;i++){
			ItemStack is = getConfig().getItemStack(string+"."+i);
			if(is==null||is.getType()==Material.AIR)continue;
			inv.addItem(is);
		}
		return inv;
	}
}
