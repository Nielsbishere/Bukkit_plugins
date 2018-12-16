package net.nielsbwashere.src.SpecialMobs;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.server.v1_8_R1.AxisAlignedBB;
import net.minecraft.server.v1_8_R1.Entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;

public enum EntityTypes
{
    AGGRESSIVE_PIG("AggressivePig", 90, AggressivePig.class),
    GHAST("Ghast", 56, LevelGhast.class),
    AGGRESSIVE_PIGZOMBIE("AggressivePigZombie", 57, AggressivePigZombie.class),
    AGGRESSIVE_SPIDER("AggressivePigZombie", 52, AggressiveSpider.class);

    private EntityTypes(String name, int id, Class<? extends Entity> custom)
    {
        addToMaps(custom, name, id);
    }

  public static Entity spawnEntity(Entity entity, Location loc)
  {
	 int mobCount = countMobs(entity,loc);
	 if(mobCount>3){
		 entity.die();
		 return null;
	 }
     entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
     ((CraftWorld)loc.getWorld()).getHandle().addEntity(entity);
     return entity;
   }

    private static int countMobs(Entity entity, Location loc) {
    	int whereToCountX = 2, whereToCountZ = 2;
   	 int x=loc.getChunk().getX();
   	 int z=loc.getChunk().getZ();
   	 AxisAlignedBB aabb = AxisAlignedBB.a(x*16-16*whereToCountX, 0, z*16-16*whereToCountZ, x*16+16+16*whereToCountX, 255, z*16+16+16*whereToCountZ);
   	 final Class<? extends Entity> clss = entity.getClass();
   	 int count = 0;
   	for(Object e : entity.getWorld().getEntities(entity, aabb)){
   		if(!(e instanceof Entity)||!((Entity)e).getClass().equals(clss))continue;
   		count++;
   	}
	return count;
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void addToMaps(Class<?> clazz, String name, int id)
    {
        ((Map)getPrivateField("c", net.minecraft.server.v1_8_R1.EntityTypes.class, null)).put(name, clazz);
        ((Map)getPrivateField("d", net.minecraft.server.v1_8_R1.EntityTypes.class, null)).put(clazz, name);
        ((Map)getPrivateField("f", net.minecraft.server.v1_8_R1.EntityTypes.class, null)).put(clazz, Integer.valueOf(id));
    }
    public static Object getPrivateField(String fieldName, Class<?> clazz, Object object)
    {
        Field field;
        Object o = null;
        try
        {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        }
        catch(Exception e){e.printStackTrace();}
        return o;
    }
}