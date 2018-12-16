package net.nielsbwashere.src.AntiSessionStealer;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
public class Core extends JavaPlugin implements Listener{
	HashMap<UUID,Integer> tologin = new HashMap<UUID,Integer>();
	HashMap<UUID,Integer> loggedin = new HashMap<UUID,Integer>();
@Override
public void onEnable() {
	Bukkit.getPluginManager().registerEvents(this, this);
}
@EventHandler(priority=EventPriority.HIGH)
public void command(PlayerCommandPreprocessEvent e){
	if(!loggedin.containsKey(e.getPlayer().getUniqueId()))e.setCancelled(true);
}
@EventHandler(priority=EventPriority.LOWEST)
public void login(PlayerLoginEvent e){
	final UUID u = e.getPlayer().getUniqueId();
	tologin.put(u,Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			Player pl=null;
			for(Player p : getServer().getOnlinePlayers()){
				if(p.getUniqueId().equals(u)){
					pl=p;
					break;
				}
			}
			if(pl!=null){
				loggedin.put(u, tologin.get(u));
				tologin.remove(u);
			}else{
				tologin.remove(u);
			}
		}}, 20*3));
}
@EventHandler(priority=EventPriority.LOWEST)
public void logout(PlayerQuitEvent e){
	UUID u = e.getPlayer().getUniqueId();
	int toCancel = Integer.MIN_VALUE;
	if(tologin.containsKey(u)){
		toCancel=tologin.get(u);
		tologin.remove(u);
	}
	if(loggedin.containsKey(u)){
		toCancel=loggedin.get(u);
		loggedin.remove(u);
	}
	if(toCancel!=Integer.MIN_VALUE){
		Bukkit.getScheduler().cancelTask(toCancel);
	}
}
}
