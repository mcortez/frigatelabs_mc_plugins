package com.frigatelabs.minecraft.rps;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

import com.iCo6.system.Accounts;
import com.iCo6.system.Holdings;


public class RPSPlayerListener extends PlayerListener 
{
	static RPSPlugin thePlugin;
	
	public static Accounts iConAccounts = new Accounts();
	
	public RPSPlayerListener(RPSPlugin plugin)
	{
		if( thePlugin == null)
		{
			thePlugin = plugin;
		}
	}
	
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Block block = e.getClickedBlock();
			BlockState state = block.getState();
			
			if(state instanceof Sign && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN))
			{
				Sign sign = (Sign)state;
				
				// Parse sign text
				String[] text = sign.getLines();
				SignConfig sc = SignConfig.getConfig(text);

				// If sign is a valid Pay to Activate sign
				if( sc.valid )
				{
					// Determine if clicking player has enough holdings (money)
					Player sourcePlayer = e.getPlayer();
					Holdings sourceHoldings = iConAccounts.get(e.getPlayer().getDisplayName()).getHoldings(); 
					
					if( sourceHoldings.hasEnough(sc.price) )
					{
						// Player has enough money, transfer fee to player listed on sign
						Holdings targetHoldings = iConAccounts.get(sc.targetPlayerName).getHoldings();
						sourceHoldings.subtract(sc.price);
						targetHoldings.add(sc.price);

						
						// Schedule reset back to a sign
                        scheduleSignReset(block);
						
						// Change block to a Redstone Torch
						if (block.getType() == Material.SIGN_POST) 
						{
                            block.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte) 0x5,true);
                            
						} else if (block.getType() == Material.WALL_SIGN) {
							
							byte wallSignData = block.getData();
							
							
                            if (wallSignData == 0x2) 
                            {
                                    block.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x4, true);
                                    
                            } else if (wallSignData == 0x3) 
                            {
                                    block.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x3, true);
                                    
                            } else if (wallSignData == 0x4) 
                            {
                                    block.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x2, true);
                                    
                            } else if (wallSignData == 0x5) 
                            {
                                    block.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(),(byte) 0x1, true);
                                    
                            } else {
                                    // This shouldn't happen
                            }
						}

						
						// Protect the torch from being broken
                        thePlugin.magicTorchLocations.add(block.getLocation());

						
					} else {
						sourcePlayer.sendMessage("You cannot afford $" + sc.price);
					}
					
					// This prevents placing a block
					e.setCancelled(true);
				}
			}
		}
	}
	
	public void scheduleSignReset(final Block block)
	{
		thePlugin.logDebug("Scheduling reset of block");
		
		// Check if setup correctly as a sign and save pertinent info and schedule reset
		BlockState state = block.getState();
		if(state instanceof Sign)
		{
			// Final variables to be passed to Scheduler
			thePlugin.logDebug("Saving Block Data");
			
			// Sign Block
			final byte savedData = block.getData();
			final String[] savedText = ((Sign)state).getLines();
			final boolean wasSignPost = (block.getType() == Material.SIGN_POST);
			
			// Block sign is attached to
			final Block savedAttachedBlock = getAttachedBlock(block);
			final int savedAttachedBlockTypeID = savedAttachedBlock.getTypeId();
			
		
			// Schedule reset
	        thePlugin.getServer().getScheduler().scheduleSyncDelayedTask(thePlugin, new Runnable() {
	            public void run() 
	            {
	    			thePlugin.logDebug("Reset running");
	            	
	            	// First remove the torch
	            	block.setType(Material.AIR);
	            	
	            	// Check to see if the block the sign was attached to has changed 
	            	if( savedAttachedBlock.getTypeId() != savedAttachedBlockTypeID )
	            	{
	            		// looks like it did change, drop sign instead of restoring
						block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SIGN, 1));
						
	            	} else {
	            		// looks like it stayed the same type re-attach

	            		// When restoring sign, CLEAR to air, THEN set type, THEN set type & data
	            		// otherwise you get invalid placement errors
	            		if( wasSignPost )
	            		{
	    	            	block.setType(Material.SIGN_POST);
	    	            	block.setTypeIdAndData(Material.SIGN_POST.getId(),(byte) savedData, true);
	            		} else {
			            	block.setType(Material.WALL_SIGN);
			            	block.setTypeIdAndData(Material.WALL_SIGN.getId(),(byte) savedData, true);
	            		}
		        
		            	// restore saved text
		            	BlockState state = block.getState();
		    			if(state instanceof Sign)
		    			{
		    				Sign sign = (Sign)state;
		    				for( int i = 0; i<savedText.length; i++ )
		    				{
		    					sign.setLine(i, savedText[i]);
		    				}
		    			}
	            		
	            	}
	            	
	            	// Remove break protection
	    			thePlugin.magicTorchLocations.remove(block.getLocation());
	            }
	        }, 10L);                            

			
			
		}
		
	}

	public static Block getAttachedBlock(Block block)
	{
    	Block connectedBlock = null;
    	
    	if(block != null)
    	{
    		byte data = block.getData();
	
	    	
			if (block.getType() == Material.SIGN_POST) 
			{
				connectedBlock = block.getRelative(0,-1,0);
				
			} else if (block.getType() == Material.WALL_SIGN) {
		    	// Find block it's supposed to be connected to
		    	if( data == 0x5 )
		    	{
		    		// Pointing East
		    		connectedBlock = block.getRelative(-1,0,0);
		    		
		    	} else if( data == 0x4 )
		    	{
		    		// Pointing West
		    		connectedBlock = block.getRelative(1,0,0);
		    		
		    	} else if( data == 0x2 )
		    	{
		    		// Pointing North
		    		connectedBlock = block.getRelative(0,0,1);
		    		
		    	} else if( data == 0x3 )
		    	{
		    		// Pointing South
		    		connectedBlock = block.getRelative(0,0,-1);
		    	}			
			}
    	}
    	
    	return connectedBlock;
	}

}
