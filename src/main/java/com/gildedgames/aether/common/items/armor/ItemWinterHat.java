package com.gildedgames.aether.common.items.armor;

import javax.annotation.Nullable;

import com.gildedgames.aether.client.models.items.ModelWinterHat;
import com.gildedgames.aether.common.AetherCore;
import com.gildedgames.aether.common.blocks.BlocksAether;
import com.gildedgames.aether.common.items.ItemsAether;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWinterHat extends ItemArmor
{
    public ItemWinterHat()
    {
        super(ItemsAether.WINTER_ARMOR, 0, EntityEquipmentSlot.HEAD);
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
        return AetherCore.getResourcePath("textures/armor/winter_hat.png");
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default)
    {
        return new ModelWinterHat();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        if(repair.isItemEqual(new ItemStack(BlocksAether.cloudwool_block))) return true;
        return super.getIsRepairable(toRepair, repair);
    }
}
