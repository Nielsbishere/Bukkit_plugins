package nielsbwashere.customenchants.src;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.EnumParticle;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Core extends JavaPlugin implements Listener{
	
	public Enchant springs = new Enchant(this,"Springs", 3, 10, 30, Enchant.ARMOR, Enchant.boots, 1/48.0, new PotionArmorEffect(new PotionEffect(PotionEffectType.JUMP,40,0)), new PotionArmorEffect(new PotionEffect(PotionEffectType.JUMP,40,1)), new PotionArmorEffect(new PotionEffect(PotionEffectType.JUMP,40,2)));
	public Enchant running = new Enchant(this,"Running", 3, 10, 30, Enchant.ARMOR, Enchant.boots, 1/36.0, new PotionArmorEffect(new PotionEffect(PotionEffectType.SPEED,40,0)), new PotionArmorEffect(new PotionEffect(PotionEffectType.SPEED,40,1)), new PotionArmorEffect(new PotionEffect(PotionEffectType.SPEED,40,2)));
	
	public Enchant nightVision = new Enchant(this,"Night-Vision", 1, 20, 30, Enchant.ARMOR, Enchant.helmet, 1/64.0, new PotionArmorEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,40,0)));
	public Enchant feeding = new Enchant(this,"Feeding", 1, 25, 30, Enchant.ARMOR, Enchant.helmet, 1/256.0, new Feeding(this));
	
	//public Enchant Revulsion = new Enchant(this,"Revulsion", 3, 25, 30, Enchant.ARMOR, Enchant.all, 1/150.0, new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*5, 0)), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*10, 0)), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*20, 0)));
	public Enchant Iceman = new Enchant(this,"Freezing", 2, 25, 30, Enchant.ARMOR, Enchant.all, 1/250.0, new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*5, 0),20), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*5, 1),20));
	public Enchant Scorpion = new Enchant(this,"Scorpion", 4, 25, 30, Enchant.ARMOR, Enchant.all, 1/500.0, new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*3, 0),10), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*5, 0),20), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*5, 0),30), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*5, 1),25));
	public Enchant Intimidation = new Enchant(this,"Intimidation", 4, 25, 30, Enchant.ARMOR, Enchant.all, 1/350.0, new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*5, 0),10), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*5, 0),20), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*5, 0),30), new ArmorHitPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*5, 1),25));
	public Enchant MagmaSuit = new Enchant(this, "Inferno", 2, 29, 30, Enchant.ARMOR, Enchant.all, 1/1000.0, new Magmasuit(1), new Magmasuit(2));
	public Enchant Pacifist = new Enchant(this, "Pacifist", 3, 28, 30, Enchant.ARMOR, Enchant.all, 1/2500.0, new Pacifist(10), new Pacifist(15), new Pacifist(20));
	
	public Enchant blinding = new Enchant(this,"Dazzle", 3, 10, 20, Enchant.WEAPON, Enchant.all, 1/24.0, new PotionItemEffect(new PotionEffect(PotionEffectType.BLINDNESS,20*2,0),12), new PotionItemEffect(new PotionEffect(PotionEffectType.BLINDNESS,20*7,0),12), new PotionItemEffect(new PotionEffect(PotionEffectType.BLINDNESS,20*15,0),15));
	public Enchant poisoning = new Enchant(this,"Poisoning", 4, 25, 30, Enchant.WEAPON, Enchant.all, 1/64.0, new PotionItemEffect(new PotionEffect(PotionEffectType.POISON,20*1,0),15), new PotionItemEffect(new PotionEffect(PotionEffectType.POISON,20*3,0),20), new PotionItemEffect(new PotionEffect(PotionEffectType.POISON,20*5,0),25), new PotionItemEffect(new PotionEffect(PotionEffectType.POISON,20*3,1),15));
	public Enchant freezing = new Enchant(this,"Frost", 3, 10, 20, Enchant.WEAPON, Enchant.all, 1/48.0, new PotionItemEffect(new PotionEffect(PotionEffectType.SLOW,20*2,0),15), new PotionItemEffect(new PotionEffect(PotionEffectType.SLOW,20*2,1),25), new PotionItemEffect(new PotionEffect(PotionEffectType.SLOW,20*2,2),35));
	public Enchant Lifesteal = new Enchant(this,"Lifesteal", 3, 25, 30, Enchant.WEAPON, Enchant.all, 1/200.0, new Lifesteal(20), new Lifesteal(30), new Lifesteal(40));
	public Enchant Deathbringer = new Enchant(this,"Death-bringer", 2, 25, 30, Enchant.WEAPON, Enchant.all, 1/500.0, new Deathbringer(10), new Deathbringer(18));
	public Enchant Vampire = new Enchant(this,"Vampire", 3, 25, 30, Enchant.WEAPON, Enchant.all, 1/1500.0, new Vampire(10), new Vampire(15), new Vampire(20));
	public Enchant Wounding = new Enchant(this,"Wounding", 2, 20, 30, Enchant.WEAPON, Enchant.all, 1/200.0, new Wounding(), new Wounding());
	public Enchant Blocking = new Enchant(this,"Blocking", 1, 30, 30, Enchant.WEAPON, Enchant.all, 1/300.0, new Blocking());
	
	public Enchant Exploding = new Enchant(this,"Exploding", 1, 30, 30, Enchant.BOW, Enchant.all, 1/2000.0, new ExplodingArrows());
	public Enchant Poisoning = new Enchant(this,"Viper", 1, 30, 30, Enchant.BOW, Enchant.all, 1/200.0, new PoisonArrows());
	public Enchant Lightning = new Enchant(this,"Lightning", 2, 30, 30, Enchant.BOW, Enchant.all, 1/580.0, new LightningArrows(25), new LightningArrows(40));
	public Enchant Light = new Enchant(this,"Torch-arrows", 3, 30, 30, Enchant.BOW, Enchant.all, 1/350.0, new LightArrows(), new LightArrows(), new LightArrows());
	
	public int repeating;
	
	public boolean enchantWithoutPerm, forceCustomEnchant;
	public ConcurrentHashMap<String, Integer> stopList = new ConcurrentHashMap<String, Integer>();
	
	public ConcurrentHashMap<String, Boolean> lst = new ConcurrentHashMap<String,Boolean>();

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
	@Override
	public void onEnable() {
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
						for(Enchantment e : enchants){
							if(e.enchant.type!=Enchant.ARMOR)continue;
							Material t = is.getType();
							if((e.enchant.effect==Enchant.helmet&&(t!=Material.LEATHER_HELMET&&t!=Material.CHAINMAIL_HELMET&&t!=Material.GOLD_HELMET&&t!=Material.IRON_HELMET&&t!=Material.DIAMOND_HELMET))||
							(e.enchant.effect==Enchant.boots&&(t!=Material.LEATHER_BOOTS&&t!=Material.CHAINMAIL_BOOTS&&t!=Material.GOLD_BOOTS&&t!=Material.IRON_BOOTS&&t!=Material.DIAMOND_BOOTS)))continue;
							Effect eff = e.enchant.onEnchantActivated.length<=e.level?e.enchant.onEnchantActivated[e.enchant.onEnchantActivated.length-1]:e.enchant.onEnchantActivated[e.level];
							if(!(eff instanceof PotionArmorEffect))continue;
							((PotionArmorEffect)eff).activate(p.getWorld(), p, null, null, e);
						}
					}
				}
			}}, 0l, 19);
		boolean init = getConfig().getBoolean("Settings.Initialized");
		if(!init){
			getConfig().set("Settings.Initialized",true);
			getConfig().set("Settings.peopleWithoutPermissionCanEnchant",true);
			getConfig().set("Settings.forceCustomEnchant",false);
			getConfig().set("Settings.ExplosiveArrows.FireExplosion",true);
			getConfig().set("Settings.ExplosiveArrows.Explosion",true);
			getConfig().set("Settings.TorchArrowPlacesTorch",true);
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
	public void onRight(PlayerInteractEvent e){
		if(e.getAction()!=Action.RIGHT_CLICK_AIR&&e.getAction()!=Action.RIGHT_CLICK_BLOCK)return;
		Player p = e.getPlayer();
		if(p.getItemInHand()==null||p.getItemInHand().getType()==Material.AIR)return;
		Enchantment[] enchants = Enchant.getFromStack(p.getItemInHand());
		if(enchants==null||enchants.length==0)return;
		for(Enchantment ench : enchants){
			Effect eff = ench.enchant.onEnchantActivated.length<=ench.level?ench.enchant.onEnchantActivated[ench.enchant.onEnchantActivated.length-1]:ench.enchant.onEnchantActivated[ench.level];
			if(!(eff instanceof Blocking))continue;
			if(p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)&&getPotion(p,PotionEffectType.DAMAGE_RESISTANCE).getAmplifier()==1)p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 19, 0));
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void shooting(EntityShootBowEvent e){
		if(!(e.getEntity() instanceof Player))return;
		Player p = (Player)e.getEntity();
		if(p.getItemInHand()==null||p.getItemInHand().getType()==Material.AIR)return;
		Enchantment[] enchants = Enchant.getFromStack(p.getItemInHand());
		if(enchants==null||enchants.length==0)return;
		for(Enchantment ench : enchants){
			Effect eff = ench.enchant.onEnchantActivated.length<=ench.level?ench.enchant.onEnchantActivated[ench.enchant.onEnchantActivated.length-1]:ench.enchant.onEnchantActivated[ench.level];
			if(eff instanceof ExplodingArrows)
			e.getProjectile().setMetadata("explode", new FixedMetadataValue(this, true));
			if(eff instanceof PoisonArrows)
			e.getProjectile().setMetadata("poison", new FixedMetadataValue(this, ench.level));
			if(eff instanceof LightningArrows)
			e.getProjectile().setMetadata("lightning", new FixedMetadataValue(this, ench.level));
			if(eff instanceof LightArrows)
			e.getProjectile().setMetadata("light", new FixedMetadataValue(this, ench.level));
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPHit(EntityDamageByEntityEvent e){
		if(!(e.getEntity() instanceof Player))return;
		Player p = (Player)e.getEntity();
		Entity hit = e.getDamager();
		if(p.getInventory().getArmorContents()==null||p.getInventory().getArmorContents().length==0)return;
		for(ItemStack is : p.getInventory().getArmorContents()){
		if(is==null||is.getType()==Material.AIR)continue;
		Enchantment[] enchants = Enchant.getFromStack(is);
		if(enchants==null||enchants.length==0)continue;
		for(Enchantment ench : enchants){
			Effect eff = ench.enchant.onEnchantActivated.length<=ench.level?ench.enchant.onEnchantActivated[ench.enchant.onEnchantActivated.length-1]:ench.enchant.onEnchantActivated[ench.level];
			if(!(eff instanceof ArmorHitPotionEffect))continue;
			((ArmorHitPotionEffect)eff).activate(p.getWorld(), p, null, hit, ench);
		}
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void hitting(EntityDamageByEntityEvent e){
		if(!(e.getDamager() instanceof Arrow))return;
		Arrow a = (Arrow) e.getDamager();
		final Location l = e.getEntity().getLocation();
		if(a.hasMetadata("explode"))a.getWorld().createExplosion(l.getX(),l.getY(),l.getZ(), 3F, getConfig().getBoolean("Settings.ExplosiveArrows.FireExplosion"), getConfig().getBoolean("Settings.ExplosiveArrows.Explosion"));
		if(a.hasMetadata("poison")&&e.getEntity() instanceof LivingEntity)((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON,20*3,a.getMetadata("poison").get(0).asInt()-1));
		if(a.hasMetadata("lightning")&&Math.random()<=a.getMetadata("lightning").get(0).asInt()*15/100.0+0.1)a.getWorld().strikeLightning(e.getEntity().getLocation());
		if(a.hasMetadata("light")){
			e.getEntity().setFireTicks(e.getEntity().getFireTicks()+20+40*a.getMetadata("light").get(0).asInt());
			if(a.getWorld().getBlockAt(e.getEntity().getLocation().add(0,-1,0)).getType()!=Material.AIR&&a.getWorld().getBlockAt(e.getEntity().getLocation()).getType()==Material.AIR&&getConfig().getBoolean("Settings.TorchArrowPlacesTorch")){
			a.getWorld().getBlockAt(e.getEntity().getLocation()).setType(Material.TORCH);
			final World w = a.getWorld();
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				@Override
				public void run() {
					if(w.getBlockAt(l).getType()==Material.TORCH)w.getBlockAt(l).setType(Material.AIR);
				}},5*20);
			}
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void hitting(ProjectileHitEvent e){
		if(!(e.getEntity() instanceof Arrow))return;
		Arrow a = (Arrow) e.getEntity();
		final Location l = e.getEntity().getLocation();
		if(a.hasMetadata("explode"))a.getWorld().createExplosion(l.getX(),l.getY(),l.getZ(), 3F, getConfig().getBoolean("Settings.ExplosiveArrows.FireExplosion"), getConfig().getBoolean("Settings.ExplosiveArrows.Explosion"));
		if(a.hasMetadata("lightning")&&Math.random()<=a.getMetadata("lightning").get(0).asInt()*15/100.0+0.1)a.getWorld().strikeLightning(e.getEntity().getLocation());
		if(a.hasMetadata("light")){
			if(a.getWorld().getBlockAt(e.getEntity().getLocation().add(0,-1,0)).getType()!=Material.AIR&&a.getWorld().getBlockAt(e.getEntity().getLocation()).getType()==Material.AIR&&getConfig().getBoolean("Settings.TorchArrowPlacesTorch")){
			a.getWorld().getBlockAt(e.getEntity().getLocation()).setType(Material.TORCH);
			final World w = a.getWorld();
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				@Override
				public void run() {
					if(w.getBlockAt(l).getType()==Material.TORCH)w.getBlockAt(l).setType(Material.AIR);
				}},5*20);
			}
		}
		if(a.hasMetadata("explode")||a.hasMetadata("lightning")||a.hasMetadata("light"))a.remove();
	}
	private PotionEffect getPotion(Player p, PotionEffectType pet) {
		for(PotionEffect pe : p.getActivePotionEffects()){
			if(pe.getType()==pet)return pe;
		}
		return null;
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
	public void onEntityHit3(EntityDamageByEntityEvent e){
		if(e.getDamager()==null||!(e.getDamager() instanceof Player))return;
		if(!(e.getEntity() instanceof LivingEntity))return;
		Player p = (Player)e.getDamager();
		if(p.getItemInHand()==null||p.getItemInHand().getType()==Material.AIR)return;
		Enchantment[] enchants = Enchant.getFromStack(p.getItemInHand());
		if(enchants==null||enchants.length==0)return;
		for(Enchantment ench : enchants){
			Effect eff = ench.enchant.onEnchantActivated.length<=ench.level?ench.enchant.onEnchantActivated[ench.enchant.onEnchantActivated.length-1]:ench.enchant.onEnchantActivated[ench.level];
			if(!(eff instanceof Vampire))continue;
			if(Math.random()>((Vampire)eff).percentage/100.0)continue;
			if(getHealth(p)+getDamage(e)/2<=getMaxHealth(p))p.setHealth(getHealth(p)+getDamage(e)/2);
			else p.setHealth(getMaxHealth(p));
		}
	}
	private double getDamage(EntityDamageByEntityEvent e) {
		Method[] met = e.getClass().getMethods();
		for(Method m : met){
			if(m.getName().equals("getDamage")&&m.getReturnType()==double.class)
				try {
					return (double) m.invoke(e);
				} catch (Exception ex) {
					System.out.println("Couldn't get the health of the entity!");
				}
		}
		return 0;
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEnchant(EnchantItemEvent e){
		Player p = e.getEnchanter();
		if(!(enchantWithoutPerm||p.isOp()||p.hasPermission("MythicalEnchants.Enchant")))return;
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
					p.sendMessage(ChatColor.GOLD + "Well done! You got " + enchantment.enchant.displayName);
				}
			}
		}
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if(label.equalsIgnoreCase("addCustomEnchant")){
			if((!sender.isOp()&&!sender.hasPermission("MythicalEnchants.Commands.addCustomEnchant"))||!(sender instanceof Player)){
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
		}else if(label.equalsIgnoreCase("MythicalEnchants")){
			if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "You are not an OP so you can't use /MythicalEnchants");
				return false;
			}
			if(args.length==1&&args[0].equalsIgnoreCase("reset")){
				getConfig().set("Settings", null);
				getConfig().set("Enchantments", null);
				saveConfig();
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "That argument is not initialized!");
			}
		}else if(label.equalsIgnoreCase("spawnStructure")){
			if(!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "You are not an OP so you can't use /spawnStructure");
				return false;
			}
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "You are not a player so you can't spawn a structure!");
				return false;
			}
			boolean debug=false;
			if(!debug){
				sender.sendMessage(ChatColor.DARK_RED + "You can't use this while not in debug mode!");
				return false;
			}
			Player p = (Player)sender;
			Tunnel t = new Tunnel(p.getWorld());
			t.generate(p.getLocation().getBlockX(),p.getLocation().getBlockY(),p.getLocation().getBlockZ());
		}
		return super.onCommand(sender, command, label, args);
	}
}

