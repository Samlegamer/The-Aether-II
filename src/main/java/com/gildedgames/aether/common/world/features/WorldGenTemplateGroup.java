package com.gildedgames.aether.common.world.features;

import com.google.common.collect.Lists;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;

import java.util.List;
import java.util.Random;

public class WorldGenTemplateGroup extends WorldGenerator
{

	protected static final Rotation[] ROTATIONS = Rotation.values();

	private List<WorldGenTemplate> templates;

	public WorldGenTemplateGroup(WorldGenTemplate... templates)
	{
		this.templates = Lists.newArrayList(templates);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos position)
	{
		//List<WorldGenTemplate> possibleTemplates = Lists.newArrayList();

		Rotation rotation = ROTATIONS[rand.nextInt(ROTATIONS.length)];

		PlacementSettings settings = new PlacementSettings().setMirror(Mirror.NONE).setRotation(rotation).setIgnoreEntities(false).setChunk(null).setReplacedBlock(null).setIgnoreStructureBlock(false);

		/*for (WorldGenTemplate template : this.templates)
		{
			if (template.canPlaceTemplate(world, rand, position, rotation))
			{
				possibleTemplates.add(template);
			}
		}*/

		if (this.templates.size() > 0)
		{
			WorldGenTemplate templateToGenerate = this.templates.get(rand.nextInt(this.templates.size()));

			boolean flag = templateToGenerate.placeTemplateWithCheck(world, position, settings);

			if (flag)
			{
				templateToGenerate.postGenerate(world, rand, position, rotation);
			}

			return true;
		}

		return false;
	}

}
