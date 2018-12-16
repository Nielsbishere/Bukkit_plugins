package net.nielsbwashere.UAS.AI;
import java.util.Random;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.nielsbwashere.UAS.src.Core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
public class HitmanTrait extends Trait {
	public Core c = (Core) Bukkit.getPluginManager().getPlugin("Ultimate Apocalyptic Survival");
	public HitmanTrait() {
		super("HitmanTrait");
	}	
	@Persist("owner") public String owner;
	@Persist("hasTarget") public boolean hasTarget = false;
	@Persist("isPlayerT") public boolean isPlayerT = false;
	@Persist("hasPickedRandom") public boolean hasPickedRandom=false;
	@Persist("hasNewHealth") public boolean hasNewHealth=false;
	@Persist("zI") public double zI = 0;
	@Persist("xI") public double xI = 0;
	@Persist("ticks") public int ticks=0;
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if(npc.isSpawned()&&ticks<10*60*20){
			if(!hasNewHealth){
				npc.getBukkitEntity().setMaxHealth(75D);
				npc.getBukkitEntity().setHealth(75D);
				hasNewHealth=true;
			}
			Navigator nnpc = npc.getNavigator();
			if(owner != null && c.getServer().getPlayer(owner)==null){
				npc.destroy();
				return;
			}
			if(owner == null){
				return;
			}
			Player p = c.getServer().getPlayer(owner);
			for(Entity e : p.getNearbyEntities(15, 5, 15)){
				if(!(e instanceof Monster) && !(e instanceof Slime)){
					continue;
				}
				if(hasTarget&&!isPlayerT){
					hasTarget = nnpc.getEntityTarget()!=null&&nnpc.getEntityTarget().getTarget()!=null&&!nnpc.getEntityTarget().getTarget().isDead()&&!(nnpc.getEntityTarget()instanceof Player);
					isPlayerT = hasTarget?(nnpc.getEntityTarget().getTarget() instanceof Player):false;
					continue;
				}
				if(!canSeeTarget(e)){
					hasTarget = nnpc.getEntityTarget()!=null&&nnpc.getEntityTarget().getTarget()!=null&&!nnpc.getEntityTarget().getTarget().isDead()&&!(nnpc.getEntityTarget()instanceof Player);
					isPlayerT = hasTarget?(nnpc.getEntityTarget().getTarget() instanceof Player):false;
					continue;
				}
					nnpc.setTarget(e,true);
					hasPickedRandom = false;
					hasTarget = nnpc.getEntityTarget()!=null&&nnpc.getEntityTarget().getTarget()!=null&&!nnpc.getEntityTarget().getTarget().isDead()&&!(nnpc.getEntityTarget()instanceof Player);
					isPlayerT = hasTarget?(nnpc.getEntityTarget().getTarget() instanceof Player):false;
					break;
			}
			if(!hasTarget&&!isPlayerT){
				if(!hasPickedRandom){
				hasPickedRandom = true;
				if((new Random()).nextBoolean()){
				xI = (1-(new Random()).nextInt(10)/((double)10))+1;	
				}else{
				xI = -(1-(new Random()).nextInt(10)/((double)10))+1;	
				}
				if((new Random()).nextBoolean()){
				zI = (1-(new Random()).nextInt(10)/((double)10))+1;	
				}else{
				zI = -(1-(new Random()).nextInt(10)/((double)10))+1;	
				}
				}
				nnpc.setTarget((Entity)p,false);
				hasTarget = nnpc.getEntityTarget()!=null&&nnpc.getEntityTarget().getTarget()!=null&&!nnpc.getEntityTarget().getTarget().isDead()&&!(nnpc.getEntityTarget()instanceof Player);
				isPlayerT = hasTarget?(nnpc.getEntityTarget().getTarget() instanceof Player):false;
			}
			if(hasTarget){
				if(npc.getNavigator().getEntityTarget()!=null&&npc.getNavigator().getEntityTarget().getTarget()!=null&&npc.getNavigator().getEntityTarget().getTarget().getLocation()!=null){
					npc.faceLocation(npc.getNavigator().getEntityTarget().getTarget().getLocation());	
				}
			}
		}
		if(ticks>=10*60*20){
			if(npc.isSpawned()){
				Navigator nnpc = npc.getNavigator();
				nnpc.cancelNavigation();
			}
		}
		if(ticks>=11*60*20){
			npc.destroy();
			if(c.getServer().getPlayer(owner)!=null){
				c.getServer().getPlayer(owner).sendMessage(ChatColor.DARK_RED + "Your hitman left you, since hitmen only help people for 10 minutes!");
			}
		}
	}
	private boolean canSeeTarget(Entity target) {
        return  npc.getEntity() instanceof LivingEntity ? ((LivingEntity) npc.getEntity())
                .hasLineOfSight(target) : true;
    }
}