class Enchant{
	public static final int ARMOR=0,WEAPON=1,BOW=2,BOOK=3,ALL=4,TOOL=5,SHEAR=6,FISHING_ROD=7,SPECIFIEDITEMWITHID=8;
	public static final int all=0,boots=1,leggings=2,chestplate=3,helmet=4;
	public static final List<Enchant> enchantmentList = new ArrayList<Enchant>();
	public String name;
	public int maxLevel, effect;
	public int minExpLevel, maxExpLevel,type;
	public double chance;
	public Effect[] onEnchantActivated;
	public String displayName;
	public boolean gottenFromEnchantTable;
	public Enchant(Core c, String name, int maxLevel, int minXP, int maxXP, int type, int itemWithEffect, double chance, Effect... activated){
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
	public static void activate(Enchantment e, Player p, Block block, Entity entity){
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
		if(p.hasPotionEffect(pe.getType()))p.removePotionEffect(pe.getType());
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
		if(Math.random()>(percentage/100.0))return;
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
class Vampire implements Effect{
	public int percentage;
	public Vampire(int perc) {
		percentage=perc;
	}
	@Override
	public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {
	}
}
class Blocking implements Effect{
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
		if(Math.random()<0.05+e.level/10){
			final Core c = (Core)Bukkit.getPluginManager().getPlugin("MythicalEnchants");
			final String uuid = hitEntity.getUniqueId().toString();
			int id = c.getServer().getScheduler().scheduleSyncRepeatingTask(c, new Runnable(){
				int ranTimes = 0;
				int maxRun = (int) (Math.random()*5+3);
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
						EntityLiving el = ((CraftLivingEntity)le).getHandle();
						el.getWorld().addParticle(EnumParticle.REDSTONE, el.locX, el.locY, el.locZ, 0.0D, 0.0D, 0.0D, 100);
					}
				}}, 20, 20*2);
			if(!c.stopList.containsKey(uuid))
			c.stopList.put(uuid, id);
		}
	}
}
class ExplodingArrows implements Effect{@Override public void activate(World w, Player p, Block hitBlock, Entity hitEntity,Enchantment e){}}
class LightningArrows implements Effect{int percentage;public LightningArrows(int i) {percentage=i;	}
@Override public void activate(World w, Player p, Block hitBlock, Entity hitEntity,Enchantment e){}}
class PoisonArrows implements Effect{@Override public void activate(World w, Player p, Block hitBlock, Entity hitEntity,Enchantment e){}}
class LightArrows implements Effect{@Override public void activate(World w, Player p, Block hitBlock, Entity hitEntity,Enchantment e){}}
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
class Pacifist extends ArmorHitPotionEffect{
	public int percentage;
	public Pacifist(int percentage) {
		super(null,percentage);
	}
	@Override
	public void activate(World w, Player p, Block hitBlock, Entity hitEntity,Enchantment e) {
		if(Math.random()>percentage/100.0)return;
		p.setHealth(Core.getHealth(p)+1D<=Core.getMaxHealth(p)?Core.getHealth(p)+1D:Core.getMaxHealth(p));
	}
}
class Magmasuit extends ArmorHitPotionEffect{
	public Magmasuit(int level) {
		super(null,level);
	}
	@Override
	public void activate(World w, Player p, Block hitBlock, Entity hitEntity,Enchantment e) {
		if(hitEntity==null||!(hitEntity instanceof LivingEntity))return;
		LivingEntity le = (LivingEntity)hitEntity;
		le.setFireTicks(le.getFireTicks()+20*5*percentage);
	}
}
class Feeding extends PotionArmorEffect{
	public Core c;
	public Feeding(Core c) {
		super(null);
		this.c=c;
	}
	@Override
	public void activate(World w, Player p, Block hitBlock, Entity hitEntity, Enchantment e) {
		if(!c.lst.containsKey(p.getUniqueId().toString())){
			c.lst.put(p.getUniqueId().toString(),true);
			final Player pfinal=p;
			final Enchantment ench = e;
			final UUID uuid = pfinal.getUniqueId();
			Bukkit.getScheduler().scheduleSyncDelayedTask(c, new Runnable(){
				@Override
				public void run() {
					if(c.getServer().getPlayer(uuid)!=null){
						if(pfinal.getInventory().getArmorContents()==null||pfinal.getInventory().getArmorContents().length==0){
							c.lst.remove(uuid);
							return;
						}
						boolean hasEnch=false;
						outerloop: for(ItemStack is : pfinal.getInventory().getArmorContents()){
							if(is==null||is.getType()==Material.AIR)continue;
							Enchantment[] enchants = Enchant.getFromStack(is);
							if(enchants==null)continue;
							for(Enchantment enc : enchants){
								if(ench.enchant==enc.enchant){
									hasEnch=true;
									break outerloop;
								}
							}
						}
						if(!hasEnch){
							c.lst.remove(uuid);
							return;
						}
						pfinal.setFoodLevel(pfinal.getFoodLevel()<20?pfinal.getFoodLevel()+1:20);
						c.lst.remove(uuid.toString());
					}else{
						c.lst.remove(uuid.toString());
					}
				}},20*8);
		}
	}
	
}
abstract class Structure{
	public World w;
	public Structure(World w) {
		this.w=w;
	}
	abstract void generate(int x, int y, int z);
}

