package dries007.SimpleCore;

import java.util.Iterator;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker 
{
	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			if(SimpleCore.server.isDedicatedServer())
			{
				if(!SimpleCore.playerData.getCompoundTag(player.username).hasKey("Rank"))
				{
					NBTTagCompound data = new NBTTagCompound();
					data.setString("Rank", SimpleCore.defaultRank);
					SimpleCore.playerData.setCompoundTag(player.username, data);
					player.addChatMessage("You have been given the rank of " + SimpleCore.defaultRank + ".");
					player.addChatMessage("This server uses the SimpleCore permissions system.");
					if(SimpleCore.rankData.getCompoundTag(Permissions.getRank(player)).hasKey("Spawn"))
					{
						data = SimpleCore.rankData.getCompoundTag(Permissions.getRank(player)).getCompoundTag("Spawn");
						Double X =  data.getDouble("X");
						Double Y =  data.getDouble("Y");
						Double Z =  data.getDouble("Z");
						Float yaw =  data.getFloat("yaw");
						Float pitch =  data.getFloat("pitch");
						Integer dim = data.getInteger("Dim");
						if (player.dimension!=dim) ModLoader.getMinecraftServerInstance().getConfigurationManager().transferPlayerToDimension(((EntityPlayerMP) player), dim);
						((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(X, Y, Z, yaw, pitch);
						player.sendChatToPlayer("Welcome to " + data.getString("name"));
					}
				}
			}
			else
			{
				if(!SimpleCore.playerData.getCompoundTag(player.username).hasKey("Rank"))
				{
					NBTTagCompound data = new NBTTagCompound();
					data.setString("Rank", SimpleCore.opRank);
					SimpleCore.playerData.setCompoundTag(player.username, data);
					if(SimpleCore.rankData.getCompoundTag(Permissions.getRank(player)).hasKey("Spawn"))
					{
						data = SimpleCore.rankData.getCompoundTag(Permissions.getRank(player)).getCompoundTag("Spawn");
						Double X =  data.getDouble("X");
						Double Y =  data.getDouble("Y");
						Double Z =  data.getDouble("Z");
						Float yaw =  data.getFloat("yaw");
						Float pitch =  data.getFloat("pitch");
						Integer dim = data.getInteger("Dim");
						if (player.dimension!=dim) ModLoader.getMinecraftServerInstance().getConfigurationManager().transferPlayerToDimension(((EntityPlayerMP) player), dim);
						((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(X, Y, Z, yaw, pitch);
						player.sendChatToPlayer("Welcome to " + data.getString("name"));
					}
				}
			}
		}
		
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) 
	{
		
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) 
	{
		
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		MinecraftServer server = ModLoader.getMinecraftServerInstance();
		ChunkCoordinates var4 = ((EntityPlayerMP) player).getSpawnChunk();
		if (var4 == null)
		{
			if(SimpleCore.rankData.getCompoundTag(Permissions.getRank(player)).hasKey("Spawn"))
			{
				NBTTagCompound data = SimpleCore.rankData.getCompoundTag(Permissions.getRank(player)).getCompoundTag("Spawn");
				Double X =  data.getDouble("X");
				Double Y =  data.getDouble("Y");
				Double Z =  data.getDouble("Z");
				Float yaw =  data.getFloat("yaw");
				Float pitch =  data.getFloat("pitch");
				Integer dim = data.getInteger("Dim");
				if (player.dimension!=dim)server.getConfigurationManager().transferPlayerToDimension(((EntityPlayerMP) player), dim);
				((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(X, Y, Z, yaw, pitch);
				player.sendChatToPlayer("Welcome to " + data.getString("name"));
			}
			else if(SimpleCore.spawnOverride)
			{
				ChunkCoordinates coords = player.worldObj.getSpawnPoint();
				player.setPosition(coords.posX, coords.posY, coords.posZ);
				while (!server.worldServerForDimension(player.dimension).getCollidingBoundingBoxes(player, player.boundingBox).isEmpty())
		        {
					player.setPosition(player.posX, player.posY + 0.5D, player.posZ);
		        }
				((EntityPlayerMP)player).playerNetServerHandler.setPlayerLocation(coords.posX,coords.posY,coords.posZ,0,0);
			}
		}
		
		
	}

}
