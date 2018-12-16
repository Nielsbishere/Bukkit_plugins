package net.nielsbwashere.src.SpecialMobs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.v1_8_R1.Entity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
@SuppressWarnings("deprecation")
public class Core extends JavaPlugin implements Listener{
	List<Short> specials = new ArrayList<Short>(Arrays.asList(new Short[]{51,52,54,57}));
	ConcurrentHashMap<LivingEntity,Integer> things = new ConcurrentHashMap<LivingEntity,Integer>();
@Override
public void onEnable() {
	Bukkit.getPluginManager().registerEvents(this, this);
	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
		@Override
		public void run() {
			int i = getConfig().getInt("Info.Day");
			getConfig().set("Info.Day", i+1);
			saveConfig();
		}}, 0, 20*60*20);
	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
		@Override
		public void run() {
			for(Entry<LivingEntity,Integer> e : things.entrySet()){
				if(e.getKey().isDead()){
					Bukkit.getScheduler().cancelTask(e.getValue());
					things.remove(e.getKey());
				}
			}
		}}, 0, 9);
}
@EventHandler(priority=EventPriority.NORMAL)
public void onFireball(EntityShootBowEvent e){
	if(e.getEntity().getCustomName()==null||!e.getEntity().getCustomName().contains("Pyromaniac"))return;
    if(e.getEntity() instanceof Skeleton)
    {
    	Skeleton p = (Skeleton) e.getEntity();
        e.setCancelled(true);
        Fireball pearl = p.launchProjectile(Fireball.class);
        pearl.setShooter(p);
    }
}
@EventHandler(priority=EventPriority.NORMAL)
public void onMobSpawn(CreatureSpawnEvent e){
	if(e.getSpawnReason()!=SpawnReason.NATURAL)return;
	if(!specials.contains(e.getEntityType().getTypeId()))return;
	LivingEntity newEntity = e.getEntity();
	if(e.getEntityType()==EntityType.PIG_ZOMBIE){
	e.getEntity().remove();
	Entity entity = EntityTypes.spawnEntity(new AggressivePigZombie(((CraftWorld)newEntity.getWorld()).getHandle()), newEntity.getLocation());
	if(entity==null)return;
	newEntity = (LivingEntity) entity.getBukkitEntity();
	}else if(e.getEntityType()==EntityType.SPIDER){
			e.getEntity().remove();
			Entity entity = EntityTypes.spawnEntity(new AggressiveSpider(((CraftWorld)newEntity.getWorld()).getHandle()), newEntity.getLocation());
			if(entity==null)return;
			newEntity = (LivingEntity) entity.getBukkitEntity();
			}
	if(newEntity==null)return;
	pickName(newEntity, e.getEntityType().getTypeId());
}
@EventHandler(priority=EventPriority.NORMAL)
public void onDeath(EntityDeathEvent e){
	if(e.getEntity().getCustomName()==null||!e.getEntity().getCustomName().contains("Loot"))return;
	if(e.getEntity().getKiller()==null)return;
	int diamonds = 10+(int)(Math.random()*5);
	for(int i=0;i<diamonds;i++){
		e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), new ItemStack(Material.DIAMOND,1));
	}
}
@EventHandler(priority=EventPriority.LOWEST)
public void onAttack(EntityDamageByEntityEvent e){
	if(e.getDamager().getCustomName()!=null&&e.getDamager().getCustomName().contains("+")){
		int level = Integer.parseInt(e.getDamager().getCustomName().split("\\+")[1]);
		double dmg = (level/5)*0.5;
		e.setDamage(dmg+e.getDamage()+(e.getDamager().getCustomName().contains("Strong")?5d:0d));
		if(e.getDamager().getCustomName().contains("Thug")&&(e.getEntity() instanceof Player)){
			e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(),((Player)e.getEntity()).getItemInHand());
			((Player)e.getEntity()).setItemInHand(null);
		}
	}
	if(e.getEntity().getCustomName()!=null&&e.getEntity().getCustomName().contains("+")){
		if(e.getEntity().getCustomName().contains("Shielded")&&Math.random()<=0.4)e.setCancelled(true);
	}
}

