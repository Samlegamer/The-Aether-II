package com.gildedgames.aether.common.entities.genes;

import com.gildedgames.aether.api.entity.genes.IGenePool;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public abstract class EntityGeneticAnimal<T extends IGenePool> extends AnimalEntity
{

	private static final DataParameter<Integer> SEED = EntityDataManager.createKey(EntityGeneticAnimal.class, DataSerializers.VARINT);

	private static final DataParameter<Integer> FATHER_SEED = EntityDataManager.createKey(EntityGeneticAnimal.class, DataSerializers.VARINT);

	private static final DataParameter<Integer> MOTHER_SEED = EntityDataManager.createKey(EntityGeneticAnimal.class, DataSerializers.VARINT);

	private static final DataParameter<Boolean> SHOULD_RETRANSFORM = EntityDataManager.createKey(EntityGeneticAnimal.class, DataSerializers.BOOLEAN);

	private final T genePool;

	protected EntityGeneticAnimal(EntityType<? extends AnimalEntity> type, World worldIn)
	{
		super(type, worldIn);

		this.genePool = this.createNewGenePool();
	}

	public abstract T createNewGenePool();

	public T getGenePool()
	{
		return this.genePool;
	}

	@Override
	public void registerData()
	{
		super.registerData();

		this.dataManager.register(SEED, 0);
		this.dataManager.register(FATHER_SEED, 0);
		this.dataManager.register(MOTHER_SEED, 0);
		this.dataManager.register(SHOULD_RETRANSFORM, false);
	}

	@Override
	public void tick()
	{
		super.tick();

		if (this.shouldRetransform() && this.world.isRemote())
		{
			if (this.getSeed() == this.getFatherSeed() && this.getSeed() == this.getMotherSeed())
			{
				this.getGenePool().transformFromSeed(this.getSeed());
			}
			else
			{
				this.getGenePool().transformFromParents(this.getSeed(), this.getFatherSeed(), this.getMotherSeed());
			}

			this.setShouldRetransform(false);
		}
	}

	public int getSeed()
	{
		return this.getDataManager().get(EntityGeneticAnimal.SEED);
	}

	protected void setSeed(int seed)
	{
		this.getDataManager().set(EntityGeneticAnimal.SEED, seed);
	}

	public int getFatherSeed()
	{
		return this.getDataManager().get(EntityGeneticAnimal.FATHER_SEED);
	}

	protected void setFatherSeed(int seed)
	{
		this.getDataManager().set(EntityGeneticAnimal.FATHER_SEED, seed);
	}

	public int getMotherSeed()
	{
		return this.getDataManager().get(EntityGeneticAnimal.MOTHER_SEED);
	}

	protected void setMotherSeed(int seed)
	{
		this.getDataManager().set(EntityGeneticAnimal.MOTHER_SEED, seed);
	}

	protected boolean shouldRetransform()
	{
		return this.getDataManager().get(EntityGeneticAnimal.SHOULD_RETRANSFORM);
	}

	protected void setShouldRetransform(boolean flag)
	{
		this.getDataManager().set(EntityGeneticAnimal.SHOULD_RETRANSFORM, flag);
	}

	@Override
	public void writeAdditional(CompoundNBT nbt)
	{
		super.writeAdditional(nbt);

		nbt.putInt("seed", this.getSeed());
		nbt.putInt("fatherSeed", this.getFatherSeed());
		nbt.putInt("motherSeed", this.getMotherSeed());
	}

	@Override
	public void readAdditional(CompoundNBT nbt)
	{
		super.readAdditional(nbt);

		int seed = nbt.getInt("seed");
		int fatherSeed = nbt.getInt("fatherSeed");
		int motherSeed = nbt.getInt("motherSeed");

		if (seed == fatherSeed && seed == motherSeed)
		{
			this.getGenePool().transformFromSeed(seed);
		}
		else
		{
			this.getGenePool().transformFromParents(seed, fatherSeed, motherSeed);
		}
	}

}