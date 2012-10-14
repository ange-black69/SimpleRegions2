package dries007.SimpleRegions.actions;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.src.*;

public class Change
{
	
	/**
	 * Gives you an NBTTagCompound with all the data of each block in the list
	 */
	
	public static NBTTagCompound save(List blockList)
	{
		NBTTagCompound backup = new NBTTagCompound();
		
		Iterator i = blockList.iterator();
		
		while (i.hasNext())
		{
			String[] coords = ((String)i.next()).split(";");
			int X = Integer.parseInt(coords[0]);
			int Y = Integer.parseInt(coords[1]);
			int Z = Integer.parseInt(coords[2]);
			int dim = Integer.parseInt(coords[3]);
			
			NBTTagCompound blockdata = new NBTTagCompound();
			World world = ModLoader.getMinecraftServerInstance().worldServerForDimension(dim);
			
			blockdata.setInteger("blockID", world.getBlockId(X, Y, Z));
			blockdata.setInteger("data", world.getBlockMetadata(X, Y, Z));
			
			try
			{
				if(world.blockHasTileEntity(X, Y, Z))
				{
					NBTTagCompound TileEntity = new NBTTagCompound();
					world.getBlockTileEntity(X, Y, Z).writeToNBT(TileEntity);
					blockdata.setCompoundTag("TileEntity", TileEntity);
				}
			}
			catch(Exception e)
			{
				ModLoader.getLogger().finest("[SR] Error placing TE @ "+X+";"+Y+";"+Y);
			}
			backup.setCompoundTag(X+";"+Y+";"+Z+";"+dim, blockdata);
		}
		
		return backup;
	}
	
	

	/**
	 * Set 1 block according to NBTTagCompound (for loading and restoring backups)
	 */
	
	public static boolean setBlock(NBTTagCompound tag)
	{
		try 
		{
			String[] coords = tag.getName().split(";");
			int X = Integer.parseInt(coords[0]);
			int Y = Integer.parseInt(coords[1]);
			int Z = Integer.parseInt(coords[2]);
			int dim = Integer.parseInt(coords[3]);
			
			World world = ModLoader.getMinecraftServerInstance().worldServerForDimension(dim);
			
			if (!world.setBlockAndMetadata(X, Y, Z, tag.getInteger("blockID"), tag.getInteger("data"))) return false;
			
			if (tag.hasKey("TileEntity"))
			{
				TileEntity tileEntity = new TileEntity();
				tileEntity.readFromNBT(tag.getCompoundTag("TileEntity"));
				world.setBlockTileEntity(X, Y, Z, tileEntity);
			}
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Set lots of blocks according to NBTTagCompound
	 */
	
	public static boolean setBlocks(NBTTagCompound tags)
	{
		Iterator i = tags.getTags().iterator();
		while (i.hasNext())
		{
			NBTTagCompound blockdata = ((NBTTagCompound)i.next());
			if (!setBlock(blockdata)) return false;
		}
		return true;
	}
	
	
	/**
	 * Regenerate the blocks in the list from the seed.
	 */
	public static boolean regenBlocks(List blockList, int dim, boolean regenOre)
	{
			HashMap chunkMap = new HashMap<Chunk,List>();
			
			WorldServer worldServer = ModLoader.getMinecraftServerInstance().worldServerForDimension(dim);
			//Get a list of all the chunks to regen blocks in
			Iterator i = blockList.iterator();
			while (i.hasNext())
			{
				String[] coords = ((String)i.next()).split(";");
				int X = Integer.parseInt(coords[0]);
				int Y = Integer.parseInt(coords[1]);
				int Z = Integer.parseInt(coords[2]);
				Chunk chunk = worldServer.getChunkFromBlockCoords(X, Z);
				// If the chunk wasn't already in the map, add it.
				if(!chunkMap.containsKey(chunk)) chunkMap.put(chunk, new ArrayList() {});
				// Add this block to the chunck
				List blockListOfChunk = (List)chunkMap.get(chunk);
				blockListOfChunk.add(coords);
				chunkMap.put(chunk, blockListOfChunk);
			}
			
			//now we have a hashmap off all the chunks that need regen, with all the blocks in that chunk.
			
			//Loop thrue every chunck that needs to be regend
			i = chunkMap.keySet().iterator();
			while(i.hasNext())
			{
				Chunk oldChunk = (Chunk)i.next();
				
				//Gen new chunk in memory
				ChunkProviderServer chunkProviderServer = worldServer.theChunkProviderServer;
				ChunkProviderGenerate chunkProviderGenerate = ((ChunkProviderGenerate)ModLoader.getPrivateValue(ChunkProviderServer.class, chunkProviderServer, "currentChunkProvider"));
				
				Chunk newChunk = chunkProviderGenerate.provideChunk(oldChunk.xPosition, oldChunk.zPosition);
				
				List blockListOfChunk = (List) chunkMap.get(oldChunk);
				//Replace only the blocks wanted from new chunk
				Iterator j = blockListOfChunk.iterator();
				while(j.hasNext())
				{
					String[] coords = ((String[])j.next());
					int X = Integer.parseInt(coords[0]);
					int Y = Integer.parseInt(coords[1]);
					int Z = Integer.parseInt(coords[2]);
					int inchunkX = X % 16;
					int inchunkZ = Z % 16;
					if (X<0)
					{
						inchunkX = 16 - (inchunkX*-1);
					}
					if (Z<0)
					{
						inchunkZ = 16 - (inchunkZ*-1);
					}
					int blockID = newChunk.getBlockID(inchunkX, Y, inchunkZ);
					int meta = newChunk.getBlockMetadata(inchunkX, Y, inchunkZ);
					worldServer.setBlockAndMetadata(X, Y, Z, blockID, meta);
					
					TileEntity tE = newChunk.getChunkBlockTileEntity(inchunkX, Z, inchunkZ); 
					if(tE !=null)
					{
						worldServer.setBlockTileEntity(X, Y, Z, tE);
					}
				}
				if(regenOre)
				{
					oldChunk.isTerrainPopulated = false;
					chunkProviderGenerate.populate(chunkProviderGenerate, oldChunk.xPosition, oldChunk.zPosition);
				}
			}
			
		return true;
	}
	
}
