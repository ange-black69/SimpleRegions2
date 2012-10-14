package dries007.SimpleRegions.asm;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.src.Packet201PlayerInfo;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import dries007.SimpleCore.SimpleCore;
import dries007.SimpleCore.VanillaInterface;
import dries007.SimpleRegions.SimpleRegions;

public class SimpleRegionsPlugin implements IFMLLoadingPlugin, IFMLCallHook
{
	public static File myLocation;
	public static File config;

	@Override
	public String[] getLibraryRequestClass() 
	{	
		return null;
	}

	@Override
	public String[] getASMTransformerClass() 
	{
		return new String[]	{"dries007.SimpleRegions.asm.SimpleRegionsTransformer"};
	}

	@Override
	public String getModContainerClass() 
	{
		return "dries007.SimpleRegions.SimpleRegions";
	}

	@Override
	public String getSetupClass() 
	{
		return "dries007.SimpleRegions.asm.SimpleRegionsPlugin";
	}

	@Override
	public void injectData(Map<String, Object> data) 
	{
		if(data.containsKey("coremodLocation"))
		{
			myLocation = (File) data.get("coremodLocation");
			config = new File((File) data.get("mcLocation") + File.separator + "config", "SimpleRegions.cfg");
		}
	}

	private void addOverrides() 
	{
		if(ObfuscationReflectionHelper.obfuscation)
		{
			SimpleRegionsTransformer.addClassOverride("afy", "Needed to protect from fire.");
			SimpleRegionsTransformer.addClassOverride("gz", "Needed to protect from players.");
			SimpleRegionsTransformer.addClassOverride("ahr", "Needed to protect from growth.");
			SimpleRegionsTransformer.addClassOverride("um", "Needed to protect from explosions.");
		}
		else
		{
			SimpleRegionsTransformer.addClassOverride("net.minecraft.src.BlockFire", "Needed to protect from fire.");
			SimpleRegionsTransformer.addClassOverride("net.minecraft.src.NetServerHandler", "Needed to protect from players.");
			SimpleRegionsTransformer.addClassOverride("net.minecraft.src.BlockSapling", "Needed to protect from growth.");
			SimpleRegionsTransformer.addClassOverride("net.minecraft.src.Explosion", "Needed to protect from explosions.");
		}
	}

	@Override
	public Void call() throws Exception 
	{
		addOverrides();
		config();
		return null;
	}
	
	public static void config()
	{
		final String CATEGORY_OVERRIDE = "Override classes";
		final String CATEGORY_WE = "WorldEdit";
		
		Configuration configuration = new Configuration(SimpleRegionsPlugin.config);
		try
		{
			configuration.load();
			Property prop;
			prop = configuration.get(Configuration.CATEGORY_ITEM, "ItemWandID", 900);
			SimpleRegions.ItemWandID = prop.getInt();
			
			prop = configuration.get(CATEGORY_WE, "maxChanges", 500000);
			prop.comment = "The maximum amount of blocks that can be edited at once.";
			SimpleRegions.maxChanges = prop.getInt();
			
			prop = configuration.get(CATEGORY_WE, "warningLevel", 100000);
			prop.comment = "If this amount of blocks (or more) is edited, a server wide message will be sent.";
			SimpleRegions.warningLevel = prop.getInt();
			
			prop = configuration.get(CATEGORY_WE, "vertLevel", 128);
			prop.comment = "The hight a selection will be set at using vert or up. Setting this to 256 will make more lagg but makes editing large things easier.";
			SimpleRegions.vertLevel = prop.getInt();
			
			prop = configuration.get(CATEGORY_WE, "bedrockRemoval", false);
			prop.comment = "Set this to true to make selections select layer 0 when using vert or down.";
			SimpleRegions.bedrockRemoval = prop.getBoolean(false);
			
			prop = configuration.get(CATEGORY_WE, "secureTNT", true);
			prop.comment = "Calculate every block destroyed in the blast, laggy. If false, you only calculate the explosion source position.";
			SimpleRegions.secureTNT = prop.getBoolean(true);
		} 
		catch (Exception e) 
		{
			System.out.println("SimpleCore has a problem loading it's configuration");
			System.out.println(e.getMessage());
		} 
		finally 
		{
			configuration.save();
		}
	}
}
