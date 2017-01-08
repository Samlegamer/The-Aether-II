package com.gildedgames.aether.common.capabilities.item.effects;

import com.gildedgames.aether.api.capabilites.entity.IPlayerAether;
import com.gildedgames.aether.api.items.equipment.effects.EffectHelper;
import com.gildedgames.aether.api.items.equipment.effects.IEffect;
import com.gildedgames.aether.api.items.equipment.effects.IEffectInstance;
import com.gildedgames.aether.api.items.equipment.effects.IEffectProvider;
import com.gildedgames.aether.common.AetherCore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.text.DecimalFormat;
import java.util.Collection;

public class RegenerationEffect implements IEffect<RegenerationEffect.Provider>
{
	private static final DecimalFormat FORMATTER = new DecimalFormat("#.##");

	private static final ResourceLocation NAME = new ResourceLocation(AetherCore.MOD_ID, "regeneration");

	@Override
	public IEffectInstance createInstance(Collection<Provider> providers)
	{
		Instance state = new Instance();
		state.healAmount = EffectHelper.combineInt(providers, instance -> instance.heal);

		return state;
	}

	@Override
	public ResourceLocation getIdentifier()
	{
		return RegenerationEffect.NAME;
	}

	public static class Provider implements IEffectProvider
	{
		private final int heal;

		public Provider(int heal)
		{
			this.heal = heal;
		}

		@Override
		public ResourceLocation getFactory()
		{
			return RegenerationEffect.NAME;
		}
	}

	class Instance implements IEffectInstance
	{
		private int healAmount;

		private int ticksUntilHeal = 120;

		@Override
		public void onEntityUpdate(IPlayerAether player)
		{
			if (player.getEntity().hurtTime > 0)
			{
				this.ticksUntilHeal = 120;
				return;
			}

			if (this.ticksUntilHeal <= 0)
			{
				player.getEntity().heal(this.healAmount);

				this.ticksUntilHeal = 20;
			}

			this.ticksUntilHeal--;
		}

		@Override
		public void addItemInformation(Collection<String> label)
		{
			label.add(TextFormatting.RED.toString() + TextFormatting.ITALIC.toString() +
					"+" + FORMATTER.format(this.healAmount) + " Regeneration per Second");
		}
	}
}
