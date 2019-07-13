package com.gildedgames.aether.common.blocks.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockCustomBookshelf extends Block
{
	public BlockCustomBookshelf(Block.Properties properties)
	{
		super(properties);
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 3;
	}

	@Override
	public float getEnchantPowerBonus(World world, BlockPos pos)
	{
		return 1;
	}

	@Override
	@Nullable
	public Item getItemDropped(BlockState state, Random rand, int fortune)
	{
		return Items.BOOK;
	}
}
