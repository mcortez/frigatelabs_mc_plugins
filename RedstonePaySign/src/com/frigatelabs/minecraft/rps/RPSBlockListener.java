package com.frigatelabs.minecraft.rps;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class RPSBlockListener extends BlockListener 
{
	static RPSPlugin thePlugin;
	
	public RPSBlockListener(RPSPlugin plugin)
	{
		if( thePlugin == null)
		{
			thePlugin = plugin;
		}
	}
	
	public void onSignChange(SignChangeEvent event) 
	{
		if( RPSPlugin.enabled )
		{
			String[] signText = event.getLines();
	
			SignConfig sc = SignConfig.getConfig(signText);
			if( !sc.validationMsg.equals("") )
			{
				event.getPlayer().sendMessage(sc.validationMsg);
				
				if( !sc.valid )
				{
					Block block = event.getBlock();
					event.setCancelled(true);
					block.setType(Material.AIR);
					block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SIGN, 1));
				}
			}
		}
	}
	
	
    public void onBlockBreak(BlockBreakEvent event) 
    {
    	//log.info("Checking for magic torch");
    	//log.info("Current Location: " + event.getBlock().getLocation());
    	
		if (event.getBlock().getType().equals(Material.REDSTONE_TORCH_ON)) 
		{
	    	//log.info("It's a torch, might be magic");
			
            for (Location loc : thePlugin.magicTorchLocations) 
            {
            	//log.info("Checking against: " + loc);
                if (loc.equals(event.getBlock().getLocation())) 
                {
		            //event.getPlayer().sendMessage("[EJPlugin] You cannot break my magic torches my friend!");
		            event.setCancelled(true);
		            return;
                }
            }
		} else {
	    	//log.info("Not a torch");
			
		}
	}
	
}
