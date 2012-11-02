package dries007.SimpleRegions.regions;

import cpw.mods.fml.common.FMLLog;
import dries007.SimpleRegions.SimpleRegions;
import net.minecraft.src.*;

public class VanillaInterface 
{
	/**
	 * Checks to see if a region has a specific tag.
	 * @param world
	 * @param X
	 * @param Y
	 * @param Z
	 * @param tag The tag
	 * @return true if region has tag.
	 */
	public static boolean hasTag(World world, int X, int Y, int Z, String tag)
	{
		if (world.isRemote) return true;
		int dim = world.getWorldInfo().getDimension();
		
		String region = API.getRegion(dim, X, Y, Z);
		if(region!=null)
		{
			NBTTagCompound flags = API.getFlags(region);
			if (flags.getBoolean(tag))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * hasTag but ignored if player is owner or member.
	 * @param world
	 * @param X
	 * @param Y
	 * @param Z
	 * @param tag The tag
	 * @param player The player (used for owner / member)
	 * @return true if regions has tag and player != owner or member
	 */
	public static boolean hasTag(World world, int X, int Y, int Z, String tag, EntityPlayer player)
	{
		if (world.isRemote) return true; 
		int dim = world.getWorldInfo().getDimension();
		
		String region = API.getRegion(dim, X, Y, Z);
		if(region!=null)
		{
			NBTTagCompound flags = API.getFlags(region);
			if (flags.getBoolean(tag))
			{
				if(!SimpleRegions.regionData.getCompoundTag(region).getString("Owner").equals(player.username))
				{
					if(!SimpleRegions.isMemberOfRegion(region, player.username))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * hasTag, uses coords of player
	 * @param world
	 * @param tag
	 * @param player
	 * @param ownerOrMember False if you don't want owner of member to count differently.
	 * @return true if regions has tag (and player != owner or member if ownerOrMember is true)
	 */
	public static boolean hasTag(World world,String tag, EntityPlayer player, boolean ownerOrMember)
	{
		if (world.isRemote) return true;
		int dim = world.getWorldInfo().getDimension();
		
		String region = API.getRegion(dim, ((Double) player.posX).intValue(), ((Double) player.posY).intValue(), ((Double) player.posZ).intValue());
		if(region!=null)
		{
			NBTTagCompound flags = API.getFlags(region);
			if (flags.getBoolean(tag))
			{
				if(ownerOrMember)
				{
					if(!SimpleRegions.regionData.getCompoundTag(region).getString("Owner").equals(player.username))
					{
						if(!SimpleRegions.isMemberOfRegion(region, player.username))
						{
							return true;
						}
					}
				}
				else
				{
					return true;
				}
			}
		}
		return false;
	}
}
