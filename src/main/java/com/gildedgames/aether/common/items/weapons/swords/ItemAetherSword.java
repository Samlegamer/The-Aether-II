package com.gildedgames.aether.common.items.weapons.swords;

import com.gildedgames.aether.common.items.ItemAbilityType;
import com.gildedgames.aether.common.registry.content.CreativeTabsAether;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemAetherSword extends ItemSword
{
	private final ItemAbilityType abilityType;

	public ItemAetherSword(final ToolMaterial material, final ItemAbilityType abilityType)
	{
		super(material);

		this.abilityType = abilityType;

		this.setCreativeTab(CreativeTabsAether.WEAPONS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(final ItemStack stack, final World world, final List<String> tooltip, final ITooltipFlag flag)
	{
		if (this.abilityType != ItemAbilityType.NONE)
		{
			tooltip.add(String.format("%s: %s",
					TextFormatting.BLUE + I18n.format("item.aether.tooltip.ability"),
					TextFormatting.WHITE + I18n.format(this.getUnlocalizedName() + ".ability.desc")));

			if (!this.abilityType.isPassive())
			{
				tooltip.add(String.format("%s: %s",
						TextFormatting.DARK_AQUA + I18n.format("item.aether.tooltip.use"),
						TextFormatting.WHITE + I18n.format(this.getUnlocalizedName() + ".use.desc")));
			}
		}
	}
}
