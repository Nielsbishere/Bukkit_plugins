package niels.b.was.here;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
@SuppressWarnings("deprecation")
public class Base extends JavaPlugin implements Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	/**
	 * The minigame plugin
	 **/
	public static Base plugin; 
	public static int minPlayers=2,minPlayersTowerSpleef=2;
	public static Inventory i;
	public static Inventory perks;
	public static Inventory debuffs; //TODO:
	public static int game = 0;
	public static int quickupdate = 1;
	public static int timeInSeconds, tIS;
	public boolean canStart, canStartTowerSpleef;
	public List<String> escape, escapeG;
	public void onDisable() {
		PluginDescriptionFile a = this.getDescription();
		this.logger.info(a.getName() + " Is now disabled");
		saveConfig();
		getServer().getScheduler().cancelTask(game);
		getServer().getScheduler().cancelTask(quickupdate);
		writeToConfig();
	}
	private void writeToConfig() {
		getConfig().set("Escape.List", escape);
		getConfig().set("Escape.Ghost.List", escapeG);
		saveConfig();
	}
	public void onEnable() {
		PluginDescriptionFile a = this.getDescription();
		this.logger.info(a.getName() + " Is now enabled");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		saveConfig();
		i = Bukkit.createInventory(null, 18, "Minigame Warps");
		perks = Bukkit.createInventory(null, 18, "Perks");
		debuffs = Bukkit.createInventory(null, 18, "Debuffs");
		addMinigameInventory(i);
		timeInSeconds = 10;
		tIS = 10;
		canStart = false;
		canStartTowerSpleef = false;
		game = getServer().getScheduler().scheduleSyncRepeatingTask(this, new scheduledTasks(this), 20, 20);
		quickupdate = getServer().getScheduler().scheduleSyncRepeatingTask(this, new scheduledTasks025S(this), 5, 5);
		getConfig().set("Escape.HasStarted", false);
		getConfig().set("Escape.HasStarted2", false);
		getConfig().set("TowerSpleef.HasStarted", false);
		getConfig().set("TowerSpleef.HasStarted2", false);
		saveConfig();
		try{
			List<String> es = getConfig().getStringList("Escape.List");
			if(es!=null){
				escape=es;
			}
		}catch(Exception e){
			System.out.println("Error! Escape list is equal to null!");
		}
		try{
			List<String> es = getConfig().getStringList("Escape.Ghost.List");
			if(es!=null){
				escapeG=es;
			}
		}catch(Exception e){
			System.out.println("Error! Escape ghost list is equal to null!");
		}
	}
	private void addMinigameInventory(Inventory i) {
		ItemStack is = new ItemStack(Material.SNOW_BALL, 1); //Paintball
		ItemStack ist = new ItemStack(Material.ARROW, 1); //Skeleton
		ItemStack ist1 = new ItemStack(Material.DIAMOND_SWORD, 1); //Hunger games
		ItemStack ist2 = new ItemStack(Material.SAND,1);//Darkness
		ItemStack ist3 = new ItemStack(Material.EGG, 1); //Egg fight
		ItemStack ist4 = new ItemStack(Material.IRON_SPADE, 1); //Tower Spleef
		ItemMeta im = is.getItemMeta();
		ItemMeta im0 = ist.getItemMeta();
		ItemMeta im1 = ist1.getItemMeta();
		ItemMeta im2 = ist2.getItemMeta();
		ItemMeta im3 = ist3.getItemMeta();
		ItemMeta im4 = ist4.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Paintball");
		im0.setDisplayName(ChatColor.GOLD + "Archery");
		im1.setDisplayName(ChatColor.GOLD + "Hunger Games");
		im2.setDisplayName(ChatColor.GOLD + "Escape");
		im3.setDisplayName(ChatColor.GOLD + "Egg fight");
		im4.setDisplayName(ChatColor.GOLD + "Tower Spleef");
		ArrayList<String> lore1 = new ArrayList<String>();
		ArrayList<String> lore2 = new ArrayList<String>();
		ArrayList<String> lore3 = new ArrayList<String>();
		ArrayList<String> lore4 = new ArrayList<String>();
		ArrayList<String> lore5 = new ArrayList<String>();
		ArrayList<String> lore6 = new ArrayList<String>();
		lore1.add(ChatColor.DARK_RED + "Kill other teams using your paintball gun!");
		lore2.add(ChatColor.DARK_RED + "Kill other teams using your bow!");
		lore3.add(ChatColor.DARK_RED + "Survive against other players and pvp");
		lore4.add(ChatColor.DARK_RED + "Watch out for blocks falling on top of you");
		lore5.add(ChatColor.DARK_RED + "Knock people off by throwing eggs!");
		lore6.add(ChatColor.DARK_RED + "Destroy the tower beneath you, be the last man standing");
		im.setLore(lore1);
		is.setItemMeta(im);
		im0.setLore(lore2);
		ist.setItemMeta(im0);
		im1.setLore(lore3);
		ist1.setItemMeta(im1);
		im2.setLore(lore4);
		ist2.setItemMeta(im2);
		im3.setLore(lore5);
		ist3.setItemMeta(im3);
		im4.setLore(lore6);
		ist4.setItemMeta(im4);
		ItemStack[] is2 = new ItemStack[]{is, ist, ist1, ist2, ist3, ist4};
		if(is2 != null){
		i.addItem(is2);
		}
	}
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(sender instanceof Player){
			if(label.equalsIgnoreCase("MinigameJoin") && !inGame(sender.getName())&&!ghostPlayer((Player) sender)){
				i.clear();
				addMinigameInventory(i);
				((Player) sender).openInventory(i);
			}else if(label.equalsIgnoreCase("MinigameLeave") && inGame(sender.getName()) ||label.equalsIgnoreCase("MinigameLeave") && ghostPlayer((Player) sender)){
				
				
				for(int i = 0;i<escape.size();i++){
					if(escape.get(i).contains(sender.getName())){
					removeFrom("escape", (Player)sender);
					saveConfig();
					Player p = (Player)sender;
					p.getInventory().clear();
					p.setLevel(0);
					p.setHealth(20D);
					p.setFoodLevel(20);
					p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have left the escape minigame.");
					int x = getConfig().getInt("Spawn.X");
					int y = getConfig().getInt("Spawn.Y");
					int z = getConfig().getInt("Spawn.Z");
					String worldname = getConfig().getString("Spawn.World");
					World world = getServer().getWorld(worldname);
					p.teleport(new Location(world,x,y,z));
					if(escape.size() == 0){
						getConfig().set("Escape.HasStarted", false);
						getConfig().set("Escape.HasStarted2", false);
						saveConfig();
					}
					}
				}
				
				

				for(int i = 0;i<escapeG.size();i++){
					if(escapeG.get(i).contains(sender.getName())){
					removeFrom("ghost",(Player)sender);
					Player player = (Player)sender;
					int xx = getConfig().getInt("Spawn.X");
					int yy = getConfig().getInt("Spawn.Y");
					int zz = getConfig().getInt("Spawn.Z");
					String worldname = getConfig().getString("Spawn.World");
					World world = getServer().getWorld(worldname);
					player.teleport(new Location(world,xx,yy,zz));
					for(Player oP : getServer().getOnlinePlayers()){
						oP.showPlayer(player);
					}
					if(escapeG.size() == 0){
						getConfig().set("Escape.HasStarted", false);
						getConfig().set("Escape.HasStarted2", false);
						saveConfig();
						int startx = getConfig().getInt("First.ClearZone.X");
						int starty = getConfig().getInt("First.ClearZone.Y");
						int startz = getConfig().getInt("First.ClearZone.Z");
						int endx = getConfig().getInt("First.ClearZone.X2");
						int endy = getConfig().getInt("First.ClearZone.Y2");
						int endz = getConfig().getInt("First.ClearZone.Z2");
						for(int x=startx;x<endx;x++){
							for(int y=starty;y<endy;y++){
								for(int z=startz;z<endz;z++){
									if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
									player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
									}
								}
								for(int z=startz;z>endz;z--){
									if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
									player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
									}
								}
							}

							for(int y=starty;y>endy;y--){
								for(int z=startz;z<endz;z++){
									if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
									player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
									}
								}
								for(int z=startz;z>endz;z--){
									if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
									player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
									}
								}
							}
						}
						for(int x=startx;x>endx;x--){
							for(int y=starty;y<endy;y++){
								for(int z=startz;z<endz;z++){
									if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
									player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
									}
								}
								for(int z=startz;z>endz;z--){
									if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
									player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
									}
								}
							}

							for(int y=starty;y>endy;y--){
								for(int z=startz;z<endz;z++){
									if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
									player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
									}
								}
								for(int z=startz;z>endz;z--){
									if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
									player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
									}
										}
									}
						}
					}
					player.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have left spectator mode from the escape minigame.");
				}
				}
			}else if(label.equalsIgnoreCase("SetClearZonePos1")){
				if(args.length==2){
					if(args[0].equalsIgnoreCase("X")){
					getConfig().set("First.ClearZone.X", Integer.parseInt(args[1]));
					saveConfig();
					}else if(args[0].equalsIgnoreCase("Y")){
							getConfig().set("First.ClearZone.Y", Integer.parseInt(args[1]));
							saveConfig();
							}else if(args[0].equalsIgnoreCase("Z")){
								getConfig().set("First.ClearZone.Z", Integer.parseInt(args[1]));
								saveConfig();
								}
					sender.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have set the " + args[0] + " position to " + args[1]);
				}else if(args.length==1){
					Player p = (Player)sender;
					if(args[0].equalsIgnoreCase("X")){
					getConfig().set("First.ClearZone.X", p.getLocation().getBlockX());
					saveConfig();
					}else if(args[0].equalsIgnoreCase("Y")){
							getConfig().set("First.ClearZone.Y", p.getLocation().getBlockY());
							saveConfig();		
					}else if(args[0].equalsIgnoreCase("Z")){
								getConfig().set("First.ClearZone.Z", p.getLocation().getBlockZ());
								saveConfig();			
					}
					sender.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have set the " + args[0] + " position");
				}
			}else if(label.equalsIgnoreCase("SetClearZonePos2")){
				if(args.length==2){
					if(args[0].equalsIgnoreCase("X")){
					getConfig().set("First.ClearZone.X2", Integer.parseInt(args[1]));
					}else if(args[0].equalsIgnoreCase("Y")){
							getConfig().set("First.ClearZone.Y2", Integer.parseInt(args[1]));
							}else if(args[0].equalsIgnoreCase("Z")){
								getConfig().set("First.ClearZone.Z2", Integer.parseInt(args[1]));
								}
					sender.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have set the " + args[0] + " position to " + args[1]);
				}else if(args.length==1){
					Player p = (Player)sender;
					if(args[0].equalsIgnoreCase("X")){
					getConfig().set("First.ClearZone.X2", p.getLocation().getBlockX());
					}else if(args[0].equalsIgnoreCase("Y")){
							getConfig().set("First.ClearZone.Y2", p.getLocation().getBlockY());
							}else if(args[0].equalsIgnoreCase("Z")){
								getConfig().set("First.ClearZone.Z2", p.getLocation().getBlockZ());
								}
					sender.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have set the " + args[0] + " position");
				}
				saveConfig();
			}else if(label.equalsIgnoreCase("setEscapeLobby")){
				getConfig().set("Escape.Lobby.X", ((Player) sender).getLocation().getBlockX());
				getConfig().set("Escape.Lobby.Y",((Player) sender).getLocation().getBlockY());
				getConfig().set("Escape.Lobby.Z",((Player) sender).getLocation().getBlockZ());
				getConfig().set("Escape.Lobby.World",((Player) sender).getWorld().getName());
				sender.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "Set lobby spawnpoint");
				saveConfig();
			}else if(label.equalsIgnoreCase("setEscapeArena")){
				getConfig().set("Escape.Arena.X", ((Player) sender).getLocation().getBlockX());
				getConfig().set("Escape.Arena.Y",((Player) sender).getLocation().getBlockY());
				getConfig().set("Escape.Arena.Z",((Player) sender).getLocation().getBlockZ());
				getConfig().set("Escape.Arena.World",((Player) sender).getWorld().getName());
				sender.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "Set arena spawnpoint");
				saveConfig();
			}else if(label.equalsIgnoreCase("setMinigameSpawn")){
				getConfig().set("Spawn.X", ((Player) sender).getLocation().getBlockX());
				getConfig().set("Spawn.Y",((Player) sender).getLocation().getBlockY());
				getConfig().set("Spawn.Z",((Player) sender).getLocation().getBlockZ());
				getConfig().set("Spawn.World",((Player) sender).getWorld().getName());
				sender.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "Set spawn");
				saveConfig();
			}
		}else{
		}
		return false;
	}
	private boolean inGame(String name) {
		if(getConfig().getBoolean(name + ".inGame")){
			return true;
		}else{
			return false;
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void minigameInventory(InventoryClickEvent e){
		checkIt(e);
	}
	private void checkIt(InventoryClickEvent e) {
			Inventory in = e.getInventory();
			if(in.getName().equalsIgnoreCase(i.getName())){
				e.setCancelled(true);
				if(e.getCurrentItem()!=null){
				getEventFor(e.getCurrentItem().getType(),e);
				}
				e.getWhoClicked().closeInventory();
			}else if(in.getName().equalsIgnoreCase(perks.getName())){
				e.setCancelled(true);
				if(e.getCurrentItem()!=null){
				getEventForPerk(e.getCurrentItem().getType(),e);
				}
				e.getWhoClicked().closeInventory();
			}
	}
	private void getEventForPerk(Material type, InventoryClickEvent e) {
			Player p = (Player) e.getWhoClicked();
		if(type == Material.ARROW){
			List<String> perks = getConfig().getStringList(p.getName() + ".perks");
			if(perks.contains("speed")){
				getConfig().set(p.getName() + ".SelectedPerk", "speed");
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have selected speed as your perk");
			}else{
				int gems = getConfig().getInt(p.getName()+".Gems");
				if(gems>=250){
				getConfig().set(p.getName()+".SelectedPerk", "speed");
				perks.add("speed");
				getConfig().set(p.getName() + ".perks", perks);
				getConfig().set(p.getName() + ".Gems", gems-250);
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have bought the speed perk!");
				}else{
					p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You don't have enough gems to buy that perk!");
				}
			}
		}else if(type == Material.GOLDEN_APPLE){
			List<String> perks = getConfig().getStringList(p.getName() + ".perks");
			if(perks.contains("respawn")){
				getConfig().set(p.getName() + ".SelectedPerk", "respawn");
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have selected the respawn perk!");
			}else{
				int gems = getConfig().getInt(p.getName()+".Gems");
				if(gems>=555){
				getConfig().set(p.getName()+".SelectedPerk", "respawn");
				perks.add("respawn");
				getConfig().set(p.getName() + ".perks", perks);
				getConfig().set(p.getName() + ".Gems", gems-555);
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have bought the respawn perk!");
				}else{
					p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You don't have enough gems to buy that perk!");
				}
			}
		}else if(type == Material.DIAMOND){
			List<String> perks = getConfig().getStringList(p.getName() + ".perks");
			if(perks.contains("double")){
				getConfig().set(p.getName() + ".SelectedPerk", "double");
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have selected the double coins perk!");
			}else{
				int gems = getConfig().getInt(p.getName()+".Gems");
				if(gems>=1500){
				getConfig().set(p.getName()+".SelectedPerk", "double");
				perks.add("double");
				getConfig().set(p.getName() + ".perks", perks);
				getConfig().set(p.getName() + ".Gems", gems-1500);
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have bought the double coins perk!");
				}else{
					p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You don't have enough gems to buy that perk!");
				}
			}
		}else if(type == Material.GHAST_TEAR){
			List<String> perks = getConfig().getStringList(p.getName() + ".perks");
			if(perks.contains("ghost")){
				getConfig().set(p.getName() + ".SelectedPerk", "ghost");
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have selected the ghost perk!");
			}else{
				int gems = getConfig().getInt(p.getName()+".Gems");
				if(gems>=200){
				getConfig().set(p.getName()+".SelectedPerk", "ghost");
				perks.add("ghost");
				getConfig().set(p.getName() + ".perks", perks);
				getConfig().set(p.getName() + ".Gems", gems-200);
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have bought the ghost perk!");
				}else{
					p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You don't have enough gems to buy that perk!");
				}
			}
		}else if(type == Material.SPIDER_EYE){
			List<String> perks = getConfig().getStringList(p.getName() + ".perks");
			if(perks.contains("vision")){
				getConfig().set(p.getName() + ".SelectedPerk", "vision");
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have selected the night vision perk!");
			}else{
				int gems = getConfig().getInt(p.getName()+".Gems");
				if(gems>=105){
				getConfig().set(p.getName()+".SelectedPerk", "vision");
				perks.add("vision");
				getConfig().set(p.getName() + ".perks", perks);
				getConfig().set(p.getName() + ".Gems", gems-105);
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have bought the night vision perk!");
				}else{
					p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You don't have enough gems to buy that perk!");
				}
			}
		}else if(type == Material.WOOL){
				getConfig().set(p.getName() + ".SelectedPerk", null);
				saveConfig();
				p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have deselected all perks!");
		}
	}
	private void getEventFor(Material currentItem, InventoryClickEvent e) {
		for(PotionEffect effect : e.getWhoClicked().getActivePotionEffects())
		{
			e.getWhoClicked().removePotionEffect(effect.getType());
		}
		e.getWhoClicked().setGameMode(GameMode.SURVIVAL);
		e.getWhoClicked().getInventory().clear();
		if(currentItem==Material.SNOW_BALL){
			//Paintball
		}else if(currentItem==Material.ARROW){
			//Archery
		}else if(currentItem==Material.DIAMOND_SWORD){
			//Hunger Games
		}else if(currentItem==Material.SAND){
			boolean hasStarted = getConfig().getBoolean("Escape.HasStarted");
			boolean hasStarted2 = getConfig().getBoolean("Escape.HasStarted2");
			if(!hasStarted&&!hasStarted2){
			escape.add(e.getWhoClicked().getName());
			getConfig().set(e.getWhoClicked().getName() + ".inGame", true);
			getConfig().set(e.getWhoClicked().getName() + ".isIn.Escape", true);
			saveConfig();
			e.getWhoClicked().closeInventory();
			ItemStack is = new ItemStack(Material.STICK, 1);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.GOLD + "Perk changer");
			is.setItemMeta(im);
			e.getWhoClicked().getInventory().addItem(is);
			int x = getConfig().getInt("Escape.Lobby.X");
			int y = getConfig().getInt("Escape.Lobby.Y");
			int z = getConfig().getInt("Escape.Lobby.Z");
			String worldname = getConfig().getString("Escape.Lobby.World");
			World world = getServer().getWorld(worldname);
			e.getWhoClicked().teleport(new Location(world,x,y,z));
			this.canStart = (underworld.size())>=minPlayers;
			if(underworld.size()==1){
				timeInSeconds = 10;
			}
			}
		}else if(currentItem==Material.EGG){
			//Eggtastic
		}
	}
	public boolean containsPlayer(Player player) {
		if(getConfig().getBoolean(player.getName() + ".isIn.Escape")){
			return true;
		}else{
			return false;
		}
	}
	public void removeFrom(String string, Player player) {
		if(string.equalsIgnoreCase("escape")&&underworld.containsKey(player.getName())||string.equalsIgnoreCase("escape")&&containsPlayer(player)){
			underworld.remove(player.getName()); //? player.getName()>?
			getConfig().set(player.getName() + ".inGame", false);
			getConfig().set(player.getName() + ".isIn.Escape",false);
	        saveConfig();
		}
		if(string.equalsIgnoreCase("ghost")&&ghosts.containsKey(player.getName())||string.equalsIgnoreCase("ghost")&&ghostPlayer(player)){
			ghosts.remove(player.getName());
			getConfig().set(player.getName() + ".isGhost", false);
	        saveConfig();
	        for(PotionEffect effect : player.getActivePotionEffects())
	        {
	            player.removePotionEffect(effect.getType());
	        }
	        player.setGameMode(GameMode.SURVIVAL);
	        for(Player onlineplayer : getServer().getOnlinePlayers()){
	        	onlineplayer.showPlayer(player);
	        }
		}
	}
	
	public boolean ghostPlayer(Player player) {
		if(getConfig().getBoolean(player.getName() + ".isGhost")){
			return true;
		}else{
			return false;
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent e){
		if(containsPlayer(e.getEntity())||underworld.containsKey(e.getEntity().getName())){
			removeFrom("escape",e.getEntity());
			addAsGhost(e.getEntity());
		}
	}
	private void addAsPlayer(Player oP) {
		underworld.put(oP.getName(), oP);
		getConfig().set(oP.getName() + ".inGame", true);
		getConfig().set(oP.getName() + ".isIn.Escape", true);
		saveConfig();
		canStart = ghosts.size() >= minPlayers;
	}
	private void addAsGhost(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 50, false)); 
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 50, 50, false)); 
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 1000000, false)); 
        for(Player onlineplayer : getServer().getOnlinePlayers()){
        	onlineplayer.hidePlayer(player);
        }
        removeFrom("escape",player);
        ghosts.put(player.getName(), player);
        getConfig().set(player.getName() + ".isGhost", true);
		saveConfig();
		player.setGameMode(GameMode.CREATIVE);
		
		if(underworld.size() == 0){
			timeInSeconds = 15;
			getConfig().set("Escape.1st", player.getName());
			saveConfig();
			int a = 1;
			int gems = getConfig().getInt(player.getName()+".Gems");
			String perk = getConfig().getString(player.getName() + ".SelectedPerk");
			if(perk==null)perk="None";
			if(perk.equalsIgnoreCase("double")){
				a=2;
			}
			getConfig().set(player.getName() + ".Gems", gems+(10*a));
			player.sendMessage(ChatColor.GOLD + "You got " + 10*a +" gems for being first place!");
			saveConfig();
			player.sendMessage(ChatColor.GOLD + "You now have " + gems +" gems!");
			//TODO: DEBUFFS! rename perks in 'selected perk' in inventory, clear area > replace obsidian, stuck in 'hasstarted' and 'hasstarted2'
			//TODO: make it compactable for more arenas AND make a max of players!
			for(Player oP : getServer().getOnlinePlayers()){
				if(ghosts.containsKey(oP.getName())||ghostPlayer(oP)){
					oP.playSound(oP.getLocation(), Sound.ENDERMAN_TELEPORT, 5, 1);
					addAsPlayer(oP);
					removeFrom("ghost", oP);
					oP.getInventory().clear();
					ItemStack is = new ItemStack(Material.STICK, 1);
					ItemMeta im = is.getItemMeta();
					im.setDisplayName(ChatColor.GOLD + "Perk changer");
					is.setItemMeta(im);
					oP.getInventory().addItem(is);
					int xx = getConfig().getInt("Escape.Lobby.X");
					int yy = getConfig().getInt("Escape.Lobby.Y");
					int zz = getConfig().getInt("Escape.Lobby.Z");
					String worldname = getConfig().getString("Escape.Lobby.World");
					World world = getServer().getWorld(worldname);
					Location lobby = new Location(world,xx,yy,zz);
					oP.teleport(lobby);
					String second = getConfig().getString("Escape.2nd");
					String third = getConfig().getString("Escape.3rd");
					if(second==null)second="None";
					if(third==null)third="None";
					oP.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "Starting new game...");
					oP.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "First place: " + ChatColor.YELLOW + player.getName());
					oP.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "Second place: " + ChatColor.AQUA + second);
					oP.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "Third place: " + ChatColor.RED + third);
			getConfig().set("Escape.HasStarted", false);
			getConfig().set("Escape.HasStarted2", false);
			getConfig().set("Escape.2nd", null);
			getConfig().set("Escape.3rd", null);
			int startx = getConfig().getInt("First.ClearZone.X");
			int starty = getConfig().getInt("First.ClearZone.Y");
			int startz = getConfig().getInt("First.ClearZone.Z");
			int endx = getConfig().getInt("First.ClearZone.X2");
			int endy = getConfig().getInt("First.ClearZone.Y2");
			int endz = getConfig().getInt("First.ClearZone.Z2");
			for(int x=startx;x<endx;x++){
				for(int y=starty;y<endy;y++){
					for(int z=startz;z<endz;z++){
						if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
						player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
						}
					}
					for(int z=startz;z>endz;z--){
						if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
						player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
						}
					}
				}

				for(int y=starty;y>endy;y--){
					for(int z=startz;z<endz;z++){
						if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
						player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
						}
					}
					for(int z=startz;z>endz;z--){
						if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
						player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
						}
					}
				}
			}
			for(int x=startx;x>endx;x--){
				for(int y=starty;y<endy;y++){
					for(int z=startz;z<endz;z++){
						if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
						player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
						}
					}
					for(int z=startz;z>endz;z--){
						if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
						player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
						}
					}
				}

				for(int y=starty;y>endy;y--){
					for(int z=startz;z<endz;z++){
						if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
						player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
						}
					}
					for(int z=startz;z>endz;z--){
						if(player.getWorld().getBlockAt(x,y,z).getType()==Material.OBSIDIAN){
						player.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
						}
							}
						}
					}
				}
			}
		}else if(underworld.size()==1){
			getConfig().set("Escape.2nd", player.getName());
			saveConfig();
			int a = 1;
			int gems = getConfig().getInt(player.getName()+".Gems");
			String perk = getConfig().getString(player.getName() + ".SelectedPerk");
			if(perk==null)perk="None";
			if(perk.equalsIgnoreCase("double")){
				a=2;
			}
			getConfig().set(player.getName() + ".Gems", gems+(7*a));
			player.sendMessage(ChatColor.GOLD + "You got " + 7*a +" gems for being second place!");
			saveConfig();
			player.sendMessage(ChatColor.GOLD + "You now have " + gems +" gems!");
		}else if(underworld.size()==2){
			getConfig().set("Escape.3rd", player.getName());
			saveConfig();
			int a = 1;
			int gems = getConfig().getInt(player.getName()+".Gems");
			String perk = getConfig().getString(player.getName() + ".SelectedPerk");
			if(perk==null)perk="None";
			if(perk.equalsIgnoreCase("double")){
				a=2;
			}
			getConfig().set(player.getName() + ".Gems", gems+(4*a));
			player.sendMessage(ChatColor.GOLD + "You got " + 4*a +" gems for being third place!");
			saveConfig();
			player.sendMessage(ChatColor.GOLD + "You now have " + gems +" gems!");
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onHitByFallingBlock(EntityChangeBlockEvent e){
		if(!(e.getEntity() instanceof FallingBlock))return;
		int x = e.getEntity().getLocation().getBlockX();
		int y = e.getEntity().getLocation().getBlockY();
		int z = e.getEntity().getLocation().getBlockZ();
		for(Player p :  getServer().getOnlinePlayers()){
			if(underworld.containsValue(p)||containsPlayer(p)){
				int px = p.getLocation().getBlockX();
				int py = p.getLocation().getBlockY();
				int pz = p.getLocation().getBlockZ();
				if(px==x&&py==y&&pz==z){
					boolean b = false;
					String perk = getConfig().getString(p.getName() + ".SelectedPerk");
					if(perk==null)perk="None";
					if(perk=="respawn")b=true;
					int respawns = getConfig().getInt(p.getName() + ".Respawns");
					boolean hasRespawnsleft = (b && respawns >= 1);
					if(!hasRespawnsleft){
					addAsGhost(p);
					p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 5, 1);
					}else{
						getConfig().set(p.getName() + ".Respawns", 0);
						saveConfig();
						p.sendMessage(ChatColor.GOLD + "[Escape] " + ChatColor.GREEN + "You have used your extra respawn!");
					}
				}
			}
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onLogOut(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(underworld.containsValue(p)||containsPlayer(p)||ghostPlayer(p)||ghosts.containsKey(p)){
		getServer().dispatchCommand(p, "minigameleave");
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void onChat(PlayerChatEvent e){
		Player p = e.getPlayer();
		if(containsPlayer(p)||underworld.containsKey(p.getName())){
			e.setMessage(ChatColor.GOLD + "[Escape] [Alive] " + ChatColor.RESET + e.getMessage());
		}
		if(ghostPlayer(p)||ghosts.containsKey(p.getName())){
			e.setMessage(ChatColor.GOLD + "[Escape] [Dead] " + ChatColor.RESET + e.getMessage());
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void disallowBlockBreaks(BlockBreakEvent e){
		if(containsPlayer(e.getPlayer())||underworld.containsKey(e.getPlayer().getName())){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.GOLD + "[MiniGames-Protect] " + ChatColor.GREEN + "Don't even think about it!");
		}
		if(ghostPlayer(e.getPlayer())||ghosts.containsKey(e.getPlayer().getName())){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.GOLD + "[MiniGames-Protect] " + ChatColor.GREEN + "Ghosts can't touch anything!");
		}
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void disallowDropping(PlayerDropItemEvent e){
		for(Player p :  getServer().getOnlinePlayers()){
			if(Base.ghosts.containsValue(p)||ghostPlayer(p)){
				p.sendMessage(ChatColor.GOLD +"[Escape] " + ChatColor.GREEN + "Trying to cheat huh?!");
				e.setCancelled(true);
			}
		}
	}
	   @EventHandler(priority=EventPriority.LOWEST)
	    public void disablePvp(EntityDamageByEntityEvent e) {
	        if (e.getDamager() instanceof Player){
	            if (e.getEntity() instanceof Player) {
	            	Player p1 = (Player) e.getDamager();
	            	Player p2 = (Player) e.getEntity();
	            	if(containsPlayer(p1)||ghostPlayer(p1)||ghosts.containsKey(p1.getName())||underworld.containsKey(p1.getName())||containsPlayer(p2)||ghostPlayer(p2)||ghosts.containsKey(p2.getName())||underworld.containsKey(p2.getName())){
	                e.setCancelled(true);
	            	}
	            }
	        }
	    }
	   @EventHandler(priority=EventPriority.LOWEST)
	   public void stopInteractionBetweenGhosts(PlayerInteractEvent e){
		   Player p = e.getPlayer();
		   if(ghostPlayer(p)||ghosts.containsKey(p.getName())){
			   e.setCancelled(true);
			   p.sendMessage(ChatColor.GOLD + "[Escape] " +ChatColor.GREEN + "You are not allowed to interact whilst being a ghost!");
		   }
		   if(containsPlayer(p)||underworld.containsKey(p.getName())){
			   if(p.getItemInHand().getType() == Material.STICK){
				   perks.clear();
				   addPerks(perks,p.getName());
				   p.openInventory(perks);
			   }
		   }
	   }
	   private void addPerks(Inventory perks2,String playername) {
		   ItemStack is = new ItemStack(Material.ARROW, 1); //Speed perk
			ItemStack ist = new ItemStack(Material.GOLDEN_APPLE, 1); //Respawn perk
			ItemStack ist1 = new ItemStack(Material.DIAMOND, 1); //Double coins perk
			ItemStack ist2 = new ItemStack(Material.GHAST_TEAR,1);//Invis
			ItemStack ist3 = new ItemStack(Material.SPIDER_EYE, 1); //Night vision
			ItemStack ist4 = new ItemStack(Material.EMERALD, 1); //gems
			String perk = getConfig().getString(playername + ".SelectedPerk");
			if(perk==null)perk="None";
			ItemStack ist5 = new ItemStack(Material.BAKED_POTATO, 1);
			if(perk=="speed"){
			ist5 = new ItemStack(Material.ARROW, 1); //selected perk
	   		}else if(perk=="respawn"){
					ist5 = new ItemStack(Material.GOLDEN_APPLE, 1); //selected perk
			   		}else if(perk=="double"){
					ist5 = new ItemStack(Material.DIAMOND, 1); //selected perk
			   		}else if(perk=="ghost"){
					ist5 = new ItemStack(Material.GHAST_TEAR, 1); //selected perk
			   		}else if(perk=="vision"){
					ist5 = new ItemStack(Material.SPIDER_EYE, 1); //selected perk
			   		}
			ItemStack air = new ItemStack(Material.WOOL, 1);
			ItemStack air2 = new ItemStack(Material.WOOL, 1);
			ItemMeta im = is.getItemMeta();
			ItemMeta im0 = ist.getItemMeta();
			ItemMeta im1 = ist1.getItemMeta();
			ItemMeta im2 = ist2.getItemMeta();
			ItemMeta im3 = ist3.getItemMeta();
			ItemMeta im4 = ist4.getItemMeta();
			ItemMeta im5 = ist5.getItemMeta();
			ItemMeta air2m = air2.getItemMeta();
			ItemMeta airm = air.getItemMeta();
			im.setDisplayName(ChatColor.GOLD + "Speed perk");
			im0.setDisplayName(ChatColor.GOLD + "Respawn perk");
			im1.setDisplayName(ChatColor.GOLD + "Double coins perk");
			im2.setDisplayName(ChatColor.GOLD + "Ghost perk");
			im3.setDisplayName(ChatColor.GOLD + "Night vision perk");
			im4.setDisplayName(ChatColor.GOLD + "Gems:");
			im5.setDisplayName(ChatColor.GOLD + "Selected Perk:");
			air2m.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
			air2m.setDisplayName("_");
			airm.setDisplayName("_");
			ArrayList<String> lore1 = new ArrayList<String>();
			ArrayList<String> lore2 = new ArrayList<String>();
			ArrayList<String> lore3 = new ArrayList<String>();
			ArrayList<String> lore4 = new ArrayList<String>();
			ArrayList<String> lore5 = new ArrayList<String>();
			ArrayList<String> lore6 = new ArrayList<String>();
			ArrayList<String> lore7 = new ArrayList<String>();
			lore1.add(ChatColor.DARK_RED + "Cost: 250 Gems");
			lore1.add(ChatColor.DARK_RED + "You will get faster ingame");
			lore2.add(ChatColor.DARK_RED + "Cost: 555 Gems");
			lore2.add(ChatColor.DARK_RED + "You will be able to respawn ONCE");
			lore3.add(ChatColor.DARK_RED + "Cost: 1500 Gems");
			lore3.add(ChatColor.DARK_RED + "You will get twice amount of gems you get without this perk");
			lore4.add(ChatColor.DARK_RED + "Cost: 200 Gems");
			lore4.add(ChatColor.DARK_RED + "You won't be visible which makes the game harder for others");
			lore5.add(ChatColor.DARK_RED + "Cost: 105 Gems");
			lore5.add(ChatColor.DARK_RED + "You will get night vision in each game");
			int gems = getConfig().getInt(playername+".Gems");
			lore6.add(ChatColor.DARK_RED + (gems + ""));
			String perk2 = "None";
			if(perk=="speed"){
				perk2="Speed";
			}else if(perk=="respawn"){
				perk2="Respawn";
			}
			lore7.add(ChatColor.DARK_RED + perk2);
			im.setLore(lore1);
			is.setItemMeta(im);
			im0.setLore(lore2);
			ist.setItemMeta(im0);
			im1.setLore(lore3);
			ist1.setItemMeta(im1);
			im2.setLore(lore4);
			ist2.setItemMeta(im2);
			im3.setLore(lore5);
			ist3.setItemMeta(im3);
			im4.setLore(lore6);
			ist4.setItemMeta(im4);
			im5.setLore(lore7);
			ist5.setItemMeta(im5);
			air2.setItemMeta(air2m);
			air.setItemMeta(airm);
			ItemStack[] is2 = new ItemStack[]{is, ist, ist1, ist2, ist3,air,air2,ist4,ist5};
			if(is2 != null){
			perks.addItem(is2);
			}
	   }
	@EventHandler(priority=EventPriority.LOWEST)
	   public void onChat(PlayerCommandPreprocessEvent e){	
		   Player sender = e.getPlayer();
		   if(containsPlayer((Player)sender)||ghostPlayer((Player)sender)||ghosts.containsKey(sender.getName())||underworld.containsKey(sender.getName())){
	           if(!e.getMessage().equalsIgnoreCase("/minigameleave")){
	               e.setCancelled(true);
	               sender.sendMessage(ChatColor.DARK_RED + "Can't use command whilst ingame; " + e.getMessage() + " is not an allowed command!");
	           }
		   }
	   }
}
