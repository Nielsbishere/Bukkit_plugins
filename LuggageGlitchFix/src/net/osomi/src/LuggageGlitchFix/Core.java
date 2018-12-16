package net.osomi.src.LuggageGlitchFix;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
public class Core extends JavaPlugin implements Listener{
@Override
public void onEnable() {
	Bukkit.getPluginManager().registerEvents(this,this);
	if(!getConfig().getBoolean("Settings.hasInitialized")){
		getConfig().set("LuggageId",-12817);
		getConfig().set("MobSpawner",619);
		getConfig().set("MobSpawnerMeta",9);
		getConfig().set("Settings.hasInitialized",true);
		saveConfig();
	}
}
@SuppressWarnings("deprecation")
@EventHandler
public void onSpawn(EntitySpawnEvent e){
	if(e.getEntityType().getTypeId()!=getConfig().getInt("LuggageId")&&!hasEquipment(e.getEntity()))return;
	for(int i=-5;i<6;i++){
		for(int j=-4;j<5;j++){
			for(int k=-5;k<6;k++){
				Block b = e.getEntity().getWorld().getBlockAt(e.getEntity().getLocation().getBlockX()+i, e.getEntity().getLocation().getBlockY()+j, e.getEntity().getLocation().getBlockZ()+k);
				if(b.getData()==getConfig().getInt("MobSpawnerMeta")&&b.getTypeId()==getConfig().getInt("MobSpawner")){
					if(e.getEntityType().getTypeId()==getConfig().getInt("LuggageId"))
					e.setCancelled(true);
					else{
						LivingEntity le = (LivingEntity) e.getEntity();
						le.getEquipment().setBoots(null);
						le.getEquipment().setHelmet(null);
						le.getEquipment().setLeggings(null);
						le.getEquipment().setChestplate(null);
						le.getEquipment().setItemInHand(null);
					}
					return;
				}
			}	
		}	
	}
}
private boolean hasEquipment(Entity entity) {
	return entity instanceof LivingEntity && isEquiped(((LivingEntity)entity).getEquipment());
}
private boolean isEquiped(EntityEquipment equipment) {
	return !Null(equipment.getItemInHand())||!Null(equipment.getBoots())||!Null(equipment.getHelmet())||!Null(equipment.getChestplate())||!Null(equipment.getLeggings());
}
private boolean Null(ItemStack is) {
	return is==null||is.getType()==Material.AIR;
}
}
