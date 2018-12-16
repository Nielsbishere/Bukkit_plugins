package nielsbwashere.extraenchants.src;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.v1_8_R1.AxisAlignedBB;
import net.minecraft.server.v1_8_R1.DamageSource;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.EnumParticle;
import net.minecraft.server.v1_8_R1.PacketPlayOutWorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Core extends JavaPlugin implements Listener{
	
	public static Core instance;
	
	public int repeating;
	
	public boolean enchantWithoutPerm, forceCustomEnchant;
	public ConcurrentHashMap<String, Integer> stopList = new ConcurrentHashMap<String, Integer>();
	
	public ConcurrentHashMap<String, Boolean> lst = new ConcurrentHashMap<String,Boolean>();
	
	public List<UUID> hadShielded = new ArrayList<UUID>();

	public static double getHealth(LivingEntity hitEntity) {
		Method[] met = hitEntity.getClass().getMethods();
		for(Method m : met){
			if(m.getName().equals("getHealth")&&m.getReturnType()==double.class)
				try {
					return (double) m.invoke(hitEntity);
				} catch (Exception e) {
					System.out.println("Couldn't get the health of the entity!");
				}
		}
		return 0;
	}
	public static double getMaxHealth(LivingEntity hitEntity) {
		Method[] met = hitEntity.getClass().getMethods();
		for(Method m : met){
			if(m.getName().equals("getMaxHealth")&&m.getReturnType()==double.class)
				try {
					return (double) m.invoke(hitEntity);
				} catch (Exception e) {
					System.out.println("Couldn't get the max health of the entity!");
				}
		}
		return 0;
	}
	public static Enchant springs, running, stomp, nightVision, block, frozen, poisoned, extinguished, shielded, demon, blinding, poisoning, airborne, lifesteal, crusher, deepWounds,
	beheading, lightning, sharpness, blazing, knockback, wither, paralyze, lightningArrows, piercing;
	@Override
	public void onEnable() {
		instance=this;
		 springs = new Enchant("Springs", 3, 10, 30, Enchant.ARMOR, Enchant.boots, 1/48.0, new PotionArmorEffect(new PotionEffect(PotionEffectType.JUMP,40,0)), new PotionArmorEffect(new PotionEffect(PotionEffectType.JUMP,40,1)), new PotionArmorEffect(new PotionEffect(PotionEffectType.JUMP,40,2)));
		 running = new Enchant("Jet", 3, 10, 30, Enchant.ARMOR, Enchant.boots, 1/36.0, new PotionArmorEffect(new PotionEffect(PotionEffectType.SPEED,40,0)), new PotionArmorEffect(new PotionEffect(PotionEffectType.SPEED,40,1)), new PotionArmorEffect(new PotionEffect(PotionEffectType.SPEED,40,2)));
		 stomp = new Enchant( "Stomp", 1, 30, 30, Enchant.ARMOR,Enchant.boots, 1/50.0, new Stomp());
		
		 nightVision = new Enchant("Bat eyes", 1, 20, 30, Enchant.ARMOR, Enchant.helmet, 1/64.0, new PotionArmorEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,40,0)));
		
		 block = new Enchant("Blocking", 3, 25, 30, Enchant.ARMOR, Enchant.all, 1/150.0, new BlockArmorEffect(), new BlockArmorEffect(), new BlockArmorEffect());
		 frozen = new Enchant("Frozen", 2, 25, 30, Enchant.ARMOR, Enchant.all, 1/250.0, new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*5, 0),20), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*5, 1),20));
		 poisoned = new Enchant("Poisoned", 4, 25, 30, Enchant.ARMOR, Enchant.all, 1/500.0, new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*3, 0),10), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*5, 0),20), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*5, 0),30), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*5, 1),25));
		 extinguished = new Enchant("Extinguished", 1, 30, 30, Enchant.ARMOR, Enchant.all, 1/1000.0, new PotionArmorEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40, 0)));
		 shielded = new Enchant("Shielded", 4, 25, 30, Enchant.ARMOR, Enchant.all, 1/1300.0, new PotionArmorEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20*60*15, 0)), new PotionArmorEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20*60*15, 1)), new PotionArmorEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20*60*15, 2)), new PotionArmorEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20*60*15, 3)));
		 demon = new Enchant( "Demon", 1, 30, 30, Enchant.ARMOR, Enchant.all, 1/1000.0, new PotionArmorEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0)));
		 blinding = new Enchant("Blinding", 1, 20, 30, Enchant.WEAPON, Enchant.all, 1/400.0, new PotionItemEffect(new PotionEffect(PotionEffectType.BLINDNESS,20*2,0),100));
		 poisoning = new Enchant("Poisoning", 2, 25, 30, Enchant.WEAPON, Enchant.all, 1/300.0, new PotionItemEffect(new PotionEffect(PotionEffectType.POISON,20*1,0),100), new PotionItemEffect(new PotionEffect(PotionEffectType.POISON,20*3,0),100));
		 airborne = new Enchant("Airborne", 1, 30, 30, Enchant.WEAPON, Enchant.all, 1/500.0,  new PotionItemEffect(null, 0));
		 lifesteal = new Enchant("Lifesteal", 1, 30, 30, Enchant.WEAPON, Enchant.all, 1/200.0, new Lifesteal(5));
		 crusher = new Enchant("Crusher", 1, 30, 30, Enchant.WEAPON, Enchant.all, 1/1000.0, new Deathbringer(100));
		 deepWounds = new Enchant("Deep-Wounds", 1, 20, 30, Enchant.WEAPON, Enchant.all, 1/200.0, new Wounding());
		 beheading = new Enchant( "Beheading", 1, 30, 30, Enchant.WEAPON, Enchant.all, 1/400.0, new Beheading());
		 lightning = new Enchant( "Lightning", 3, 25, 30, Enchant.WEAPON, Enchant.all, 1/500.0, new Lightning(10), new Lightning(25), new Lightning(40));
		 sharpness = new Enchant( "Sharpness", 5, 10, 30, Enchant.WEAPON, Enchant.axe, 1/5.0, new Sharpness(), new Sharpness(), new Sharpness(), new Sharpness(), new Sharpness());
		 blazing = new Enchant( "Fire-aspect", 2, 10, 30, Enchant.WEAPON, Enchant.axe, 1/3.0, new Fireaspect(), new Fireaspect());
		 knockback = new Enchant( "Knockback", 3, 10, 30, Enchant.WEAPON, Enchant.axe, 1/10.0, new Knockback(), new Knockback(), new Knockback());
		
		 wither = new Enchant("Wither", 1, 30, 30, Enchant.BOW, Enchant.all, 1/2000.0, new WitherArrows());
		 paralyze = new Enchant("Paralyze", 1, 30, 30, Enchant.BOW, Enchant.all, 1/3000.0, new ParalyzeArrows());
		 lightningArrows = new Enchant("Thor's-Arrows", 2, 25, 30, Enchant.BOW, Enchant.all, 1/580.0, new LightningArrows(25), new LightningArrows(40));
		 piercing = new Enchant("Piercing", 1, 30, 30, Enchant.BOW, Enchant.all, 1/350.0, new Piercing());
		Bukkit.getPluginManager().registerEvents(this, this);
		repeating = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for(Player p : getServer().getOnlinePlayers()){
					if(p.getInventory().getArmorContents()==null||p.getInventory().getArmorContents().length==0)continue;
					for(ItemStack is : p.getInventory().getArmorContents()){
						if(is==null||is.getType()==Material.AIR)continue;
						Enchantment[] enchants = Enchant.getFromStack(is);
						if(enchants==null)continue;
						boolean hasShield=false;
						for(Enchantment e : enchants){
							if(e.enchant==Core.shielded){
								hasShield=true;
							}
							if(e.enchant.type!=Enchant.ARMOR)continue;
							Material t = is.getType();
							if((e.enchant.effect==Enchant.helmet&&(t!=Material.LEATHER_HELMET&&t!=Material.CHAINMAIL_HELMET&&t!=Material.GOLD_HELMET&&t!=Material.IRON_HELMET&&t!=Material.DIAMOND_HELMET))||
							(e.enchant.effect==Enchant.boots&&(t!=Material.LEATHER_BOOTS&&t!=Material.CHAINMAIL_BOOTS&&t!=Material.GOLD_BOOTS&&t!=Material.IRON_BOOTS&&t!=Material.DIAMOND_BOOTS)))continue;
							Effect eff = e.enchant.onEnchantActivated.length<=e.level?e.enchant.onEnchantActivated[e.enchant.onEnchantActivated.length-1]:e.enchant.onEnchantActivated[e.level];
							if(e.enchant.name.equalsIgnoreCase(demon.name)){
								if(p.hasPotionEffect(PotionEffectType.BLINDNESS))p.removePotionEffect(PotionEffectType.BLINDNESS);
								p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
							}
//							if(e.enchant.name.equalsIgnoreCase(enderShift.name)){
//								if(getHealth(p)>2.5d)continue;
//								if(p.hasPotionEffect(PotionEffectType.SPEED))p.removePotionEffect(PotionEffectType.SPEED);
//								p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*5, 1));
//								p.setHealth(getHealth(p)+1.5d);
//								continue;
//							}
							if(!(eff instanceof PotionArmorEffect))continue;
							((PotionArmorEffect)eff).activate(p.getWorld(), p, null, null, e);
						}
						if(hasShield&&!hadShielded.contains(p.getUniqueId()))hadShielded.add(p.getUniqueId());
						if(!hasShield&&hadShielded.contains(p.getUniqueId())){
							hadShielded.remove(p.getUniqueId());
							p.removePotionEffect(PotionEffectType.HEALTH_BOOST);
						}
					}
				}
			}}, 0l, 19);
		boolean init = getConfig().getBoolean("Settings.Initialized");
		if(!init){
			getConfig().set("Settings.Initialized",true);
			getConfig().set("Settings.peopleWithoutPermissionCanEnchant",true);
			getConfig().set("Settings.forceCustomEnchant",false);
			getConfig().set("Settings.RandomLevels", true);
			saveConfig();
		}
		enchantWithoutPerm = getConfig().getBoolean("Settings.peopleWithoutPermissionCanEnchant");
		forceCustomEnchant = getConfig().getBoolean("Settings.forceCustomEnchant");
	}
	@Override
	public void onDisable() {
		saveConfig();
		Bukkit.getScheduler().cancelTask(repeating);
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void shooting(EntityShootBowEvent e){
		if(!(e.getEntity() instanceof Player))return;
		Player p = (Player)e.getEntity();
		if(p.getItemInHand()==null||p.getItemInHand().getType()==Material.AIR)return;
		Enchantment[] enchants = Enchant.getFromStack(p.getItemInHand());
		if(enchants==null||enchants.length==0)return;
		for(Enchantment ench : enchants){
			if(ench==null)continue;
			Effect eff = ench.enchant.onEnchantActivated.length<=ench.level?ench.enchant.onEnchantActivated[ench.enchant.onEnchantActivated.length-1]:ench.enchant.onEnchantActivated[ench.level];
			if(eff==null)continue;
			if(eff instanceof WitherArrows)
			e.getProjectile().setMetadata("wither", new FixedMetadataValue(this, true));
			if(eff instanceof ParalyzeArrows)
			e.getProjectile().setMetadata("paralyze", new FixedMetadataValue(this, ench.level));
			if(eff instanceof LightningArrows)
			e.getProjectile().setMetadata("lightning", new FixedMetadataValue(this, ench.level));
			if(eff instanceof Piercing)
			e.getProjectile().setMetadata("piercing", new FixedMetadataValue(this, ench.level));
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onFall(EntityDamageEvent e){
		if(!(e.getEntity() instanceof Player))return;
		if(e.getCause()!=DamageCause.FALL)return;
		Player p = (Player)e.getEntity();
		if(p.getInventory().getArmorContents()==null||p.getInventory().getArmorContents().length==0)return;
		boolean hasStomp=false;
		for(ItemStack is : p.getInventory().getArmorContents()){
		if(is==null||is.getType()==Material.AIR)continue;
		Enchantment[] enchants = Enchant.getFromStack(is);
		if(enchants==null||enchants.length==0)continue;
		for(Enchantment ench : enchants){
			Effect eff = ench.enchant.onEnchantActivated.length<=ench.level?ench.enchant.onEnchantActivated[ench.enchant.onEnchantActivated.length-1]:ench.enchant.onEnchantActivated[ench.level];
			if(eff instanceof Stomp){
				hasStomp=true;
				break;
			}
		}
		}
		if(!hasStomp)return;
		double dmg = e.getFinalDamage();
		AxisAlignedBB aabb = AxisAlignedBB.a(p.getLocation().getX()-1, p.getLocation().getY()-1, p.getLocation().getZ()-1, p.getLocation().getX()+1, p.getLocation().getY()+1, p.getLocation().getZ()+1);
		List<?> elist = ((CraftWorld)p.getWorld()).getHandle().getEntities(((CraftPlayer)p).getHandle(), aabb);
		if(elist==null||elist.isEmpty())return;
		boolean damaged = false;
		for(Object o : elist){
			if(o==null||!(o instanceof EntityLiving))continue;
			EntityLiving el = (EntityLiving)o;
			if(el.getUniqueID().toString().equalsIgnoreCase(p.getUniqueId().toString()))continue;
			el.damageEntity(DamageSource.FALL, (float)dmg);
			damaged=true;
		}
		if(!damaged)return;
		e.setDamage(0d);
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPHit(EntityDamageByEntityEvent e){
		if(!(e.getEntity() instanceof Player))return;
		Player p = (Player)e.getEntity();
		Entity hit = e.getDamager();
		if(p.getInventory().getArmorContents()==null||p.getInventory().getArmorContents().length==0)return;
		int block = 0;
		for(ItemStack is : p.getInventory().getArmorContents()){
		if(is==null||is.getType()==Material.AIR)continue;
		Enchantment[] enchants = Enchant.getFromStack(is);
		if(enchants==null||enchants.length==0)continue;
		for(Enchantment ench : enchants){
			Effect eff = ench.enchant.onEnchantActivated.length<=ench.level?ench.enchant.onEnchantActivated[ench.enchant.onEnchantActivated.length-1]:ench.enchant.onEnchantActivated[ench.level];
			if(eff instanceof BlockArmorEffect&&block<ench.level)block=ench.level;
			if(!(eff instanceof ArmorHitPotionEffect))continue;
			((ArmorHitPotionEffect)eff).activate(p.getWorld(), p, null, hit, ench);
		}
		}
		if(block!=0&&Math.random()<=0.1*(block-1))e.setCancelled(true);
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void hitting(EntityDamageByEntityEvent e){
		if(!(e.getDamager() instanceof Arrow))return;
		Arrow a = (Arrow) e.getDamager();
		if(a.hasMetadata("wither")&& e.getEntity() instanceof LivingEntity)((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WITHER,20*2,1));
		if(a.hasMetadata("paralyze")&&e.getEntity() instanceof LivingEntity){
			((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW,20*3,a.getMetadata("paralyze").get(0).asInt()-1));
			((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,20*3,a.getMetadata("paralyze").get(0).asInt()-1));
			((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,20*3,a.getMetadata("paralyze").get(0).asInt()-1));
		}
		if(a.hasMetadata("lightning")&&Math.random()<=a.getMetadata("lightning").get(0).asInt()*15/100.0+0.1)a.getWorld().strikeLightning(e.getEntity().getLocation());
		if(a.hasMetadata("piercing"))e.setDamage(DamageModifier.ARMOR, 0);
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void hitting(ProjectileHitEvent e){
		if(!(e.getEntity() instanceof Arrow))return;
		Arrow a = (Arrow) e.getEntity();
		if(a.hasMetadata("lightning")&&Math.random()<=a.getMetadata("lightning").get(0).asInt()*15/100.0+0.1)a.getWorld().strikeLightning(e.getEntity().getLocation());
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEntityHit(EntityDamageByEntityEvent e){
		if(e.getDamager()==null||!(e.getDamager() instanceof Player))return;
		if(!(e.getEntity() instanceof LivingEntity))return;
		Player p = (Player)e.getDamager();
		if(p.getItemInHand()==null||p.getItemInHand().getType()==Material.AIR)return;
		Enchantment[] enchants = Enchant.getFromStack(p.getItemInHand());
		if(enchants==null||enchants.length==0)return;
		for(Enchantment ench : enchants){
			Effect eff = ench.enchant.onEnchantActivated.length<=ench.level?ench.enchant.onEnchantActivated[ench.enchant.onEnchantActivated.length-1]:ench.enchant.onEnchantActivated[ench.level];
			if(ench.enchant.name.equals(airborne.name)){
				if(Math.random()>0.01)continue;
				((CraftLivingEntity)((LivingEntity)e.getEntity())).getHandle().move(0,10.0,0);
				continue;
			}
			if(!(eff instanceof PotionItemEffect))continue;
			((PotionItemEffect)eff).activate(p.getWorld(), p, null, ((LivingEntity)e.getEntity()), ench);
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEntityHit2(EntityDamageByEntityEvent e){
		if(e.getDamager()==null||!(e.getDamager() instanceof Player))return;
		if(!(e.getEntity() instanceof LivingEntity))return;
		Player p = (Player)e.getDamager();
		if(p.getItemInHand()==null||p.getItemInHand().getType()==Material.AIR)return;
		Enchantment[] enchants = Enchant.getFromStack(p.getItemInHand());
		if(enchants==null||enchants.length==0)return;
		for(Enchantment ench : enchants){
			Effect eff = ench.enchant.onEnchantActivated.length<=ench.level?ench.enchant.onEnchantActivated[ench.enchant.onEnchantActivated.length-1]:ench.enchant.onEnchantActivated[ench.level];
			if(!(eff instanceof Deathbringer))continue;
			if(Math.random()>((Deathbringer)eff).percentage/100.0)continue;
			e.setDamage(getDamage(e)*2);
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void entityKill(EntityDeathEvent e){
		if(e.getEntity()==null||e.getEntity().getKiller()==null)return;
		Player p = e.getEntity().getKiller();
		if(p.getItemInHand()==null||p.getItemInHand().getType()==Material.AIR)return;
		Enchantment[] enchants = Enchant.getFromStack(p.getItemInHand());
		if(enchants==null||enchants.length==0)return;
		for(Enchantment ench : enchants){
			if(!ench.enchant.equals(beheading))continue;
			ench.enchant.activate(ench, p, null, e.getEntity());
		}
	}
	private double getDamage(EntityDamageByEntityEvent e) {
		Method[] met = e.getClass().getMethods();
		for(Method m : met){
			if(m.getName().equals("getDamage")&&m.getReturnType()==double.class)
				try {
					return (double) m.invoke(e);
				} catch (Exception ex) {
					System.out.println("Couldn't get the damage dealt to the entity!");
				}
		}
		return 0;
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEnchant(EnchantItemEvent e){
		Player p = e.getEnchanter();
		if(!(enchantWithoutPerm||p.isOp()||p.hasPermission("ExtraEnchants.Enchant")))return;
		List<Enchantment> enchList = new ArrayList<Enchantment>();
		int chances = (int) (Math.random()*2+1);
		int chance=0;
		while(enchList.size()<chances&&chance++<1){
		for(Enchant ench : Enchant.enchantmentList){
			if(Math.random()<=ench.chance*(ench.maxExpLevel==e.getExpLevelCost()?2:1)&&e.getExpLevelCost()>=ench.minExpLevel&&e.getExpLevelCost()<=ench.maxExpLevel&&ench.gottenFromEnchantTable&&ench.appropFor(e.getItem(),ench,false)){
				int costPer = (ench.maxExpLevel-ench.minExpLevel)/ench.maxLevel;
				int level = (e.getExpLevelCost()-ench.minExpLevel)/costPer;
				if(getConfig().getBoolean("Settings.RandomLevels"))level-=level*Math.random()*0.5;
				if(level<1)level=1;
				if(level>ench.maxLevel)level=ench.maxLevel;
				enchList.add(new Enchantment(ench,level));
				if(Math.random()<=0.3){
					org.bukkit.enchantments.Enchantment toRemove=null;
					while(toRemove==null&&e.getEnchantsToAdd().size()>1){
						for(org.bukkit.enchantments.Enchantment enchant : e.getEnchantsToAdd().keySet()){
							if(Math.random()<=0.4)continue;
							toRemove=enchant;
							break;
						}
					}
					e.getEnchantsToAdd().remove(toRemove);
				}
				for(Enchantment enchantment : enchList){
					Enchant.addToStack(enchantment.enchant, enchantment.level, e.getItem(), false);
					if(enchantment.enchant!=sharpness&&enchantment.enchant!=blazing&&enchantment.enchant!=knockback)
					p.sendMessage(ChatColor.GOLD + "Well done! You got the " + enchantment.enchant.displayName + " enchant!");
				}
			}
		}
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if(label.equalsIgnoreCase("addExtraEnchant")){
			if((!sender.isOp()&&!sender.hasPermission("ExtraEnchants.Commands.addExtraEnchant"))||!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "You're not permitted to use this command!");
				return false;
			}
			if(args.length!=2){
				sender.sendMessage(ChatColor.DARK_RED + "Incorrect usage: /<command> <enchantName> <level>");
				return false;
			}
			Player p = (Player)sender;
			if(p.getItemInHand()==null||p.getItemInHand().getType()==Material.AIR){
				p.sendMessage(ChatColor.DARK_RED + "You must be holding something to enchant!");
				return false;
			}
			Enchant specifiedEnchant;
			if(!Enchant.contains(args[0])){
				p.sendMessage(ChatColor.DARK_RED + "Error! Couldn't find the specified enchant: " + args[0]);
				p.sendMessage(ChatColor.GOLD + "Down here there's a list of all of the enchants!");
				for(Enchant e : Enchant.enchantmentList){
					p.sendMessage(ChatColor.GOLD + " - " + e.displayName);
				}
				return false;
			}else specifiedEnchant = Enchant.get(args[0]);
			int level;
			try{
				level=Integer.parseInt(args[1]);
			}catch(Exception e){
				p.sendMessage(ChatColor.DARK_RED + "Error! You put in an invalid number!: " + args[1] + " is not a number!");
				return false;
			}
			if(level>specifiedEnchant.maxLevel)level=specifiedEnchant.maxLevel;
			Enchant.addToStack(specifiedEnchant, level, p.getItemInHand(),forceCustomEnchant);
			p.sendMessage(ChatColor.GOLD + "You've successfully added the enchant to your itemstack!");
		}else if(label.equalsIgnoreCase("ExtraEnchants")){
			if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "You are not an OP so you can't use /ExtraEnchants");
				return false;
			}
			if(args.length==1&&args[0].equalsIgnoreCase("reset")){
				getConfig().set("Settings", null);
				getConfig().set("Enchantments", null);
				saveConfig();
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "That argument is not initialized!");
			}
		}
		return super.onCommand(sender, command, label, args);
	}
}

class Enchant{
	public static final int ARMOR=0,WEAPON=1,BOW=2,BOOK=3,ALL=4,TOOL=5,SHEAR=6,FISHING_ROD=7,SPECIFIEDITEMWITHID=8;
	public static final int all=0,boots=1,leggings=2,chestplate=3,helmet=4, axe = 5;
	public static final List<Enchant> enchantmentList = new ArrayList<Enchant>();
	public String name;
	public int maxLevel, effect;
	public int minExpLevel, maxExpLevel,type;
	public double chance;
	public Effect[] onEnchantActivated;
	public String displayName;
	public boolean gottenFromEnchantTable;
	public Enchant(String name, int maxLevel, int minXP, int maxXP, int type, int itemWithEffect, double chance, Effect... activated){
		Core c = Core.instance;
		FileConfiguration config = c.getConfig();
		this.name=name;
		if(!config.getBoolean("Enchantments."+name+".Initialized")){
		this.maxLevel=maxLevel;
		minExpLevel=minXP;
		maxExpLevel=maxXP;
		this.type=type;
		effect=itemWithEffect;
		onEnchantActivated=activated;
		this.chance=chance;
		displayName=ChatColor.RESET+name;
		gottenFromEnchantTable=true;
		config.set("Enchantments."+name+".Initialized",true);
		config.set("Enchantments."+name+".maxLevel",maxLevel);
		config.set("Enchantments."+name+".minXPLvl",minXP);
		config.set("Enchantments."+name+".maxXPLvl",maxXP);
		config.set("Enchantments."+name+".chance",chance);
		config.set("Enchantments."+name+".display",displayName);
		config.set("Enchantments."+name+".canBeGottenByEnchanting",gottenFromEnchantTable);
		c.saveConfig();
		}else{
			this.maxLevel=config.getInt("Enchantments."+name+".maxLevel");
			minExpLevel=config.getInt("Enchantments."+name+".minXPLvl");
			maxExpLevel=config.getInt("Enchantments."+name+".maxXPLvl");
			this.type=type;
			effect=itemWithEffect;
			onEnchantActivated=activated;
			this.chance=config.getDouble("Enchantments."+name+".chance");
			displayName = config.getString("Enchantments."+name+".display");
			gottenFromEnchantTable = config.getBoolean("Enchantments."+name+".canBeGottenByEnchanting");
		}
		Enchant.enchantmentList.add(this);
	}
	public boolean appropFor(ItemStack itemInHand, Enchant specifiedEnchant,boolean force) {
		if(force)return true;
		int armorType = Enchant.armorType(itemInHand);
		if(specifiedEnchant.type==Enchant.ARMOR&&armorType==-1)return false;
		if(specifiedEnchant.type==Enchant.ARMOR&&armorType!=0&&specifiedEnchant.effect==Enchant.boots)return false;
		if(specifiedEnchant.type==Enchant.ARMOR&&armorType!=3&&specifiedEnchant.effect==Enchant.helmet)return false;
		if(specifiedEnchant.type==Enchant.WEAPON&&specifiedEnchant.effect==Enchant.axe&&!itemInHand.getType().name().endsWith("AXE"))return false;
		if(specifiedEnchant.type==Enchant.WEAPON&&!itemInHand.getType().name().endsWith("SWORD")&&!itemInHand.getType().name().endsWith("AXE"))return false;
		if(specifiedEnchant.type==Enchant.BOW&&!itemInHand.getType().name().endsWith("BOW"))return false;
		if(specifiedEnchant.type==Enchant.BOOK&&!itemInHand.getType().name().endsWith("BOOK"))return false;
		if(specifiedEnchant.type==Enchant.TOOL&&!itemInHand.getType().name().endsWith("AXE")&&!itemInHand.getType().name().endsWith("SHOVEL"))return false;
		if(specifiedEnchant.type==Enchant.FISHING_ROD&&!itemInHand.getType().name().endsWith("ROD"))return false;
		if(specifiedEnchant.type==Enchant.SHEAR&&!itemInHand.getType().name().endsWith("SHEAR"))return false;
		return !Enchant.hasEnchant(specifiedEnchant, itemInHand.getItemMeta().getLore());
	}
	public static void addToStack(Enchant specifiedEnchant, int level, ItemStack itemInHand, boolean force) {
		if(level>=specifiedEnchant.onEnchantActivated.length)level=specifiedEnchant.onEnchantActivated.length;
		if(!specifiedEnchant.appropFor(itemInHand,specifiedEnchant,force))return;
		if(specifiedEnchant==Core.sharpness){
			if(!itemInHand.containsEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ALL))itemInHand.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ALL, level);
			return;
		}
		if(specifiedEnchant==Core.blazing){
			if(!itemInHand.containsEnchantment(org.bukkit.enchantments.Enchantment.FIRE_ASPECT))itemInHand.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.FIRE_ASPECT, level);
			return;
		}
		if(specifiedEnchant==Core.knockback){
			if(!itemInHand.containsEnchantment(org.bukkit.enchantments.Enchantment.KNOCKBACK))itemInHand.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.KNOCKBACK, level);
			return;
		}
		ItemMeta im = itemInHand.getItemMeta();
		List<String> lore = im.hasLore()?im.getLore():new ArrayList<String>();
		if(lore.isEmpty()){
			lore.add(specifiedEnchant.displayName + " " + Enchant.convertToRoman(level));
			im.setLore(lore);
		}else{
			List<String> newLore = new ArrayList<String>();
			if(!hasEnchant(specifiedEnchant,lore))
			newLore.add(specifiedEnchant.displayName + " " + Enchant.convertToRoman(level));
			for(String s : lore){
				newLore.add(s);
			}
			im.setLore(newLore);
		}
		itemInHand.setItemMeta(im);
	}
	public static int armorType(ItemStack is) {
		if(is==null||is.getType()==Material.AIR)return -1;
		String name = is.getType().name();
		if(name.endsWith("BOOTS"))return 0;
		if(name.endsWith("LEGGINGS"))return 1;
		if(name.endsWith("CHESTPLATE"))return 2;
		if(name.endsWith("HELMET"))return 3;
		return -1;
	}
	public static boolean hasEnchant(Enchant specifiedEnchant, List<String> lore) {
		if(lore==null)return false;
		for(String s : lore){
			if(s.startsWith(specifiedEnchant.displayName+" "))return true;
		}
		return false;
	}
	public static Enchantment[] getFromStack(ItemStack itemInHand){
		if(!itemInHand.hasItemMeta()||!itemInHand.getItemMeta().hasLore()||itemInHand.getItemMeta().getLore().isEmpty())return null;
		List<Enchantment> ench = new ArrayList<Enchantment>();
		for(Enchant e : Enchant.enchantmentList){
			for(String st : itemInHand.getItemMeta().getLore()){
				if(st.startsWith(e.displayName))ench.add(new Enchantment(e,Enchant.fromRoman(st.replace(e.displayName + " ", ""))));
			}
		}
		Enchantment[] enchants = new Enchantment[ench.size()];
		int i=0;
		for(Enchantment e : ench){
			enchants[i++]=e;
		}
		return enchants;
	}
	public static boolean contains(String string) {
		return get(string)!=null;
	}
	public static Enchant get(String string) {
		for(Enchant e : Enchant.enchantmentList){
			if(e.name.equalsIgnoreCase(string))return e;
		}
		return null;
	}
	public void activate(Enchantment e, Player p, Block block, Entity entity){
		int lvl=e.level;
		if(e.level>=e.enchant.onEnchantActivated.length)lvl=e.enchant.onEnchantActivated.length;
		e.enchant.onEnchantActivated[lvl-1].activate(p.getWorld(), p, block, entity, e);
	}
	
	public static int fromRoman(String toRoman){
		int i=0;
		while(!toRoman.equals("")){
			if(toRoman.contains("L")&&!toRoman.startsWith("XL")){
				toRoman = toRoman.replace("L", "");
				i+=50;
			}else if(toRoman.contains("L")&&toRoman.startsWith("X")){
				toRoman = toRoman.replace("XL", "");
			i+=40;
			}else if(toRoman.contains("XXX")){
				toRoman = toRoman.replace("XXX", "");
				i+=30;
			}else if(toRoman.contains("XX")){
				toRoman = toRoman.replace("XX", "");
					i+=20;
			}else if(toRoman.contains("X")&&!toRoman.startsWith("I")){
				toRoman = toRoman.replace("X", "");
				i+=10;
			}else if(toRoman.contains("IX")){
				toRoman = toRoman.replace("IX", "");
				i+=9;
			}else if(toRoman.contains("V")&&!toRoman.startsWith("I")){
				toRoman = toRoman.replace("V", "");
				i+=5;
			}else if(toRoman.contains("V")&&toRoman.startsWith("I")){
				toRoman = toRoman.replace("IV", "");
				i+=4;
			}else if(toRoman.contains("III")){
				toRoman = toRoman.replace("III", "");
				i+=3;
			}else if(toRoman.contains("II")){
				toRoman = toRoman.replace("II", "");
				i+=2;
			}else if(toRoman.contains("I")){
				toRoman = toRoman.replace("I", "");
				i+=1;
			}
		}
		return i;
	}
	
	public static String convertToRoman(int i){
		String s="";
		while(i>0){
			if(i>=50){
				s=s+"L";
				i-=50;
			}else if(i>=40){
				s=s+"XL";
				i-=40;
			}else if(i>=30){
				s=s+"XXX";
				i-=30;
			}else if(i>=20){
				s=s+"XX";
				i-=20;
			}else if(i>=10){
				s=s+"X";
				i-=10;
			}else if(i>=9){
				s=s+"IX";
				i-=9;
			}else if(i>=5){
				s=s+"V";
				i-=5;
			}else if(i>=4){
				s=s+"IV";
				i-=5;
			}else if(i>=1){
				s=s+"I";
				i-=1;
			}
		}
		return s;
	}
}
class Enchantment{
	public Enchant enchant;
	public int level;
	public Enchantment(Enchant ench, int lvl) {
		enchant=ench;
		level=lvl;
	}
}

interface Effect{
	abstract void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e);
}

class PotionArmorEffect implements Effect{
	public PotionEffect pe;
	public PotionArmorEffect(PotionEffect pe) {
		this.pe=pe;
	}
	@Override
	public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {
		if(p.hasPotionEffect(pe.getType())&&pe.getType()!=PotionEffectType.HEALTH_BOOST)p.removePotionEffect(pe.getType());
		p.addPotionEffect(pe);
	}
}

class PotionItemEffect implements Effect{
	public PotionEffect pe;
	public int percentage;
	public PotionItemEffect(PotionEffect pe, int percentage) {
		this.pe=pe;
		this.percentage=percentage;
	}
	@Override
	public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {
		if(hitEntity==null)return;
		if(!(hitEntity instanceof LivingEntity))return;
		if(Math.random()>(percentage/100.0))return;
		((LivingEntity)hitEntity).addPotionEffect(pe);
	}
}
class Lifesteal extends PotionItemEffect{
	public Lifesteal(int percentage){
		super(null,percentage);
	}
	@Override
	public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {
		if(hitEntity==null)return;
		if(!(hitEntity instanceof LivingEntity))return;
		if(Math.random()>(percentage/1000.0))return;
		((LivingEntity)hitEntity).setHealth(Core.getHealth((LivingEntity)hitEntity)-0.5D>=0.5?Core.getHealth((LivingEntity)hitEntity)-0.5D:0.5);
		p.setHealth(Core.getHealth(p)+0.5D<=Core.getMaxHealth(p)?Core.getHealth(p)+0.5:Core.getMaxHealth(p));
	}
}
class Deathbringer implements Effect{
	public int percentage;
	public Deathbringer(int chance) {
		percentage=chance;
	}
	@Override
	public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {
	}
}
class Wounding extends PotionItemEffect{
	public Wounding() {
		super(null,100);
	}

	@Override
	public void activate(World w, Player p, Block hitBlock, final Entity hitEntity, Enchantment e) {
		if(hitEntity==null||!(hitEntity instanceof LivingEntity))return;
		if(Math.random()<0.3){
			final Core c = (Core)Bukkit.getPluginManager().getPlugin("ExtraEnchants");
			final String uuid = hitEntity.getUniqueId().toString();
			int id = c.getServer().getScheduler().scheduleSyncRepeatingTask(c, new Runnable(){
				int ranTimes = 0;
				int maxRun = (int) (Math.random()*5+3);
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					LivingEntity le = (LivingEntity)hitEntity;
					if(le.isDead()){
						Bukkit.getScheduler().cancelTask(c.stopList.get(uuid));
						return;
					}
					if(ranTimes>maxRun){
						Bukkit.getScheduler().cancelTask(c.stopList.get(uuid));
						return;
					}
					if(Math.random()<=0.1)return;
					ranTimes++;
					if(Core.getHealth(le)>=1D){
						le.damage(1D);
						le.getWorld().playSound(le.getLocation(), Sound.FIZZ, 1F, 1F);
						for(Player p : le.getServer().getOnlinePlayers())((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float)le.getLocation().getX(), (float)le.getLocation().getY(), (float)le.getLocation().getZ(), 0.0f, 0.0f, 0.0f, 0f, 100));
					}
				}}, 20, 20*2);
			if(!c.stopList.containsKey(uuid))
			c.stopList.put(uuid, id);
		}
	}
}
class LightningArrows implements Effect{int percentage;public LightningArrows(int i) {percentage=i;	}
@Override public void activate(World w, Player p, Block hitBlock, Entity hitEntity,Enchantment e){}}
class ArmorHitPotionEffect implements Effect{
	public PotionEffect pe;
	public int percentage;
	public ArmorHitPotionEffect(PotionEffect pe, int i) {
		this.pe=pe;
		percentage=i;
	}
	@Override
	public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {
		if(hitEntity==null)return;
		if(!(hitEntity instanceof LivingEntity))return;
		if(Math.random()>percentage/100.0)return;
		((LivingEntity)hitEntity).addPotionEffect(pe);
	}
}
@SuppressWarnings("deprecation")
class Beheading implements Effect{
	@Override
	public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {
		String name = hitEntity.getType().getName();
		int i = 0;
		ItemStack is;
		if(name.equalsIgnoreCase(EntityType.SKELETON.getName())&&((Skeleton)hitEntity).getSkeletonType()==SkeletonType.NORMAL){
			i=0;
		}else if(name.equalsIgnoreCase(EntityType.SKELETON.getName())&&((Skeleton)hitEntity).getSkeletonType()==SkeletonType.WITHER){
			i=1;
		}else if(name.equalsIgnoreCase(EntityType.ZOMBIE.getName())){
			i=2;
		}else if(name.equalsIgnoreCase(EntityType.CREEPER.getName())){
			i=4;
		}else if(hitEntity instanceof Player){
			i=3;
		}else{
			return;
		}
		is = new ItemStack(Material.SKULL_ITEM,1,(byte)i);
		if(hitEntity instanceof Player){
			ItemMeta im = is.getItemMeta();
			SkullMeta sm = (SkullMeta)im;
			sm.setOwner(((Player)hitEntity).getName());
		}
		if(Math.random()>0.01)return;
		w.dropItem(hitEntity.getLocation(), is);
	}
}
class Lightning extends PotionItemEffect{
	public Lightning(int percentage) {
		super(null, percentage);
	}
	@Override
	public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {
		if(Math.random()>percentage/100.0)return;
		w.strikeLightning(hitEntity.getLocation());
	}
}
class WitherArrows implements Effect{ @Override public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {} }
class ParalyzeArrows implements Effect{ @Override public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {} }
class BlockArmorEffect implements Effect{public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {};}
class Stomp implements Effect{public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {};}
class Piercing implements Effect{public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {};}
class Fireaspect implements Effect{public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {};}
class Sharpness implements Effect{public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {};}
class Knockback implements Effect{public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {};}