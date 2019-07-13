package com.gildedgames.aether.common.blocks.natural.ores;

import com.gildedgames.aether.api.registrar.ItemsAether;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class BlockIcestoneOre extends BlockAetherOre
{
	public BlockIcestoneOre(Block.Properties properties)
	{
		super(properties);
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune)
	{
		return ItemsAether.icestone;
	}

	@Override
	protected int getUnmodifiedExpDrop(Random rand)
	{
		return MathHelper.getInt(rand, 0, 1);
	}
}
