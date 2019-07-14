package com.gildedgames.aether.common.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class EntityAIBreakBlocks extends Goal
{

	private final float movementSpeed;

	private final CreatureEntity entity;

	private double targetX;

	private double targetY;

	private double targetZ;

	private boolean enabled;

	private final Block[] blocks;

	public EntityAIBreakBlocks(CreatureEntity entity, float movementSpeed, Block[] blocks)
	{
		this.entity = entity;
		this.movementSpeed = movementSpeed;
		this.blocks = blocks;
		this.enabled = true;

		this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.entity == null || !this.enabled)
		{
			return false;
		}

		BlockPos entityPos = new BlockPos(this.entity.posX, this.entity.getBoundingBox().minY, this.entity.posZ);

		Vec3d vec3d = this.findBlocks();

		if (vec3d == null)
		{
			return false;
		}
		else
		{
			this.targetX = vec3d.x;
			this.targetY = vec3d.y;
			this.targetZ = vec3d.z;

			return true;
		}
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (this.entity.getDistanceSq(this.targetX, this.targetY, this.targetZ) < 15)
		{
			System.out.println("ATTack");
		}

		return !this.entity.getNavigator().noPath();
	}

	@Override
	public void startExecuting()
	{
		this.entity.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, this.movementSpeed);

		if (this.entity.getDistanceSq(this.targetX, this.targetY, this.targetZ) < 15)
		{
			System.out.println("ATTack");
		}
	}

	@Nullable
	private Vec3d findBlocks()
	{
		Random random = this.entity.getRNG();
		BlockPos blockpos = new BlockPos(this.entity.posX, this.entity.getBoundingBox().minY, this.entity.posZ);

		final int rad = 5;

		for (int i = -rad; i < rad; i++)
		{
			for (int j = -rad; j < rad; j++)
			{
				for (int l = -2; l < 2; l++)
				{
					BlockPos pos2 = blockpos.add(i, j, l);

					if (!this.entity.getEntityWorld().isAirBlock(pos2))
					{
						BlockState block = this.entity.getEntityWorld().getBlockState(pos2);

						for (Block bs : this.blocks)
						{
							if (block.getBlock().equals(bs))
							{
								this.targetX = pos2.getX();
								this.targetY = pos2.getY();
								this.targetZ = pos2.getZ();
							}
						}
					}

				}
			}
		}

		return null;
	}

	public void setEnabled(boolean b)
	{
		this.enabled = b;
	}
}
