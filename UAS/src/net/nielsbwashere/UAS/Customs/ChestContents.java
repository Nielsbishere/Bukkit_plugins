package net.nielsbwashere.UAS.Customs;

import org.bukkit.inventory.Inventory;

public class ChestContents {
	public String name;
	public Inventory i;
	public ChestContents(String s, Inventory inv) {
		name=s;
		i=inv;
	}
}
