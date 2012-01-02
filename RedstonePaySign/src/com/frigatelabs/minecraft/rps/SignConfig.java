package com.frigatelabs.minecraft.rps;


import com.iCo6.system.Accounts;


public class SignConfig 
{
	public String targetPlayerName = "";
	public double price = 0.0f;
	public boolean valid = false;
	public String validationMsg = "";
	
	public static Accounts iConAccounts = new Accounts();
	
	
	public static SignConfig getConfig(String[] signText)
	{
		//Logger log = Logger.getLogger("Minecraft");
		
		SignConfig sc = new SignConfig();
		
		
		
		for(String s : signText)
		{
			if( s.startsWith("Cost: $") || s.startsWith("Price: $") || s.startsWith("Pay: $") )
			{
				String[] costParts = s.split(" ",0);
				String priceString = costParts[1].replace('$', ' ').trim();
				
				try
				{
					sc.price = Double.parseDouble(priceString);
					
					if( sc.price < 0.0f )
					{
						sc.validationMsg = "You cannot specify a negative cost [" + sc.price + "]";
						sc.price = 0.0f;
					}
					
				} catch (Exception e) {
					sc.validationMsg = "Cannot parse cost [" + sc.price + "]";
				}
			} else {
				
				//log.info("Checking: [" + s + "]");
				if( iConAccounts.exists(s) )
				{
					sc.targetPlayerName = s;
					//log.info("Found: [" + sc.targetPlayerName + "]");
				}
			}
		}
		
		if( sc.price != 0.0f && !sc.targetPlayerName.equals(""))
		{
			sc.valid = true;
			sc.validationMsg = "Payment sign properly configured for $" + sc.price + " to be paid to " + sc.targetPlayerName + ".";
		} else {
			if( sc.price != 0.0f )
			{
				sc.validationMsg = "Could not find a valid iConomy account listed on sign.";
			}
		}
		
		return sc;
	}
}
