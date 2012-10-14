package dries007.SimpleRegions.commands;

import java.util.Iterator;

import cpw.mods.fml.common.Side;
import dries007.SimpleCore.*;
import dries007.SimpleRegions.SimpleRegions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandExpand extends CommandBase
{	
    public String getCommandName()
    {
        return "/expand";
    }
    
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return getCommandName() + " <vert|size|up|down>";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
		if(args.length!=1)
		{
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
		
    	EntityPlayerMP player = ((EntityPlayerMP)sender);
    	ItemStack stack = player.inventory.getCurrentItem();
    	NBTTagCompound regionSelection = stack.getTagCompound();
		NBTTagCompound pos1 = regionSelection.getCompoundTag("pos1");
		NBTTagCompound pos2 = regionSelection.getCompoundTag("pos2");
		
		if(args[0].equalsIgnoreCase("vert"))
		{
			int lvl = 1;
			if (SimpleRegions.bedrockRemoval) lvl = 0;
			if (pos1.getInteger("Y")>pos2.getInteger("Y"))
			{
				pos1.setInteger("Y", lvl);
				pos2.setInteger("Y", SimpleRegions.vertLevel);
			}
			else
			{
				pos1.setInteger("Y", SimpleRegions.vertLevel);
				pos2.setInteger("Y", lvl);
			}
			sender.sendChatToPlayer("Region expended vertically.");
		}
		else if(args[0].equalsIgnoreCase("up"))
		{
			if (pos1.getInteger("Y")>pos2.getInteger("Y")) pos1.setInteger("Y", SimpleRegions.vertLevel);
			else pos2.setInteger("Y", SimpleRegions.vertLevel);
			sender.sendChatToPlayer("Region expended to layer "+SimpleRegions.vertLevel+".");
		}
		else if(args[0].equalsIgnoreCase("down"))
		{
			int lvl = 1;
			if (SimpleRegions.bedrockRemoval) lvl = 0;
			if (pos1.getInteger("Y")<pos2.getInteger("Y")) pos1.setInteger("Y", lvl);
			else pos2.setInteger("Y", lvl);
			sender.sendChatToPlayer("Region expended to layer "+lvl+".");
		}
		else
		{
			int pitch = MathHelper.floor_double(MathHelper.wrapAngleTo180_float(player.rotationPitch));
			int yaw = MathHelper.floor_double(MathHelper.wrapAngleTo180_float(player.rotationYaw));
			
			if (-60 > pitch)
			{
				if (pos1.getInteger("Y")>pos2.getInteger("Y")) pos1.setInteger("Y", pos1.getInteger("Y") + Integer.parseInt(args[0]));
				else pos2.setInteger("Y", pos2.getInteger("Y") + Integer.parseInt(args[0]));
				sender.sendChatToPlayer("Expanded selection " + args[0] + " blocks up.");
			}
        	else if (pitch > 60)
        	{
				if (pos1.getInteger("Y")<pos2.getInteger("Y")) pos1.setInteger("Y", pos1.getInteger("Y") - Integer.parseInt(args[0]));
				else pos2.setInteger("Y", pos2.getInteger("Y") - Integer.parseInt(args[0]));
				sender.sendChatToPlayer("Expanded selection " + args[0] + " row down.");
        	}
			else if (-30 < yaw && yaw < 30)
			{
				if (pos1.getInteger("Z")>pos2.getInteger("Z")) pos1.setInteger("Z", pos1.getInteger("Z") + Integer.parseInt(args[0]));
				else pos2.setInteger("Z", pos2.getInteger("Z") + Integer.parseInt(args[0]));
				sender.sendChatToPlayer("Expanded selection " + args[0] + " row forward. (f:0)");
			}
			else if(60 < yaw && yaw < 120)
			{
				if (pos1.getInteger("X")<pos2.getInteger("X")) pos1.setInteger("X", pos1.getInteger("X") - Integer.parseInt(args[0]));
				else pos2.setInteger("X", pos2.getInteger("X") - Integer.parseInt(args[0]));
				sender.sendChatToPlayer("Expanded selection " + args[0] + " row forward. (f:1)");
			}
			else if(-60 > yaw && yaw > -120)
			{
				if (pos1.getInteger("X")>pos2.getInteger("X")) pos1.setInteger("X", pos1.getInteger("X") + Integer.parseInt(args[0]));
				else pos2.setInteger("X", pos2.getInteger("X") + Integer.parseInt(args[0]));
				sender.sendChatToPlayer("Expanded selection " + args[0] + " row forward. (f:3)");
			}
			else if(150 < yaw || yaw < -150)
			{
				if (pos1.getInteger("Z")<pos2.getInteger("Z")) pos1.setInteger("Z", pos1.getInteger("Z") - Integer.parseInt(args[0]));
				else pos2.setInteger("Z", pos2.getInteger("Z") - Integer.parseInt(args[0]));
				sender.sendChatToPlayer("Expanded selection " + args[0] + " row forward. (f:2)");
			}
		}
		
		regionSelection.setCompoundTag("pos1", pos1);
		regionSelection.setCompoundTag("pos2", pos2);
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return true;
    }
    
   

}
