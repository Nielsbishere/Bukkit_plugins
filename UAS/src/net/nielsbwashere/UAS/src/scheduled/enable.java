package net.nielsbwashere.UAS.src.scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.nielsbwashere.UAS.AI.HitmanTrait;
import net.nielsbwashere.UAS.AI.ZombieTrait;
import net.nielsbwashere.UAS.src.Core;
import net.nielsbwashere.UAS.src.handlers.ChestRefill;
import net.nielsbwashere.UAS.src.handlers.DayHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class enable implements Runnable {
	Core c;
	public enable(Core c) {
		this.c = c;
	}
	@Override
	public void run() {
		if(c.getServer().getPluginManager().getPlugin("Citizens") == null || c.getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			c.getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
			c.getServer().getPluginManager().disablePlugin(c.getServer().getPluginManager().getPlugin("Ultimate Apocalyptic Survival"));	
			return;
		}	
		c.getServer().getPluginManager().registerEvents(c, c);
		int j = c.getServer().getScheduler().scheduleSyncRepeatingTask(c, new DayHandler(c), 0, (20*60));
		c.dayHandler = j;
		ItemStack is = new ItemStack(Material.ENDER_CHEST,1);
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.DARK_PURPLE + "Can store up to 27 items!");
		lore.add(ChatColor.DARK_PURPLE + "Crafted by using an enderchest and 8 leather.");
		String name = ChatColor.GOLD + "Ender-Chestpack";
		im.setDisplayName(name);
		im.setLore(lore);
		is.setItemMeta(im);
		ShapedRecipe r = new ShapedRecipe(is);
		r.shape("aca", "cbc", "aca");
		r.setIngredient('a', Material.LEATHER);
		r.setIngredient('b', Material.ENDER_CHEST);
		r.setIngredient('c', Material.DIAMOND_BLOCK);
		Bukkit.addRecipe(r);
		Bukkit.getScheduler().scheduleSyncDelayedTask(c, new ChestRefill(c));
		Bukkit.getScheduler().scheduleSyncRepeatingTask(c, new ChestRefill(c), 0, 8*60*20);
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ZombieTrait.class).withName("ZombieTrait"));
		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(HitmanTrait.class).withName("HitmanTrait"));
		for(World w : c.getServer().getWorlds()){
			for(Zombie z : w.getEntitiesByClass(Zombie.class)){
				if(c.getConfig().getBoolean(z.getUniqueId().toString() + ".isBody")){
					z.remove();
					c.getServer().broadcastMessage(ChatColor.DARK_RED + z.getCustomName() + " has been forcefully removed by a reload/disable!");
					c.getConfig().set(z.getUniqueId().toString(), null);
					c.saveConfig();
				}
			}
		}
	}

}
