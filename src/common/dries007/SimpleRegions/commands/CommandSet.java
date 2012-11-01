package dries007.SimpleRegions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;

import dries007.SimpleCore.Permissions;
import dries007.SimpleRegions.SimpleRegions;
import dries007.SimpleRegions.actions.*;

import net.minecraft.src.*;

public class CommandSet extends CommandBase
{
	public String getCommandName()
	{
		return "/set";
	}
	
    public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"/Set"});
    }
	
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return getCommandName() +" <ID:meta>|<pattern(%:ID:data,%:ID:data,...)>";
    }
	
	public void processCommand(ICommandSender sender, String[] args)
	{
		String command = getCommand(args);

		if(args.length!=1)
		{
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
		
		if(args[0].contains(",")||args[0].contains("%"))
		{
			pattern(sender, args[0]);
		}
		else
		{
			singleBlock(sender, args[0]);
		}
	}
		
	public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return Permissions.hasPermission(sender.getCommandSenderName(), "SR.all");
    }
	
	public String getCommand(String[] args)
	{
		String command = getCommandName();
		for(int i = 0; i>args.length; i++)
		{
			command = command + " " + args[i];
		}
		return command.replaceAll("/", "");
	}
	
	public void singleBlock(ICommandSender sender, String data)
	{
		int blockID = 0;
		int blockData = 0;
		// Get blockId and Data from arguments, def 0:0
		if(data.contains(":"))
		{
			String[] var1 = data.split(":");
			blockID = Integer.parseInt(var1[0]);
			blockData = Integer.parseInt(var1[1]);
		}
		else
		{
			blockID = Integer.parseInt(data);
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
			NBTTagCompound block = new NBTTagCompound(i.next().toString());
			block.setInteger("blockID", blockID);
			block.setInteger("data", blockData);
			Change.setBlock(block);
		}
		
		sender.sendChatToPlayer("Done!");
	}
	
	public void pattern(ICommandSender sender, String imput)
	{
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
		String[] var1 = imput.split(",");

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
			int rndInt = rnd.nextInt(100); //99 is the max, 0 is a value too!
			String[] var3 = ((String)pattern.get(rndInt)).split(":");
			NBTTagCompound block = new NBTTagCompound(i.next().toString());
			block.setInteger("blockID", Integer.parseInt(var3[0]));
			block.setInteger("data", Integer.parseInt(var3[1]));
			Change.setBlock(block);
		}
		
		sender.sendChatToPlayer("Done!");
		
	}
}
