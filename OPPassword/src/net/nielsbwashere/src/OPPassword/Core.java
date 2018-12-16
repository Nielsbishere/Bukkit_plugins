package net.nielsbwashere.src.OPPassword;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
public class Core extends JavaPlugin implements Listener{
	List<UUID> loggedIn = new ArrayList<UUID>();
@Override
public void onEnable() {
	Bukkit.getPluginManager().registerEvents(this, this);
}
@EventHandler(priority=EventPriority.HIGH)
public void onLogout(PlayerQuitEvent e){
	if(loggedIn.contains(e.getPlayer().getUniqueId()))loggedIn.remove(e.getPlayer().getUniqueId());
}
@EventHandler(priority=EventPriority.HIGH)
public void onCommand(PlayerCommandPreprocessEvent e){
	if(e.getMessage().startsWith("/login")||e.getMessage().startsWith("/register"))return;
	if(!e.getPlayer().isOp()&&!e.getPlayer().hasPermission("OPPasswordProtect.Protect"))return;
	if(loggedIn.contains(e.getPlayer().getUniqueId()))return;
	e.setCancelled(true);
}
@EventHandler(priority=EventPriority.HIGH)
public void onInteract(PlayerInteractEvent e){
	if(!e.getPlayer().isOp()&&!e.getPlayer().hasPermission("OPPasswordProtect.Protect"))return;
	if(loggedIn.contains(e.getPlayer().getUniqueId()))return;
	e.setCancelled(true);
}
@Override
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if(label.equalsIgnoreCase("register")){
		if(args.length!=1){
			sender.sendMessage(ChatColor.DARK_RED + "Invalid use /register <password>");
			return false;
		}
		if(!(sender.isOp()||sender.hasPermission("OPPasswordProtect.Protect"))){
			sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
			return false;
		}
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.DARK_RED + "Consoles don't have passwords.");
			return false;
		}
		String s = getConfig().getString(((Player)sender).getUniqueId().toString()+".p");
		if(s!=null&&!s.equals("")){
			sender.sendMessage(ChatColor.DARK_RED + "You've already registered!");
			return false;
		}
		getConfig().set(((Player)sender).getUniqueId().toString()+".p",args[0]);
		saveConfig();
		args[0]=null;
		sender.sendMessage(ChatColor.DARK_RED + "You've now got a password!");
		return false;
	}else if(label.equalsIgnoreCase("login")){
		if(args.length!=1){
			sender.sendMessage(ChatColor.DARK_RED + "Invalid use /login <password>");
			return false;
		}
		if(!(sender.isOp()||sender.hasPermission("OPPasswordProtect.Protect"))){
			sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
			return false;
		}
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.DARK_RED + "Consoles don't have passwords.");
			return false;
		}
		String s = getConfig().getString(((Player)sender).getUniqueId().toString()+".p");
		if(s==null||s.equals("")){
			sender.sendMessage(ChatColor.DARK_RED + "You're not registered yet!");
			return false;
		}
		if(!loggedIn.contains(((Player)sender).getUniqueId())){
			if(!args[0].equals(s)){
				sender.sendMessage(ChatColor.DARK_RED + "Incorrect password!");
				return false;
			}
			loggedIn.add(((Player)sender).getUniqueId());
			sender.sendMessage(ChatColor.DARK_RED + "Logged in!");
			return false;
		}else{
			sender.sendMessage(ChatColor.DARK_RED + "You're already logged in!");
			return false;
		}
	}
	
	return false;
}
}
