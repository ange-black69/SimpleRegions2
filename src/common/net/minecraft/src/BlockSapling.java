package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.List;
import java.util.Random;

public class BlockSapling extends BlockFlower
{
    public static final String[] WOOD_TYPES = new String[] {"oak", "spruce", "birch", "jungle"};

    protected BlockSapling(int par1, int par2)
    {
        super(par1, par2);
        float var3 = 0.4F;
        this.setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, var3 * 2.0F, 0.5F + var3);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote)
        {
            super.updateTick(par1World, par2, par3, par4, par5Random);

            if (par1World.getBlockLightValue(par2, par3 + 1, par4) >= 9 && par5Random.nextInt(7) == 0)
            {
                int var6 = par1World.getBlockMetadata(par2, par3, par4);

                if ((var6 & 8) == 0)
                {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, var6 | 8);
                }
                else
                {
                    this.growTree(par1World, par2, par3, par4, par5Random);
                }
            }
        }
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        par2 &= 3;
        return par2 == 1 ? 63 : (par2 == 2 ? 79 : (par2 == 3 ? 30 : super.getBlockTextureFromSideAndMetadata(par1, par2)));
    }

    /**
     * Attempts to grow a sapling into a tree
     */
    public void growTree(World par1World, int par2, int par3, int par4, Random par5Random)
    {
		if(dries007.SimpleRegions.regions.VanillaInterface.hasTag(par1World, par2, par3, par4, "nogrowth")) return;
        int var6 = par1World.getBlockMetadata(par2, par3, par4) & 3;
        Object var7 = null;
        int var8 = 0;
        int var9 = 0;
        boolean var10 = false;

        if (var6 == 1)
        {
            var7 = new WorldGenTaiga2(true);
        }
        else if (var6 == 2)
        {
            var7 = new WorldGenForest(true);
        }
        else if (var6 == 3)
        {
            for (var8 = 0; var8 >= -1; --var8)
            {
                for (var9 = 0; var9 >= -1; --var9)
                {
                    if (this.isSameSapling(par1World, par2 + var8, par3, par4 + var9, 3) && this.isSameSapling(par1World, par2 + var8 + 1, par3, par4 + var9, 3) && this.isSameSapling(par1World, par2 + var8, par3, par4 + var9 + 1, 3) && this.isSameSapling(par1World, par2 + var8 + 1, par3, par4 + var9 + 1, 3))
                    {
                        var7 = new WorldGenHugeTrees(true, 10 + par5Random.nextInt(20), 3, 3);
                        var10 = true;
                        break;
                    }
                }

                if (var7 != null)
                {
                    break;
                }
            }

            if (var7 == null)
            {
                var9 = 0;
                var8 = 0;
                var7 = new WorldGenTrees(true, 4 + par5Random.nextInt(7), 3, 3, false);
            }
        }
        else
        {
            var7 = new WorldGenTrees(true);

            if (par5Random.nextInt(10) == 0)
            {
                var7 = new WorldGenBigTree(true);
            }
        }

        if (var10)
        {
            par1World.setBlock(par2 + var8, par3, par4 + var9, 0);
            par1World.setBlock(par2 + var8 + 1, par3, par4 + var9, 0);
            par1World.setBlock(par2 + var8, par3, par4 + var9 + 1, 0);
            par1World.setBlock(par2 + var8 + 1, par3, par4 + var9 + 1, 0);
        }
        else
        {
            par1World.setBlock(par2, par3, par4, 0);
        }

        if (!((WorldGenerator)var7).generate(par1World, par5Random, par2 + var8, par3, par4 + var9))
        {
            if (var10)
            {
                par1World.setBlockAndMetadata(par2 + var8, par3, par4 + var9, this.blockID, var6);
                par1World.setBlockAndMetadata(par2 + var8 + 1, par3, par4 + var9, this.blockID, var6);
                par1World.setBlockAndMetadata(par2 + var8, par3, par4 + var9 + 1, this.blockID, var6);
                par1World.setBlockAndMetadata(par2 + var8 + 1, par3, par4 + var9 + 1, this.blockID, var6);
            }
            else
            {
                par1World.setBlockAndMetadata(par2, par3, par4, this.blockID, var6);
            }
        }
    }

    /**
     * Determines if the same sapling is present at the given location.
     */
    public boolean isSameSapling(World par1World, int par2, int par3, int par4, int par5)
    {
        return par1World.getBlockId(par2, par3, par4) == this.blockID && (par1World.getBlockMetadata(par2, par3, par4) & 3) == par5;
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int par1)
    {
        return par1 & 3;
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
        par3List.add(new ItemStack(par1, 1, 2));
        par3List.add(new ItemStack(par1, 1, 3));
    }
}
