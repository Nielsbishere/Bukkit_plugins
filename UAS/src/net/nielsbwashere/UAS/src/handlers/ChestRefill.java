package net.nielsbwashere.UAS.src.handlers;
import java.util.Random;

import net.nielsbwashere.UAS.Customs.ChestContents;
import net.nielsbwashere.UAS.src.Core;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
public class ChestRefill implements Runnable {
	Core core;
	public ChestRefill(Core c) {
		core = c;
	}
	@Override
	public void run() {
		core.chestList.clear();
		for(String s : core.getConfig().getStringList("Chest.List")){
			String chestType = core.getConfig().getString("Chest.All." + s + ".chestType");
			int chestAm = core.getConfig().getInt(chestType + ".amItems");
			Inventory inv = core.getServer().createInventory(null, 27, s.replace("_", " "));
			for(int o=1;o<inv.getSize();o++){
				boolean b = (new Random()).nextInt(10)<=3;
				if(b){
					for(int i=1;i<chestAm+1;i++){
						Material m = Material.getMaterial(core.getConfig().getString("ChestContents." + chestType + "." + i + ".Material"));
						int chance = core.getConfig().getInt("ChestContents." + chestType + "." + i + ".Chance");
						int rand = (new Random()).nextInt(10000);
						if(rand<=chance){
							inv.addItem(new ItemStack(m,1));
							break;
						}
					}
				}
			}
			ChestContents cc = new ChestContents(s,inv);
			core.chestList.add(cc);
			}
		}
}
