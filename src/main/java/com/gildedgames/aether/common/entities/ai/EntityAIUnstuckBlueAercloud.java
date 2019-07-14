package com.gildedgames.aether.common.entities.ai;

import com.gildedgames.aether.api.registrar.BlocksAether;
import com.gildedgames.aether.common.blocks.natural.BlockAercloud;
import com.gildedgames.aether.common.util.helpers.EntityUtil;
import com.gildedgames.aether.common.util.helpers.WorldUtil;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class EntityAIUnstuckBlueAercloud extends Goal
{

	private final CreatureEntity entity;

	private double targetX;

	private double targetY;

	private double targetZ;

	public EntityAIUnstuckBlueAercloud(CreatureEntity entity)
	{
		this.entity = entity;

		this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.entity == null)
		{
			return false;
		}

		if (!WorldUtil.isBlockBelowAABB(this.entity.getBoundingBox(), this.entity.world,
				BlocksAether.aercloud.getDefaultState().with(BlockAercloud.PROPERTY_VARIANT, BlockAercloud.BLUE_AERCLOUD)))
		{
			return false;
		}

		Vec3d vec3d = this.findPossibleEscape();

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
		return WorldUtil.isBlockBelowAABB(this.entity.getBoundingBox(), this.entity.world,
				BlocksAether.aercloud.getDefaultState().with(BlockAercloud.PROPERTY_VARIANT, BlockAercloud.BLUE_AERCLOUD));
	}

	@Override
	public void tick()
	{
		this.entity.moveRelative(0.0F, new Vec3d(0.0F, 0.5F, 0.1F));
		EntityUtil.facePos(this.entity, this.targetX, this.targetY, this.targetZ, 10.0F, 10.0F);
	}

	@Nullable
	private Vec3d findPossibleEscape()
	{
		Random random = this.entity.getRNG();
		BlockPos blockpos = new BlockPos(this.entity.posX, this.entity.getBoundingBox().minY, this.entity.posZ);

		for (int i = 0; i < 10; ++i)
		{
			BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

			if (EntityUtil.getBlockBelow(this.entity.world, blockpos1) != BlocksAether.aercloud.getDefaultState().with(BlockAercloud.PROPERTY_VARIANT, BlockAercloud.BLUE_AERCLOUD))
			{
				return new Vec3d((double) blockpos1.getX(), (double) blockpos1.getY(), (double) blockpos1.getZ());
			}
		}

		return null;
	}

}
