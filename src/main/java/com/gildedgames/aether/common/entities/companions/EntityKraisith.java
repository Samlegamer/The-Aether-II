package com.gildedgames.aether.common.entities.companions;

import net.minecraft.entity.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityKraisith extends EntityCombatCompanion
{

	public EntityKraisith(EntityType<? extends CreatureEntity> type, World worldIn)
	{
		super(type, worldIn);
	}

	@Override
	protected void registerAttributes()
	{
		super.registerAttributes();

		this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this),
				(float) ((int) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));

		if (flag)
		{
			this.applyEnchantments(this, entity);

			if (entity instanceof LivingEntity)
			{
				LivingEntity living = (LivingEntity) entity;

				living.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 20, 3));
			}
		}

		return flag;
	}

}