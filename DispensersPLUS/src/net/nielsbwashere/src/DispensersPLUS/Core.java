package net.nielsbwashere.src.DispensersPLUS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.server.v1_8_R1.AxisAlignedBB;
import net.minecraft.server.v1_8_R1.DamageSource;
import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityCreeper;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.World;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Directional;
import org.bukkit.plugin.java.JavaPlugin;
public class Core extends JavaPlugin implements Listener{
public static Core instance;
public List<Integer> toRemove = new ArrayList<Integer>();
@Override
public void onEnable() {
	Bukkit.getPluginManager().registerEvents(this, this);
	instance = this;
	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
		@Override
		public void run() {
			for(int i : toRemove){
				Bukkit.getScheduler().cancelTask(i);
			}
		}}, 0, 20*10);
	ItemStack is = new ItemStack(Material.DISPENSER,1);
	ItemMeta im = is.getItemMeta();
	im.setDisplayName(ChatColor.GOLD + "Beam dispenser");
	is.setItemMeta(im);
	Bukkit.addRecipe(new ShapedRecipe(is).shape("CDC","BAB","CDC").setIngredient('C', Material.DIAMOND).setIngredient('D', Material.GOLD_BLOCK).setIngredient('B', Material.REDSTONE_BLOCK).setIngredient('A', Material.DISPENSER));
}
	@Override
	public void onDisable() {
		saveConfig();
		Bukkit.getScheduler().cancelTasks(this);
	}
	@EventHandler
	public void onUse(BlockPlaceEvent e){
		if(e.getItemInHand()==null||e.getItemInHand().getType()!=Material.DISPENSER)return;
		if(!e.getItemInHand().hasItemMeta()||!e.getItemInHand().getItemMeta().hasDisplayName())return;
		if(!e.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Beam dispenser"))return;
		Location l = e.getBlock().getLocation();
		String st = l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
		List<String> beamList = getConfig().getStringList("BeamDispensers");
		if(beamList==null||beamList.isEmpty())beamList=new ArrayList<String>();
		if(!beamList.contains(st)){
			beamList.add(st);
			getConfig().set("BeamDispensers",beamList);
			saveConfig();
		}
	}
	@EventHandler
	public void onBreak(BlockBreakEvent e){
		Location l = e.getBlock().getLocation();
		String st = l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
		List<String> beamList = getConfig().getStringList("BeamDispensers");
		if(beamList==null||beamList.isEmpty())beamList=new ArrayList<String>();
		if(beamList.contains(st)){
			beamList.remove(st);
			getConfig().set("BeamDispensers",beamList);
			saveConfig();
			e.getBlock().setType(Material.AIR);
			ItemStack is = new ItemStack(Material.DISPENSER,1);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.GOLD + "Beam dispenser");
			is.setItemMeta(im);
			e.getPlayer().getInventory().addItem(is);
		}
	}
