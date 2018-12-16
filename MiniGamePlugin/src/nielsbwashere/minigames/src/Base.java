package nielsbwashere.minigames.src;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
public class Base extends JavaPlugin implements Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Base plugin; 
	public static ArrayList<Integer> numPlayer = new ArrayList<Integer>(); //TODO: add Integers to this list
	public static Inventory mgI,epI,dpI; //TODO: (minigameInventory, escapeperkInventory, escapebebuffInventory)
	public static int game = 0,quickupdate = 1;
	public static int timeInSeconds, tIS;
	public boolean canStart, canStartTowerSpleef;
	public List<String> escape, escapeG;
	public void onDisable() {
		PluginDescriptionFile a = this.getDescription();
		this.logger.info(a.getName() + " Is now disabled");
		getServer().getScheduler().cancelTask(game);
		getServer().getScheduler().cancelTask(quickupdate);
		writeToConfig();
		saveConfig();
	}
	private void writeToConfig() {
		// TODO Auto-generated method stub
	}
}
