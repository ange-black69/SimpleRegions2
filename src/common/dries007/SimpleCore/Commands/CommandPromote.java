package dries007.SimpleCore.Commands;

import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandPromote extends CommandBase
{
    public String getCommandName()
    {
        return "promote";
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/" + getCommandName() + " <player> <rank>";
    }

    public List getCommandAliases()
    {
        return null;
    }
    
    public void processCommand(ICommandSender sender, String[] args)
    {
    	EntityPlayer target = this.func_71540_a(args[0]);
    	Iterator ranks = SimpleCore.rankData.getTags().iterator();
    	while (ranks.hasNext())
    	{
    		NBTTagCompound rank = (NBTTagCompound) ranks.next();
    		if (rank.getName().equalsIgnoreCase(args[1]))
    		{
    			NBTTagCompound playerData = SimpleCore.playerData.getCompoundTag(target.username);
    			playerData.setString("Rank", rank.getName());
    			SimpleCore.playerData.setCompoundTag(target.username, playerData);
    			sender.sendChatToPlayer("You have promoted " + target.username + " to the rank of " + Permissions.getRank(target));
    			target.sendChatToPlayer("You have been promoted to the rank of " + Permissions.getRank(target) + " by " + sender.getCommandSenderName());
    			return;
    		}
    	}
    	sender.sendChatToPlayer("Can't find the rank '"+args[1]+"'");
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
        if(args.length == 1)
        {
        	return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        else if(args.length == 2)
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
