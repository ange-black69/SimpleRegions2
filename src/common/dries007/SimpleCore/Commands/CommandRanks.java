package dries007.SimpleCore.Commands;

import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandRanks extends CommandBase
{
    public String getCommandName()
    {
        return "ranks";
    }

    public String getCommandUsage(ICommandSender sender)
    {
    	return "/" + getCommandName();
    }

    public List getCommandAliases()
    {
        return null;
    }
    
    public void processCommand(ICommandSender sender, String[] args)
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
    	sender.sendChatToPlayer("List of ranks: " + rankNames1.substring(0, rankNames1.length()-1) + ".");
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return true;
    }
}
