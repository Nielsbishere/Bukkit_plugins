import java.io.File;
import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R1.ChatComponentText;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.EntityWither;
import net.minecraft.server.v1_8_R1.EnumParticle;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.ItemStack;
import net.minecraft.server.v1_8_R1.Items;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutHeldItemSlot;
import net.minecraft.server.v1_8_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R1.PacketPlayOutSetSlot;
import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R1.PacketPlayOutWorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.Configuration;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.mojang.authlib.GameProfile;

@SuppressWarnings("unused")
public class Main{
	public void sendTitle(Player p, String title, String sub, int ticks){
		if(title.length()>64)title=title.substring(0,63);
		PacketPlayOutTitle titleP = new PacketPlayOutTitle(EnumTitleAction.TITLE,new ChatComponentText(title));
		PacketPlayOutTitle subtitleP = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE,new ChatComponentText(title));
		PacketPlayOutTitle durationP = new PacketPlayOutTitle(EnumTitleAction.TIMES,new ChatComponentText(""+ticks));
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(titleP);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(subtitleP);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(durationP);
	}
	public void sendParticle(Player p, EnumParticle particle, Location l, int spreadX, int spreadY, int spreadZ, int amount, float speed){
		PacketPlayOutWorldParticles particleP = new PacketPlayOutWorldParticles(particle, true, (float)l.getX(), (float)l.getY(), (float)l.getZ(), spreadX, spreadY, spreadZ, speed, amount);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(particleP);
	}
	@SuppressWarnings("deprecation")
	private void vanish(Player p) {
		for(Player pl : p.getServer().getOnlinePlayers())((CraftPlayer)pl).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(p.getEntityId()));
	}
	@SuppressWarnings("deprecation")
	private void show(Player p) {
		for(Player pl : p.getServer().getOnlinePlayers())((CraftPlayer)pl).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn((EntityHuman)p));
	}
	public void rename(Player p, String name){
		vanish(p);
		EntityHuman eh = renamedP(name,p);
		if(eh==null)return;
		PacketPlayOutNamedEntitySpawn playerNameP = new PacketPlayOutNamedEntitySpawn(eh);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(playerNameP);
	}
	private EntityHuman renamedP(String name, Player p) {
		EntityHuman eh = (EntityHuman) p;
		try{
		Field f = eh.getClass().getDeclaredField("i");
		f.setAccessible(true);
		GameProfile gp = (GameProfile) f.get(eh);
		f = gp.getClass().getDeclaredField("name");
		f.setAccessible(true);
		f.set(gp, name);
		return eh;
		}catch(Exception e){
			return null;
		}
	}
	public void changeSlot(int i, Player p){
		PacketPlayOutHeldItemSlot changeSlotP = new PacketPlayOutHeldItemSlot(i);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(changeSlotP);
	}
	@SuppressWarnings("deprecation")
	public void sendPopup(Player p, String popup){
		PacketPlayOutSetSlot popupP = new PacketPlayOutSetSlot(0,1,getPopup(popup));
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(popupP);
		p.updateInventory();
	}
	private ItemStack getPopup(String popup) {
		ItemStack is = new ItemStack(Items.STICK);
		is.getTag().setString("Name", popup);
		return null;
	}
	public void sendBossBar(String title, double percentage, double seconds, Player p, JavaPlugin jp){
		final EntityWither wither = new EntityWither(((CraftWorld)p.getWorld()).getHandle());
		wither.setCustomName(title);
		wither.setHealth((float) (percentage/100*wither.getMaxHealth()));
		wither.setLocation(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), 0, 0);
		PacketPlayOutSpawnEntityLiving fakeSpawn = new PacketPlayOutSpawnEntityLiving(wither);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(fakeSpawn);
		final Player pl = p;
		Bukkit.getScheduler().scheduleSyncDelayedTask(jp, new Runnable(){
			@Override
			public void run() {
				((CraftPlayer)pl).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(wither.getId()));
			}}, (long)(seconds*20));
	}
	
	public void displayStats(Configuration c, Player p){
		ScoreboardManager sbm = Bukkit.getScoreboardManager();
		Scoreboard sb = sbm.getNewScoreboard();
		Objective obj = sb.registerNewObjective(p.getName() + "_stats", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(ChatColor.GOLD + p.getName() + "'s stats");
		for(String s : c.getKeys(false)){
			int value = c.getInt(s);
			Score score = obj.getScore(ChatColor.GREEN + s + "");
			score.setScore(value);
		}
		p.getPlayer().setScoreboard(sb);
	}
	public void refreshStats(Configuration c, Player p){
		Scoreboard sb = p.getScoreboard();
		if(sb==null)return;
		Objective obj = sb.getObjective(p.getName() + "_stats");
		if(obj==null)return;
		for(String s : c.getKeys(false)){
			int value = c.getInt(s);
			Score score = obj.getScore(ChatColor.GREEN + s + "");
			score.setScore(value);
		}
	}
	public void backupWorld(World w, Server s){
		if(s.getWorld(w.getName() + " - copy")!=null)deleteWorld(w.getName() + " - copy",s);
		WorldCreator wc = new WorldCreator(w.getName() + " - copy");
		wc = ((new WorldCreator(w.getName())).copy(wc));
		Bukkit.createWorld(wc);
	}
	private void deleteWorld(String w, Server s) {
		if(s.getWorld(w)==null)return;
		for(Player p : s.getWorld(w).getPlayers()){
			if(s.getWorld("world")==null)break;
			p.teleport(new Location(s.getWorld("world"),0,68,0));
			p.sendMessage(ChatColor.DARK_RED + "The world is dissolving beneath your feet, you managed to get out!");
		}
		s.unloadWorld(w, false);
		File f = new File(s.getWorld(w).getWorldFolder().getPath());
		if(f.isDirectory()){
			deleteDirectory(f);
		}
	}
	private void deleteDirectory(File f) {
		if(!f.isDirectory())return;
		for(File file : f.listFiles()){
			if(file.isDirectory())deleteDirectory(file);
			else if(file.exists())file.delete();
		}
		f.delete();
	}
	public static Inventory getInventory(String s, Configuration c){
		if(s==null||s.equals(""))return null;
		int length = c.getInt(s+".Length");
		Inventory inv = Bukkit.createInventory(null, length);
		for(int i=0;i<length;i++){
			org.bukkit.inventory.ItemStack is = c.getItemStack(s+"."+i);
			if(is==null)continue;
			inv.addItem(is);
		}
		return inv;
	}
	public static void setInventory(String s, JavaPlugin jp, Inventory inv){
		if(s==null||s.equals(""))return;
		jp.getConfig().set(s+".Length", inv.getSize());
		for(int i=0;i<inv.getSize();i++){
			jp.getConfig().set(s+"."+i, inv.getContents()[i]);
		}
		jp.saveConfig();
	}
}
