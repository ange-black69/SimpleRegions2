package dries007.SimpleRegions;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dries007.SimpleCore.*;
import dries007.SimpleRegions.commands.*;
import dries007.SimpleRegions.regions.API;
import dries007.SimpleRegions.regions.VanillaInterface;

public class SimpleRegions extends DummyModContainer 
{
	@SidedProxy(clientSide = "dries007.SimpleRegions.ClientProxy", serverSide = "dries007.SimpleRegions.CommonProxy")
	public static CommonProxy proxy;
	
	public static Integer ItemWandID;
	public static Item ItemWand; 
	
	public static Integer maxChanges;
	public static Integer warningLevel;
	public static Integer vertLevel;
	public static boolean bedrockRemoval;
	public static boolean secureTNT;
	
	public static MinecraftServer server;

	public static NBTTagCompound regionData;
	
	public static int opID;
	public static Map<String, String> availableFlags = new HashMap();
	public static Set<String> availableRegions = new HashSet();
	
	public SimpleRegions()
	{
		super(new ModMetadata());
		ModMetadata meta	=	getMetadata();
        meta.modId      	=	"SimpleRegions";
        meta.name       	=	"SimpleRegions";
        meta.version    	=	"0.1";
        meta.authorList 	=	Arrays.asList("Dries007");
        meta.credits		=	"Dries007, ChickenBones for making his mods open-source!";
        meta.description	=	"Flag based region system with world edit functionality";
        meta.url        	=	"http://ssm.dries007.net";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller)
	{
		bus.register(this);
		MinecraftForge.EVENT_BUS.register(this);
		return true;
	}
	
	@Subscribe
	public void init(FMLInitializationEvent event) 
	{
		if (event.getSide().isClient())
		{
			net.minecraftforge.client.MinecraftForgeClient.preloadTexture("/dries007/SimpleRegions/wands.png");
		}
		ItemWand = new ItemWand(ItemWandID);
		LanguageRegistry.addName(ItemWand, "Wand");
		GameRegistry.addShapelessRecipe(new ItemStack(ItemWand, 1), new Object[] {new ItemStack(Item.stick, 1)});
		MinecraftForge.EVENT_BUS.register(new VanillaInterface());
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Subscribe
	public void serverStarting(FMLServerStartingEvent event)
	{
		server = ModLoader.getMinecraftServerInstance();
		
		Permissions.addPermission("SR.all");
		
		addCommands();
		addFlags();
		
		regionData = data.loadData("regionData");
		opID = regionData.getInteger("opID");
	}
	
	@Subscribe
	public void serverStopping(FMLServerStoppingEvent event)
	{
		regionData.setInteger("opID", opID);
		data.saveData(regionData, "regionData");
	}
	
	@ForgeSubscribe
	public void chuckSave(WorldEvent.Save event)
	{
		data.saveData(regionData, "regionData");
	}
	
	public void addFlags()
	{
		API.addFlag("nofirespread", "This flag turns off firespread in this region. Mod needs to be a code mod for this!");		//1
		API.addFlag("nogrowth", "This flag turns off treegrowth in this region. Mod needs to be a code mod for this!");			//2
		API.addFlag("noexplosions", "This flag turns off explosions in this region. Mod needs to be a code mod for this!");		//3
		API.addFlag("noplayerblock", "This flag makes placing and removing blocks impossible, exept for the members.");			//4
		API.addFlag("noplayeritem", "This flag makes use of items impossible (food too!), exept for the members.");				//5
		API.addFlag("godmode", "This flag makes all players invincible in this region. Overrules pvp.");						//6
		API.addFlag("nopvp", "This flag makes pvp impossible in this region.");													//7
		API.addFlag("nofalldamage", ";-)");																						//8
		API.addFlag("nochest", "This tag makes opening chest impossible");														//9
	}
	
	public void addCommands()
	{
		ICommandManager commandManager = server.getCommandManager();
		ServerCommandManager manager = ((ServerCommandManager) commandManager); 
		
		manager.registerCommand(new CommandSet());
		manager.registerCommand(new CommandRegion());
		manager.registerCommand(new CommandMembers());
		manager.registerCommand(new CommandRegen());
		manager.registerCommand(new CommandExpand());
		manager.registerCommand(new CommandSphere());
		manager.registerCommand(new CommandReplace());
		manager.registerCommand(new CommandExtinguish());
		manager.registerCommand(new CommandFix());
		manager.registerCommand(new CommandFlags());
		manager.registerCommand(new CommandDrain());
		manager.registerCommand(new CommandTest());		//DEBUG
	}
	
	public static int IncrementAndGetOpID()
	{
		opID ++;
		return opID;
	}
	
	 public static float rot(float par0)
	 {
		 par0 %= 360.0F;
		 if (par0<0)
		 {
			 par0 +=360.0F;
		 }
		 return par0;
	 }
	 
	public static String[] getRegions()
	{
		return availableRegions.toArray(new String[availableRegions.size()]);
	}
	
	public static String[] getFlags()
	{
		return availableFlags.keySet().toArray(new String[availableFlags.size()]);
	}
	
	public static boolean saveData(NBTTagCompound data, String subfolder)
	{
		String folder;
		if (SimpleCore.server.isDedicatedServer()) folder = SimpleCore.server.getFolderName();
		else folder = "saves" + File.separator + SimpleCore.server.getFolderName();
		try
		{
			String savelocation = folder+File.separator+"SimpleRegions"+File.separator+subfolder;
			File file = new File(savelocation);
			if(!file.exists())
			{
				file.mkdirs();
			}
			
			File var4 = new File(savelocation, data.getName() + ".dat");
			
			if (var4.exists())
			{
				return false;
			}
			
			CompressedStreamTools.writeCompressed(data, new FileOutputStream(var4));

			return true;
		}
		catch (Exception var5)
		{
			ModLoader.getLogger().severe("Failed to save " + data.getName() + ".dat!");
			return false;
		}
	}
	
	public static NBTTagCompound loadData(String filename, String subfolder)
	{
		String folder;
		if (SimpleCore.server.isDedicatedServer()) folder = SimpleCore.server.getFolderName();
		else folder = "saves" + File.separator + SimpleCore.server.getFolderName();
		try
		{
			String savelocation = folder+File.separator+"SimpleRegions"+File.separator+subfolder;
			File var2 = new File(savelocation, filename + ".dat");
			
			if (var2.exists())
			{
				return CompressedStreamTools.readCompressed(new FileInputStream(var2));
			}
			else
			{
				return new NBTTagCompound();
			}
		}
		catch (Exception var3)
		{
			ModLoader.getLogger().severe("Failed to load SimpleRegions' " + filename + " data!");
			return null;
		}
	}
	
	public static boolean isMemberOfRegion(String region, String username)
	{
		NBTTagList tagList = SimpleRegions.regionData.getCompoundTag(region).getTagList("Members");
		
		for(int i = 0; i < tagList.tagCount();i++)
		{
			try
			{
				NBTTagString stringTag = (NBTTagString) tagList.tagAt(i);
				if(stringTag.data.equalsIgnoreCase(username))
				{
					return true;
				}
			}
			catch(Exception e)
			{}
		}
		
		return false;
	}
	

}
