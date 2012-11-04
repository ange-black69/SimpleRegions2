package dries007.SimpleRegions.regions;

import cpw.mods.fml.common.FMLLog;
import dries007.SimpleCore.Permissions;
import dries007.SimpleCore.SimpleCore;
import dries007.SimpleRegions.SimpleRegions;
import net.minecraft.src.*;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class VanillaInterface 
{
	@ForgeSubscribe
	public void godmode(LivingHurtEvent event)
	{
		EntityLiving entity = event.entityLiving;
		if (entity instanceof EntityPlayer)
		{
			if(VanillaInterface.hasTag(entity.worldObj, "godmode", (EntityPlayer) entity, false))
			{
				event.setCanceled(true);
			}
		}
		
		if(event.source == DamageSource.fall)
		{
			if(VanillaInterface.hasTag(entity.worldObj, ((Double) entity.posX).intValue(), ((Double) entity.posY).intValue(), ((Double) entity.posZ).intValue(), "nofalldamage"))
			{
				event.setCanceled(true);
			}
		}
	}
	
	@ForgeSubscribe
	public void pvp(AttackEntityEvent event) 
	{
		Entity target = event.target;
		if (target instanceof EntityPlayer)
		{
			if(VanillaInterface.hasTag(target.worldObj, "nopvp", (EntityPlayer) target, false))
			{
				event.setCanceled(true);
			}
		}
	}
	
	@ForgeSubscribe
	public void playerInteractin(PlayerInteractEvent event)
	{
		//Chestprotection
		World world = event.entityPlayer.worldObj;
		if(world.isRemote) return;
		
		
		
		if(event.action!=event.action.RIGHT_CLICK_BLOCK) return;
		
		if(world.getBlockTileEntity(event.x, event.y, event.z) instanceof IInventory)
		{
			if(VanillaInterface.hasTag(world, event.x, event.y, event.z, "nochest", event.entityPlayer))
			{
				event.entityPlayer.sendChatToPlayer("You are not allowed to interact in this region");
				event.setResult(Result.DENY);
				event.setCanceled(true);
			}
		}
	}
	
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
		if (world.isRemote) return false;
		if(Permissions.getRank(player.username).equalsIgnoreCase(SimpleCore.opRank)) return false;
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
		if (world.isRemote) return false;
		if(Permissions.getRank(player.username).equalsIgnoreCase(SimpleCore.opRank)) return false;
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
