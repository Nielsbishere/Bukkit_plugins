package net.nielsbwashere.UAS.AI;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.nielsbwashere.UAS.src.Core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
public class ZombieTrait extends Trait {
	public Core c = (Core) Bukkit.getPluginManager().getPlugin("Ultimate Apocalyptic Survival");
	public ZombieTrait() {
		super("ZombieTrait");
	}
	@Persist("ticks") public int ticks = 0;
	 @Persist("a") public boolean a=false;
	 @Persist("hasTarget") public boolean hasTarget=false;
        @SuppressWarnings("deprecation")
		@Override
        public void run() {
    		if(npc.isSpawned()){
    			ticks++;
    			if(!a){
    				LivingEntity el = npc.getBukkitEntity();
    				el.setMaxHealth(50.0+(c.getConfig().getInt("info.day")*5));
    				el.setHealth(50.0+(c.getConfig().getInt("info.day")*5)-5);
    				a=true;
    			}
    			Navigator nnpc = npc.getNavigator();
    			for(Entity e : npc.getBukkitEntity().getNearbyEntities(16, 8, 16)){
    				if(!(e instanceof Player)&&!(e instanceof Villager)){
    					continue;
    				}
    				if(hasTarget){
    					hasTarget = nnpc.getEntityTarget()!=null&&nnpc.getEntityTarget().getTarget()!=null&&!nnpc.getEntityTarget().getTarget().isDead();
    					continue;
    				}
    				if(!canSeeTarget(e)){
    					continue;
    				}
    					nnpc.setTarget(e,true);
    					hasTarget = true;
    					break;
    			}
    			hasTarget = nnpc.getEntityTarget()!=null&&nnpc.getEntityTarget().getTarget()!=null&&!nnpc.getEntityTarget().getTarget().isDead();
    			if(!hasTarget&&ticks>=15*20){
    				npc.getBukkitEntity().setHealth(0D);
    				npc.destroy();
    			}else if(hasTarget&&ticks>=60*20){
    				npc.getBukkitEntity().setHealth(0D);
    				npc.destroy();
    			}
    			if(hasTarget){
					npc.faceLocation(npc.getNavigator().getEntityTarget().getTarget().getLocation());
    			}
    		}
        }
    	private boolean canSeeTarget(Entity target) {
            return  npc.getEntity() instanceof LivingEntity ? ((LivingEntity) npc.getEntity())
                    .hasLineOfSight(target) : true;
        }

}