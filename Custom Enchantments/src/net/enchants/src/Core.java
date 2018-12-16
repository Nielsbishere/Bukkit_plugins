package net.enchants.src;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
public class Core extends JavaPlugin implements Listener{
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		new RepairChecker(this);
	}
	public void onDisable() {
		saveConfig();
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.LOWEST)
	public void entityDamageUsingEnchantedGear(BlockBreakEvent e){
		if(e.isCancelled())return;
		if(smeltedItem(e.getBlock().getType(),e.getBlock().getData())==null)return;
		Player p = e.getPlayer();
		if(p.getItemInHand()==null||!(p.getItemInHand().getType().name().endsWith("AXE")||p.getItemInHand().getType().name().endsWith("SHOVEL")))return;
		if(!p.getItemInHand().getItemMeta().hasLore()||!p.getItemInHand().hasItemMeta())return;
		if(!hasCustomEnchant(p.getItemInHand().getItemMeta().getLore()))return;
		boolean b = false;
		for(String s : p.getItemInHand().getItemMeta().getLore()){
		if(!isCustomEnchant(s))continue;
		if(s.startsWith(ChatColor.RESET + "Smelting "))b=true;
		}
		if(!b)return;
		e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), smeltedItem(e.getBlock().getType(),e.getBlock().getData()));
		e.getBlock().setType(Material.AIR);
	}
	private ItemStack smeltedItem(Material type, byte data) {
		if(type==Material.IRON_ORE)return new ItemStack(Material.IRON_INGOT,1);
		if(type==Material.GOLD_ORE)return new ItemStack(Material.GOLD_INGOT,1);
		if(type==Material.SAND)return new ItemStack(Material.GLASS,1);
		if(type==Material.COBBLESTONE)return new ItemStack(Material.STONE,1);
		if(type==Material.CLAY)return new ItemStack(Material.HARD_CLAY,1);
		if(type==Material.NETHERRACK)return new ItemStack(Material.NETHER_BRICK_ITEM,1);
		if(type==Material.LOG)return new ItemStack(Material.COAL,1,(short)1);
		if(type==Material.CACTUS)return new ItemStack(Material.INK_SACK,1,(short)2);
		if(type==Material.SPONGE&&data==(byte)1)return new ItemStack(Material.SPONGE,1);
		return null;
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void entityDamageUsingEnchantedGear(EntityDamageByEntityEvent e){
		if(!(e.getDamager()instanceof Player))return;
		if(!(e.getEntity()instanceof LivingEntity))return;
		Player p = (Player)e.getDamager();
		if(p.getItemInHand()==null||!p.getItemInHand().getType().name().endsWith("SWORD")||!p.getItemInHand().hasItemMeta())return;
		LivingEntity el = (LivingEntity)e.getEntity();
		if(!p.getItemInHand().getItemMeta().hasLore())return;
		if(!hasCustomEnchant(p.getItemInHand().getItemMeta().getLore()))return;
		for(String s : p.getItemInHand().getItemMeta().getLore()){
			if(!isCustomEnchant(s))continue;
		int level = parseRomanNumber(s.split("\\s+")[1]);
		if(s.startsWith(ChatColor.RESET + "Poison "))el.addPotionEffect(new PotionEffect(PotionEffectType.POISON,5*20,level,false));
		if(s.startsWith(ChatColor.RESET + "Wither "))el.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,5*20,level,false));
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void entityDamagedByArrow(EntityDamageByEntityEvent e){
		if(!(e.getDamager()instanceof Arrow))return;
		if(!(e.getEntity()instanceof LivingEntity))return;
		Arrow a = (Arrow)e.getDamager();
		LivingEntity le = (LivingEntity)e.getEntity();
		if(a.hasMetadata("isExploding"))le.getWorld().createExplosion(le.getLocation(), 3F);
		if(a.hasMetadata("poisonous"))le.addPotionEffect(new PotionEffect(PotionEffectType.POISON,5*20,a.getMetadata("poisonous").get(0).asInt(),false));
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void entityDamageUsingEnchantedGear(EntityShootBowEvent e){
		if(!(e.getEntity() instanceof Player))return;
		Player p = (Player)e.getEntity();
		if(p.getItemInHand()==null||!p.getItemInHand().getType().name().endsWith("BOW")||!p.getItemInHand().hasItemMeta())return;
		if(!p.getItemInHand().getItemMeta().hasLore())return;
		if(!hasCustomEnchant(p.getItemInHand().getItemMeta().getLore()))return;
		for(String s : p.getItemInHand().getItemMeta().getLore()){
			if(!isCustomEnchant(s))continue;
		int level = parseRomanNumber(s.split("\\s+")[1]);
		if(s.startsWith(ChatColor.RESET + "Exploding ")){
			if(p.getInventory().contains(Material.SULPHUR)){
				e.getProjectile().setMetadata("isExploding", new FixedMetadataValue(this, true));
				removeItem(p.getInventory(),Material.SULPHUR,1);
			}
		}
		if(s.startsWith(ChatColor.RESET + "Poisonous "))e.getProjectile().setMetadata("poisonous", new FixedMetadataValue(this, level));
		}
	}
	private void removeItem(PlayerInventory inventory, Material m, int i) {
		for(int j=0;j<inventory.getContents().length;j++){
			if(inventory.getContents()[j]!=null&&inventory.getContents()[j].getType()==m){
				if(inventory.getContents()[j].getAmount()-1>0)inventory.getContents()[j].setAmount(inventory.getContents()[j].getAmount()-1);
				else inventory.clear(j);
				break;
			}
		}
	}
	private int parseRomanNumber(String string) {
		if(string.equalsIgnoreCase("I"))return 1;
		if(string.equalsIgnoreCase("II"))return 2;
		if(string.equalsIgnoreCase("III"))return 3;
		if(string.equalsIgnoreCase("IV"))return 4;
		if(string.equalsIgnoreCase("V"))return 5;
		if(string.equalsIgnoreCase("VI"))return 6;
		if(string.equalsIgnoreCase("VII"))return 7;
		if(string.equalsIgnoreCase("VIII"))return 8;
		if(string.equalsIgnoreCase("IX"))return 9;
		if(string.equalsIgnoreCase("X"))return 10;
		if(string.equalsIgnoreCase("XI"))return 11;
		if(string.equalsIgnoreCase("XII"))return 12;
		if(string.equalsIgnoreCase("XIII"))return 13;
		if(string.equalsIgnoreCase("XIV"))return 14;
		if(string.equalsIgnoreCase("XV"))return 15;
		if(string.equalsIgnoreCase("XVI"))return 16;
		if(string.equalsIgnoreCase("XVII"))return 17;
		if(string.equalsIgnoreCase("XVIII"))return 18;
		if(string.equalsIgnoreCase("XIX"))return 19;
		if(string.equalsIgnoreCase("XX"))return 20;
		if(string.equalsIgnoreCase("XXI"))return 21;
		if(string.equalsIgnoreCase("XXII"))return 22;
		if(string.equalsIgnoreCase("XXIII"))return 23;
		if(string.equalsIgnoreCase("XXIV"))return 24;
		if(string.equalsIgnoreCase("XXV"))return 25;
		if(string.equalsIgnoreCase("XXVI"))return 26;
		if(string.equalsIgnoreCase("XXVII"))return 27;
		if(string.equalsIgnoreCase("XXVIII"))return 28;
		if(string.equalsIgnoreCase("XXIX"))return 29;
		if(string.equalsIgnoreCase("XXX"))return 30;
		if(string.equalsIgnoreCase("XXXI"))return 31;
		if(string.equalsIgnoreCase("XXXII"))return 32;
		if(string.equalsIgnoreCase("XXXIII"))return 33;
		if(string.equalsIgnoreCase("XXXIV"))return 34;
		if(string.equalsIgnoreCase("XXXV"))return 35;
		if(string.equalsIgnoreCase("XXXVI"))return 36;
		if(string.equalsIgnoreCase("XXXVII"))return 37;
		if(string.equalsIgnoreCase("XXXVIII"))return 38;
		if(string.equalsIgnoreCase("XXXIX"))return 39;
		if(string.equalsIgnoreCase("XL"))return 40;
		if(string.equalsIgnoreCase("XLI"))return 41;
		if(string.equalsIgnoreCase("XLII"))return 42;
		if(string.equalsIgnoreCase("XLIII"))return 43;
		if(string.equalsIgnoreCase("XLIV"))return 44;
		if(string.equalsIgnoreCase("XLV"))return 45;
		if(string.equalsIgnoreCase("XLVI"))return 46;
		if(string.equalsIgnoreCase("XLVII"))return 47;
		if(string.equalsIgnoreCase("XLVIII"))return 48;
		if(string.equalsIgnoreCase("XLIX"))return 49;
		if(string.equalsIgnoreCase("L"))return 50;
		return 0;
	}
	public boolean hasCustomEnchant(List<String> lore) {
		for(String s : lore){
			if(isCustomEnchant(s))return true;
		}
		return false;
	}
	public boolean isCustomEnchant(String s) {
		return s.startsWith(ChatColor.RESET + "Poison ")||s.startsWith(ChatColor.RESET + "Wither ")||s.startsWith(ChatColor.RESET + "Exploding ")||s.startsWith(ChatColor.RESET + "Poisonous ")||s.startsWith(ChatColor.RESET + "Smelting ")||s.startsWith(ChatColor.RESET + "Repair ")||s.startsWith(ChatColor.RESET + "Flying ");
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEnchant(EnchantItemEvent e){
		if(e.getItem().getType().name().endsWith("SWORD")){
			int randomint = (int)(Math.random()*1000);
			if(randomint>=25&&randomint<=99){
				enchantItem("Poison",e.getExpLevelCost()<=10?1:e.getExpLevelCost()<=20?2:e.getExpLevelCost()==30?4:3,e.getItem());
				e.getEnchanter().sendMessage(ChatColor.GOLD + "Your sword has been enchanted with poison!");
			}else if(randomint<25){
				if(e.getExpLevelCost()>=20){
					enchantItem("Wither",e.getExpLevelCost()<=25?1:e.getExpLevelCost()<=30?2:0,e.getItem());
					e.getEnchanter().sendMessage(ChatColor.GOLD + "Your sword has been enchanted with wither!");
				}
			}
		}else if(e.getItem().getType().name().endsWith("BOW")){
			int randomint = (int)(Math.random()*1000);
			if(randomint>=25&&randomint<=99){
				enchantItem("Poisonous",e.getExpLevelCost()<=10?1:e.getExpLevelCost()<=20?2:e.getExpLevelCost()==30?4:3,e.getItem());
				e.getEnchanter().sendMessage(ChatColor.GOLD + "Your bow has been enchanted with poisonous!");
			}else if(randomint<25){
				if(e.getExpLevelCost()==30){
					enchantItem("Exploding",1,e.getItem());
					e.getEnchanter().sendMessage(ChatColor.GOLD + "Your bow has been enchanted with exploding!");
				}
			}
			}else if(e.getItem().getType().name().endsWith("AXE")||e.getItem().getType().name().endsWith("SHOVEL")){
				int randomint = (int)(Math.random()*1000);
				if(randomint<25){
				if(e.getExpLevelCost()==30){
					enchantItem("Smelting",1,e.getItem());
					e.getEnchanter().sendMessage(ChatColor.GOLD + "Your tool has been enchanted with smelting!");
				}
				}
			}else if(e.getItem().getType().name().endsWith("CHESTPLATE")){
				int randomint = (int)(Math.random()*1000);
				if(randomint<10){
					if(e.getExpLevelCost()==30){
						enchantItem("Flying",1,e.getItem());
						e.getEnchanter().sendMessage(ChatColor.GOLD + "Your chestplate has been enchanted with flying!");
					}
				}
			}
		int randomint = (int)(Math.random()*1000);
		if(randomint<25){
		if(e.getExpLevelCost()==30){
			enchantItem("Repair",1,e.getItem());
			e.getEnchanter().sendMessage(ChatColor.GOLD + "Your tool has been enchanted with repair!");
		}
		}
	}
	private void enchantItem(String enchant, int level, ItemStack item) {
		if(level==0)return;
		ItemMeta im = item.getItemMeta();
		List<String> lore = im.getLore();
		if(lore==null||isEmpty(lore)){
			List<String> newLore = new ArrayList<String>();
			newLore.add(ChatColor.RESET + enchant + " " + getRomanNumber(level));
			im.setLore(newLore);
		}else{
			if(!containsEnchant(enchant,item))
			lore.add(ChatColor.RESET + enchant + " " + getRomanNumber(level));
			im.setLore(lore);
		}
		item.setItemMeta(im);
	}
	private boolean containsEnchant(String enchant, ItemStack item) {
		for(String s : item.getItemMeta().getLore()){
			if(s.startsWith(ChatColor.RESET + enchant))return true;
		}
		return false;
	}
	private String getRomanNumber(int level) {
		if(level==1)return "I";
		if(level==2)return "II";
		if(level==3)return "III";
		if(level==4)return "IV";
		if(level==5)return "V";
		if(level==6)return "VI";
		if(level==7)return "VII";
		if(level==8)return "VIII";
		if(level==9)return "IX";
		if(level==10)return "X";
		if(level==11)return "XI";
		if(level==12)return "XII";
		if(level==13)return "XIII";
		if(level==14)return "XIV";
		if(level==15)return "XV";
		if(level==16)return "XVI";
		if(level==17)return "XVII";
		if(level==18)return "XVIII";
		if(level==19)return "XIX";
		if(level==20)return "XX";
		if(level==21)return "XXI";
		if(level==22)return "XXII";
		if(level==23)return "XXIII";
		if(level==24)return "XXIV";
		if(level==25)return "XXV";
		if(level==26)return "XXVI";
		if(level==27)return "XXVII";
		if(level==28)return "XXVIII";
		if(level==29)return "XXIX";
		if(level==30)return "XXX";
		if(level==31)return "XXXI";
		if(level==32)return "XXXII";
		if(level==33)return "XXXIII";
		if(level==34)return "XXXIV";
		if(level==35)return "XXXV";
		if(level==36)return "XXXVI";
		if(level==37)return "XXXVII";
		if(level==38)return "XXXVIII";
		if(level==39)return "XXXIX";
		if(level==40)return "XL";
		if(level==41)return "XLI";
		if(level==42)return "XLII";
		if(level==43)return "XLIII";
		if(level==44)return "XLIV";
		if(level==45)return "XLV";
		if(level==46)return "XLVI";
		if(level==47)return "XLVII";
		if(level==48)return "XLVIII";
		if(level==49)return "XLIX";
		if(level==50)return "L";
		return null;
	}
	private boolean isEmpty(List<String> lore) {
		for(String s : lore){
			if(s!=""&&s!=null)return false;
		}
		return true;
	}
}