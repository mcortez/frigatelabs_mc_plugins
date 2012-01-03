package com.frigatelabs.minecraft.mcc;

import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MCCPlugin  extends JavaPlugin
{
	private Logger log = Logger.getLogger("Minecraft");
	private boolean DEBUG = true;

	public static boolean enabled = false;
	
	MCCVehicleListener vehiclelistener = new MCCVehicleListener(this);

	@Override
	public void onDisable() {
		enabled = false;

		PluginDescriptionFile pdf = getDescription();
		log.info("[" + pdf.getName() + "]: v" + pdf.getVersion() + " has been disabled.");		
	}

	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Type.VEHICLE_MOVE, vehiclelistener, Priority.Monitor, this);
		
		enabled = true;
		
		PluginDescriptionFile pdf = getDescription();
		log.info("[" + pdf.getName() + "]: v" + pdf.getVersion() + " has been enabled.");		
		
	}
	
	public void logDebug(String s)
	{
		if( DEBUG )
		{
			log.info("[MCC] " + s);
		}
	}
}
