package dries007.SimpleCore;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import cpw.mods.fml.common.FMLCommonHandler;

public class VanillaInterface 
{
	public static String tapMakeup;
	
	public static String doMakeup(String username)
	{
		try
		{
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(username);
			String result = tapMakeup;
			
			result = result.replaceAll("%U", username);
			
			result = result.replaceAll("%Hn", ""+player.getHealth());
			
			result = result.replaceAll("%Rn", Permissions.getRank(player));
			result = result.replaceAll("%Rc", Permissions.getRankSetting(player).getString("color"));
		
			if (result.length() > 16)
			{
				result.trim();
				result = result.substring(0, 16);
				
			}
			return result;
			
		}
		catch(Exception e)
		{
			return username;
		}
	}
	
	public static boolean isHidden(String username, boolean def)
	{
		if(!def) return true; //Packet201PlayerInfo , Is not connected? then say so!
		try
		{
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(username);
			return player.getEntityData().getBoolean("hidden");
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
