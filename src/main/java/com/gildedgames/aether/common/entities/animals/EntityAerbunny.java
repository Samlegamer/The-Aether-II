package com.gildedgames.aether.common.entities.animals;

import com.gildedgames.aether.api.entity.damage.DamageTypeAttributes;
import com.gildedgames.aether.api.registrar.BlocksAether;
import com.gildedgames.aether.api.registrar.ItemsAether;
import com.gildedgames.aether.common.AetherCore;
import com.gildedgames.aether.common.entities.EntityTypesAether;
import com.gildedgames.aether.common.entities.ai.*;
import com.gildedgames.aether.common.init.LootTablesAether;
import com.gildedgames.aether.common.network.NetworkingAether;
import com.gildedgames.aether.common.network.packets.PacketAerbunnySetRiding;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Set;

public class EntityAerbunny extends EntityAetherAnimal
{
	private static final Ingredient TEMPTATION_ITEMS = Ingredient.fromItems(
			Items.CARROT, Items.POTATO, Items.BEETROOT, ItemsAether.blueberries, ItemsAether.orange, ItemsAether.enchanted_blueberry,
					ItemsAether.enchanted_wyndberry, ItemsAether.wyndberry);

	@OnlyIn(Dist.CLIENT)
	private double prevMotionY;

	@OnlyIn(Dist.CLIENT)
	private int puffiness;

	@OnlyIn(Dist.CLIENT)
	private float curRotation;

	public EntityAerbunny(EntityType<? extends EntityAerbunny> type, World world)
	{
		super(type, world);
	}

