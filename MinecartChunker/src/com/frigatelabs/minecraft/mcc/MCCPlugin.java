package com.frigatelabs.minecraft.mcc;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MCCPlugin  extends JavaPlugin
{
	private Logger log = Logger.getLogger("Minecraft");
	private boolean DEBUG = true;
	private String debugName;

	public static boolean enabled = false;
	
	MCCVehicleListener vehiclelistener = new MCCVehicleListener(this);
	MCCWorldListener worldlistener = new MCCWorldListener(this);
	MCCServerListener serverlistener = new MCCServerListener(this);

	//HashMap<UUID, Location> carts = new HashMap<UUID,Location>();
	
	@Override
	public void onDisable() {
		enabled = false;

		PluginDescriptionFile pdf = getDescription();
		log.info("[" + debugName + "]: v" + pdf.getVersion() + " has been disabled.");		
	}

	@Override
	public void onEnable() {
		
		PluginManager pm = getServer().getPluginManager();

		pm.registerEvent(Type.SERVER_COMMAND, serverlistener, Priority.Normal, this);
		pm.registerEvent(Type.VEHICLE_MOVE, vehiclelistener, Priority.Monitor, this);
		pm.registerEvent(Type.CHUNK_LOAD, worldlistener, Priority.Monitor, this);
		
		enabled = true;
		
		PluginDescriptionFile pdf = getDescription();
		debugName = pdf.getName();
		log.info("[" + debugName + "]: v" + pdf.getVersion() + " has been enabled.");		

		/*
		for( World world : getServer().getWorlds() )
		{
			logDebug("World: " + world.getName());
		}
		*/
		World world = getServer().getWorld("world");
		List<Entity> allEntities = world.getEntities();
		
		for(Entity curEntity : allEntities)
		{
			if(    curEntity instanceof Minecart 
				|| curEntity instanceof StorageMinecart 
				|| curEntity instanceof PoweredMinecart)
			{
				/*
				if( !carts.containsKey(curEntity.getUniqueId()) )
				{
					carts.put(curEntity.getUniqueId(), curEntity.getLocation().getBlock().getLocation());
				}
				*/
				
				Location cartLocation = curEntity.getLocation();
				logDebug("Cart at: " + cartLocation + " requesting chunk be loaded.");
				cartLocation.getBlock().getChunk().load();
			}
		}

	}
	
	public void logDebug(String s)
	{
		if( DEBUG )
		{
			log.info("[" + debugName + "] " + s);
		}
	}
}
