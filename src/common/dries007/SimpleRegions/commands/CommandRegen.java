package dries007.SimpleRegions.commands;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Side;
import dries007.SimpleCore.*;
import dries007.SimpleRegions.SimpleRegions;
import dries007.SimpleRegions.actions.Change;
import dries007.SimpleRegions.actions.Selection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandRegen extends CommandBase
{	
    public String getCommandName()
    {
        return "/regen";
    }
    
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return getCommandName() + " [false for no ores]";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
    	boolean genOres = true;
    	if(args.length==1)
    	{
    		if(args[0].equalsIgnoreCase("false")) genOres = false;
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
    	
    	if (Change.regenBlocks(blockList, player.dimension, genOres))
    	{
    		sender.sendChatToPlayer("Done!");
    	}
    	else
    	{
    		sender.sendChatToPlayer("Failed!");
    	}
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return true;
    }

}
