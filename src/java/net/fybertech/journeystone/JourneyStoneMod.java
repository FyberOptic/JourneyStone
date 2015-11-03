package net.fybertech.journeystone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import net.fybertech.meddle.Meddle;
import net.fybertech.meddleapi.ConfigFile;
import net.fybertech.meddleapi.ConfigFile.ConfigKey;
import net.fybertech.meddleapi.MeddleAPI;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;


public class JourneyStoneMod {
	
	public static ItemJourneyStone journeyStone = new ItemJourneyStone();
	public static CommonProxy proxy;
	
	private static final int DEFAULT_ITEM_ID = 5151;
	private int journeyStoneID = -1;
	
	
	private void loadConfig()
	{
		ConfigFile config = new ConfigFile(new File(Meddle.getConfigDir(), "journeystone.cfg"));
		config.load();
		
		journeyStoneID = config.get(ConfigFile.key("journeyStoneID", DEFAULT_ITEM_ID));
		
		if (config.hasChanged()) config.save();
	}
	
	
	public void init()
	{
		loadConfig();	
		Meddle.LOGGER.info("[JourneyStone] Using item id " + journeyStoneID);
		
		MeddleAPI.registerItem(journeyStoneID, "journeyStone",  journeyStone);
		
		CraftingManager.getInstance().addRecipe(new ItemStack(journeyStone), " S ", "RER", " S ", Character.valueOf('S'), Blocks.stone, Character.valueOf('R'), Items.redstone, Character.valueOf('E'), Items.ender_pearl); 
		
		proxy = (CommonProxy)MeddleAPI.createProxyInstance("net.fybertech.journeystone.CommonProxy", "net.fybertech.journeystone.ClientProxy");
		proxy.init();		
	}
	
}
