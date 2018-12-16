package net.nielsbwashere.src.SpecialMobs;

import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.EntitySpider;
import net.minecraft.server.v1_8_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_8_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_8_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_8_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_8_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_8_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R1.World;

import org.bukkit.craftbukkit.v1_8_R1.util.UnsafeList;

public class AggressiveSpider extends EntitySpider {
		public AggressiveSpider(World world) {
			super(world);
			UnsafeList<?> goalB = (UnsafeList<?>)EntityTypes.getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
			UnsafeList<?> goalC = (UnsafeList<?>)EntityTypes.getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
			UnsafeList<?> targetB = (UnsafeList<?>)EntityTypes.getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
	        UnsafeList<?> targetC = (UnsafeList<?>)EntityTypes.getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
	        this.goalSelector.a(0, new PathfinderGoalFloat(this));
	        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, false));
	        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
	        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
	        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
	        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
	        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
	        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
	    }
}