@EventHandler
public void onDispense(BlockDispenseEvent e){
	if(!(e.getBlock().getState() instanceof Dispenser))return;
	Dispenser d = ((Dispenser)e.getBlock().getState());
	if(e.getItem().getType()==Material.DIAMOND_PICKAXE){
		Block b = getFacing(d.getBlock());
		if(b!=null&&(canDestroy(b.getType()))){
			b.breakNaturally();
			damageFirstPickaxe(d.getInventory(),e.getItem(),e);
		}
		e.setCancelled(true);
	}else if(e.getItem().getType()==Material.DIAMOND_AXE){
		Block b = getFacing(d.getBlock());
		if(b!=null&&(canAxe(b.getType()))){
			b.breakNaturally();
			damageFirstPickaxe(d.getInventory(),e.getItem(),e);
		}
		e.setCancelled(true);
	}else if(e.getItem().getType()==Material.DIAMOND_HOE){
		Block b = getFacing(d.getBlock());
		if(b!=null&&(canHoe(b.getType()))){
			b.breakNaturally();
			damageFirstPickaxe(d.getInventory(),e.getItem(),e);
		}
		e.setCancelled(true);
	}else if(e.getItem().getType()==Material.DIAMOND_SPADE){
		Block b = getFacing(d.getBlock());
		if(b!=null&&(canShovel(b.getType()))){
			b.breakNaturally();
			damageFirstPickaxe(d.getInventory(),e.getItem(),e);
		}
		e.setCancelled(true);
	}else if(e.getItem().getType()==Material.SHEARS){
		Block b = getFacing(d.getBlock());
		if(b!=null&&(canShear(b.getType()))){
			b.breakNaturally();
			damageFirstPickaxe(d.getInventory(),e.getItem(),e);
		}
		e.setCancelled(true);
	}
	else if(e.getItem().getType()==Material.SEEDS){
		Block b = getFacing(d.getBlock());
		if(b!=null&&b.getType()==Material.AIR){
			b.setType(Material.CROPS);
			consume(d.getInventory(),e.getItem(),e.getBlock());
		}
		e.setCancelled(true);
	}
	else if(e.getItem().getType()==Material.CARROT_ITEM){
		Block b = getFacing(d.getBlock());
		if(b!=null&&b.getType()==Material.AIR){
			b.setType(Material.CARROT);
			consume(d.getInventory(),e.getItem(),e.getBlock());
		}
		e.setCancelled(true);
	}
	else if(e.getItem().getType()==Material.POTATO_ITEM){
		Block b = getFacing(d.getBlock());
		if(b!=null&&b.getType()==Material.AIR){
			b.setType(Material.POTATO);
			consume(d.getInventory(),e.getItem(),e.getBlock());
		}
		e.setCancelled(true);
	}
	else if(e.getItem().getType()==Material.NETHER_WARTS){
		Block b = getFacing(d.getBlock());
		if(b!=null&&b.getType()==Material.AIR){
			b.setType(Material.NETHER_STALK);
			consume(d.getInventory(),e.getItem(),e.getBlock());
		}
		e.setCancelled(true);
	}
	else if(e.getItem().getType()==Material.MELON_SEEDS){
		Block b = getFacing(d.getBlock());
		if(b!=null&&b.getType()==Material.AIR){
			b.setType(Material.MELON_STEM);
			consume(d.getInventory(),e.getItem(),e.getBlock());
		}
		e.setCancelled(true);
	}
	else if(e.getItem().getType()==Material.PUMPKIN_SEEDS){
		Block b = getFacing(d.getBlock());
		if(b!=null&&b.getType()==Material.AIR){
			b.setType(Material.PUMPKIN_STEM);
			consume(d.getInventory(),e.getItem(),e.getBlock());
		}
		e.setCancelled(true);
	}
	else if(e.getItem().getType()==Material.DIAMOND_SWORD){
		Block b = getFacing(d.getBlock());
		if(b!=null&&b.getType()==Material.WEB){
			b.breakNaturally();
			damageFirstPickaxe(d.getInventory(),e.getItem(),e);
		}else if(b==null||b.getType()==Material.AIR){
			Location l = b.getLocation();
			AxisAlignedBB aabb = AxisAlignedBB.a(l.getBlockX()-1, l.getBlockY()-1, l.getBlockZ()-1, l.getBlockX()+1, l.getBlockY()+1, l.getBlockZ()+1);
			World w = ((CraftWorld)l.getWorld()).getHandle();
			@SuppressWarnings("unchecked")
			List<Entity> ent = w.getEntities(new EntityCreeper(w), aabb);
			for(Entity ec : ent){
				if(!(ec instanceof EntityLiving))continue;
				if(!ec.isAlive())continue;
				ec.damageEntity(DamageSource.MAGIC, 7f);
				Dispenser dd = ((Dispenser)e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation()).getState());
				damageFirstPickaxe(dd.getInventory(), e.getItem(), e);
				if(!contains(dd.getInventory(),Material.DIAMOND_SWORD))break;
			}
		}
		e.setCancelled(true);
	}
	else if(e.getItem().getType()==Material.REDSTONE){
		e.setCancelled(true);
		Location l = e.getBlock().getLocation();
		String st = l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
		List<String> beamList = getConfig().getStringList("BeamDispensers");
		if(beamList==null||beamList.isEmpty())beamList=new ArrayList<String>();
		if(!beamList.contains(st))return;
		Block bf = getFacing(e.getBlock());
		BlockFace b = e.getBlock().getFace(bf);
		particles(e.getBlock().getLocation(),b,10);
		l = e.getBlock().getLocation();
		AxisAlignedBB aabb = AxisAlignedBB.a(l.getBlockX()-1-xMin(b), l.getBlockY()-1-yMin(b), l.getBlockZ()-1-zMin(b), l.getBlockX()+1+xMax(b), l.getBlockY()+1+yMax(b), l.getBlockZ()+1+zMax(b));
		World w = ((CraftWorld)l.getWorld()).getHandle();
		@SuppressWarnings("unchecked")
		List<Entity> ent = w.getEntities(new EntityCreeper(w), aabb);
		for(Entity ec : ent){
			if(!(ec instanceof EntityLiving))continue;
			if(!ec.isAlive())continue;
			((EntityLiving)ec).setOnFire(20*2);
		}
	}
	else if(e.getItem().getType()==Material.NETHER_STAR){
		e.setCancelled(true);
		Location l = e.getBlock().getLocation();
		String st = l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
		List<String> beamList = getConfig().getStringList("BeamDispensers");
		if(beamList==null||beamList.isEmpty())beamList=new ArrayList<String>();
		if(!beamList.contains(st))return;
		consume(d.getInventory(),e.getItem(),e.getBlock());
		Block bf = getFacing(e.getBlock());
		final BlockFace b = e.getBlock().getFace(bf);
		final Location fl = e.getBlock().getLocation();
		final LocationLoop ll = new LocationLoop(new Location(fl.getWorld(),fl.getX(),fl.getY(),fl.getZ()),-1,-2,1);
		final int length = (int) (32+Math.random()*16+Math.random()*16);
		final TestVar tv = new TestVar(0);
		tv.i = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run() {
				if(ll.j==1&&ll.i==1){
					ll.j=-2;
					ll.z+=1;
				}
				if(ll.i==1){
					ll.i=-1;
					ll.j+=1;
				}else ll.i++;
				if(ll.z>length){
					Core.instance.toRemove.add(tv.i);
				}
				Block bl = fl.getWorld().getBlockAt(asLocation(ll.l,b,ll.i,ll.j,ll.z));
				if(canDestroy(bl)){
					bl.breakNaturally();
					fl.getWorld().playEffect(bl.getLocation(), Effect.MOBSPAWNER_FLAMES,1);
				}
				ll.l=new Location(fl.getWorld(),fl.getX(),fl.getY(),fl.getZ());
			}
			private boolean canDestroy(Block bl) {
				return bl!=null&&bl.getType()!=Material.AIR&&(bl.getType()==Material.STONE||bl.getType()==Material.COBBLESTONE||bl.getType()==Material.NETHERRACK||bl.getType()==Material.DIRT||bl.getType()==Material.GRASS||bl.getType()==Material.SAND
						||bl.getType()==Material.SOUL_SAND||bl.getType()==Material.GRAVEL||bl.getType()==Material.SAND||bl.getType()==Material.SANDSTONE);
			}
		}, 0, 4);
	}
	else if(e.getItem().getType()==Material.WORKBENCH){
		e.setCancelled(true);
		Block facing = getFacing(e.getBlock());
		Location l = e.getBlock().getLocation();
		if(block(l.add(0,1,0)).getType()==Material.DISPENSER&&block(l.add(0,1,0)).getType()==Material.DISPENSER){
			Block b = block(l);
			Block b2 = block(l.add(0,-1,0));
			HashMap<Character,ItemStack> needed = new HashMap<Character,ItemStack>();
			Dispenser d1 = ((Dispenser)b.getState());
			Dispenser d2 = ((Dispenser)b2.getState());
			for(int i=0;i<9;i++){
				ItemStack is = d2.getInventory().getContents()[i];
				if(is==null||is.getType()==Material.AIR){
					needed.put((char)(i+'a'),null);
					continue;
				}
				needed.put((char)(i+'a'),is);
			}
			ShapedRecipe target = null;
			while(Bukkit.recipeIterator().hasNext()){
				Recipe r = Bukkit.recipeIterator().next();
				if(!(r instanceof ShapedRecipe))continue;
				ShapedRecipe sr = (ShapedRecipe)r;
				List<String> sha = new ArrayList<String>();
				convertToShape(needed,sha,d2.getInventory().getContents());
				if(sha.size()==0)continue;
				if(!equals(sr.getShape(),sha.toArray(new String[]{}),needed,sr.getIngredientMap()))continue;
			target=sr;
			break;
			}
			if(target==null)return;
			boolean canCraft = true;
			List<ItemStack> isl = combine(target.getIngredientMap().values());
			for(ItemStack is : isl){
				if(is==null||is.getType()==Material.AIR)continue;
				if(!d1.getInventory().contains(is.getType(),is.getAmount())){
					canCraft=false;
					break;
				}
			}
			if(!canCraft)return;
			for(ItemStack is : target.getIngredientMap().values()){
				if(is==null||is.getType()==Material.AIR)continue;
				consume(d1.getInventory(),is,b);
			}
			facing.getWorld().dropItemNaturally(facing.getLocation(), target.getResult());
		}
	}
	else if(e.getItem().getType().isBlock()){
		Block b = getFacing(d.getBlock());
		if(b!=null&&b.getType()==Material.AIR){
			b.setType(e.getItem().getType());
			consume(d.getInventory(),e.getItem(),e.getBlock());
		}
		e.setCancelled(true);
	}
}
private boolean equals(String[] shape, String[] shape2, HashMap<Character, ItemStack> needed, Map<Character, ItemStack> ingredientMap) {
	if(shape==null||shape2==null)return false;
	if(shape.length!=shape2.length)return false;
	for(int i=0;i<shape.length;i++){
		if(!shape[i].equals(shape2[i]))return false;
	}
	for(Entry<Character,ItemStack> ecis : needed.entrySet()){
		ItemStack confirmed = confirm(ecis.getValue(), ecis.getKey(), shape2);
		if(Null(ingredientMap.get(ecis.getKey()))&&Null(confirmed))continue;
		else if(!Null(ingredientMap.get(ecis.getKey()))&&!Null(confirmed)&&confirmed.getType()==ingredientMap.get(ecis.getKey()).getType())continue;
		else return false;
	}
	return true;
}
private ItemStack confirm(ItemStack value, Character key, String[] shape2) {
	for(String s : shape2){
		if(s.contains(key+""))return value;
	}
	return null;
}
private void convertToShape(HashMap<Character, ItemStack> needed, List<String> sha, ItemStack[] contents) {
	int startX=0, endX=0;
	for(int i=0;i<2;i++){
		if(Null(contents[0*3+i])&&Null(contents[1*3+i])&&Null(contents[2*3+i]))startX++;
		else break;
	}
	endX=startX;
	for(int i=startX;i<3;i++){
		if(!Null(contents[0*3+i])||!Null(contents[1*3+i])||!Null(contents[2*3+i]))endX++;
		else break;
	}

	int startY=0, endY=0;
	for(int j=0;j<2;j++){
		if(Null(contents[j*3+0])&&Null(contents[j*3+1])&&Null(contents[j*3+2]))startY++;
		else break;
	}
	endY=startY;
	for(int j=startY;j<3;j++){
		if(!Null(contents[j*3+0])||!Null(contents[j*3+1])||!Null(contents[j*3+2]))endY++;
		else break;
	}
	char start = 'a';
	for(int j=startY;j<endY;j++){
		String s = "";
		for(int i=startX;i<endX;i++){
			needed.put(start,contents[j*3+i]);
			s=s+start;
			start++;
		}
		sha.add(s);
	}
}
private boolean Null(ItemStack itemStack) {
	return itemStack==null||itemStack.getType()==Material.AIR;
}
private List<ItemStack> combine(Collection<ItemStack> values) {
	List<ItemStack> isl = new ArrayList<ItemStack>();
	outerloop: for(ItemStack is : values){
		for(ItemStack is2 : isl){
			if(is2.isSimilar(is)&&is2.getAmount()<64){
				is2.setAmount(is2.getAmount()+1);
				continue outerloop;
			}
		}
		isl.add(is);
	}
	return isl;
}
private Block block(Location add) {
	return add.getWorld().getBlockAt(add);
}
protected Location asLocation(Location fl, BlockFace face, int i, int j, int z) {
	if(face==BlockFace.DOWN)
		return fl.add(i,-z,j);
	if(face==BlockFace.UP)
		return fl.add(i,z,j);
	if(face==BlockFace.SOUTH)
		return fl.add(i,j,z);
	if(face==BlockFace.NORTH)
		return fl.add(i,j,-z);
	if(face==BlockFace.EAST)
		return fl.add(z,j,i);
	if(face==BlockFace.WEST)
		return fl.add(-z,j,i);
	return null;
}
private int xMin(BlockFace bf) {return bf==BlockFace.WEST?16:0;}
private int yMin(BlockFace bf) {return bf==BlockFace.DOWN?16:0;}
private int zMin(BlockFace bf) {return bf==BlockFace.NORTH?16:0;}
private int xMax(BlockFace bf) {return bf==BlockFace.EAST?16:0;}
private int yMax(BlockFace bf) {return bf==BlockFace.UP?16:0;}
private int zMax(BlockFace bf) {return bf==BlockFace.SOUTH?16:0;}
private void particles(Location start, BlockFace face, int i) {
	int xyz = face==BlockFace.UP?0:face==BlockFace.DOWN?1:face==BlockFace.SOUTH?2:face==BlockFace.NORTH?3:face==BlockFace.EAST?4:5;
		for(int j=0;j<i;j++){
			double jj = j/2.0;
			if(jj>5)break;
			if(xyz==0)
				start.getWorld().playEffect(start.add(0,jj,0), Effect.MOBSPAWNER_FLAMES, 1);
			if(xyz==1)
				start.getWorld().playEffect(start.add(0,-jj,0), Effect.MOBSPAWNER_FLAMES, 1);
			if(xyz==2)
				start.getWorld().playEffect(start.add(0,0,jj), Effect.MOBSPAWNER_FLAMES, 1);
			if(xyz==3)
				start.getWorld().playEffect(start.add(0,0,-jj), Effect.MOBSPAWNER_FLAMES, 1);
			if(xyz==4)
				start.getWorld().playEffect(start.add(jj,0,0), Effect.MOBSPAWNER_FLAMES, 1);
			if(xyz==5)
				start.getWorld().playEffect(start.add(-jj,0,0), Effect.MOBSPAWNER_FLAMES, 1);
		}
}
private boolean contains(Inventory inventory, Material type) {
	for(ItemStack is : inventory.getContents()){
		if(is==null)continue;
		if(is.getType()==type)return true;
	}
	return false;
}
private void consume(Inventory i, ItemStack dispense, Block b) {
	int in=-1;
	for(ItemStack is : i.getContents()){
		in++;
		if(is==null||is.getType()==Material.AIR){
			if(in==i.getContents().length-1)in=-1;
			continue;
		}
		if(is.getType().name().equals(dispense.getType().name())){
			consume(i,in,is,b,dispense);
			return;
		}
	}
}
private boolean canDestroy(Material type) {
	return (new ArrayList<Material>(Arrays.asList(new Material[]{
		
	}))).contains(type)||type.name().endsWith("ORE")||type.name().endsWith("STONE")||type.name().startsWith("IRON")||type.name().startsWith("GOLD")||type.name().startsWith("DIAMOND")||type.name().startsWith("EMERALD")
	||type.name().startsWith("LAPIS")||type.name().startsWith("REDSTONE")||type.name().startsWith("OBSIDIAN");
}
private boolean canAxe(Material type) {
	return (new ArrayList<Material>(Arrays.asList(new Material[]{
		
	}))).contains(type)||type.name().endsWith("PLANKS")||type.name().startsWith("WOOD")||type.name().endsWith("LOG");
}
private boolean canShovel(Material type) {
	return (new ArrayList<Material>(Arrays.asList(new Material[]{
		
	}))).contains(type)||type.name().endsWith("DIRT")||type.name().endsWith("GRASS");
}
private boolean canHoe(Material type) {
	return (new ArrayList<Material>(Arrays.asList(new Material[]{
			Material.POTATO, Material.CARROT, Material.MELON_BLOCK, Material.COCOA
	}))).contains(type)||type.name().endsWith("MELON")||type.name().endsWith("PUMPKIN")||type.name().endsWith("SEEDS")||type.name().endsWith("CROPS")||type.name().endsWith("CANE");
}
private boolean canShear(Material type) {
	return (new ArrayList<Material>(Arrays.asList(new Material[]{
		
	}))).contains(type)||type.name().endsWith("LEAVES");
}
private Block getFacing(Block block) {
	return block.getRelative(((Directional)block.getState().getData()).getFacing());
}
private void damageFirstPickaxe(Inventory i, ItemStack dispense, BlockDispenseEvent e) {
	if(blockBelow(e.getBlock(),Material.BEACON))return;
	int in=-1;
	for(ItemStack is : i.getContents()){
		in++;
		if(is==null||is.getType()==Material.AIR)continue;
		if(is.getType().name().equals(dispense.getType().name())){
			is.setDurability((short) (is.getDurability()+1));
			if(is.getDurability()>=is.getType().getMaxDurability())
				consume(i,in,is,e.getBlock(), e.getItem());
			return;
		}
	}
}
private boolean blockBelow(Block block, Material type) {
	Block b = block.getRelative(BlockFace.DOWN);
	if(b==null||b.getType()==Material.AIR)return false;
	return b.getType().name().equals(type.name());
}
@SuppressWarnings("deprecation")
private void consume(Inventory i, int in, ItemStack is, Block dispenser, ItemStack dispense) {
	if(is.getAmount()>1)
	is.setAmount(is.getAmount()-1);
	else{
		byte data = dispenser.getData();
		ItemStack[] cont = clone(i.getContents(), dispense, i.getContents()[in]);
		((Dispenser)dispenser.getState()).getInventory().setContents(new ItemStack[]{});
		dispenser.setType(Material.AIR);
		dispenser.setType(Material.DISPENSER);
		cont[in]=null;
		((Dispenser)dispenser.getState()).getInventory().setContents(cont);
		dispenser.setData(data);
	}
}
private ItemStack[] clone(ItemStack[] contents, ItemStack dispense, ItemStack original) {
	ItemStack[] clone = new ItemStack[contents.length];
	int i=-1;
	for(ItemStack is : contents){
		i++;
		if(is==null)continue;
		ItemStack clo = is.clone();
		if(is.getAmount()==0&&is.equals(original)){
			clo.setAmount(1);
		}else if(is.getAmount()==0)continue;
		clone[i]=clo;
	}
	return clone;
}
}
class LocationLoop{
	Location l;
	int i,j,z;
	public LocationLoop(Location l, int i, int j, int k) {
		this.i=i;
		this.j=j;
		this.z=k;
		this.l=l;
	}
}
class TestVar{
	public int i=0;
	public TestVar(int i) {
		this.i=i;
	}
}