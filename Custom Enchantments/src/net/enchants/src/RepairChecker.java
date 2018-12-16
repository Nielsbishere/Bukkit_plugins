package net.enchants.src;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairChecker implements Runnable{
	Core c;
	public RepairChecker(Core c){
		this.c=c;
		c.getServer().getScheduler().scheduleSyncRepeatingTask(c, this, 0, 20*5);
	}
	@SuppressWarnings("deprecation")
	public void run() {
		for(Player p : c.getServer().getOnlinePlayers()){
			for(ItemStack is : p.getInventory().getContents()){
				if(is==null||is.getType()==Material.AIR||!is.hasItemMeta())continue;
				if(!is.getItemMeta().hasLore())continue;
				if(!c.hasCustomEnchant(is.getItemMeta().getLore()))continue;
				boolean b = false;
				for(String s : is.getItemMeta().getLore()){
				if(!c.isCustomEnchant(s))continue;
				if(s.startsWith(ChatColor.RESET + "Repair "))b=true;
				}
				if(!b)continue;
				if(is.getDurability()>0)is.setDurability((short) (is.getDurability()-1));
			}
			boolean hasChestplateOn = false;
			for(ItemStack is : p.getInventory().getArmorContents()){
				if(is==null||!is.getType().name().endsWith("CHESTPLATE")||!is.hasItemMeta())continue;
				if(!is.getItemMeta().hasLore())continue;
				if(!c.hasCustomEnchant(is.getItemMeta().getLore()))continue;
				boolean b = false;
				for(String s : is.getItemMeta().getLore()){
				if(!c.isCustomEnchant(s))continue;
				if(s.startsWith(ChatColor.RESET + "Flying "))b=true;
				}
				if(!b)continue;
				if(!hasChestplateOn){
					if(!p.isFlying()){
						p.sendMessage(ChatColor.GOLD + "Flying enabled!");
					}
				p.setAllowFlight(true);
				p.setFlying(true);
				hasChestplateOn = true;
				}
			}
			if(!hasChestplateOn&&p.getGameMode()!=GameMode.CREATIVE&&p.isFlying()){
				p.setFlying(false);
				p.setAllowFlight(false);
			}
		}
	}
}
