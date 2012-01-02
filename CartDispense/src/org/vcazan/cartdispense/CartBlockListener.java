package org.vcazan.cartdispense;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;

import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;



public class CartBlockListener extends BlockListener{

	public final CartDispense plugin;
	
	public CartBlockListener(CartDispense instance) {
		this.plugin = instance;
	}
	Logger log = Logger.getLogger("Minecraft");
	Location spawnCart;


	public void onBlockDispense(BlockDispenseEvent event){
		ItemStack dispenseItem = event.getItem();
		if (dispenseItem.getTypeId() == 328 ||dispenseItem.getTypeId() == 343 || dispenseItem.getTypeId() == 342){
			event.setCancelled(true);
			Block block = event.getBlock();
			Location under = block.getLocation();
			under.setY(block.getY()-1);
			
			if (checkForTrack(block.getLocation()) == true || checkForTrack(under) == true){
				World world = block.getLocation().getWorld();
				
				switch (dispenseItem.getTypeId()) {
/*				
					 case 328:world.spawn(spawnCart, CraftMinecart.class); break;
					 case 343:world.spawn(spawnCart, CraftPoweredMinecart.class); break;
					 case 342:world.spawn(spawnCart, CraftStorageMinecart.class); break;
*/
				 case 328:world.spawn(spawnCart, Minecart.class); break;
				 case 343:world.spawn(spawnCart, PoweredMinecart.class); break;
				 case 342:world.spawn(spawnCart, StorageMinecart.class); break;
				 }
				
				
				Dispenser dispenserBlock = (Dispenser) block.getState();
				Inventory dispenserInv = dispenserBlock.getInventory();
				int slot = dispenserInv.first(dispenseItem);
				if(dispenseItem.getAmount() <= 1)
				{
					log.info("Clearing slot " + slot);
					dispenserInv.clear(slot);
				} else {
					dispenseItem.setAmount(dispenseItem.getAmount() - 1);
					dispenserInv.setItem(slot, dispenseItem);
					
					
					log.info("Reducing slot " + slot + " to " + dispenseItem.getAmount());
				}
				
			}			
			
			
		}
	}
	
	public boolean checkForTrack(Location loc){
		Block block = loc.getBlock();
		for(BlockFace face : BlockFace.values()) {
			if (block.getRelative(face).getTypeId() == 66|| block.getRelative(face).getTypeId() == 27 || block.getRelative(face).getTypeId() == 28) {
				loc.setY(block.getY() + face.getModY() );
				loc.setZ(block.getZ() + face.getModZ() );
				loc.setX(block.getX() + face.getModX() );
				spawnCart = loc;
				return true;
			}
			
		}
		return false;
	}
}