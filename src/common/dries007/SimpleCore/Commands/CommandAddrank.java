package dries007.SimpleCore.Commands;

import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandAddrank extends CommandBase
{
    public String getCommandName()
    {
        return "addrank";
    }

    public String getCommandUsage(ICommandSender sender)
    {
    	return "/" + getCommandName() + " <name> [copyOtherRankName]";
    }

    public List getCommandAliases()
    {
        return null;
    }
    
    public void processCommand(ICommandSender sender, String[] args)
    {
    	if(args.length==0) throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	if(args.length>2) throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	if(!SimpleCore.rankData.hasKey(args[0]))
    	{
    		if(args.length==2)
    		{
    			Iterator ranks = SimpleCore.rankData.getTags().iterator();
            	int i = 0;
            	while (ranks.hasNext())
            	{
            		NBTTagCompound rank = (NBTTagCompound) ranks.next();
            		if(rank.getName().trim().equalsIgnoreCase(args[1]))
            		{
            			SimpleCore.rankData.setCompoundTag(args[0], (NBTTagCompound) SimpleCore.rankData.getCompoundTag(rank.getName()).copy());
            			sender.sendChatToPlayer("Rank " + args[0] + " made by copying " + args[1] + ".");
            			return;
            		}
            	}
            	sender.sendChatToPlayer("Rank to copy (" + args[1] + ") doesn't exist.");
    		}
    		else
    		{
    			SimpleCore.newRank(args[0]);
    			sender.sendChatToPlayer("Rank " + args[0] + " made.");
    		}
    	}
    	else
    	{
    		sender.sendChatToPlayer("Rank " + args[0] + " already exists.");
    	}
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return Permissions.hasPermission(sender.getCommandSenderName(), "SC.admin");
    }
    
    protected EntityPlayer func_71540_a(String par1Str)
    {
        EntityPlayerMP var2 = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(par1Str);

        if (var2 == null)
        {
            throw new PlayerNotFoundException();
        }
        else
        {
            return var2;
        }
    }
    
    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if(args.length == 2)
        {
        	Iterator ranks = SimpleCore.rankData.getTags().iterator();
        	String[] rankNames = new String[SimpleCore.rankData.getTags().size()-1];
        	String rankNames1 = new String();
        	int i = 0;
        	while (ranks.hasNext())
        	{
        		NBTTagCompound rank = (NBTTagCompound) ranks.next();
        		rankNames[i]=rank.getName().trim().toLowerCase();
        		rankNames1 = rankNames1 + rank.getName().trim().toLowerCase() + ","; 
        	}
        	sender.sendChatToPlayer("List of ranks: " + rankNames1.substring(0, rankNames1.length()-1));
        	return getListOfStringsMatchingLastWord(args, rankNames);
        }
        return null;
    }
}