private void pickName(LivingEntity entity, short id) {
	int level = (day()+(int)(Math.random()*day()*0.3+1));
	level=id==57&&level>75?level:id!=57&&level>75?75:level;
	String type = special(day(),level);
	String name = ChatColor.DARK_RED + (Math.random()<=0.3+(level/150.0<0.4?level/150.0:0.4)?"Armored ":"") + type + (type!=""?" ":"") + pick((int)id)+" +"+(level<150?level:150);
	entity.setCustomName(name);
	entity.setCustomNameVisible(true);
	entity.setMaxHealth(10d+0.5*level+(name.contains("Tank")?5d:0d));
	entity.setHealth(10d+0.5*level+(name.contains("Tank")?5d:0d));
	applyBuffs(entity,name);
}
private String special(int day, int lvl) {
	if(day>5){
		double modifier = 0.01*(lvl)-Math.random()*0.1;
		modifier=modifier<0?0:modifier>0.4?0.4:modifier;
		if(Math.random()<=0.2){
			if(Math.random()<=0.3+modifier)return "Strong";
			if(Math.random()<=0.2+modifier)return "Tank";
			if(Math.random()<=0.15+modifier)return "Fast";
			if(Math.random()<=0.1+modifier)return "Shielded";
			if(Math.random()<=0.05+modifier)return "Regenerating";
			if(Math.random()<=0.02+modifier)return "Thug";
			if(Math.random()<=0.01)return "Lootkeeping";
		}
	}
	return "";
}
private String pick(int entityId) {
	if(entityId==54){
		int i = day();
		if(i>5&&Math.random()<0.1){
			return ChatColor.DARK_RED + "Dark warrior";
		}
		if(i>10&&Math.random()<0.05){
			return ChatColor.DARK_RED + "Dark spirit";				//TODO:
		}
		if(i>20&&Math.random()<0.01){
			return ChatColor.DARK_RED + "Dark assassin";
		}
		if(i>50&&Math.random()<0.001){
			return ChatColor.DARK_RED + "Boss zombie";				//TODO:
		}
		return ChatColor.DARK_RED + "Zombie";
	}else if(entityId==51){
			int i = getConfig().getInt("Info.Day");
			if(i>5&&Math.random()<0.1){
				return ChatColor.DARK_RED + "Wither skeleton";
			}
			if(i>10&&Math.random()<0.05){
				return ChatColor.DARK_RED + "Pyromaniac";
			}
			if(i>20&&Math.random()<0.01){
				return ChatColor.DARK_RED + "Necromancer";
			}
			if(i>50&&Math.random()<0.001){
				return ChatColor.DARK_RED + "Boss skeleton";		//TODO:
			}
			return ChatColor.DARK_RED + "Skeleton";
	}else if(entityId==57){
		int i = getConfig().getInt("Info.Day");
		if(i>5&&Math.random()<0.1){
			return ChatColor.DARK_RED + "Golden warrior";
		}
		if(i>10&&Math.random()<0.05){
			return ChatColor.DARK_RED + "Hell transporter";
		}
		if(i>20&&Math.random()<0.01){
			return ChatColor.DARK_RED + "Pig hoarder";
		}
		if(i>50&&Math.random()<0.001){
			return ChatColor.DARK_RED + "Boss pigman";				//TODO:
		}
		return ChatColor.DARK_RED + "Zombie pigman";
	}else if(entityId==52){
		int i = getConfig().getInt("Info.Day");
		if(i>50&&Math.random()<0.001){
			return ChatColor.DARK_RED + "Boss spider";				//TODO:
		}
		return ChatColor.DARK_RED + "Spider";
	}
	return "";
}
private int day() {
	return getConfig().getInt("Info.Day");
}



