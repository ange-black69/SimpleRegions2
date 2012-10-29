package dries007.SimpleCore;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.*;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.server.FMLServerHandler;
import dries007.SimpleCore.Commands.*;
import dries007.SimpleCore.asm.SimpleCorePlugin;
import dries007.SimpleCore.asm.SimpleCoreTransformer;

public class SimpleCore extends DummyModContainer
{
	public static NBTTagCompound playerData;
	public static NBTTagCompound rankData;
	public static String defaultRank;
	public static String opRank;
	public static Boolean spawnOverride;

	public static MinecraftServer server;
	
	public static NBTTagCompound defSettings = new NBTTagCompound();
	
	public SimpleCore()
	{
		super(new ModMetadata());
		ModMetadata meta	=	getMetadata();
        meta.modId      	=	"SimpleCore";
        meta.name       	=	"SimpleCore";
        meta.version    	=	"0.1";
        meta.authorList 	=	Arrays.asList("Dries007");
        meta.credits		=	"Dries007, ChickenBones for making his mods open-source!";
        meta.description	=	"Provides a framework for other SimpleServer mods. This includes world bound data storage and a basic permission system.";
        meta.url        	=	"http://ssm.dries007.net";
	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller)
	{
		bus.register(this);
		return true;		
	}
	
	@Subscribe
	public void serverStarting(FMLServerStartingEvent event)
	{
		server = ModLoader.getMinecraftServerInstance();
		
		addcommands();
		
		NBTTagInt example = new NBTTagInt("Example", 42);
		Permissions.addDefaultSetting(example);
		
		playerData=data.loadData("playerData");
		rankData=data.loadData("rankData");
		
		if(!rankData.hasKey(opRank)) newRank(opRank);
		if(!rankData.hasKey(defaultRank)) newRank(defaultRank);
		
		GameRegistry.registerPlayerTracker(new PlayerTracker());
	}
	
	@Subscribe
	public void serverStopping(FMLServerStoppingEvent event)
	{
		data.saveData(playerData, "playerData");
		data.saveData(rankData, "rankData");
	}
	
	public void addcommands()
	{
		ICommandManager commandManager = server.getCommandManager();
		ServerCommandManager manager = ((ServerCommandManager) commandManager); 
		
		manager.registerCommand(new CommandPromote());
		manager.registerCommand(new CommandAddrank());
		manager.registerCommand(new CommandRanks());
		manager.registerCommand(new CommandPlayer());
		manager.registerCommand(new CommandRank());
		manager.registerCommand(new CommandSetSpawn());
	}
	
	public static boolean newRank(String name)
	{
		if (!rankData.hasKey(name))
		{
			NBTTagCompound rank = new NBTTagCompound();
			rank.setCompoundTag("Settings", defSettings);
			rank.setCompoundTag("Permissions", new NBTTagCompound());
			rankData.setCompoundTag(name, rank);
			return true;
		}
		else
		{
			return false;
		}
	}
}