class Tunnel extends Structure{
	public Tunnel(World w) {
		super(w);
	}
	
	public static final int 
	FACING_TOWARDS_POSITIVE_Z=0,FACING_TOWARDS_POSITIVE_X=1,
	FACING_TOWARDS_NEGATIVE_Z=2,FACING_TOWARDS_NEGATIVE_X=3;
	
	@Override
	void generate(int i, int j, int k) {
		generateLib(i,j,k);
		int length = (int) (Math.random()*5+Math.random()*5+Math.random()*5+6);
		generateTunnel(i, j, k+10, length, Tunnel.FACING_TOWARDS_POSITIVE_Z);
		generateTunnel(i, j-4, k+10, length, Tunnel.FACING_TOWARDS_POSITIVE_Z);
		generateTunnel(i, j-19, k+10, length, Tunnel.FACING_TOWARDS_POSITIVE_Z);
		length = (int) (Math.random()*5+Math.random()*5+Math.random()*5+6);
		generateTunnel(i, j, k-10, length, Tunnel.FACING_TOWARDS_NEGATIVE_Z);
		generateTunnel(i, j-4, k-10, length, Tunnel.FACING_TOWARDS_NEGATIVE_Z);
		generateTunnel(i, j-19, k-10, length, Tunnel.FACING_TOWARDS_NEGATIVE_Z);
		length = (int) (Math.random()*5+Math.random()*5+Math.random()*5+6);
		generateTunnel(i-10, j, k, length, Tunnel.FACING_TOWARDS_NEGATIVE_X);
		generateTunnel(i-10, j-4, k, length, Tunnel.FACING_TOWARDS_NEGATIVE_X);
		generateTunnel(i-10, j-19, k, length, Tunnel.FACING_TOWARDS_NEGATIVE_X);
		length = (int) (Math.random()*5+Math.random()*5+Math.random()*5+6);
		generateTunnel(i+10, j, k, length, Tunnel.FACING_TOWARDS_POSITIVE_X);
		generateTunnel(i+10, j-4, k, length, Tunnel.FACING_TOWARDS_POSITIVE_X);
		generateTunnel(i+10, j-19, k, length, Tunnel.FACING_TOWARDS_POSITIVE_X);
	}
	void generateTunnel(int i, int j, int k, int length, int facingDir){
		for(int x=i-2;x<=i+2;x+=4){
			for(int y=j;y>j-4;y--){
				if(facingDir==Tunnel.FACING_TOWARDS_POSITIVE_Z)
					set(x,y,k+length*4+4,Material.LOG,(byte)1);
				else if(facingDir==Tunnel.FACING_TOWARDS_NEGATIVE_Z)
						set(x,y,k-length*4-4,Material.LOG,(byte)1);
				else if(facingDir==Tunnel.FACING_TOWARDS_NEGATIVE_X)
					set(i-length*4-4,y,k+(x-i),Material.LOG,(byte)1);
				else if(facingDir==Tunnel.FACING_TOWARDS_POSITIVE_X)
					set(i+length*4+4,y,k+(x-i),Material.LOG,(byte)1);
			}
		}
		for(int z=k;z<k+length*4+5;z++){
		for(int x=i-1;x<=i+1;x++){
			for(int y=j;y>=j-3;y-=3){
				if(x==i){
					if(y==j)set(getX(i,x,k,z,facingDir),y,getZ(i,x,k,z,facingDir),Material.WOOD_STEP,(byte)9);
					else if(y==j-3)set(getX(i,x,k,z,facingDir),y,getZ(i,x,k,z,facingDir),Material.WOOD_STEP,(byte)1);
					else set(getX(i,x,k,z,facingDir),y,getZ(i,x,k,z,facingDir),Material.AIR,(byte)0);
				}
				else{
					if(y==j)set(getX(i,x,k,z,facingDir),y,getZ(i,x,k,z,facingDir),Material.WOOD_STEP,(byte)10);
					else if(y==j-3)set(getX(i,x,k,z,facingDir),y,getZ(i,x,k,z,facingDir),Material.WOOD_STEP,(byte)2);
					else set(getX(i,x,k,z,facingDir),y,getZ(i,x,k,z,facingDir),Material.AIR,(byte)0);
				}
					
			}
		}
		for(int x=i-2;x<=i+2;x+=4){
			for(int y=j;y>=j-3;y--){
				if((z-k)%4==0){
					set(getX(i,x,k,z,facingDir),y,getZ(i,x,k,z,facingDir),Material.LOG,(byte)1);
					continue;
				}
				if(y==j||y==j-3)set(getX(i,x,k,z,facingDir),y,getZ(i,x,k,z,facingDir),Material.WOOD,(byte)2);
				else set(getX(i,x,k,z,facingDir),y,getZ(i,x,k,z,facingDir),Material.BOOKSHELF,(byte)0);
			}
		}
		}
	}
	private int getZ(int i, int x, int k, int z, int facingDir){
		return facingDir==Tunnel.FACING_TOWARDS_NEGATIVE_Z?k-(z-k):facingDir==Tunnel.FACING_TOWARDS_POSITIVE_Z?z:k+(x-i);
	}
	private int getX(int i, int x, int k, int z, int facingDir){
		return facingDir==Tunnel.FACING_TOWARDS_POSITIVE_X?i+(z-k):facingDir==Tunnel.FACING_TOWARDS_NEGATIVE_X?i-(z-k):x;
	}
	void generateLib(int i, int j, int k){
		int xStart = i-10;
		int zStart = k-10;
		int xEnd = i+10;
		int zEnd = k+10;
		for(int x=i-1;x<=i+1;x++){
			for(int z=k-1;z<=k+1;z++){
				set(x,j+1,z,Material.SEA_LANTERN,(byte)0);
			}
		}
		for(int x=xStart;x<=xEnd;x++){
			for(int z=zStart;z<=zEnd;z++){
				for(int y=j;y>j-23;y--){
					setLibRoof(x,y,z,i,j,k);
				}
			}	
		}
	}
	