private void applyBuffs(final LivingEntity entity, String name) {
	int i = day();
	int level = Integer.parseInt(entity.getCustomName().split("\\+")[1]);
	int helmet=0,chestplate=0,leggings=0,boots=0,weapon=0;
	if(name.contains("Armored")){
		while(!(helmet==0&chestplate==0&&leggings==0&&boots==0)){
		if(Math.random()<=0.4+(i/10))helmet=1;
		if(Math.random()<=0.3+(i/15))chestplate=1;
		if(Math.random()<=0.35+(i/13))leggings=1;
		if(Math.random()<=0.5+(i/8))boots=1;
		}
		
		if(Math.random()<=0.2+(i/20))helmet=2;
		if(Math.random()<=0.1+(i/25))chestplate=2;
		if(Math.random()<=0.15+(i/23))leggings=2;
		if(Math.random()<=0.25+(i/19))boots=2;

		if(Math.random()<=0.05+(i/25))helmet=3;
		if(Math.random()<=0.01+(i/30))chestplate=3;
		if(Math.random()<=0.02+(i/27))leggings=3;
		if(Math.random()<=0.07+(i/24))boots=3;

		if(Math.random()<=0.01+(i/45))helmet=4;
		if(Math.random()<=0.005+(i/55))chestplate=4;
		if(Math.random()<=0.007+(i/50))leggings=4;
		if(Math.random()<=0.015+(i/43))boots=4;

		if(Math.random()<=0+(i/80))helmet=5;
		if(Math.random()<=0+(i/100))chestplate=5;
		if(Math.random()<=0+(i/90))leggings=5;
		if(Math.random()<=0+(i/70))boots=5;
	}
	
	if(Math.random()<=0.3+(i/20))weapon=1;
	if(Math.random()<=0.2+(i/40))weapon=2;
	if(Math.random()<=0.1+(i/60))weapon=3;
	if(Math.random()<=0.05+(i/80))weapon=4;
	if(Math.random()<=i/100)weapon=5;

	if(name.contains("Golden warrior"))helmet=chestplate=leggings=boots=weapon=2;
	if(name.contains("Loot"))helmet=6;
	if(name.contains("Dark warrior")){
		helmet=chestplate=leggings=boots=5;
		weapon=6;
	}
	
	ItemStack helm = helmet!=0?new ItemStack((helmet==6?Material.CHEST:helmet==1?Material.LEATHER_HELMET:helmet==2?Material.GOLD_HELMET:helmet==3?Material.CHAINMAIL_HELMET:helmet==4?Material.IRON_HELMET:Material.DIAMOND_HELMET),1):null;
	ItemStack chest = chestplate!=0?new ItemStack((chestplate==1?Material.LEATHER_CHESTPLATE:chestplate==2?Material.GOLD_CHESTPLATE:chestplate==3?Material.CHAINMAIL_CHESTPLATE:chestplate==4?Material.IRON_CHESTPLATE:Material.DIAMOND_CHESTPLATE),1):null;
	ItemStack pants = leggings!=0?new ItemStack((leggings==1?Material.LEATHER_LEGGINGS:leggings==2?Material.GOLD_LEGGINGS:leggings==3?Material.CHAINMAIL_LEGGINGS:leggings==4?Material.IRON_LEGGINGS:Material.DIAMOND_LEGGINGS),1):null;
	ItemStack shoes = boots!=0?new ItemStack((boots==1?Material.LEATHER_BOOTS:boots==2?Material.GOLD_BOOTS:boots==3?Material.CHAINMAIL_BOOTS:boots==4?Material.IRON_BOOTS:Material.DIAMOND_BOOTS),1):null;
	ItemStack sword = weapon!=0?new ItemStack((weapon==6?Material.DIAMOND_AXE:weapon==1?Material.WOOD_SWORD:weapon==2?Material.GOLD_SWORD:weapon==3?Material.STONE_SWORD:weapon==4?Material.IRON_SWORD:Material.DIAMOND_SWORD),1):null;
	
	if(level>=20&&i>=10){
		for(int k=0;k<4;k++){
		for(int j=0;j<(int)((i/40)+Math.random()*0.5*Math.random()*level);j++){
			for(Enchantment e : new Enchantment[]{Enchantment.OXYGEN,Enchantment.WATER_WORKER,Enchantment.DEPTH_STRIDER,Enchantment.PROTECTION_ENVIRONMENTAL,Enchantment.PROTECTION_EXPLOSIONS,Enchantment.PROTECTION_FALL,Enchantment.PROTECTION_FIRE,Enchantment.PROTECTION_PROJECTILE,Enchantment.DURABILITY}){
				if(k!=0&&(e==Enchantment.OXYGEN||e==Enchantment.WATER_WORKER))continue;
				ItemStack is = k==1?chest:k==2?pants:k==3?shoes:helm;
				if(is==null)break;
				int enchItem=(int) ((level)/40+Math.random()*2);
				if(!is.containsEnchantment(e))is.addUnsafeEnchantment(e, enchItem<=0?1:enchItem>5?5:enchItem);
			}
		}
		}
	}
	if(level>=20&&i>=10){
		for(int j=0;j<(int)((i/40)+Math.random()*0.5*Math.random()*level);j++){
			for(Enchantment e : new Enchantment[]{Enchantment.DAMAGE_ALL,Enchantment.DAMAGE_UNDEAD,Enchantment.DAMAGE_ARTHROPODS,Enchantment.LOOT_BONUS_MOBS,Enchantment.FIRE_ASPECT,Enchantment.KNOCKBACK,Enchantment.DURABILITY}){
				if(weapon==0)break;
				int enchItem=(int) ((level)/40+Math.random()*2);
				if(!sword.containsEnchantment(e))sword.addUnsafeEnchantment(e, enchItem<=0?1:enchItem>5?5:enchItem);
			}
		}
	}
	if(helmet!=0)entity.getEquipment().setHelmet(helm);
	if(chestplate!=0)entity.getEquipment().setChestplate(chest);
	if(leggings!=0)entity.getEquipment().setLeggings(pants);
	if(boots!=0)entity.getEquipment().setBoots(shoes);
	if(weapon!=0)entity.getEquipment().setItemInHand(sword);
	
	if(name.contains("Wither skeleton"))((Skeleton)entity).setSkeletonType(SkeletonType.WITHER);
	if(name.contains("Hell transporter"))things.put(entity, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
		@Override
		public void run() {
			for(int x=entity.getLocation().getBlockX()-5;x<entity.getLocation().getBlockX()+5;x++){
				for(int z=entity.getLocation().getBlockZ()-5;z<entity.getLocation().getBlockZ()+5;z++){
					for(int y=entity.getLocation().getBlockY()-5;y<entity.getLocation().getBlockY()+5;y++){
						if(Math.random()>0.1)continue;
						if(entity.getWorld().getBlockAt(x,y,z).getType()==Material.STONE)entity.getWorld().getBlockAt(x,y,z).setType(Material.NETHER_BRICK);
						if(entity.getWorld().getBlockAt(x,y,z).getType()==Material.FENCE)entity.getWorld().getBlockAt(x,y,z).setType(Material.NETHER_FENCE);
						if(entity.getWorld().getBlockAt(x,y,z).getType()==Material.GRASS)entity.getWorld().getBlockAt(x,y,z).setType(Material.SOUL_SAND);
						if(entity.getWorld().getBlockAt(x,y,z).getType()==Material.DIRT)entity.getWorld().getBlockAt(x,y,z).setType(Material.NETHERRACK);
					}
					entity.getWorld().setBiome(x, z, Biome.HELL);
				}	
			}
		}}, 0, 20));
	if(name.contains("Regenerating"))things.put(entity, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
		@Override
		public void run() {
			entity.damage(-1d);
		}}, 0, 3*20));
	if(name.contains("Pig hoarder"))things.put(entity, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
		@Override
		public void run() {
			EntityTypes.spawnEntity(new AggressivePig(((CraftEntity)entity).getHandle().getWorld()), entity.getLocation());
		}}, 0, 10*20));
	if(name.contains("Necromancer"))things.put(entity, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
		@Override
		public void run() {
			entity.getWorld().spawn(entity.getLocation(),Zombie.class);
		}}, 0, 10*20));
	if(name.contains("assassin"))things.put(entity, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
		@Override
		public void run() {
			entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*2, 0));
		}}, 0, 5*20));
	if(name.contains("Fast"))entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*60*24, 1));
}
}
