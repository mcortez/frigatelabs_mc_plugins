package com.frigatelabs.minecraft.mcc;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleMoveEvent;


public class MCCVehicleListener extends VehicleListener 
{
	static MCCPlugin thePlugin;
	
	public MCCVehicleListener (MCCPlugin plugin)
	{
		thePlugin = plugin;
	}
	
	public void onVehicleMove(VehicleMoveEvent e) 
	{
		Vehicle v = e.getVehicle();
		
		Block curBlock = v.getLocation().getBlock();
		Chunk curChunk = curBlock.getChunk();
		World curWorld = curChunk.getWorld();
		
		int chunkLoadRange = 3;
		
		int curX = curChunk.getX();
		int curZ = curChunk.getZ();
		
		int deltaChunkX;
		int deltaChunkZ;
		
		
		Location oldLocation = thePlugin.carts.get(v.getUniqueId());
		if( oldLocation != null )
		{
			Location newLocation = curBlock.getLocation();
			if( !oldLocation.equals(newLocation) )
			{
				thePlugin.logDebug("Cart moved [" + oldLocation.getBlockX() + ", " + oldLocation.getBlockZ() + "] - [" + newLocation.getBlockX() + ", " + newLocation.getBlockZ() + "]");

				if( !oldLocation.getBlock().getChunk().equals(curBlock.getChunk()) )
				{
					for(int deltaX = -chunkLoadRange; deltaX <= chunkLoadRange; deltaX++ )
					{
						for(int deltaZ = -chunkLoadRange; deltaZ <= chunkLoadRange; deltaZ++ )
						{
							deltaChunkX = curX + deltaX;
							deltaChunkZ = curZ + deltaZ;
							
							Chunk deltaChunk = curWorld.getChunkAt(deltaChunkX, deltaChunkZ);
							
							// thePlugin.logDebug("**** Force Loading Chunk: " + deltaChunkX + ", " + deltaChunkZ);
							if( !deltaChunk.load() )
							{
								thePlugin.logDebug("Chunk.load() returned false!");
							}
							
						}
					}
				}
			}
		}
		
		thePlugin.carts.put(v.getUniqueId(), curBlock.getLocation() );
		
		
		
		
		
		// thePlugin.logDebug("Cart moved, currently in Chunk @" + curX + ", " + curZ);		
		
		
	}
}
