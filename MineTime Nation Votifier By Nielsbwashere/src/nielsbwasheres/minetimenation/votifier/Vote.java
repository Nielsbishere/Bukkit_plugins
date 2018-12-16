package nielsbwasheres.minetimenation.votifier;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.vexsoftware.votifier.model.VoteListener;
public class Vote extends JavaPlugin implements VoteListener,Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Vote plugin; 
	public void onDisable() {
		PluginDescriptionFile a = this.getDescription();
		this.logger.info(a.getName() + " Is now disabled");
		saveConfig();
	}
	public void onEnable() {
		PluginDescriptionFile a = this.getDescription();
		this.logger.info(a.getName() + " Is now enabled");
		if(getConfig().get("votelinks")==null){
			ArrayList<String> testlink = new ArrayList<String>();
			testlink.add("http://nielsbwashere.veoserv.com");
			testlink.add("http://freedom.tm/via/nielsbwashere");
			getConfig().set("votelinks", testlink);
		}
		saveConfig();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
	}
	public void voteMade(com.vexsoftware.votifier.model.Vote v) {
		String p = v.getUsername();
		for(Player player : getServer().getOnlinePlayers()){
			if(player.getName()==p){
				int votes = getConfig().getInt(player.getName()+".votes");
				getConfig().set(player.getName() + ".votes", votes+1);
				int vtc = getConfig().getInt(player.getName()+".votestoclaim");
				getConfig().set(player.getName() + ".votestoclaim", vtc+1);
				saveConfig();
				claimRewards(player);
				getServer().broadcastMessage(ChatColor.RESET + "[" + ChatColor.GOLD + "MineTime Raiding" + ChatColor.RESET + "] " + ChatColor.GREEN + p + " has voted for us and received 2 DIAMONDS, some money and XP levels! Want it to? Use /vote and vote for us on those websites");
			}
		}
		if(!getServer().getOnlinePlayers().toString().contains(p)){
			int votes = getConfig().getInt(p+".votes");
			getConfig().set(p + ".votes", votes+1);
			int vtc = getConfig().getInt(p+".votestoclaim");
			getConfig().set(p + ".votestoclaim", vtc+1);
			saveConfig();
			getServer().broadcastMessage(ChatColor.RESET + "[" + ChatColor.GOLD + "MineTime Raiding" + ChatColor.RESET + "] " + ChatColor.GREEN + p + " has voted for us and will receive 2 DIAMONDS, some money and XP levels! Want it to? Use /vote and vote for us on those websites");
		}
		System.out.println(p + " has voted and should get a reward, if not; ask nielsbwashere.");
	}
	private void claimRewards(Player player) {
		if(player.isOnline()){	
			int votes = getConfig().getInt(player+".votes");
			int vtc = getConfig().getInt(player+".votestoclaim");
			int votesBeforeVTC = votes-vtc;
			if(vtc>=1){
				player.sendMessage(ChatColor.RESET + "[" + ChatColor.GOLD + "MineTime Raiding" + ChatColor.RESET + "] " + ChatColor.GREEN + "you have voted " + votes + " times and you didn't receive a reward " + vtc + " times");
				player.sendMessage(ChatColor.RESET + "[" + ChatColor.GOLD + "MineTime Raiding Votifier" + ChatColor.RESET + "] " + ChatColor.GREEN + "auto-claiming " + vtc + " rewards..");
				for(int i=0;i>vtc;i++){
				player.getInventory().addItem(new ItemStack(Material.DIAMOND, 2));
				}
					for(int i=0;i<vtc;i++){
						int a = 50+((votesBeforeVTC+i)*10);
						int xp = 0+((votesBeforeVTC+i)*5);
						if(a<=500){
						getServer().dispatchCommand(getServer().getConsoleSender(), "eco give " + player.getName() + " " + a);
						}else{
							getServer().dispatchCommand(getServer().getConsoleSender(), "eco give " + player.getName() + " 500");
						}
						if(xp<=80){
							player.giveExp(xp);
						}else{
							player.giveExp(80);
						}
					}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		claimRewards(p);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(label.equalsIgnoreCase("vote")){
			@SuppressWarnings("unchecked")
			ArrayList<String> votelinks = (ArrayList<String>) getConfig().get("votelinks");
			sender.sendMessage(ChatColor.RESET + "[" + ChatColor.GOLD + "MineTime Raiding Votifier" + ChatColor.RESET + "] " + ChatColor.GREEN + "you can vote on: " + votelinks);
		}
		return true;
	}
}