	@SuppressWarnings("unused")
	private void setLibRoof(int x, int y, int z, int i, int j, int k) {
		int movX = x-i;
		int movY = y-j;
		int movZ = z-k;
		int mX = movX<0?-movX:movX;
		int mY = movY<0?-movY:movY;
		int mZ = movZ<0?-movZ:movZ;
		if(((mX==6||mX==2)&&mZ==10)||((mZ==6||mZ==2)&&mX==10)||(mX==6&&mZ==6)||(y<=j-23+6&&mX==10&&mZ==10)){
			set(x,y,z,Material.LOG,(byte)1);
			return;
		}
		if((y==j-7&&(mX>=7&&mZ>=7&&mX!=10&&mZ!=10))||(y<j-7&&((mX>=3&&mZ==10)||(mX==10&&mZ>=3))&&!(mX==10&&mZ==10))){
			set(x,y,z,Material.WOOD,(byte)2);
			if((y<j-8&&y>=j-15)||(y<j-17&&y>=j-21))set(x,y,z,Material.BOOKSHELF);
			return;
		}
		
		if((x==i||z==k)&&y==j&&!(x>=i-1&&x<=i+1&&z>=k-1&&z<=k+1))set(x,y,z,Material.WOOD_STEP,(byte)9);
		else if(((movX>=-6&&movX<=6&&(movZ==-10||movZ==10))||(movZ>=-6&&movZ<=6&&(movX==-10||movX==10))||
				(mX==6&&mZ>=6)||(mX>=6&&mZ==6))&&movY>-7)set(x,y,z,Material.WOOD,(byte)2);
		else if(y==j&&!(x>=i-1&&x<=i+1&&z>=k-1&&z<=k+1)&&!(movZ<-6&&movX<-6)&&!(movZ<-6&&movX>6)
				&&!(movZ>6&&movX<-6)&&!(movZ>6&&movX>6))set(x,y,z,Material.WOOD_STEP,(byte)10);
		else set(x,y,z,Material.AIR);
	}
	@SuppressWarnings("deprecation")
	private void set(int x, int y, int z, Material wood, byte... b) {
		w.getBlockAt(new Location(w,x,y,z)).setType(wood);
		if(b.length==1)w.getBlockAt(new Location(w,x,y,z)).setData(b[0]);
	}
}