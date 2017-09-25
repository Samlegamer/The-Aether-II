package com.gildedgames.aether.api.world;

import com.gildedgames.aether.api.AetherAPI;
import com.gildedgames.aether.api.util.NBT;
import com.gildedgames.aether.api.util.NBTHelper;
import com.gildedgames.aether.api.util.TemplateUtil;
import com.gildedgames.aether.api.world.generation.TemplateDefinition;
import com.gildedgames.aether.api.world.generation.TemplateLoc;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TemplateInstance implements NBT
{

	private final ChunkPos[] chunksOccupied;

	private TemplateDefinition def;

	private int templateID;

	private TemplateLoc loc;

	public TemplateInstance(final TemplateDefinition def, final TemplateLoc loc)
	{
		this.def = def;
		this.templateID = AetherAPI.content().templates().getID(this.def);
		this.loc = loc;
		this.chunksOccupied = TemplateUtil.getChunksInsideTemplate(this.getDef(), this.getLoc());
	}

	public TemplateInstance(final NBTTagCompound tag)
	{
		this.read(tag);

		this.chunksOccupied = TemplateUtil.getChunksInsideTemplate(this.getDef(), this.getLoc());
	}

	public TemplateDefinition getDef()
	{
		return this.def;
	}

	public TemplateLoc getLoc()
	{
		return this.loc;
	}

	public ChunkPos[] getChunksOccupied()
	{
		return this.chunksOccupied;
	}

	@Override
	public boolean equals(final Object obj)
	{
		boolean flag = false;

		if (obj == this)
		{
			flag = true;
		}
		else if (obj instanceof TemplateInstance)
		{
			final TemplateInstance o = (TemplateInstance) obj;
			final EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.templateID, o.templateID);
			builder.append(this.loc, o.loc);

			flag = builder.isEquals();
		}

		return flag;
	}

	@Override
	public int hashCode()
	{
		final HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(this.templateID);
		builder.append(this.loc);

		return builder.toHashCode();
	}

	@Override
	public void write(final NBTTagCompound tag)
	{
		tag.setInteger("id", this.templateID);
		tag.setTag("loc", NBTHelper.write(this.loc));
	}

	@Override
	public void read(final NBTTagCompound tag)
	{
		this.def = AetherAPI.content().templates().get(tag.getInteger("id"));
		this.loc = new TemplateLoc(tag.getCompoundTag("loc"));
	}
}
