package net.nielsbwashere.src.ChiselBugFix;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin implements Listener{
@Override
public void onEnable() {
Bukkit.getPluginManager().registerEvents(this, this);
getConfig().set("ChiselId", 7558);
saveConfig();
}
@SuppressWarnings("deprecation")
@EventHandler
public void onDrag(InventoryClickEvent e){
	if(!chiselInventory(e.getWhoClicked().getItemInHand()))return;
	if(e.getSlot()!=24)return;
	if(!(e.getAction()==InventoryAction.SWAP_WITH_CURSOR))return;
	add(e.getCurrentItem(),(Player)e.getWhoClicked());
	e.setCurrentItem(e.getCursor());
	e.setCursor(null);
	resetChisel((Player)e.getWhoClicked());
}
private void add(ItemStack itemStack, Player whoClicked) {
	if(!canHold(whoClicked,itemStack))whoClicked.getWorld().dropItem(whoClicked.getLocation(), itemStack);
	else whoClicked.getInventory().addItem(itemStack);
}
private boolean canHold(Player whoClicked, ItemStack itemStack) {
	Inventory i = whoClicked.getInventory();
	for(ItemStack is : i.getContents())if(is==null||is.getType()==Material.AIR||(is.isSimilar(itemStack)&&is.getAmount()+itemStack.getAmount()<=itemStack.getMaxStackSize()))return true;
	if(getTotalToStack(itemStack,i)>=itemStack.getAmount())return true;
	return false;
}
private int getTotalToStack(ItemStack itemStack, Inventory i) {
	int j=0;
	for(ItemStack is : i.getContents())if(is.isSimilar(itemStack))j+=is.getAmount();
	return j;
}
private void resetChisel(Player whoClicked) {
	@SuppressWarnings("deprecation")
	ItemStack newChisel = new ItemStack(getChiselId(),1,whoClicked.getItemInHand().getDurability());
	whoClicked.setItemInHand(newChisel);
}
@SuppressWarnings("deprecation")
private boolean chiselInventory(ItemStack is) {
	return is!=null&&is.getTypeId()==getChiselId();
}
private int getChiselId() {
	return getConfig().getInt("ChiselId");
}
}
