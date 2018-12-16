package net.nielsbwashere.src.MovableSpawners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
@SuppressWarnings("deprecation")
public class Core extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("getSpawners")){
			if(!sender.isOp()||!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "Only opped players can use that command!");
				return false;
			}
			for(String s : new String[]{"Pig","Cow","Sheep","Skeleton","Zombie","Enderman","Ozelot","Wolf","Chicken","Ghast","PigZombie","Spider","Slime","CaveSpider","Silverfish","Blaze","MagmaCube","Witch","Endermite","Bat","Guardian","Rabbit","Squid","SnowMan","MushroomCow","Villager","VillagerGolem","EntityHorse"}){
				ItemStack is = new ItemStack(Material.MOB_SPAWNER);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.RESET + s + " Spawner");
				is.setItemMeta(im);
				((Player)sender).getWorld().dropItem(((Player)sender).getLocation(), is);
			}
		}
		return false;
	}
	@EventHandler
	public void onRightClick(PlayerInteractEvent e){
		if(e.getAction()!=Action.RIGHT_CLICK_BLOCK)return;
		Block b = e.getPlayer().getTargetBlock(null, 64);
		if(b==null||b.getType()!=Material.MOB_SPAWNER)return;
		ItemStack i = e.getItem();
		if(i==null||i.getType()!=Material.NETHER_STAR)return;
		if(i.getAmount()==1)e.getPlayer().getInventory().clear(e.getPlayer().getInventory().getHeldItemSlot());
		else i.setAmount(i.getAmount()-1);
		ItemStack is = new ItemStack(Material.MOB_SPAWNER);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RESET + ((CreatureSpawner)b.getState()).getCreatureTypeName() + " Spawner");
		is.setItemMeta(im);
		b.getWorld().dropItem(b.getLocation(), is);
		b.setType(Material.AIR);
	}
	@EventHandler
	public void onRightClick(BlockPlaceEvent e){
		if(e.getPlayer()==null||e.getPlayer().getItemInHand()==null||e.getPlayer().getItemInHand().getType()!=Material.MOB_SPAWNER)return;
		ItemStack is = e.getPlayer().getItemInHand();
		if(!is.hasItemMeta()||!is.getItemMeta().hasDisplayName())return;
		String name = is.getItemMeta().getDisplayName();
		if(name==null||name.equals("")||!name.startsWith(ChatColor.RESET+"")||!name.endsWith(" Spawner"))return;
		String type = name.replaceFirst(ChatColor.RESET + "", "").replace(" Spawner", "");
		e.getBlock().setType(Material.MOB_SPAWNER);
		((CreatureSpawner)e.getBlock().getState()).setCreatureTypeByName(type);
	}
}
