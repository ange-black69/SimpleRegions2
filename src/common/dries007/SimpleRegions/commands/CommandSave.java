package dries007.SimpleRegions.commands;

import java.util.List;

import dries007.SimpleCore.SimpleCore;
import dries007.SimpleCore.data;
import dries007.SimpleRegions.SimpleRegions;
import dries007.SimpleRegions.actions.*;

import net.minecraft.src.*;

public class CommandSave extends CommandBase
{
	public String getCommandName()
	{
		return "/save";
	}
	
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return getCommandName() + " <name>";
    }
	
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(args.length!=1)
		{
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
		
		EntityPlayerMP player = ((EntityPlayerMP)sender);
		ItemStack stack = player.inventory.getCurrentItem();
		
		NBTTagCompound regionSelection = (NBTTagCompound) stack.getTagCompound().copy();
		
		if (regionSelection.hasKey("pos1")&&regionSelection.hasKey("pos2"))
		{
			if (SimpleRegions.regionData.hasKey(args[0]))
			{
				throw new WrongUsageException("Region name already used.");
			}
			else
			{
				regionSelection.setString("Owner", sender.getCommandSenderName());
				SimpleRegions.regionData.setCompoundTag(args[0], regionSelection); 
			}
			sender.sendChatToPlayer("Region " + args[0] + " saved.");
		}
		else
		{
			throw new WrongUsageException("Selection has to be square.");
		}
	}
		
	public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
	{
		return true;
	}
}
