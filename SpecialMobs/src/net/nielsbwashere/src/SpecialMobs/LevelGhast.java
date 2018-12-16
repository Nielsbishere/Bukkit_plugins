package net.nielsbwashere.src.SpecialMobs;

import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R1.EntityGhast;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_8_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R1.World;

import org.bukkit.craftbukkit.v1_8_R1.util.UnsafeList;

public class LevelGhast extends EntityGhast {
		public LevelGhast(World world, int level) {
			super(world);
			UnsafeList<?> goalB = (UnsafeList<?>)EntityTypes.getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
			UnsafeList<?> goalC = (UnsafeList<?>)EntityTypes.getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
			UnsafeList<?> targetB = (UnsafeList<?>)EntityTypes.getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
	        UnsafeList<?> targetC = (UnsafeList<?>)EntityTypes.getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
	        this.goalSelector.a(0, new PathfinderGoalFloat(this));
	        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
	        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
	        Field f=null;
			try {
				f = getClass().getDeclaredField("a");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(f!=null){
	        f.setAccessible(true);
	        try {
				f.set(this, 10);
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
	    }
}
