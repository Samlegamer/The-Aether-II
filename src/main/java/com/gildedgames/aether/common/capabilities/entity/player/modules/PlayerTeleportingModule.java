package com.gildedgames.aether.common.capabilities.entity.player.modules;

import com.gildedgames.aether.client.ClientEventHandler;
import com.gildedgames.aether.client.gui.misc.GuiIntro;
import com.gildedgames.aether.common.AetherCore;
import com.gildedgames.aether.common.CommonEvents;
import com.gildedgames.aether.common.capabilities.entity.player.PlayerAether;
import com.gildedgames.aether.common.capabilities.entity.player.PlayerAetherModule;
import com.gildedgames.aether.common.registry.content.DimensionsAether;
import com.gildedgames.aether.common.registry.content.SoundsAether;
import com.gildedgames.aether.common.util.helpers.IslandHelper;
import com.gildedgames.aether.common.world.aether.island.ChunkGeneratorAether;
import com.gildedgames.orbis.api.util.TeleporterGeneric;
import com.gildedgames.orbis.api.util.io.NBTFunnel;
import com.gildedgames.orbis.api.util.mc.BlockPosDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.function.Supplier;

public class PlayerTeleportingModule extends PlayerAetherModule
{

	private float prevTimeInPortal, timeInPortal, timeCharged;

	private boolean teleported, teleporting;

	private BlockPosDimension nonAetherPos, aetherPos;

	private boolean playedIntro;

	public PlayerTeleportingModule(final PlayerAether playerAether)
	{
		super(playerAether);
	}

	public float getPrevTimeInPortal()
	{
		return this.prevTimeInPortal;
	}

	public float getTimeInPortal()
	{
		return this.timeInPortal;
	}

	public BlockPosDimension getNonAetherPos()
	{
		return this.nonAetherPos;
	}

	public void setNonAetherPos(final BlockPosDimension pos)
	{
		this.nonAetherPos = pos;
	}

	public BlockPosDimension getAetherPos()
	{
		return this.aetherPos;
	}

	public void setAetherPos(final BlockPosDimension pos)
	{
		this.aetherPos = pos;
	}

	public boolean hasPlayedIntro()
	{
		return this.playedIntro;
	}

	@Override
	public void onUpdate()
	{
		if (this.getWorld().isRemote && this.getWorld().provider.getDimensionType() == DimensionsAether.NECROMANCER_TOWER)
		{
			if (Minecraft.getMinecraft().currentScreen == null && !this.playedIntro)
			{
				Minecraft.getMinecraft().displayGuiScreen(new GuiIntro());

				this.playedIntro = true;

				ClientEventHandler.DRAW_BLACK_SCREEN = false;
			}
		}

		this.prevTimeInPortal = this.timeInPortal;

		if (this.teleporting)
		{
			if (this.getEntity().world.isRemote && this.timeCharged == 0 && !this.teleported)
			{
				if (Minecraft.getMinecraft().player.getEntityId() == this.getEntity().getEntityId())
				{
					Minecraft.getMinecraft().player.playSound(SoundsAether.glowstone_portal_trigger, 1.0F, 1.0F);
				}

				this.timeCharged = 70.0F;
			}

			this.timeInPortal += 0.0125F;

			if (this.timeInPortal >= 1.0F)
			{
				this.timeInPortal = 1.0F;
			}

			if (!this.teleported && (this.getEntity().capabilities.isCreativeMode || this.timeInPortal == 1.0F))
			{
				this.teleportToAether();
			}
		}
		else if (this.getEntity().isPotionActive(MobEffects.NAUSEA)
				&& this.getEntity().getActivePotionEffect(MobEffects.NAUSEA).getDuration() > 60)
		{
			this.timeInPortal += 0.006666667F;

			if (this.timeInPortal > 1.0F)
			{
				this.timeInPortal = 1.0F;
			}
		}
		else
		{
			if (this.timeInPortal > 0.0F)
			{
				this.timeInPortal -= 0.05F;
			}

			if (this.timeInPortal < 0.0F)
			{
				this.timeInPortal = 0.0F;
			}

			this.teleported = false;
		}

		if (this.timeCharged > 0)
		{
			--this.timeCharged;
		}

		if (this.getEntity().timeUntilPortal > 0)
		{
			--this.getEntity().timeUntilPortal;
		}

		this.teleporting = false;
	}

	public void processTeleporting()
	{
		this.teleporting = true;
	}

	private void teleportToAether()
	{
		this.getEntity().timeUntilPortal = this.getEntity().getPortalCooldown();
		this.teleported = true;

		if (this.getEntity().world.isRemote && Minecraft.getMinecraft().player.getEntityId() == this.getEntity().getEntityId())
		{
			Minecraft.getMinecraft().player.playSound(SoundsAether.glowstone_portal_travel, 1.0F, 1.0F);
		}

		if (this.getEntity().world instanceof WorldServer)
		{
			final WorldServer worldServer = (WorldServer) this.getEntity().world;

			final EntityPlayer player = this.getEntity();

			final boolean inAether = this.getEntity().world.provider.getDimensionType() == DimensionsAether.AETHER;

			final int transferToID = inAether ? 0 : AetherCore.CONFIG.getAetherDimID();

			CommonEvents
					.teleportEntity(this.getEntity(), worldServer, new TeleporterGeneric(worldServer), transferToID, !inAether ? (Supplier<BlockPos>) () -> {
						final PlayerAether playerAether = PlayerAether.getPlayer(player);

						if (playerAether.getTeleportingModule().getAetherPos() == null)
						{
							final BlockPos pos = new BlockPos(100, 0, 100);
							final IChunkGenerator generator = worldServer.getChunkProvider().chunkGenerator;

							if (generator instanceof ChunkGeneratorAether)
							{
								final ChunkGeneratorAether aetherGen = (ChunkGeneratorAether) generator;

								aetherGen.getPreparation().checkAndPrepareIfAvailable(pos.getX() >> 4, pos.getZ() >> 4);
							}

							final BlockPos respawn = IslandHelper.getRespawnPoint(player.world, pos);

							playerAether.getTeleportingModule()
									.setAetherPos(new BlockPosDimension(respawn.getX(), respawn.getY(), respawn.getZ(), AetherCore.CONFIG.getAetherDimID()));
						}

						return playerAether.getTeleportingModule().getAetherPos();
					} : null);
		}

		this.timeInPortal = 0.0F;
	}

	@Override
	public void write(final NBTTagCompound output)
	{
		final NBTFunnel funnel = new NBTFunnel(output);

		final NBTTagCompound root = new NBTTagCompound();

		output.setTag("Teleport", root);
		root.setFloat("timeCharged", this.timeCharged);

		funnel.set("nonAetherPos", this.nonAetherPos);
		funnel.set("aetherPos", this.aetherPos);

		output.setBoolean("playedIntro", this.playedIntro);
	}

	@Override
	public void read(final NBTTagCompound input)
	{
		final NBTFunnel funnel = new NBTFunnel(input);

		final NBTTagCompound root = input.getCompoundTag("Teleport");
		this.timeCharged = root.getFloat("timeCharged");

		this.nonAetherPos = funnel.get("nonAetherPos");
		this.aetherPos = funnel.get("aetherPos");

		this.playedIntro = input.getBoolean("playedIntro");
	}
}