package net.nielsbwashere.src.Structure2Code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin implements Listener {
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("saveStructure")){
			if(!sender.isOp()||!(sender instanceof Player)){
				sender.sendMessage(ChatColor.DARK_RED + "Only opped players can use that command!");
				return false;
			}
			if(args.length!=11){
				sender.sendMessage(ChatColor.DARK_RED + "Invalid usage: /saveStructure <xStart> <yStart> <zStart> <xEnd> <yEnd> <zEnd> <xMiddle> <yMiddle> <zMiddle> <name> <mod/plugin/structure>");
				return false;
			}
			String name = args[9];
			String modPlugin = args[10].equalsIgnoreCase("mod")?"m":args[10].equalsIgnoreCase("plugin")?"p":args[10].equalsIgnoreCase("structure")?"s":"";
			if(modPlugin.equals("")){
				sender.sendMessage(ChatColor.DARK_RED + "Please put in mod or plugin at the end!");
				return false;
			}
			int[] ints = new int[9];
			for(int i=0;i<9;i++){
				try{
				ints[i]=Integer.parseInt(args[i]);
				}catch(Exception e){
					sender.sendMessage(ChatColor.DARK_RED + "Coordinates are meant to be a number!");
					return false;
				}
			}
			int xMin=0, yMin=0, zMin=0, xMax=0, yMax=0, zMax=0;
			if(ints[0]>ints[3])xMax=ints[0];
			else xMax=ints[3];
			if(ints[0]<ints[3])xMin=ints[0];
			else xMin=ints[3];
			if(ints[1]>ints[4])yMax=ints[1];
			else yMax=ints[4];
			if(ints[1]<ints[4])yMin=ints[1];
			else yMin=ints[4];
			if(ints[2]>ints[5])zMax=ints[2];
			else zMax=ints[5];
			if(ints[2]<ints[5])zMin=ints[2];
			else zMin=ints[5];
			World w = ((Player)sender).getWorld();
			List<String> saved = new ArrayList<String>();
			for(int i=xMin;i<=xMax;i++){
				for(int j=yMin;j<=yMax;j++){
					for(int k=zMin;k<=zMax;k++){
						Block b = w.getBlockAt(i,j,k);
						if(b==null)continue;
						Material m = b.getType();
						String base2 = (b.getData()!=0?b.getData()+"":"0");
						String base1 = modPlugin.equals("p")?"world.getBlockAt("+getCoords(i,j,k,ints[6],ints[7],ints[8])+").setType(Material."+m.name()+");world.getBlockAt("+getCoords(i,j,k,ints[6],ints[7],ints[8])+").setData((byte)"+base2+");":modPlugin.equals("m")?
						"world.setBlock("+getCoords(i,j,k,ints[6],ints[7],ints[8])+","+m.getId()+base2+",3"+");":modPlugin.equals("s")?(i-ints[6])+","+(j-ints[7])+","+(k-ints[8])+","+m.getId()+","+b.getData():"";
						if(m==Material.AIR&&modPlugin.equals("p"))base1="world.getBlockAt("+getCoords(i,j,k,ints[6],ints[7],ints[8])+").setType(Material."+m.name()+");";
						if(base1.equals(""))continue;
						if(b.getType()==Material.MOB_SPAWNER){
							CreatureSpawner cs = ((CreatureSpawner)b.getState());
							String Name = cs.getCreatureTypeName();
							short id = cs.getCreatureType().getTypeId();
							if(modPlugin.equals("p"))base1=base1+"((CreatureSpawner)world.getBlockAt("+getCoords(i,j,k,ints[6],ints[7],ints[8])+").getState()).setCreatureTypeByName(\""+Name+"\");";
							else if(modPlugin.equals("m"))base1=base1+"((TileEntityMobSpawner)world.getTileEntity("+getCoords(i,j,k,ints[6],ints[7],ints[8])+")).setMobID("+id+");";
							else base1=base1+","+Name;
						}
						if(b.getType()==Material.CHEST)base1=base1+"//TODO: Chest!";
						saved.add(base1);
					}
				}
			}
			File f = new File(getDataFolder().getPath() + "/saves/");
			f.mkdirs();
			f = new File(f.getPath() + "/" + name + ".txt");
			try {
			f.createNewFile();
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			for(String s : saved){
				bw.write(s);
				bw.newLine();
			}
			bw.close();
			fw.close();
			sender.sendMessage(ChatColor.GOLD + "Succesfully transformed it into code!");
			} catch (IOException e) {
				sender.sendMessage(ChatColor.DARK_RED + "Couldn't create a file!");
				return false;
			}
		}
		return false;
	}
	@SuppressWarnings("deprecation")
	public static void set(JavaPlugin jp, String name, World w, int i, int j, int k){
		File f = new File(jp.getDataFolder().getPath() + "/saves/");
		f.mkdirs();
		f = new File(f.getPath() + "/" + name + ".txt");
		try {
		f.createNewFile();
		FileReader fw = new FileReader(f);
		BufferedReader bw = new BufferedReader(fw);
		String s = "";
		while((s=bw.readLine())!=null){
			String[] sav = s.split(",");
			int x=0,y=0,z=0,id=0,data=0;
			try{
				x=Integer.parseInt(sav[0]);
				y=Integer.parseInt(sav[1]);
				z=Integer.parseInt(sav[2]);
				id=Integer.parseInt(sav[3]);
				data=Integer.parseInt(sav[4]);
			}catch(Exception e){continue;}
			w.getBlockAt(i+x,j+y,k+z).setType(Material.getMaterial(id));
			w.getBlockAt(i+x,j+y,k+z).setData((byte)data);
			if(id==Material.MOB_SPAWNER.getId()&&sav.length>=6&&sav[5]!=null)((CreatureSpawner)w.getBlockAt(i+x,j+y,k+z).getState()).setCreatureTypeByName(sav[5]);
		}
		bw.close();
		fw.close();
		} catch (IOException e) {
			return;
		}
	}
	private String getCoords(int i, int j, int k, int l, int m, int n) {
		int ii = (i-l);
		int jj = (j-m);
		int kk = (k-n);
		return "i" + (ii<0?ii:"+"+ii) +","+"j"+(jj<0?jj:"+"+jj)+","+"k"+(kk<0?kk:"+"+kk);
	}
}
