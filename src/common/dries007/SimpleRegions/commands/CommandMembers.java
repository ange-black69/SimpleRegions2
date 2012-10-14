package dries007.SimpleRegions.commands;

import java.util.List;

import dries007.SimpleCore.SimpleCore;
import dries007.SimpleCore.data;
import dries007.SimpleRegions.SimpleRegions;
import dries007.SimpleRegions.actions.*;

import net.minecraft.src.*;

public class CommandMembers extends CommandBase
{
	public String getCommandName()
	{
		return "/members";
	}
	
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return getCommandName() + " <region> <list OR add OR remove> [username]";
    }
	
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(!(args.length==2 || args.length==3))
		{
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
		
		if (!SimpleRegions.regionData.hasKey(args[0]))
		{
			throw new WrongUsageException("Region doesn't exist.");
		}
		else
		{
			NBTTagCompound regionData = SimpleRegions.regionData.getCompoundTag(args[0]);
			NBTTagList tagList = regionData.getTagList("Members");
			if(args[1].equalsIgnoreCase("list"))
			{
				sender.sendChatToPlayer("This is a list of members of the region " + args[0] + ".");
				String msg = "";
				
				for(int i = 0; i < tagList.tagCount();i++)
				{
					try
					{
						NBTTagString stringTag = (NBTTagString) tagList.tagAt(i);
						msg = msg + stringTag.data + ",";
					}
					catch(Exception e)
					{}
				}
				
				sender.sendChatToPlayer(msg);
			}
			else if(args[1].equalsIgnoreCase("add"))
			{
				if(args.length!=3)
				{
					throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
				}
				NBTTagString stringTagString = new NBTTagString(args[2], args[2]);
				tagList.appendTag(stringTagString);
				sender.sendChatToPlayer("Player " + args[2] + " added.");
				regionData.setTag("Members", tagList);
				SimpleRegions.regionData.setCompoundTag(args[0], regionData);
			}
			else if(args[1].equalsIgnoreCase("remove"))
			{
				if(args.length!=3)
				{
					throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
				}
				
				NBTTagList newTagList = new NBTTagList();
				
				for(int i = 0; i < tagList.tagCount();i++)
				{
					try
					{
						NBTTagString stringTag = (NBTTagString) tagList.tagAt(i);
						if(stringTag.data.equalsIgnoreCase(args[2]))
						{
							sender.sendChatToPlayer("Player " + args[2] + " removed.");
							regionData.setTag("Members", newTagList);
							SimpleRegions.regionData.setCompoundTag(args[0], regionData);
							return;
						}
						else
						{
							newTagList.appendTag(stringTag);
						}
					}
					catch(Exception e)
					{}
				}
				sender.sendChatToPlayer("The player " + args[2] + " was no member, no changes made.");
			}
		}
	}
		
	public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
	{
		return true;
	}
}
