package com.gildedgames.aether.common.entities.monsters;

import com.gildedgames.aether.api.entity.damage.DamageTypeAttributes;
import com.gildedgames.aether.api.registrar.BlocksAether;
import com.gildedgames.aether.api.registrar.ItemsAether;
import com.gildedgames.aether.common.entities.ai.EntityAIAechorPlantAttack;
import com.gildedgames.aether.common.init.LootTablesAether;
import com.gildedgames.aether.common.util.helpers.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

public class EntityAechorPlant extends EntityAetherMonster
{
	private static final int MAX_PETALS = 8;

	private static final DataParameter<Boolean> CAN_SEE_PREY = new DataParameter<>(16, DataSerializers.BOOLEAN);

	private static final DataParameter<Byte> PLANT_SIZE = new DataParameter<>(17, DataSerializers.BYTE);

	private static final DataParameter<Byte> PLANT_PETALS = new DataParameter<>(18, DataSerializers.BYTE);

	private boolean[] petals;

	@OnlyIn(Dist.CLIENT)
	public float sinage, prevSinage;

	private int poisonLeft;

	private int petalGrowTimer = 3000;

	public EntityAechorPlant(EntityType<EntityAechorPlant> type, World world)
	{
		super(type, world);
	}

	public boolean[] getPetalsPresent()
	{
		return this.petals;
	}

	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new EntityAIAechorPlantAttack(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	}

	@Override
	protected void registerData()
	{
		this.dataManager.register(EntityAechorPlant.CAN_SEE_PREY, Boolean.FALSE);
		this.dataManager.register(EntityAechorPlant.PLANT_SIZE, (byte) 0);
		this.dataManager.register(EntityAechorPlant.PLANT_PETALS, this.serializePlantPetals());

		if (this.world.isRemote())
		{
			this.sinage = this.rand.nextFloat() * 6F;
		}

		super.registerData();

		this.petals = new boolean[MAX_PETALS];

		Arrays.fill(this.petals, true);

		this.setPoisonLeft(2);

	}

	@Override
	protected void registerAttributes()
	{
		super.registerAttributes();

		this.setPlantSize(this.rand.nextInt(3) + 1);

		this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(3.0F);
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);

