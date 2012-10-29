package dries007.SimpleCore.Commands;

import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandPlayer extends CommandBase
{
    public String getCommandName()
    {
        return "player";
    }

    public String getCommandUsage(ICommandSender sender)
    {
    	return "/" + getCommandName() + " <player> <permission> <allow|deny> ";
    }

    public List getCommandAliases()
    {
        return null;
    }
    
    public void processCommand(ICommandSender sender, String[] args)
    {	
    	if(args.length!=3) throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	EntityPlayer target = this.func_71540_a(args[0]);
    	
    	NBTTagCompound data1 = SimpleCore.playerData.getCompoundTag(target.username);
    	NBTTagCompound permissions = data1.getCompoundTag("Permissions");
    	
    	if(args[2].equalsIgnoreCase("allow"))
    	{
    		permissions.setBoolean(args[1], true);
    		sender.sendChatToPlayer("You have allowed '" + target.username + "' '" + args[1] + "'.");
    	}
    	else if(args[2].equalsIgnoreCase("deny"))
    	{
    		permissions.setBoolean(args[1], false);
    		sender.sendChatToPlayer("You have denied '" + target.username + "' '" + args[1] + "'.");
    	}
    	else throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	data1.setCompoundTag("Permissions", permissions);
    	SimpleCore.playerData.setCompoundTag(target.username, data1);
    	data.saveData(SimpleCore.playerData, "playerData");
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
}
