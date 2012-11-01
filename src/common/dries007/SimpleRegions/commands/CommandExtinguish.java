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

public class CommandExtinguish extends CommandBase
{	
    public String getCommandName()
    {
        return "/extinguish";
    }
    
    public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"/ex"});
    }
    
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return getCommandName() + " <radius>";
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return Permissions.hasPermission(sender.getCommandSenderName(), "SR.all");
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
    	List blockList = new ArrayList();
    	EntityPlayerMP player = ((EntityPlayerMP)sender);
    	
		int centerX = ((Double) player.posX).intValue();
		int centerY = ((Double) player.posY).intValue();
		int centerZ = ((Double) player.posZ).intValue();
		int R = 0;
		
		try
		{
			R = Integer.parseInt(args[0]);
		}
		catch(Exception e)
		{
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}
		
		for (int X = -R; X <= R; X++)
    	{
			for (int Z = -R; Z <= R; Z++)
			{
				for (int Y = -10; Y <= 10; Y++)
				{
					if (in_circle(centerX, centerZ, R, centerX+X, centerZ+Z))
					{
						blockList.add((centerX+X) + ";" + (centerY+Y) + ";" + (centerZ+Z));
					}
				}
			}
    	}
		
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
		
		World world = ModLoader.getMinecraftServerInstance().worldServerForDimension(player.dimension);
		
		Iterator i = blockList.iterator();
		while(i.hasNext())
		{
			String[] coords = ((String)i.next()).split(";");
			int X = Integer.parseInt(coords[0]);
			int Y = Integer.parseInt(coords[1]);
			int Z = Integer.parseInt(coords[2]);
			
			
			if(world.getBlockId(X, Y, Z)==Block.fire.blockID)
			{
				world.setBlock(X, Y, Z, 0);
			}
		}
		
		
    }
    
	public static boolean in_circle(int center_x,int center_z,int radius,int x,int z)
    {
        double dist = ((center_x - x) * (center_x - x)) + ((center_z - z) * (center_z - z));
        return dist <= (radius * radius);
    }
}
