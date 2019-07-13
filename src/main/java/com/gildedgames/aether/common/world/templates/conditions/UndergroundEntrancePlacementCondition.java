package com.gildedgames.aether.common.world.templates.conditions;

import com.gildedgames.aether.api.registrar.BlocksAether;
import com.gildedgames.aether.api.util.TemplateUtil;
import com.gildedgames.aether.api.world.templates.PlacementConditionTemplate;
import com.gildedgames.orbis.lib.processing.IBlockAccess;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.template.Template;

import java.util.List;

public class UndergroundEntrancePlacementCondition implements PlacementConditionTemplate
{

	@Override
	public boolean canPlace(final Template template, final IBlockAccess world, final BlockPos placedAt, final Template.BlockInfo block)
	{
		return true;
	}

	@Override
	public boolean canPlaceCheckAll(final Template template, final IBlockAccess world, final BlockPos placedAt, final List<Template.BlockInfo> blocks)
	{
		final List<BlockPos> posToDelete = Lists.newArrayList();

		for (final Template.BlockInfo block : blocks)
		{
			if (block.pos.getY() == placedAt.getY() + template.getSize().getY() - 1 && block.blockState.getBlock() != Blocks.STRUCTURE_VOID)
			{
				final BlockPos up = block.pos.up();

				if (!world.canAccess(up))
				{
					return false;
				}

				if (!TemplateUtil.isReplaceable(world, up))
				{
					return false;
				}
				else if (!world.isAirBlock(up))
				{
					posToDelete.add(up);
				}

				if (block.blockState.getBlock() != Blocks.AIR)
				{
					final BlockState state = world.getBlockState(block.pos);

					if (state.getBlock() != BlocksAether.aether_grass)
					{
						return false;
					}
				}
			}
		}

		for (final BlockPos pos : posToDelete)
		{
			world.removeBlock(pos, false);
		}

		return true;
	}

}