	@Override
	public float getBlockPathWeight(BlockPos pos, IWorldReader reader)
	{
		return super.getBlockPathWeight(pos);
	}

	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(2, new EntityAIRestrictRain(this));
		this.goalSelector.addGoal(3, new EntityAIUnstuckBlueAercloud(this));
		this.goalSelector.addGoal(3, new EntityAIHideFromRain(this, 1.3D));
		this.goalSelector.addGoal(1, new SwimGoal(this));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.2D, false, TEMPTATION_ITEMS));
		this.goalSelector.addGoal(3, new EntityAIEggnogTempt(this, 2.2D));
		this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PlayerEntity.class, 12.0F, 1.2F, 1.8F));
		this.goalSelector.addGoal(5, new RandomWalkingGoal(this, 1.0D, 10));
		this.goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 10.0F));

		this.jumpController = new AerbunnyJumpHelper(this);
	}

	@Override
	protected void registerAttributes()
	{
		super.registerAttributes();

		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);

		this.getAttribute(DamageTypeAttributes.SLASH_DEFENSE_LEVEL).setBaseValue(2);
		this.getAttribute(DamageTypeAttributes.PIERCE_DEFENSE_LEVEL).setBaseValue(4);
		this.getAttribute(DamageTypeAttributes.IMPACT_DEFENSE_LEVEL).setBaseValue(4);
	}

	@Override
	public void livingTick()
	{
		if (this.getMotion().getX() != 0 || this.getMotion().getZ() != 0)
		{
			this.setJumping(true);
		}

		super.livingTick();

		double motionY = this.getMotion().getY();

		if (this.world.isRemote())
		{
			if (this.puffiness > 0)
			{
				this.puffiness--;
			}

			if (this.prevMotionY <= 0 && this.getMotion().getY() > 0)
			{
				final BlockPos pos = this.getPosition();

				// Make sure we only spawn particles when it's jumping off a block
				if (Block.isOpaque(this.world.getBlockState(pos.down()).getShape(this.world, pos.down())))
				{
					AetherCore.PROXY.spawnJumpParticles(this.world, this.posX, pos.getY(), this.posZ, 0.6D, 6);
				}

				this.puffiness = 10;
			}

			this.prevMotionY = motionY;
		}

		final Entity riding = this.getRidingEntity();

		if (riding != null)
		{
			if (!this.world.isRemote() && riding.isSneaking() && riding.onGround)
			{
				NetworkingAether.sendPacketToWatching(new PacketAerbunnySetRiding(null, this), this, false);

				this.dismountEntity(riding);
				this.setPosition(riding.posX, riding.posY + riding.getEyeHeight() + 0.5D, riding.posZ);
			}

			if (motionY < 0.0D)
			{
				motionY *= riding.isSneaking() ? 0.9D : 0.7D;

				riding.fallDistance = 0;
			}

			this.setRotation(riding.rotationYaw, riding.rotationPitch);
		}

		this.setMotion(this.getMotion().getX(), Math.max(motionY, -0.1D), this.getMotion().getZ());

		this.fallDistance = 0.0F;
	}

	@Override
	public boolean processInteract(final PlayerEntity player, final Hand hand)
	{
		final ItemStack stack = player.getHeldItem(hand);

		if (!super.processInteract(player, hand) && !this.isBreedingItem(stack))
		{
			if (!this.isRidingOrBeingRiddenBy(player) && player.getPassengers().size() <= 0)
			{
				if (!this.world.isRemote())
				{
					this.startRiding(player, true);

					NetworkingAether.sendPacketToWatching(new PacketAerbunnySetRiding(player, this), this, false);
				}

				return true;
			}
			else
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public double getYOffset()
	{
		return this.getRidingEntity() != null ? 0.45D : 0.0D;
	}

	@Override
	public ResourceLocation getLootTable()
	{
		return LootTablesAether.ENTITY_AERBUNNY;
	}

	@Override
	protected SoundEvent getAmbientSound()
	{
		return new SoundEvent(AetherCore.getResource("mob.aerbunny.ambient"));
	}

	@Override
	protected SoundEvent getHurtSound(final DamageSource src)
	{
		return new SoundEvent(AetherCore.getResource("mob.aerbunny.hurt"));
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return new SoundEvent(AetherCore.getResource("mob.aerbunny.death"));
	}

	@Override
	protected PathNavigator createNavigator(final World worldIn)
	{
		return new AerbunnyNavigator(this, worldIn);
	}

	@Override
	public AgeableEntity createChild(final AgeableEntity ageable)
	{
		return new EntityAerbunny(EntityTypesAether.AERBUNNY, this.world);
	}

	@OnlyIn(Dist.CLIENT)
	public int getPuffiness()
	{
		return this.puffiness;
	}

	@OnlyIn(Dist.CLIENT)
	public float getRotation()
	{
		if (this.getMotion().getY() > 0)
		{
			this.curRotation += MathHelper.clamp(this.curRotation / 10f, -4f, -2f);
		}
		else if (this.getMotion().getY() < 0)
		{
			this.curRotation += MathHelper.clamp(this.curRotation / 10f, 2f, 4f);
		}

		if (this.onGround)
		{
			this.curRotation = 0f;
		}

		this.curRotation = MathHelper.clamp(this.curRotation, -30f, 30f);

		return this.curRotation;
	}

	@Override
	public boolean canRiderInteract()
	{
		return true;
	}

	@Override
	public boolean isBreedingItem(final ItemStack stack)
	{
		return TEMPTATION_ITEMS.test(stack);
	}

	@Override
	public boolean isEntityInsideOpaqueBlock()
	{
		return !this.isPassenger() && super.isEntityInsideOpaqueBlock();
	}

	private class AerbunnyJumpHelper extends JumpController
	{
		private final MobEntity entity;

		public AerbunnyJumpHelper(final EntityAerbunny entity)
		{
			super(entity);

			this.entity = entity;
		}

		@Override
		public void setJumping()
		{
			this.entity.setJumping(true);

			Vec3d motion = this.entity.getMotion();

			if (motion.getX() == 0 && motion.getZ() == 0)
			{
				this.isJumping = false;
				this.entity.setJumping(false);
			}
		}
	}

	private class AerbunnyNavigator extends AetherNavigateGround
	{
		public AerbunnyNavigator(final MobEntity entity, final World world)
		{
			super(entity, world);
		}

		@Override
		protected boolean canNavigate()
		{
			return !this.entity.isPassenger();
		}
	}

}