package niels.b.was.here;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class scheduledTasks implements Runnable {
	public Base base;
	public scheduledTasks(Base b){
		this.base = b;
	}
	public void run() {
		for(Player p : base.getServer().getOnlinePlayers()){
			if(base.containsPlayer(p)&&!Base.underworld.containsKey(p.getName())){
				Base.underworld.put(p.getName(), p);
			}
		}
		for(Player p : base.getServer().getOnlinePlayers()){
			if(base.ghostPlayer(p)&&!Base.ghosts.containsKey(p.getName())){
				Base.ghosts.put(p.getName(), p);
			}
		}
		if(Base.timeInSeconds<=10&&Base.timeInSeconds > 0){
			note();
		}
		if(Base.timeInSeconds == 1){
			if(base.canStart){
				for(Player p :  base.getServer().getOnlinePlayers()){
					boolean hasStarted = base.getConfig().getBoolean("Escape.HasStarted");
					boolean hasStarted2 = base.getConfig().getBoolean("Escape.HasStarted2");
					if(Base.underworld.containsValue(p)||base.containsPlayer(p)){
						if(!hasStarted){
						p.sendMessage(ChatColor.GOLD +"[Escape] " + ChatColor.GREEN + "Game started!");
						p.setHealth(20);
						p.setFoodLevel(20);
						int x = base.getConfig().getInt("Escape.Arena.X");
						int y = base.getConfig().getInt("Escape.Arena.Y");
						int z = base.getConfig().getInt("Escape.Arena.Z");
						String worldname = base.getConfig().getString("Escape.Arena.World");
						World world = base.getServer().getWorld(worldname);
						p.teleport(new Location(world,x,y,z));
						Base.timeInSeconds = 5;
						}
						if(!hasStarted2&&hasStarted){
							p.setHealth(20D);
							p.setFoodLevel(20);
							String perk = base.getConfig().getString(p.getName() + ".SelectedPerk");
							if(perk==null)perk="None";
							int respawns = base.getConfig().getInt(p.getName() + ".Respawns");
							if(perk=="respawn"&&respawns!=1){
							base.getConfig().set(p.getName() + ".Respawns", 1);
							base.saveConfig();
							}
						}
					}
				}
				boolean hasStarted = base.getConfig().getBoolean("Escape.HasStarted");
				boolean hasStarted2 = base.getConfig().getBoolean("Escape.HasStarted2");
				if(!hasStarted){
					base.getConfig().set("Escape.HasStarted", true);
					base.saveConfig();
				}
				if(!hasStarted2&&hasStarted){
					base.getConfig().set("Escape.HasStarted2", true);
					base.saveConfig();
				}
			}else{
				for(Player p :  base.getServer().getOnlinePlayers()){
					if(Base.underworld.containsValue(p)||base.containsPlayer(p)){
						p.sendMessage(ChatColor.GOLD +"[Escape] " + ChatColor.GREEN + "Not enough players!");
						p.setHealth(20);
						p.setFoodLevel(20);
					}
				}
				reset();
				Base.timeInSeconds=10;
				base.canStart=Base.underworld.size()>=Base.minPlayers;
			}
		}
		if(Base.timeInSeconds == 0){
			for(Player p :  base.getServer().getOnlinePlayers()){
				if(Base.underworld.containsValue(p)||base.containsPlayer(p)){
			String perk = base.getConfig().getString(p.getName() + ".SelectedPerk");
			if(perk==null)perk="None";
			if(perk.equalsIgnoreCase("speed")&&!p.getActivePotionEffects().contains(PotionEffectType.SPEED)){
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 0));
					}else if(perk.equalsIgnoreCase("vision")&&!p.getActivePotionEffects().contains(PotionEffectType.NIGHT_VISION)){
						p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 10));
					}else if(perk.equalsIgnoreCase("ghost")&&!p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY)){
						p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 10));
					}
				}
			}
			for(Player p :  base.getServer().getOnlinePlayers()){
				if(Base.ghosts.containsValue(p)||base.ghostPlayer(p)){
					for(PotionEffect pe : p.getActivePotionEffects()){
						if(pe.getType()==PotionEffectType.SPEED){
							p.removePotionEffect(pe.getType());
						}else if(pe.getType()==PotionEffectType.INVISIBILITY){
							p.removePotionEffect(pe.getType());
						}else if(pe.getType()==PotionEffectType.NIGHT_VISION){
							p.removePotionEffect(pe.getType());
						}
					}
			}
		}
		}
		if(Base.timeInSeconds>0){
			Base.timeInSeconds--;
		}
		changeXp();
		for(Player p :  base.getServer().getOnlinePlayers()){
			if(Base.underworld.containsValue(p)||base.containsPlayer(p)){
				p.setFoodLevel(20);
			}
		}
			for(Player p :  base.getServer().getOnlinePlayers()){
				if(Base.underworld.containsValue(p)||base.containsPlayer(p)){
						if(p.getLocation().getBlockY()>=20){
							boolean started1 = base.getConfig().getBoolean("Escape.HasStarted");
							boolean started2 = base.getConfig().getBoolean("Escape.HasStarted2");
							boolean started = (started1 == started2 == true);
							if(started){
						p.damage(20000D);
						int gems = base.getConfig().getInt(p.getName()+".Gems");
						int a = 1;
						String perk = base.getConfig().getString(p.getName() + ".SelectedPerk");
						if(perk==null)perk="None";
						if(perk.equalsIgnoreCase("double")){
								a = 2;
								}
						base.getConfig().set(p.getName() + ".Gems", gems+(15*a));
						base.saveConfig();
						p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "Beating the game: +"+ 15*a +"");
							}
						}
				}
				
			}
	}
	
	private void note() {
		for(final Player p :  base.getServer().getOnlinePlayers()){
			if(Base.underworld.containsValue(p)||base.containsPlayer(p)){
				final Location l = p.getLocation();
				new Thread(
				new Runnable()
							{
								public void run() {
									try{
										for(int i=0;i<Base.underworld.size();i++){
											p.playSound(l, Sound.CLICK, 10, 1);
											Thread.sleep(450);
										}
									}catch(Exception e){
										e.printStackTrace();
									}
								}
							}
						).start();
			}
		}
	}
	private void reset() {
		for(Player p :  base.getServer().getOnlinePlayers()){
			if(Base.underworld.containsValue(p)||base.containsPlayer(p)){
				p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 10, 1);
			}
		}
	}
	public void changeXp(){
		for(Player p :  base.getServer().getOnlinePlayers()){
			if(Base.underworld.containsValue(p)||base.containsPlayer(p)){
				if(Base.underworld.size()>=Base.minPlayers){
				p.setLevel(Base.timeInSeconds);
				}else{
					if(Base.timeInSeconds>0){
						p.setLevel(Base.timeInSeconds);
					}
				}
			}
		}
	}
}
