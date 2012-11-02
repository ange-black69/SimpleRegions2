package dries007.SimpleRegions.commands;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.Permissions;
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
		if(getCommandSenderAsPlayer(sender).worldObj.isRemote) return;
		//   0 = List, 1 = none, 2 = Status, 3 = Set Status
		if(args.length>3 || args.length==1)
		{
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
		
		//FlagList
		if(args.length==0)
		{
			sender.sendChatToPlayer("List of available flags:");
			Iterator<String> i = API.getFlagList().iterator();
			while(i.hasNext())
			{
				String flag = i.next();
				sender.sendChatToPlayer(dries007.SimpleCore.Color.DARK_GREEN + flag + dries007.SimpleCore.Color.WHITE + "-" + API.getFlagHelp(flag));
			}
			return;
		}
		
		//Rest
		if(!SimpleCore.rankData.hasKey(args[0].trim()))
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
	
	 public List addTabCompletionOptions(ICommandSender sender, String[] args)
	 {
		 if(args.length == 1)
		 {
			 String msg = "";
			 for(String st :  SimpleRegions.getRegions()) msg = msg + st + ", ";
			 sender.sendChatToPlayer("List of regions: " + msg);
			 return getListOfStringsMatchingLastWord(args, SimpleRegions.getRegions());
		 }
		 else if(args.length == 2)
		 {
			 String msg = "";
			 for(String st :  SimpleRegions.getFlags()) msg = msg + st + ", ";
			 sender.sendChatToPlayer("List of flags: " + msg);
			 return getListOfStringsMatchingLastWord(args, SimpleRegions.getFlags());
		 }
		 return null;
	 }
		
	public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return Permissions.hasPermission(sender.getCommandSenderName(), "SR.all");
    }
}
