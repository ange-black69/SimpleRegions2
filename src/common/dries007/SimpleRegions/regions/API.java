package dries007.SimpleRegions.regions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.src.*;
import dries007.SimpleRegions.*;
import dries007.SimpleRegions.actions.*;
import dries007.SimpleRegions.commands.*;
import dries007.SimpleRegions.regions.*;

public class API extends SimpleRegions
{
	/**
	 * Returns all the flags asociated with the region name
	 * @param name
	 * @return
	 */
	public static NBTTagCompound getFlags(String name)
	{
		if(SimpleRegions.regionData.hasKey(name))
		{
			return SimpleRegions.regionData.getCompoundTag(name).getCompoundTag("flags");
		}
		return null;
	}
	
	/**
	 * 
	 * @param dim Dimention
	 * @param centerX centerpoint
	 * @param centerZ centerpoint
	 * @param R Radius around center (square)
	 * @param Ymin Min Y level
	 * @param Ymax Max Y level
	 * @param flag The wanted flag
	 * @return List<String> of all the protected blocks within R from center coords X;Y;Z.
	 */
	public static List<String> getBlockInRegionsWithFlag(int dim, int centerX, int centerZ, int R, int Ymin, int Ymax, String flag)
	{
		List<String> output = new ArrayList();
		
		for (int X = -R; X <= R; X++)
    	{
			for (int Z = -R; Z <= R; Z++)
			{
				for(int Y = Ymin; Y <= Ymax; Y++)
				{
					if(getFlags(getRegion(dim, centerX+X, Y, centerZ+Z)).getBoolean(flag));
					{
						output.add((centerX+X) + ";" + Y + ";" + (centerZ+Z));
					}
				}
			}
    	}
		
		return output;
	}
	
	/**
	 * Get the region for a block. Returns null if no region 
	 * @param dim
	 * @param X
	 * @param Y
	 * @param Z
	 * @return
	 */
	public static String getRegion(int dim, int X, int Y, int Z)
	{
		Iterator i = SimpleRegions.regionData.getTags().iterator();
		while (i.hasNext())
		{
			try
			{
				NBTTagCompound regionSelection = (NBTTagCompound) i.next();
				
				NBTTagCompound pos1 = regionSelection.getCompoundTag("pos1");
				NBTTagCompound pos2 = regionSelection.getCompoundTag("pos2");
				
				int X1 = pos1.getInteger("X");
				int Y1 = pos1.getInteger("Y");
				int Z1 = pos1.getInteger("Z");

				int X2 = pos2.getInteger("X");
				int Y2 = pos2.getInteger("Y");
				int Z2 = pos2.getInteger("Z");
				
				if (X1>X2)
				{
					int a = X1;
					int b = X2;
					X1 = b;
					X2 = a;
				}
				
				if (Y1>Y2)
				{
					int a = Y1;
					int b = Y2;
					Y1 = b;
					Y2 = a;
				}
			
				if (Z1>Z2)
				{
					int a = Z1;
					int b = Z2;
					Z1 = b;
					Z2 = a;
				}
				
				if (X >= X1 && X <= X2)
				{
					if (Y >= Y1 && Y <= Y2)
					{
						if (Z >= Z1 && Z <= Z2)
						{
							return regionSelection.getName();
						}
					}
				}
				
			}
			catch (Exception e)
			{}
		}
		return null;
	}
	
	
	/**
	 * Add a flag to the flaglist. The help displays when you use this command: /flags <flag>
	 * @param name
	 * @param helpText
	 */
	public static void addFlag(String name, String helpText)
	{
		if(!SimpleRegions.availableFlags.containsKey(name.toLowerCase()))
		{
			SimpleRegions.availableFlags.put(name.toLowerCase(), helpText.trim());
		}
	}
	
	/**
	 * ...
	 * @param name
	 * @return The help if name exists, otherwise null.
	 */
	public static String getFlagHelp(String name)
	{
		if(SimpleRegions.availableFlags.containsKey(name.toLowerCase()))
		{
			return SimpleRegions.availableFlags.get(name.toLowerCase());
		}
		else
		{
			return null;
		}
	}
	
	public static Set<String> getFlagList()
	{
		return SimpleRegions.availableFlags.keySet();
	}
	
}
