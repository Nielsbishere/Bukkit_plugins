package niels.b.was.here;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
public class scheduledTasks025S implements Runnable {
	private Base base;
	public scheduledTasks025S(Base b){
		this.base = b;
	}
	public void run() {
		for(Player p :  base.getServer().getOnlinePlayers()){
			if(Base.underworld.containsValue(p)||base.containsPlayer(p)){
				if(p.getLevel()==0){
						int x = p.getLocation().getBlockX();
						int y = p.getLocation().getBlockY()+6;
						int z = p.getLocation().getBlockZ();
						World world = p.getWorld();
						FallingBlock fe = world.spawnFallingBlock(new Location(world,x,y,z), Material.OBSIDIAN, (byte)0);
						fe.setVelocity(new Vector(0,-0.50,0));
						fe.setDropItem(false);
					}
				}
			}
	}

}