		this.getAttribute(DamageTypeAttributes.SLASH_DEFENSE_LEVEL).setBaseValue(4);
		this.getAttribute(DamageTypeAttributes.PIERCE_DEFENSE_LEVEL).setBaseValue(8);
		this.getAttribute(DamageTypeAttributes.IMPACT_DEFENSE_LEVEL).setBaseValue(8);
	}

	@Override
	public void tick()
	{
		super.tick();

		this.setMotion(0.0D, this.getMotion().getY(), 0.0D);

		if (!this.world.isRemote())
		{
			this.petalGrowTimer--;

			if (this.petalGrowTimer <= 0)
			{
				this.petalGrowTimer = 2400 + this.getRNG().nextInt(1600);

				int remainingPetals = this.getPetalCountInState(true);

				if (remainingPetals < MAX_PETALS)
				{
					this.setPetalState(this.getRandomPetal(false), true);
				}

				this.heal((float) (Math.round((this.getMaxHealth() / MAX_PETALS) * 2.0) / 2.0));
			}
		}

		if (this.world.isRemote())
		{
			this.tickAnimation();

			return;
		}

		boolean isTargeting = this.getAttackTarget() != null;

		if (this.canSeePrey() != isTargeting)
		{
			this.setCanSeePrey(isTargeting);
		}

		if (!this.canStayHere(new BlockPos(this)))
		{
			this.setHealth(0);
		}
	}

	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount)
	{
		float prevHealth = this.getHealth();

		super.damageEntity(damageSrc, damageAmount);

		if (this.getHealth() != prevHealth)
		{
			this.petalGrowTimer = 6000;

			if (!this.world.isRemote())
			{
				int targetPetals = (int) Math.floor((this.getHealth() / this.getMaxHealth()) * MAX_PETALS);
				int remainingPetals = this.getPetalCountInState(true);

				int damage = remainingPetals - targetPetals;

				Block.spawnAsEntity(this.world, this.getPosition(), new ItemStack(ItemsAether.aechor_petal, damage));

				while (remainingPetals > targetPetals)
				{
					this.setPetalState(this.getRandomPetal(true), false);

					remainingPetals--;
				}
			}
		}
	}

	private int getRandomPetal(boolean state)
	{
		int total = this.getPetalCountInState(state);
		int nth = this.rand.nextInt(total);

		int selected = -1;

		for (int i = 0, k = 0; i < this.petals.length; i++)
		{
			boolean present = this.petals[i];

			if (present == state)
			{
				if (k == nth)
				{
					selected = i;

					break;
				}

				k++;
			}
		}

		return selected;
	}

	private void setPetalState(int index, boolean state)
	{
		this.petals[index] = state;
		this.sendPetalUpdate();
	}

	private void sendPetalUpdate()
	{
		this.dataManager.set(PLANT_PETALS, this.serializePlantPetals());
	}

	private int getPetalCountInState(boolean state)
	{
		int i = 0;

		for (boolean a : this.petals)
		{
			if (a == state)
			{
				i++;
			}
		}

		return i;
	}

	private boolean canStayHere(final BlockPos pos)
	{
		if (!Block.isOpaque(this.world.getBlockState(pos).getShape(this.world, pos)))
		{
			return false;
		}

		final BlockState rootBlock = this.world.getBlockState(pos.down());

		return rootBlock.getBlock() == BlocksAether.aether_grass
				|| rootBlock.getBlock() == BlocksAether.aether_dirt
				|| rootBlock.getBlock() == BlocksAether.highlands_snow_layer
				|| rootBlock.getBlock() == BlocksAether.highlands_snow;
	}

	@Override
	public void knockBack(Entity entity, float distance, double x, double y)
	{
	}

	@Override
	public void move(MoverType type, Vec3d vec)
	{
		if (type == MoverType.PISTON)
		{
			super.move(type, vec);
		}
	}

	@Override
	public ResourceLocation getLootTable()
	{
		return LootTablesAether.ENTITY_AECHOR_PLANT;
	}

	@Override
	public boolean processInteract(PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (!player.isCreative() && stack.getItem() == ItemsAether.skyroot_bucket)
		{
			if (this.getPoisonLeft() > 0)
			{
				PlayerUtil.fillBucketInHand(player, hand, stack, new ItemStack(ItemsAether.skyroot_poison_bucket));

				this.setPoisonLeft(this.getPoisonLeft() - 1);

				return true;
			}
		}

		return false;
	}

	@OnlyIn(Dist.CLIENT)
	private void tickAnimation()
	{
		this.prevSinage = this.sinage;

		if (this.hurtTime > 0)
		{
			this.sinage += 0.5F;
		}
		else
		{
			this.sinage += this.canSeePrey() ? 0.3F : 0.1F;
		}

		float pie2 = 3.141593F * 2F;

		if (this.sinage > pie2)
		{
			this.sinage -= pie2;
			this.prevSinage -= pie2;
		}
	}

	@Override
	public void writeAdditional(CompoundNBT nbt)
	{
		super.writeAdditional(nbt);

		nbt.putInt("plantSize", this.getPlantSize());
		nbt.putInt("poisonLeft", this.getPoisonLeft());

		nbt.putByte("petals", this.serializePlantPetals());

		nbt.putInt("petalGrowTimer", this.petalGrowTimer);
	}

	@Override
	public void readAdditional(CompoundNBT nbt)
	{
		super.readAdditional(nbt);

		this.setPlantSize(nbt.getInt("plantSize"));
		this.setPoisonLeft(nbt.getInt("poisonLeft"));

		if (nbt.contains("petals"))
		{
			this.deserializePlantPetals(nbt.getByte("petals"));
		}
		else
		{
			Arrays.fill(this.petals, true);
		}

		this.petalGrowTimer = nbt.getInt("petalGrowTimer");
	}

	public boolean canSeePrey()
	{
		return this.dataManager.get(EntityAechorPlant.CAN_SEE_PREY);
	}

	public void setCanSeePrey(boolean canSee)
	{
		this.dataManager.set(EntityAechorPlant.CAN_SEE_PREY, canSee);
	}

	public int getPlantSize()
	{
		return this.dataManager.get(EntityAechorPlant.PLANT_SIZE);
	}

	public void setPlantSize(int size)
	{
		this.dataManager.set(EntityAechorPlant.PLANT_SIZE, (byte) size);
	}

	public int getPoisonLeft()
	{
		return this.poisonLeft;
	}

	public void setPoisonLeft(int poisonLeft)
	{
		this.poisonLeft = poisonLeft;
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key)
	{
		if (key == PLANT_PETALS)
		{
			this.deserializePlantPetals(this.dataManager.get(PLANT_PETALS));
		}
	}

	private void deserializePlantPetals(byte val)
	{
		for (int i = 0; i < this.petals.length; i++)
		{
			this.petals[i] = ((val >>> i) & 1) == 1;
		}
	}

	private byte serializePlantPetals()
	{
		byte val = 0;

		for (int i = 0; i < this.petals.length; i++)
		{
			if (this.petals[i])
			{
				val |= (1 << i);
			}
		}

		return val;
	}
}