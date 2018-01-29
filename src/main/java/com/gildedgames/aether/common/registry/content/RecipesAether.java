package com.gildedgames.aether.common.registry.content;

import com.gildedgames.aether.api.AetherAPI;
import com.gildedgames.aether.common.blocks.BlocksAether;
import com.gildedgames.aether.common.items.ItemsAether;
import com.gildedgames.aether.common.items.weapons.ItemDartType;
import com.gildedgames.aether.common.recipes.altar.AltarEnchantRecipe;
import com.gildedgames.aether.common.recipes.altar.AltarRepairRecipe;
import com.gildedgames.aether.common.registry.minecraft.AetherFuelHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RecipesAether
{
	private static final Set<IRecipe> CRAFTABLE_RECIPES = new HashSet<>();

	public static void preInit()
	{
		OreDictionary.registerOre("skyrootplanks", BlocksAether.skyroot_planks);
		OreDictionary.registerOre("skyrootplanks", BlocksAether.dark_skyroot_planks);
		OreDictionary.registerOre("skyrootplanks", BlocksAether.light_skyroot_planks);
		OreDictionary.registerOre("feather", ItemsAether.moa_feather);
		OreDictionary.registerOre("feather", ItemsAether.cockatrice_feather);
		OreDictionary.registerOre("aerleather", ItemsAether.taegore_hide);
		OreDictionary.registerOre("aerleather", ItemsAether.burrukai_pelt);
		OreDictionary.registerOre("sugar", ItemsAether.swet_sugar);
	}

	public static void init()
	{
		registerFurnaceRecipes();
		registerCraftingRecipes();
		registerAltarRecipes();

		GameRegistry.registerFuelHandler(new AetherFuelHandler());
	}

	private static void registerFurnaceRecipes()
	{
		registerSmeltingRecipe(new ItemStack(BlocksAether.holystone), new ItemStack(BlocksAether.agiosite), 0.1f);
		registerSmeltingRecipe(new ItemStack(BlocksAether.arkenium_ore), new ItemStack(ItemsAether.arkenium), 0.85f);
		registerSmeltingRecipe(new ItemStack(BlocksAether.gravitite_ore), new ItemStack(ItemsAether.gravitite_plate), 1.0f);
		registerSmeltingRecipe(new ItemStack(BlocksAether.quicksoil), new ItemStack(BlocksAether.quicksoil_glass), 0.1f);
		registerSmeltingRecipe(new ItemStack(ItemsAether.moa_egg), new ItemStack(ItemsAether.fried_moa_egg), 0.4f);
		registerSmeltingRecipe(new ItemStack(ItemsAether.rainbow_moa_egg), new ItemStack(ItemsAether.fried_moa_egg), 0.4f);
		registerSmeltingRecipe(new ItemStack(BlocksAether.crude_scatterglass), new ItemStack(BlocksAether.scatterglass), 0.1f);
		registerSmeltingRecipe(new ItemStack(ItemsAether.raw_taegore_meat), new ItemStack(ItemsAether.taegore_steak), 0.4f);
		registerSmeltingRecipe(new ItemStack(ItemsAether.burrukai_rib_cut), new ItemStack(ItemsAether.burrukai_ribs), 0.4f);
		registerSmeltingRecipe(new ItemStack(ItemsAether.kirrid_loin), new ItemStack(ItemsAether.kirrid_cutlet), 0.4f);
	}

	private static void registerCraftingRecipes()
	{
		//TODO:
		//RecipeSorter.register("aether:wrappingPaper", RecipeWrappingPaper.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
		//RecipeSorter.register("aether:presentCrafting", RecipePresentCrafting.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");

		//CraftingManager.getInstance().addRecipe(new RecipeWrappingPaper());
		//CraftingManager.getInstance().addRecipe(new RecipePresentCrafting());
	}

	private static void registerAltarRecipes()
	{
		// Enchanted Dart Shooter
		AetherAPI.content().altar().registerAltarRecipe(new AltarEnchantRecipe(4, new ItemStack(ItemsAether.dart_shooter, 1, ItemDartType.GOLDEN.ordinal()),
				new ItemStack(ItemsAether.dart_shooter, 1, ItemDartType.ENCHANTED.ordinal())));

		// Enchanted Darts
		AetherAPI.content().altar().registerAltarRecipe(new AltarEnchantRecipe(1, new ItemStack(ItemsAether.dart, 1, ItemDartType.GOLDEN.ordinal()),
				new ItemStack(ItemsAether.dart, 1, ItemDartType.ENCHANTED.ordinal())));

		// Enchanted Strawberry
		AetherAPI.content().altar().registerAltarRecipe(new AltarEnchantRecipe(2, new ItemStack(ItemsAether.blueberries),
				new ItemStack(ItemsAether.enchanted_blueberry)));

		// Rainbow Strawberry
		AetherAPI.content().altar().registerAltarRecipe(new AltarEnchantRecipe(4, new ItemStack(ItemsAether.wyndberry),
				new ItemStack(ItemsAether.enchanted_wyndberry)));

		// Tool Repair Recipes
		AetherAPI.content().altar().registerAltarRecipe(new AltarRepairRecipe());

		// Healing Stone
		AetherAPI.content().altar().registerAltarRecipe(new AltarEnchantRecipe(5, new ItemStack(ItemsAether.healing_stone_depleted),
				new ItemStack(ItemsAether.healing_stone)));
	}

	private static void registerSmeltingRecipe(final ItemStack input, final ItemStack output, final float xp)
	{
		GameRegistry.addSmelting(input, output, xp);
	}

	public static Collection<IRecipe> getCraftableRecipes()
	{
		return Collections.unmodifiableCollection(CRAFTABLE_RECIPES);
	}
}
