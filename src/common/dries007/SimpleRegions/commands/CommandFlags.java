package dries007.SimpleRegions.commands;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.SimpleCore;
import dries007.SimpleRegions.SimpleRegions;
import dries007.SimpleRegions.actions.*;
import dries007.SimpleRegions.regions.API;

import net.minecraft.src.*;

public class CommandFlags extends CommandBase
{
	public String getCommandName()
	{
		return "/flags";
	}
	
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return getCommandName() + " [region] [flag] [0|1]";
    }
	
	public void processCommand(ICommandSender sender, String[] args)
	{
		//   0 = List, 1 = Help, 2 = Status, 3 = Set Status
		if(args.length>3)
		{
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
		
		//FlagList
		if(args.length==0)
		{
			sender.sendChatToPlayer("Use: " + getCommandUsage(sender));
			sender.sendChatToPlayer("List of available flags:");
			sender.sendChatToPlayer("Use //flags <flag> for help.");
			String msg = "";
			Iterator<String> i = API.getFlagList().iterator();
			while(i.hasNext())
			{
				if (msg.equals("")) msg = i.next();
				else msg = msg + ", " + i.next();
			}
			sender.sendChatToPlayer(msg);
			return;
		}
		
		//FlagHelp
		if(args.length==1)
		{
			if(API.getFlagList().contains(args[0].toLowerCase()))
			{
				sender.sendChatToPlayer(args[0] + " help: " + API.getFlagHelp(args[0].toLowerCase()));
			}
			else
			{
				throw new WrongUsageException("That flag doesn't exist.", new Object[0]);
			}
			return;
		}
		
		//Rest
		if(SimpleCore.rankData.hasKey(args[0]))
		{
			throw new WrongUsageException("Region " + args[0] + " doesn't exist!", new Object[0]);
		}
		else
		{
			NBTTagCompound region = SimpleRegions.regionData.getCompoundTag(args[0].trim());
			NBTTagCompound flags = region.getCompoundTag("flags");
			
			if(!API.getFlagList().contains(args[1].toLowerCase()))
			{
				throw new WrongUsageException("That flag doesn't exist.", new Object[0]);
			}
			
			//Flag & Region status
			if(args.length==3)
			{
				if(args[2].equals("1"))
				{
					flags.setBoolean(args[1], true);
				}
				else if(args[2].equals("0"))
				{
					flags.setBoolean(args[1], false);
				}
				else
				{
					throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
				}
			}
			
			//Set flag status
			if(flags.hasKey(args[1]))
			{
				sender.sendChatToPlayer("Region: " + region.getName() + " Flag: " + args[1] + ":" + flags.getBoolean(args[1]));
			}
			else
			{
				sender.sendChatToPlayer("Region: " + region.getName() + " Flag: " + args[1] + ":null");
			}
			
			region.setCompoundTag("flags", flags);
			SimpleRegions.regionData.setCompoundTag(region.getName(), region);
		}
	}
		
	public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
	{
		return true;
	}
}
