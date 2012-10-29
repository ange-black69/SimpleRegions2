package dries007.SimpleCore.Commands;

import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandRank extends CommandBase
{
    public String getCommandName()
    {
        return "rank";
    }

    public String getCommandUsage(ICommandSender sender)
    {
    	return "/" + getCommandName() + " <rank> <permission> <allow|deny> ";
    }

    public List getCommandAliases()
    {
        return null;
    }
    
    public void processCommand(ICommandSender sender, String[] args)
    {	
    	if(args.length!=3) throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	
    	String rank = getRank(args[0]);
    	
    	NBTTagCompound data1 = SimpleCore.rankData.getCompoundTag(rank);
    	NBTTagCompound permissions = data1.getCompoundTag("Permissions");
    	
    	if(args[2].equalsIgnoreCase("allow"))
    	{
    		permissions.setBoolean(args[1], true);
    		sender.sendChatToPlayer("You have allowed '" + rank + "' '" + args[1] + "'.");
    	}
    	else if(args[2].equalsIgnoreCase("deny"))
    	{
    		permissions.setBoolean(args[1], false);
    		sender.sendChatToPlayer("You have denied '" + rank + "' '" + args[1] + "'.");
    	}
    	else throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	data1.setCompoundTag("Permissions", permissions);
    	SimpleCore.rankData.setCompoundTag(rank, data1);
    	data.saveData(SimpleCore.rankData, "rankData");
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return Permissions.hasPermission(sender.getCommandSenderName(), "SC.admin");
    }
    
    protected String getRank(String input)
    {
    	Iterator ranks = SimpleCore.rankData.getTags().iterator();
    	while (ranks.hasNext())
    	{
    		NBTTagCompound rank = (NBTTagCompound) ranks.next();
    		if (rank.getName().equalsIgnoreCase(input)) return rank.getName();
    	}
    	throw new WrongUsageException("Rank '" + input + "' not found!", new Object[0]);
    }
    
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
