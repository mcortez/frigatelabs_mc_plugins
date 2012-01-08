package com.frigatelabs.minecraft.rt;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.bekvon.bukkit.residence.protection.ResidenceManager;
import com.bekvon.bukkit.residence.selection.SelectionManager;

public class RTPlugin extends JavaPlugin 
{
	private Logger log = Logger.getLogger("Minecraft");
	private boolean DEBUG = false;
	private String debugName;

	public static boolean enabled = false;

	@Override
	public void onDisable() {
		enabled = false;

		PluginDescriptionFile pdf = getDescription();
		log.info("[" + debugName + "]: v" + pdf.getVersion() + " has been disabled.");		
	}

	@Override
	public void onEnable() {
		
		
		/*
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.VEHICLE_MOVE, vehiclelistener, Priority.Monitor, this);
		pm.registerEvent(Type.CHUNK_LOAD, worldlistener, Priority.Monitor, this);
		*/
		enabled = true;
		
		PluginDescriptionFile pdf = getDescription();
		debugName = pdf.getName();
		log.info("[" + debugName + "]: v" + pdf.getVersion() + " has been enabled.");		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) 
	{
		if(command.getName().equalsIgnoreCase("rtdebug"))
		{ 
			DEBUG = !DEBUG;
			
			logDebug("Debug set to: " + DEBUG, true);
			return true;
		} 
		
		Player player = null;
		if (sender instanceof Player) 
		{
			player = (Player) sender;
			
			if(command.getName().equalsIgnoreCase("rtselect"))
			{
				sender.sendMessage("Attempting to select current residence around " + player.getDisplayName());
				
				SelectionManager smanager = Residence.getSelectionManager();
				ResidenceManager rmanager = Residence.getResidenceManager();
				
				ClaimedResidence cr = rmanager.getByLoc(player.getLocation());
				if( cr != null)
				{
					cr = cr.getTopParent();

					String areaName = null;
					
					String[] areas = cr.getAreaList();
					if( areas.length > 1 )
					{
						if( args.length > 0 )
						{
							for( String name : areas )
							{
								for( String arg : args )
								{
									if( name.equalsIgnoreCase(arg) )
									{
										areaName = arg;
										break;
									}
								}
								if( areaName != null) break;
							}
						}
						
						if( areaName == null )
						{
							sender.sendMessage("Please specify area name, possible options include:");					
							for( String name : areas )
							{
								sender.sendMessage(name);
								return true;
								
							}
						}
					} else if(areas.length == 1) {
						areaName = areas[0];
					} else {
						sender.sendMessage("Does not appear to be any areas.");					
						return true;
					}
					
					
					if( areaName != null )
					{
						CuboidArea ca = cr.getArea(areaName);
						
						smanager.placeLoc1(player.getName(), ca.getLowLoc());
						smanager.placeLoc1(player.getName(), ca.getHighLoc());
						smanager.showSelectionInfo(player);
						return true;
					}
					
				} else {
					sender.sendMessage("You do not appear to be in a residence.");					
					return true;
				}
				//String placeName = cr.
			}
			
		}		
		
		return false;
	}

	public void logDebug(String s)
	{
		logDebug(s, false);
	}
	
	public void logDebug(String s, boolean force)
	{
		if( DEBUG || force)
		{
			log.info("[" + debugName + "] " + s);
		}
	}
	
}
