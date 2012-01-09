package com.frigatelabs.minecraft.ct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.ShapedRecipes;
import net.minecraft.server.ShapelessRecipes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CTPlugin extends JavaPlugin {
	private Set<Integer> forbidden = new HashSet<Integer>();
	private Logger log = Logger.getLogger("minecraft");

	@SuppressWarnings("unchecked")
	public void onEnable() {
		List<Integer> list = this.getConfig().getList("ids", null);
		if (list == null || list.size() == 0) {
			list = new ArrayList<Integer>();
			list.add(46);
			this.getConfig().set("ids", list);
			this.saveConfig();
			log.info("[CTPlugin] Default configuration created.");
		}
		
		log.info("[CTPlugin] Attempting to forbid the following: ");
		for(Integer i : list)
		{
			log.info("[CTPlugin] - " + i);
		}
		
		
		int g = 0;
		forbidden = new HashSet<Integer>(list);
		Iterator<?> itr = CraftingManager.getInstance().b().iterator();
		
		while (itr.hasNext()) {
			Object o = itr.next();
			if (o instanceof ShapedRecipes) {
//				log.info("Checking: " + ((ShapedRecipes) o).b().id);
				
				if (forbidden.contains(((ShapedRecipes) o).b().id)) {
					log.info("[CTPlugin] Forbidden: " + ((ShapedRecipes) o).b().getItem().j());
					itr.remove();
					++g;
				}
			} else if (o instanceof ShapelessRecipes) {
//				log.info("Checking: " + ((ShapelessRecipes) o).b().id);
				
				if (forbidden.contains(((ShapelessRecipes) o).b().id)) 
				{
					log.info("[CTPlugin] Forbidden: " + ((ShapelessRecipes) o).b().getItem().j());
					itr.remove();
					++g;
				}
			} else {
				log.info("Type is: " + o.getClass() + " :: " + o.toString());
			}
		}
		log.info("[CTPlugin] " + g + " recipes disabled.");
	}

	public void onDisable() {
		this.saveConfig();
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage("[CTPlugin] You can't do that!");
			return true;
		}
		int i = 0;
		try {
			i = Integer.parseInt(args[1]);
		} catch (Exception e) {
			return false;
		}
		if (args[0].equalsIgnoreCase("add")) {
			forbidden.add(i);
		} else if (args[0].equalsIgnoreCase("remove")) {
			forbidden.remove(i);
		} else {
			return false;
		}
		sender.sendMessage("[CTPlugin] This will take effect after the next server restart.");
		this.getConfig().set("ids", new ArrayList<Integer>(forbidden));
		return true;
	}
}