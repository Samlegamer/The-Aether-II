package com.gildedgames.aether.common.blocks.natural;

import com.gildedgames.aether.common.blocks.IBlockMultiName;
import com.gildedgames.aether.common.blocks.properties.BlockVariant;
import com.gildedgames.aether.common.blocks.properties.PropertyVariant;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockAetherDirt extends Block implements IBlockMultiName
{

	public static final BlockVariant DIRT = new BlockVariant(0, "dirt"),
			COARSE_DIRT = new BlockVariant(1, "coarse_dirt");

	public static final PropertyVariant PROPERTY_VARIANT = PropertyVariant.create("variant", DIRT, COARSE_DIRT);

	public BlockAetherDirt(Block.Properties properties)
	{
		super(properties);

		this.setDefaultState(this.stateContainer.getBaseState().with(PROPERTY_VARIANT, DIRT));
	}

	@Override
	public int damageDropped(final BlockState state)
	{
		return state.get(PROPERTY_VARIANT).getMeta();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getSubBlocks(final ItemGroup tab, final NonNullList<ItemStack> list)
	{
		for (final BlockVariant variant : PROPERTY_VARIANT.getAllowedValues())
		{
			list.add(new ItemStack(this, 1, variant.getMeta()));
		}
	}

	@Override
	public BlockState getStateFromMeta(final int meta)
	{
		final BlockVariant variant = PROPERTY_VARIANT.fromMeta(meta);

		return this.getDefaultState().with(PROPERTY_VARIANT, variant);
	}

	@Override
	public int getMetaFromState(final BlockState state)
	{
		return state.get(PROPERTY_VARIANT).getMeta();
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(PROPERTY_VARIANT);
	}

	@Override
	public String getTranslationKey(final ItemStack stack)
	{
		return PROPERTY_VARIANT.fromMeta(stack.getMetadata()).getName();
	}

}
