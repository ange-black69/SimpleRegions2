package dries007.SimpleRegions.actions;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.src.NBTTagCompound;

public class Selection 
{
	/**
	 * Gives a list of blocks based on 2 points
	 */
	
	public static List getBlocks(NBTTagCompound regionSelection)
	{
		List output = new ArrayList();
		int dim = regionSelection.getInteger("dim");
		
		if (regionSelection.hasKey("pos1")&&regionSelection.hasKey("pos2"))
		{
			NBTTagCompound pos1 = regionSelection.getCompoundTag("pos1");
			NBTTagCompound pos2 = regionSelection.getCompoundTag("pos2");

			if (pos1.hasKey("X") && pos2.hasKey("X")) return cube(pos1, pos2, dim);
		}
		else  if(regionSelection.hasKey("pos1")&&regionSelection.hasKey("sphereR"))
		{
			NBTTagCompound pos1 = regionSelection.getCompoundTag("pos1");
			int R = regionSelection.getInteger("sphereR");

			if (pos1.hasKey("X") && (R !=0)) return sphere(pos1, R, dim);
		}
		else  if(regionSelection.hasKey("pos1")&&regionSelection.hasKey("hSphereR"))
		{
			NBTTagCompound pos1 = regionSelection.getCompoundTag("pos1");
			int R = regionSelection.getInteger("hSphereR");

			if (pos1.hasKey("X") && (R !=0)) return hSphere(pos1, R, dim);
		}
		
		
		return output;
	}

	
	/**
	 * The selection is a cube
	 */
	public static List cube(NBTTagCompound pos1, NBTTagCompound pos2, int dim)
	{
		List output = new ArrayList();
		
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
		
		for (int X = X1; X <= X2; X++)
    	{
    		for (int Y = Y1; Y <= Y2; Y++)
        	{
    			for (int Z = Z1; Z <= Z2; Z++)
    	    	{
    				output.add(X + ";" + Y + ";" + Z + ";" + dim);
    	    	}
        	}
    	}
		
		return output;
	}

	/**
	 * The selection is a sphere
	 */
	
	public static List sphere(NBTTagCompound pos1, int R, int dim)
	{
		List output = new ArrayList();
		
		int centerX = pos1.getInteger("X");
		int centerY = pos1.getInteger("Y");
		int centerZ = pos1.getInteger("Z");
		
		for (int X = -R; X <= R; X++)
    	{
			for (int Z = -R; Z <= R; Z++)
			{
				for (int Y = -R; Y <= R; Y++)
				{
					if (in_circle(centerX, centerZ, centerY, R, centerX+X, centerZ+Z, centerY+Y))
					{
						output.add((centerX+X) + ";" + (centerY+Y) + ";" + (centerZ+Z) + ";" + dim);
					}
				}
			}
    	}
		
		return output;
	}
	
	public static boolean in_circle(int center_x,int center_z,int center_y,int radius,int x,int z, int y)
    {
        double dist = ((center_x - x) * (center_x - x)) + ((center_z - z) * (center_z - z)) + ((center_y - y) * (center_y - y));
        return dist <= (radius * radius);
    }
	
	/**
	 * The selection is a sphere
	 */
	
	public static List hSphere(NBTTagCompound pos1, int R, int dim)
	{
		List output = new ArrayList();
		
		int centerX = pos1.getInteger("X");
		int centerY = pos1.getInteger("Y");
		int centerZ = pos1.getInteger("Z");
		
		for (int X = -R; X <= R; X++)
    	{
			for (int Z = -R; Z <= R; Z++)
			{
				for (int Y = -R; Y <= R; Y++)
				{
					if (in_circle(centerX, centerZ, centerY, R, centerX+X, centerZ+Z, centerY+Y) && (!in_circle(centerX, centerZ, centerY, R-1, centerX+X, centerZ+Z, centerY+Y)))
					{
						output.add((centerX+X) + ";" + (centerY+Y) + ";" + (centerZ+Z) + ";" + dim);
					}
				}
			}
    	}
		
		return output;
	}

}