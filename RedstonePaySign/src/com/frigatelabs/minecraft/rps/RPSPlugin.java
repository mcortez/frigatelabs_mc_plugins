package com.frigatelabs.minecraft.rps;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RPSPlugin extends JavaPlugin
{
	private Logger log = Logger.getLogger("Minecraft");
	private boolean DEBUG = true;

	public static boolean enabled = false;

	private final RPSBlockListener blockListener = new RPSBlockListener(this);
	private final RPSPlayerListener playerListener = new RPSPlayerListener(this);

	public List<Location> magicTorchLocations = new ArrayList<Location>();
	
	
	@Override
	public void onDisable() {
		enabled = false;
		log.info("[RPS] Redstone Pay Sign Plug-in Disabled");
		
	}

	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		
		// Use these to detect if iConomy has been disabled/enabled
		//pm.registerEvent(Type.PLUGIN_ENABLE, new server(this), Priority.Monitor, this);
		//pm.registerEvent(Type.PLUGIN_DISABLE, new server(this), Priority.Monitor, this);
		
		pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		
		
		enabled = true;
		log.info("[RPS] Redstone Pay Sign Plug-in Enabled");
		
	}
	
	public void logDebug(String s)
	{
		if( DEBUG )
		{
			log.info("[RPS] " + s);
		}
	}
}
