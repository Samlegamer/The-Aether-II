package com.gildedgames.aether.api.items.equipment.effects;

import net.minecraft.util.ResourceLocation;

/**
 * Provides an effect to an entity. This class should never be mutable.
 */
public interface IEffectProvider
{
	/**
	 * Returns the unique ID of this instance's {@link IEffect}.
	 * @return The {@link ResourceLocation} which identifies the factory
	 */
	ResourceLocation getFactory();
}
