package net.osomi.src.PresentsFix;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin implements Listener{
@Override
public void onEnable() {
if(!getConfig().getBoolean("Settings.isInitialized")){
	getConfig().set("PresentId",6052);
	getConfig().set("Settings.isInitialized",true);
	saveConfig();
}
Bukkit.getPluginManager().registerEvents(this,this);
}
@EventHandler
public void onRight(PlayerInteractEvent e){
	if(e.getAction()!=Action.RIGHT_CLICK_AIR)return;
	if(!isPresent(e.getItem()))return;
	if(e.getItem().getAmount()<=1)return;
	int instances = e.getItem().getAmount()-1;
	ItemStack is = e.getItem();
	is.setAmount(1);
	for(int i=0;i<instances;i++){
		ItemStack clone = is.clone();
		add(e.getPlayer(),clone);
	}
	e.getPlayer().setItemInHand(is);
}
private void add(Player player, ItemStack clone) {
	int freeSlot = -1, slot=-1;
	for(ItemStack is : player.getInventory().getContents()){
		slot++;
		if(is==null||is.getType()==Material.AIR){
			freeSlot=slot;
			break;
		}
	}
	if(freeSlot<0){
		player.getWorld().dropItem(player.getLocation(), clone);
		return;
		
	}
	player.getInventory().setItem(freeSlot, clone);
}
@SuppressWarnings("deprecation")
public boolean isPresent(ItemStack is){
	return is!=null&&is.getTypeId()==getConfig().getInt("PresentId");
}
}
