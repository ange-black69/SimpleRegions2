package dries007.SimpleRegions.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.relauncher.ArgsWrapper;
import cpw.mods.fml.relauncher.FMLRelauncher;
import dries007.SimpleCore.*;
import dries007.SimpleRegions.SimpleRegions;
import dries007.SimpleRegions.actions.Change;
import dries007.SimpleRegions.actions.Selection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandReplace extends CommandBase
{	
    public String getCommandName()
    {
        return "/replace";
    }
    
    public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"/Replace"});
    }
    
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "test <oldID>[:oldData] <newID>[:newID]";
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return Permissions.hasPermission(sender.getCommandSenderName(), "SR.all");
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
    	if(args.length==2)
    	{
    		if(args[1].contains(",")||args[1].contains("%"))
    		{
    			pattern(sender, args);
    		}
    		else
    		{
    			singleBlock(sender, args);
    		}
    	}
    	else if (args.length==1)
    	{
    		
    	}
    	else
    	{
    		throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	}
    }
    
    public void singleBlock(ICommandSender sender, String[] args)
    {
    	int oldBlockID = 0;
		int oldBlockData = -1;
		int newBlockID = 0;
		int newBlockData = 0;
		// Get oldBlockId and Data from arguments, def 0:0
		if(args[0].contains(":"))
		{
			String[] var1 = args[0].split(":");
			oldBlockID = Integer.parseInt(var1[0]);
			oldBlockData = Integer.parseInt(var1[1]);
		}
		else
		{
			oldBlockID = Integer.parseInt(args[0]);
		}
		
		// Get newBlockId and Data from arguments, def 0:0
		if(args[1].contains(":"))
		{
			String[] var1 = args[1].split(":");
			newBlockID = Integer.parseInt(var1[0]);
			newBlockData = Integer.parseInt(var1[1]);
		}
		else
		{
			newBlockID = Integer.parseInt(args[1]);
		}
		
		// Get region and blocklist	    	
		EntityPlayerMP player = ((EntityPlayerMP)sender);
		
		ItemStack stack = player.inventory.getCurrentItem();
				
		NBTTagCompound regionSelection = stack.getTagCompound();
				
		List blockList = Selection.getBlocks(regionSelection);
				
		if(blockList.size()>SimpleRegions.maxChanges)
		{
			sender.sendChatToPlayer("Region to big, select a smaller region and try again.");
			return;
		}
		if(blockList.size()>SimpleRegions.warningLevel)
		{
			FMLLog.warning("[Warning!] Large amount of blocks are being edited. Expect lagg!");
			ModLoader.getMinecraftServerInstance().getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat("\u00a75[Warning!] Large amount of blocks are beeing edited. Expect lagg!"));
		}
				
		sender.sendChatToPlayer("Making backup of " + blockList.size() + " blocks.");
		
		String name = "#" + SimpleRegions.IncrementAndGetOpID();
		NBTTagCompound backup = Change.save(blockList);
		backup.setName(name);
		SimpleRegions.saveData(backup, "backups");
				
		sender.sendChatToPlayer("Making changes");
		
		Iterator i = blockList.iterator();
		while(i.hasNext())
		{
			String[] coords = ((String)i.next()).split(";");
			int X = Integer.parseInt(coords[0]);
			int Y = Integer.parseInt(coords[1]);
			int Z = Integer.parseInt(coords[2]);
			int dim = Integer.parseInt(coords[3]);
			
			World world = ModLoader.getMinecraftServerInstance().worldServerForDimension(dim);
				
			if(world.getBlockId(X, Y, Z)==oldBlockID)
			{
				if(world.getBlockMetadata(X, Y, Z)==oldBlockData)
				{
					world.setBlockAndMetadata(X, Y, Z, newBlockID, newBlockData);
				}
				else if (oldBlockData==-1)
				{
					world.setBlockAndMetadata(X, Y, Z, newBlockID, newBlockData);
				}
			}
		}
    }


    public void pattern(ICommandSender sender, String[] args)
	{
    	int oldBlockID = 0;
		int oldBlockData = -1;
		int newBlockID = 0;
		int newBlockData = 0;
		// Get oldBlockId and Data from arguments, def 0:0
		if(args[0].contains(":"))
		{
			String[] var1 = args[0].split(":");
			oldBlockID = Integer.parseInt(var1[0]);
			oldBlockData = Integer.parseInt(var1[1]);
		}
		else
		{
			oldBlockID = Integer.parseInt(args[0]);
		}
    	
		// Get region and blocklist	    	
		EntityPlayerMP player = ((EntityPlayerMP)sender);
		ItemStack stack = player.inventory.getCurrentItem();
		NBTTagCompound regionSelection = stack.getTagCompound();
		List blockList = Selection.getBlocks(regionSelection);		
		if(blockList.size()>SimpleRegions.maxChanges)
		{
			sender.sendChatToPlayer("Region to big, select a smaller region and try again.");
			return;
		}
		if(blockList.size()>SimpleRegions.warningLevel)
		{
			FMLLog.warning("[Warning!] Large amount of blocks are being edited. Expect lagg!");
			ModLoader.getMinecraftServerInstance().getConfigurationManager().sendPacketToAllPlayers(new Packet3Chat("\u00a75[Warning!] Large amount of blocks are beeing edited. Expect lagg!"));
		}
		
		//1 pice of the pattern.
		String[] var1 = args[1].split(",");

		//build a list of 100 blockID:blockData
		List pattern = new ArrayList();
		for(int i = 0; i < var1.length; i++)
		{
			//% | ID:data
			String[] var2 = var1[i].split("%");
			//ID, data
			String[] var3 = var2[1].split(":");
			for(int c = 0; c < Integer.parseInt(var2[0]); c++)
			{
				if(var3.length==1) pattern.add(var3[0]+":"+0);
				else pattern.add(var3[0]+":"+var3[1]);
			}
		}
		
		if (pattern.size()!=100)
		{
			sender.sendChatToPlayer("Pattern incomplete, filled " + (100 - pattern.size()) + "% filled with air(0).");
			int rest = 100 - pattern.size();
			for(int c = 0; c < rest; c++)
			{
				pattern.add(0+":"+0);
			}
		}
		
		sender.sendChatToPlayer("Making backup of " + blockList.size() + " blocks.");
		String name = "#" + SimpleRegions.IncrementAndGetOpID();
		NBTTagCompound backup = Change.save(blockList);
		backup.setName(name);
		SimpleRegions.saveData(backup, "backups");
		
		sender.sendChatToPlayer("Making changes");
		
		//loop through all block and randomly select 1 blockID:blockData from the pattern
		Random rnd = new Random();
		Iterator i = blockList.iterator();
		while(i.hasNext())
		{
			String[] coords = ((String)i.next()).split(";");
			int X = Integer.parseInt(coords[0]);
			int Y = Integer.parseInt(coords[1]);
			int Z = Integer.parseInt(coords[2]);
			int dim = Integer.parseInt(coords[3]);
			int rndInt = rnd.nextInt(100); //99 is the max, 0 is a value too!
			
			String[] var3 = ((String)pattern.get(rndInt)).split(":");
			newBlockID = Integer.parseInt(var3[0]);
			newBlockData = Integer.parseInt(var3[1]);

			World world = ModLoader.getMinecraftServerInstance().worldServerForDimension(dim);
			
			if(world.getBlockId(X, Y, Z)==oldBlockID)
			{
				if(world.getBlockMetadata(X, Y, Z)==oldBlockData)
				{
					world.setBlockAndMetadata(X, Y, Z, newBlockID, newBlockData);
				}
				else if (oldBlockData==-1)
				{
					world.setBlockAndMetadata(X, Y, Z, newBlockID, newBlockData);
				}
			}
			
		}
		
		sender.sendChatToPlayer("Done!");
		
	}
}
