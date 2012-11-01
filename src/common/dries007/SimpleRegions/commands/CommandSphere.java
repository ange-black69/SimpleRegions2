package dries007.SimpleRegions.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Side;
import dries007.SimpleCore.*;
import dries007.SimpleRegions.SimpleRegions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandSphere extends CommandBase
{	
    public String getCommandName()
    {
        return "/sphere";
    }
    
    public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"/r", "/R", "/Sphere"});
    }

    
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return getCommandName() + " <Radius> [H if you want a hollow sphere]";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
    	if(args.length==1)
    	{
    		EntityPlayerMP player = ((EntityPlayerMP)sender);
        	
        	ItemStack stack = player.inventory.getCurrentItem();
    		
        	NBTTagCompound tag = stack.getTagCompound();
    		NBTTagCompound newTag = new NBTTagCompound();
    		newTag.setInteger("dim", player.dimension);
    		Iterator i = tag.getTags().iterator();
    		while(i.hasNext())
    		{
    			NBTBase next = (NBTBase) i.next();
    			if (next.getId()==10) //compound
    			{
    				NBTTagCompound realTag = (NBTTagCompound) next;
    				if(realTag.getName().equals("pos1"))
    				{
    					newTag.setCompoundTag("pos1", realTag);
    				}
    			}
    		}
    		newTag.setInteger("sphereR", Integer.parseInt(args[0]));
    		stack.setTagCompound(newTag);
    		sender.sendChatToPlayer("Sphere selected with R=" + args[0]);
    	}
    	else if(args.length==2)
    	{
    		EntityPlayerMP player = ((EntityPlayerMP)sender);
        	
        	ItemStack stack = player.inventory.getCurrentItem();
    		
        	NBTTagCompound tag = stack.getTagCompound();
    		NBTTagCompound newTag = new NBTTagCompound();
    		newTag.setInteger("dim", player.dimension);
    		Iterator i = tag.getTags().iterator();
    		while(i.hasNext())
    		{
    			NBTBase next = (NBTBase) i.next();
    			if (next.getId()==10) //compound
    			{
    				NBTTagCompound realTag = (NBTTagCompound) next;
    				if(realTag.getName().equals("pos1"))
    				{
    					newTag.setCompoundTag("pos1", realTag);
    				}
    			}
    		}
    		newTag.setInteger("hSphereR", Integer.parseInt(args[0]));
    		stack.setTagCompound(newTag);
    		sender.sendChatToPlayer("Hollow sphere selected with R=" + args[0]);
    		
    	}
    	else
    	{
    		
    	}
    	
    	
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return Permissions.hasPermission(sender.getCommandSenderName(), "SR.all");
    }

}
