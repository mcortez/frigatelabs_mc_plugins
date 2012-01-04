package com.frigatelabs.minecraft.mcc;

import org.bukkit.Chunk;
import org.bukkit.World;
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
		
		Chunk curChunk = v.getLocation().getBlock().getChunk();
		World curWorld = curChunk.getWorld();
		
		int chunkLoadRange = 3;
		
		int curX = curChunk.getX();
		int curZ = curChunk.getZ();
		
		int deltaChunkX;
		int deltaChunkZ;
		
		// thePlugin.logDebug("Cart moved, currently in Chunk @" + curX + ", " + curZ);		
		
		for(int deltaX = -chunkLoadRange; deltaX <= chunkLoadRange; deltaX++ )
		{
			for(int deltaZ = -chunkLoadRange; deltaZ <= chunkLoadRange; deltaZ++ )
			{
				deltaChunkX = curX + deltaX;
				deltaChunkZ = curZ + deltaZ;
				
				Chunk deltaChunk = curWorld.getChunkAt(deltaChunkX, deltaChunkZ);
				
				// thePlugin.logDebug("**** Force Loading Chunk: " + deltaChunkX + ", " + deltaChunkZ);
				deltaChunk.load();
				
				//thePlugin.logDebug("Checking chunk at " + deltaChunkX + ", " + deltaChunkZ);

				/*
				if( !deltaChunk.isLoaded() )
				{
					deltaChunk.load();
				}
				*/
			}
		}
		
	}
}
