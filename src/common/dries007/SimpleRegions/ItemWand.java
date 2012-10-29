package dries007.SimpleRegions;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.*;

public class ItemWand extends Item
{
	
	protected ItemWand(int i) 
	{
		super(i);
		setCreativeTab(CreativeTabs.tabTools);
		setItemName("Wand");
		setHasSubtypes(true);
		setNoRepair();
		setMaxStackSize(1);
	}
		
	public String getTextureFile()
	{
		return "/dries007/SimpleRegions/wands.png";
	}	
	 	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		return stack;
	}
	 
	public int getIconFromDamage(int par1)
	{
		return par1;
	}
	 
	public int getMetadata(int par1)
	{
		return par1;
	}
	 
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, List list) 
	{
		if(stack.getTagCompound()==null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}
	
		Iterator i = stack.getTagCompound().getTags().iterator();
		while(i.hasNext())
		{
			NBTBase tag = (NBTBase) i.next();
			if (tag.getId()==10) //compound
			{
				NBTTagCompound realTag = (NBTTagCompound) tag;
				list.add(realTag.getName() + " X=" + realTag.getInteger("X") + " Y=" + realTag.getInteger("Y") + " Z=" + realTag.getInteger("Z"));
			}
			else if(tag.getId()==3) //int
			{
				NBTTagInt realTag = (NBTTagInt) tag;
				list.add(realTag.getName() + "=" +realTag.data);
			}
		}
	}	
	
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) 
	{
    	return true;
	}
	
	
	public boolean onBlockStartBreak(ItemStack stack, int X, int Y, int Z, EntityPlayer player) 
    {
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return true;
			
		if(stack.getItemDamage()==0)
		{
			stack.setItemDamage(1);
		}
		
		NBTTagCompound tag = stack.getTagCompound();
		
		if(tag==null)
		{
			tag = new NBTTagCompound();
		}
		
		if(tag.getInteger("dim")!=player.dimension)
		{
			tag = new NBTTagCompound();
		}
		
		NBTTagCompound pos1 = new NBTTagCompound();
			pos1.setInteger("X", X);
			pos1.setInteger("Y", Y);
			pos1.setInteger("Z", Z);
		tag.setCompoundTag("pos1", pos1);
		tag.setInteger("dim", player.dimension);
			
		player.sendChatToPlayer("Pos1: X=" + X + " Y=" + Y + " Z=" + Z);
			
		stack.setTagCompound(tag);
		return true;
    }
	

	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) 
	{	
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer()) return false;
		 
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
		
		NBTTagCompound pos2 = new NBTTagCompound();
			pos2.setInteger("X", x);
			pos2.setInteger("Y", y);
			pos2.setInteger("Z", z);
		newTag.setCompoundTag("pos2", pos2);
		stack.setTagCompound(newTag);
		player.sendChatToPlayer("Pos2: X=" + x + " Y=" + y + " Z=" + z);
		return true;
	}    	

	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering()
	{	
		return true;
	}
}
