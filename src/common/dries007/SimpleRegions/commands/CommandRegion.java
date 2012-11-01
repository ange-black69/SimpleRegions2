package dries007.SimpleRegions.commands;

import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.Permissions;
import dries007.SimpleCore.SimpleCore;
import dries007.SimpleCore.data;
import dries007.SimpleRegions.SimpleRegions;
import dries007.SimpleRegions.actions.*;

import net.minecraft.src.*;

public class CommandRegion extends CommandBase
{
	public String getCommandName()
	{
		return "/region";
	}
	
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return getCommandName() + " <add OR remove> <name>";
    }
	
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(args.length!=2)
		{
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
		
		EntityPlayerMP player = ((EntityPlayerMP)sender);
		ItemStack stack = player.inventory.getCurrentItem();
		
		if(args[0].equalsIgnoreCase("add"))
		{
			NBTTagCompound regionSelection = (NBTTagCompound) stack.getTagCompound().copy();
		
			if (regionSelection.hasKey("pos1")&&regionSelection.hasKey("pos2"))
			{
				if (SimpleRegions.regionData.hasKey(args[1]))
				{
					throw new WrongUsageException("Region name already used.");
				}
				else
				{
					regionSelection.setString("Owner", sender.getCommandSenderName());
					SimpleRegions.regionData.setCompoundTag(args[1], regionSelection); 
				}
				sender.sendChatToPlayer("Region " + args[1] + " added. You have been set as owner.");
			}
			else
			{
				throw new WrongUsageException("Selection has to be square.");
			}
		}
		else if(args[0].equalsIgnoreCase("remove"))
		{
			if(SimpleRegions.regionData.hasKey(args[1]))
			{
				NBTTagCompound newData = new NBTTagCompound();
				Iterator i = SimpleRegions.regionData.getTags().iterator();
				while(i.hasNext())
				{
					NBTBase tag = (NBTBase) i.next();
					if (!tag.getName().equals(args[1]))
					{
						newData.setTag(args[1], tag);
					}
					else
					{
						sender.sendChatToPlayer("Region " + args[1] + " removed.");
						return;
					}
				}
			}
			else
			{
				sender.sendChatToPlayer("Region " + args[1] + " not found!");
			}
		}
		else
		{
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
	}
		
	public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return Permissions.hasPermission(sender.getCommandSenderName(), "SR.all");
    }
}
