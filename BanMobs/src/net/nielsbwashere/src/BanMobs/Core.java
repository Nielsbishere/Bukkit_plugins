package net.nielsbwashere.src.BanMobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.java.JavaPlugin;
public class Core extends JavaPlugin implements Listener{
@Override
public void onEnable() {
	Bukkit.getPluginManager().registerEvents(this, this);
}
@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("BanMobs")){
			if(!sender.hasPermission("BanMobs")&&!sender.isOp()){
				sender.sendMessage(ChatColor.DARK_RED + "Permission denied");
				return false;
			}
			if(args.length!=2){
				sender.sendMessage(ChatColor.DARK_RED + "Incorrect usage: /BanMobs <World> <0=Unban,1=ALL,2=NATURAL,3=NOT CUSTOM,4=NOT NATURAL>");
				return false;
			}
			if(getServer().getWorld(args[0])==null){
				sender.sendMessage(ChatColor.DARK_RED + "That world doesn't exist!");
				return false;
			}
			int i=0;
			try{
				i=Integer.parseInt(args[1]);
			}catch(Exception e){
				sender.sendMessage(ChatColor.DARK_RED + "You can only input numbers!");
				return false;
			}
			getConfig().set(args[0], i);
			saveConfig();
			sender.sendMessage(ChatColor.GOLD + "The world now disabled/enabled that certain type of mob spawning!");
		}
		return false;
	}
@EventHandler
public void onMobSpawn(CreatureSpawnEvent e){
	if(isEnabled(e.getEntity().getWorld(),e.getSpawnReason()))return;
	e.setCancelled(true);
}
private boolean isEnabled(World world, SpawnReason spawnReason) {
	int i = getConfig().getInt(world.getName());
	if(i==1)return false;
	if(i==2&&spawnReason==SpawnReason.NATURAL)return false;
	if(i==3&&spawnReason!=SpawnReason.CUSTOM)return false;
	if(i==4&&spawnReason!=SpawnReason.NATURAL)return false;
	return true;
}
}
