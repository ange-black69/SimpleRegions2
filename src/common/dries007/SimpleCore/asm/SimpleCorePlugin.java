package dries007.SimpleCore.asm;

import java.io.File;
import java.util.Map;

import net.minecraft.src.Packet201PlayerInfo;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import dries007.SimpleCore.SimpleCore;
import dries007.SimpleCore.VanillaInterface;

public class SimpleCorePlugin implements IFMLLoadingPlugin, IFMLCallHook
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
		return new String[]	{"dries007.SimpleCore.asm.SimpleCoreTransformer"};
	}

	@Override
	public String getModContainerClass() 
	{
		return "dries007.SimpleCore.SimpleCore";
	}

	@Override
	public String getSetupClass() 
	{
		return "dries007.SimpleCore.asm.SimpleCorePlugin";
	}

	@Override
	public void injectData(Map<String, Object> data) 
	{
		if(data.containsKey("coremodLocation"))
		{
			myLocation = (File) data.get("coremodLocation");
			config = new File((File) data.get("mcLocation") + File.separator + "config", "SimpleCore.cfg");
		}
	}

	private void addOverrides() 
	{
		if(ObfuscationReflectionHelper.obfuscation)
		{
			SimpleCoreTransformer.addClassOverride("dg", "Needed to display rank on the tap-screen.");
		}
		else
		{
			SimpleCoreTransformer.addClassOverride("net.minecraft.src.Packet201PlayerInfo", "Needed to display rank on the tap-screen.");
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
		final String CATEGORY_RANK = "Ranks";
		final String CATEGORY_OVERRIDE = "Override classes";
		
		Configuration configuration = new Configuration(SimpleCorePlugin.config);
		try
		{
			configuration.load();
			Property prop;
			
			prop = configuration.get(CATEGORY_RANK, "defaultRank", "Guest");
			prop.comment = "Default rank";
			SimpleCore.defaultRank = prop.value;
	    	
			prop = configuration.get(CATEGORY_RANK, "opRank", "Admin");
			prop.comment = "Name of the OP rank";
			SimpleCore.opRank = prop.value;
			
	    	prop = configuration.get(configuration.CATEGORY_GENERAL, "spawnOverride", true);
			prop.comment = "When a player repsawns to the server spawn, override the location to allow 1 block specific spawn zone. Use setspawn to set the spawn, you can specify ranks for different spawn per rank.";
			SimpleCore.spawnOverride = prop.getBoolean(true);
			
			prop = configuration.get(configuration.CATEGORY_GENERAL, "tap-makeup", "%Rc[%Rn]%U");
			prop.comment = "%U = username ; %Hn = Health numaric ; %Rn = rankname ; %Rc = ranks color ; Request MOAR @ ssm.dries007.net";
			VanillaInterface.tapMakeup = prop.value;
			
			for(String name : SimpleCoreTransformer.override.keySet())
			{
				prop = configuration.get(CATEGORY_OVERRIDE, name, true);
				prop.comment = SimpleCoreTransformer.override.get(name);
				
				if (prop.getBoolean(true))
				{
					SimpleCoreTransformer.override.put(name, prop.comment);
				}
				else
				{
					SimpleCoreTransformer.override.remove(name);
				}
			}
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
