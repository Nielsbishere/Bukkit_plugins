package main.disguise.src;
import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityArmorStand;
import net.minecraft.server.v1_8_R1.EntityBat;
import net.minecraft.server.v1_8_R1.EntityBlaze;
import net.minecraft.server.v1_8_R1.EntityBoat;
import net.minecraft.server.v1_8_R1.EntityCaveSpider;
import net.minecraft.server.v1_8_R1.EntityChicken;
import net.minecraft.server.v1_8_R1.EntityCow;
import net.minecraft.server.v1_8_R1.EntityCreeper;
import net.minecraft.server.v1_8_R1.EntityEnderCrystal;
import net.minecraft.server.v1_8_R1.EntityEnderDragon;
import net.minecraft.server.v1_8_R1.EntityEnderman;
import net.minecraft.server.v1_8_R1.EntityEndermite;
import net.minecraft.server.v1_8_R1.EntityGhast;
import net.minecraft.server.v1_8_R1.EntityGiantZombie;
import net.minecraft.server.v1_8_R1.EntityGuardian;
import net.minecraft.server.v1_8_R1.EntityHorse;
import net.minecraft.server.v1_8_R1.EntityIronGolem;
import net.minecraft.server.v1_8_R1.EntityMagmaCube;
import net.minecraft.server.v1_8_R1.EntityMinecartChest;
import net.minecraft.server.v1_8_R1.EntityMinecartCommandBlock;
import net.minecraft.server.v1_8_R1.EntityMinecartFurnace;
import net.minecraft.server.v1_8_R1.EntityMinecartHopper;
import net.minecraft.server.v1_8_R1.EntityMinecartMobSpawner;
import net.minecraft.server.v1_8_R1.EntityMinecartRideable;
import net.minecraft.server.v1_8_R1.EntityMinecartTNT;
import net.minecraft.server.v1_8_R1.EntityMushroomCow;
import net.minecraft.server.v1_8_R1.EntityOcelot;
import net.minecraft.server.v1_8_R1.EntityPig;
import net.minecraft.server.v1_8_R1.EntityPigZombie;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EntityRabbit;
import net.minecraft.server.v1_8_R1.EntitySheep;
import net.minecraft.server.v1_8_R1.EntitySilverfish;
import net.minecraft.server.v1_8_R1.EntitySkeleton;
import net.minecraft.server.v1_8_R1.EntitySlime;
import net.minecraft.server.v1_8_R1.EntitySnowman;
import net.minecraft.server.v1_8_R1.EntitySquid;
import net.minecraft.server.v1_8_R1.EntityVillager;
import net.minecraft.server.v1_8_R1.EntityWitch;
import net.minecraft.server.v1_8_R1.EntityWither;
import net.minecraft.server.v1_8_R1.EntityWolf;
import net.minecraft.server.v1_8_R1.EntityZombie;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R1.World;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
public class Main extends JavaPlugin implements Listener{
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		disguiseAll();
	}
	public void onDisable() {
		saveConfig();
		undisguiseAll();
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.LOWEST)
	public void redisguise(PlayerJoinEvent e){
		for(Player p : getServer().getOnlinePlayers()){
			String entity = getConfig().getString(p.getName() + ".Disguise");
			if(entity==null||entity==""||entity.equalsIgnoreCase("None"))continue;
			disguise(p,e.getPlayer());
		}
	}
	public void disguise(Player disguised, Player normal){
		EntityPlayer disguisedp = ((CraftPlayer)disguised).getHandle();
		EntityPlayer ep = ((CraftPlayer)normal).getHandle();
		Entity e = entityFromName(getConfig().getString(disguised.getName() + ".Disguise"),ep.world);
		e.setPosition(disguised.getLocation().getX(), disguised.getLocation().getY(), disguised.getLocation().getZ());
		NBTTagCompound nbttc = e.getNBTTag();
		nbttc.setBoolean("NoAI",true);
		ep.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(disguisedp.getId()));
		try{
		Field id = Entity.class.getField("id");
		id.setAccessible(true);
		id.setInt(e, disguisedp.getId());
		}catch(Exception exception){exception.printStackTrace();}
        ep.playerConnection.sendPacket(new PacketPlayOutSpawnEntity(e,0));
	}
	private Entity entityFromName(String string, World world) {
		if(string.equalsIgnoreCase("CHICKEN"))return new EntityChicken(world);
		if(string.equalsIgnoreCase("COW"))return new EntityCow(world);
		if(string.equalsIgnoreCase("PIG"))return new EntityPig(world);
		if(string.equalsIgnoreCase("SHEEP"))return new EntitySheep(world);
		if(string.equalsIgnoreCase("SQUID"))return new EntitySquid(world);
		if(string.equalsIgnoreCase("VILLAGER"))return new EntityVillager(world);
		if(string.equalsIgnoreCase("BAT"))return new EntityBat(world);
		if(string.equalsIgnoreCase("MOOSHROOM"))return new EntityMushroomCow(world);
		if(string.equalsIgnoreCase("ENDERMAN"))return new EntityEnderman(world);
		if(string.equalsIgnoreCase("PIGMAN"))return new EntityPigZombie(world);
		if(string.equalsIgnoreCase("WOLF"))return new EntityWolf(world);
		if(string.equalsIgnoreCase("OCELOT"))return new EntityOcelot(world);
		if(string.equalsIgnoreCase("HORSE"))return new EntityHorse(world);
		if(string.equalsIgnoreCase("SNOWMAN"))return new EntitySnowman(world);
		if(string.equalsIgnoreCase("IRONGOLEM"))return new EntityIronGolem(world);
		if(string.equalsIgnoreCase("ENDERDRAGON"))return new EntityEnderDragon(world);
		if(string.equalsIgnoreCase("WITHER"))return new EntityWither(world);
		if(string.equalsIgnoreCase("BLAZE"))return new EntityBlaze(world);
		if(string.equalsIgnoreCase("CREEPER"))return new EntityCreeper(world);
		if(string.equalsIgnoreCase("SKELETON"))return new EntitySkeleton(world);
		if(string.equalsIgnoreCase("ZOMBIE"))return new EntityZombie(world);
		if(string.equalsIgnoreCase("SPIDER"))return new EntityHorse(world);
		if(string.equalsIgnoreCase("CAVESPIDER"))return new EntityCaveSpider(world);
		if(string.equalsIgnoreCase("GHAST"))return new EntityGhast(world);
		if(string.equalsIgnoreCase("MAGMACUBE"))return new EntityMagmaCube(world);
		if(string.equalsIgnoreCase("SLIME"))return new EntitySlime(world);
		if(string.equalsIgnoreCase("SILVERFISH"))return new EntitySilverfish(world);
		if(string.equalsIgnoreCase("WITCH"))return new EntityWitch(world);
		if(string.equalsIgnoreCase("WITHERSKELETON")){
			EntitySkeleton skel = new EntitySkeleton(world);
			skel.setSkeletonType(1);
			return skel;
		}
		if(string.equalsIgnoreCase("CAT")){
			EntityOcelot ocel = new EntityOcelot(world);
			ocel.setTamed(true);
			ocel.setCatType(Math.random()*3>2?3:Math.random()*3>1?2:1);
			return ocel;
		}
		if(string.equalsIgnoreCase("ENDERMITE"))return new EntityEndermite(world);
		if(string.equalsIgnoreCase("ENDERCRYSTAL"))return new EntityEnderCrystal(world);
		if(string.equalsIgnoreCase("ARMORSTAND"))return new EntityArmorStand(world);
		if(string.equalsIgnoreCase("BOAT"))return new EntityBoat(world);
		if(string.equalsIgnoreCase("GIANT"))return new EntityGiantZombie(world);
		if(string.equalsIgnoreCase("GUARDIAN"))return new EntityGuardian(world);
		if(string.equalsIgnoreCase("ELDERGUARDIAN")){
			EntityGuardian eg = new EntityGuardian(world);
			NBTTagCompound nbttc = eg.getNBTTag();
			nbttc.setBoolean("Elder",true);
			return eg;
		}
		if(string.equalsIgnoreCase("MINECARTCHEST"))return new EntityMinecartChest(world);
		if(string.equalsIgnoreCase("MINECART"))return new EntityMinecartRideable(world);
		if(string.equalsIgnoreCase("MINECARTSPAWNER"))return new EntityMinecartMobSpawner(world);
		if(string.equalsIgnoreCase("MINECARTHOPPER"))return new EntityMinecartHopper(world);
		if(string.equalsIgnoreCase("MINECARTFURNACE"))return new EntityMinecartFurnace(world);
		if(string.equalsIgnoreCase("MINECARTCOMMANDBLOCK"))return new EntityMinecartCommandBlock(world);
		if(string.equalsIgnoreCase("MINECARTTNT"))return new EntityMinecartTNT(world);
		if(string.equalsIgnoreCase("RABBIT"))return new EntityRabbit(world);
		return null;
	}
	@SuppressWarnings("deprecation")
	public void disguiseForAll(Player disguised){
		for(Player p : getServer().getOnlinePlayers()){
			disguise(disguised,p);
		}
	}
	public void undisguise(Player disguised, Player normal){
		EntityPlayer disguisedp = ((CraftPlayer)disguised).getHandle();
		EntityPlayer ep = ((CraftPlayer)normal).getHandle();
		ep.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(disguisedp.getId()));
		ep.playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(disguisedp));
	}
	@SuppressWarnings("deprecation")
	public void undisguiseForAll(Player disguised){
		for(Player p : getServer().getOnlinePlayers()){
			undisguise(disguised,p);
		}
	}
	@SuppressWarnings("deprecation")
	public void disguiseAll() {
		for(Player p : getServer().getOnlinePlayers()){
			String entity = getConfig().getString(p.getName() + ".Disguise");
			if(entity==null||entity==""||entity.equalsIgnoreCase("None"))continue;
			disguiseForAll(p);
		}
	}
	@SuppressWarnings("deprecation")
	public void undisguiseAll() {
			for(Player p : getServer().getOnlinePlayers()){
				String entity = getConfig().getString(p.getName() + ".Disguise");
				if(entity==null||entity==""||entity.equalsIgnoreCase("None"))continue;
				undisguiseForAll(p);
			}
	}
}
