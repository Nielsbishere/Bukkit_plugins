package net.nielsbwashere.UAS.src.handlers;

import net.nielsbwashere.UAS.src.Core;
//
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.Sound;
//import org.bukkit.entity.Player;
//import org.bukkit.potion.PotionEffect;
//import org.bukkit.potion.PotionEffectType;

public class DayHandler implements Runnable {
	Core c;
	public DayHandler(Core c){
		this.c = c;
	}
	@Override
	public void run() {
//		int days = c.getConfig().getInt("info.day");
//		if(days<=0){
//			c.getConfig().set("info.day", 1);
//			c.saveConfig();
//			c.getServer().dispatchCommand(c.getServer().getConsoleSender(), "difficulty peaceful");
//		}
//		int minutes = c.getConfig().getInt("info.minutesintoday");
//		boolean timeStopped = c.getConfig().getBoolean("info.timestopped");
//		if(!timeStopped){
//			minutes+=1;
//			c.getConfig().set("info.minutesintoday",minutes);
//		}
//		if(minutes>=24){
//			minutes = 0;
//			c.getConfig().set("info.minutesintoday",minutes);
//			c.getConfig().set("info.day", days+1);
//			c.getServer().dispatchCommand(c.getServer().getConsoleSender(), "time set 0");
//			days = days+1;
//		}
//		c.saveConfig();
//		if(days==4&&minutes==0){
//			for(Player p : c.getServer().getOnlinePlayers()){
//				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*75, 255));
//			}
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//					c.getServer().broadcastMessage(ChatColor.GOLD + "Aedifex" + ChatColor.RESET + ": My helper!");}},75);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.DARK_RED + "Auxiliator" + ChatColor.RESET + ": Yes my lord?");}},150);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GOLD + "Aedifex" + ChatColor.RESET + ": I need your help, people in my world have been greedy, arrogant, ungrateful, arrogant and murderers!");}},225);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.DARK_RED + "Auxiliator" + ChatColor.RESET + ": I agree sir, we should do something to make them realize how beautiful our world is and make them thankful!");}},300);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GOLD + "Aedifex" + ChatColor.RESET + ": How would we do that?");}},375);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.DARK_RED + "Auxiliator" + ChatColor.RESET + ": By taking something from them! They will be begging you for the old world!");}},450);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GOLD + "Aedifex" + ChatColor.RESET + ": That doesn't sound like me..");}},525);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.DARK_RED + "Auxiliator" + ChatColor.RESET + ": Are you just going to let them destroy your world?! Kill your creatures?! AND EAT THEM RAW?!");}},600);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GOLD + "Aedifex" + ChatColor.RESET + ": NEVER!");}},675);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//					for(Player p : c.getServer().getOnlinePlayers()){
//					p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1F, 1F);
//					p.sendMessage(ChatColor.DARK_RED + "Aedifex has changed the difficulty to easy");
//					}
//					c.getServer().dispatchCommand(c.getServer().getConsoleSender(), "difficulty easy");
//					}},750);
//			
//		}
//		if(days==5&&minutes==0){
//			for(Player p : c.getServer().getOnlinePlayers()){
//				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 255));
//			}
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//					c.getServer().broadcastMessage(ChatColor.GREEN + "Facies" + ChatColor.RESET + ": Aedifex?");}},0);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GOLD + "Aedifex" + ChatColor.RESET + ": Yes Facies?");}},100);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GREEN + "Facies" + ChatColor.RESET + ": I can't do this any longer.. they destroy nature.. I get weaker every day.");}},200);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GOLD + "Aedifex" + ChatColor.RESET + ": I have the same problem, they won't stop being greedy!");}},300);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GREEN + "Facies" + ChatColor.RESET + ": I can't protect them against eachother now.. I am too weak.");}},400);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GOLD + "Aedifex" + ChatColor.RESET + ": You mean.. You can't stop them from hurting eachother?");}},500);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GREEN + "Facies" + ChatColor.RESET + ": Exactly.. They will kill eachother for their loot..");}},600);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GOLD + "Aedifex" + ChatColor.RESET + ": We can't stop them..");}},700);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GREEN + "Facies" + ChatColor.RESET + ": I will still need your help Aedifex. I can't do this alone, you need to help me restore nature!");}},800);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.GOLD + "Aedifex" + ChatColor.RESET + ": I will heal you, until I lose power.");}},900);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//					for(Player p : c.getServer().getOnlinePlayers()){
//					p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1F, 1F);
//					p.sendMessage(ChatColor.DARK_RED + "Facies is weak and can't stop people from hitting eachother.");
//					}
//					}},1000);
//			
//		}
//		if(days==6&&minutes==0){
//			for(Player p : c.getServer().getOnlinePlayers()){
//				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 255));
//			}
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//					c.getServer().broadcastMessage(ChatColor.DARK_RED + "Auxiliator" + ChatColor.RESET + ": Dark lord? It's me Auxiliator!");}},0);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.BLACK + "Tenebrae" + ChatColor.RESET + ": You have finaly returned!");}},100);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.DARK_RED + "Auxiliator" + ChatColor.RESET + ": I did what you asked, I used Aedifex.");}},200);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.BLACK + "Tenebrae" + ChatColor.RESET + ": Good, is he destroying the world?");}},300);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.DARK_RED + "Auxiliator" + ChatColor.RESET + ": Almost, just a little more help from the humans, then we'll take over!");}},400);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.BLACK + "Tenebrae" + ChatColor.RESET + ": Aedifex will find out.. He'll turn on us, before that happens we need to be prepared!");}},500);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.DARK_RED + "Auxiliator" + ChatColor.RESET + ": We need help...");}},600);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.BLACK + "Tenebrae" + ChatColor.RESET + ": Maybe someone in that world can help us and we'll give them goods in return.");}},700);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.DARK_RED + "Auxiliator" + ChatColor.RESET + ": That will be good, we shall kill Aedifex!");}},800);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//				c.getServer().broadcastMessage(ChatColor.BLACK + "Tenebrae" + ChatColor.RESET + ": Then we can finaly bring darkness and destruction to this world!");}},900);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//					for(Player p : c.getServer().getOnlinePlayers()){
//					p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1F, 1F);
//					p.sendMessage(ChatColor.DARK_RED + "Auxiliator" + ChatColor.RESET + ": We need to check them first, if they really want it..");
//					p.sendMessage(ChatColor.BLACK + "Tenebrae" + ChatColor.RESET + ": Let them drop a gold nugget in the right hopper at the entrance.");
//					p.sendMessage(ChatColor.DARK_RED + "Everyone will be teleported in 1 minute, take your best gear and a gold nugget!");
//					}
//					}},1000);
//			c.getServer().getScheduler().scheduleSyncDelayedTask(c, new Runnable(){@Override public void run() {
//						for(Player p : c.getServer().getOnlinePlayers()){
//							p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1F, 1F);
//							p.teleport(new Location(p.getWorld(),3.5,157,81));
//							}
//						c.getServer().dispatchCommand(c.getServer().getConsoleSender(), "gamerule doDaylightCycle false");
//						c.getConfig().set("info.timestopped",true);
//							}},1000+(60*20));
//			
//		}
	}

}
