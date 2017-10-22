package com.gildedgames.orbis.client.gui.util;

import com.gildedgames.orbis.client.util.rect.ModDim2D;
import com.gildedgames.orbis.client.util.rect.Rect;
import com.gildedgames.orbis.client.util.rect.RectHolder;
import com.gildedgames.orbis.client.util.rect.RectModifier;
import com.gildedgames.orbis.common.containers.ContainerGeneric;
import com.gildedgames.orbis.common.util.InputHelper;
import com.gildedgames.orbis.common.util.ObjectFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class GuiFrame extends GuiContainer implements RectHolder
{

	private final List<GuiFrame> children = new CopyOnWriteArrayList<>();

	private final ModDim2D dim = new ModDim2D();

	private boolean drawDefaultBackground;

	private boolean hasInit, enabled = true, visible = true, hoverEntered = false;

	public GuiFrame()
	{
		super(new ContainerGeneric(Minecraft.getMinecraft().player));
	}

	public GuiFrame(final Rect rect)
	{
		this(rect, new ContainerGeneric(Minecraft.getMinecraft().player));
	}

	public GuiFrame(final Rect rect, final Container inventorySlotsIn)
	{
		super(inventorySlotsIn);

		this.dim.set(rect);
	}

	public Collection<GuiFrame> getChildren()
	{
		return this.children;
	}

	public void setDrawDefaultBackground(final boolean flag)
	{
		this.drawDefaultBackground = flag;
	}

	public void clearChildren()
	{
		this.children.clear();
	}

	public void onHovered()
	{

	}

	public void onHoverEnter()
	{

	}

	public void onHoverExit()
	{

	}

	public abstract void init();

	public List<GuiFrame> seekAllContent()
	{
		return this.children;
	}

	public boolean isVisible()
	{
		return this.visible;
	}

	public void setVisible(final boolean flag)
	{
		this.visible = flag;
	}

	public boolean isEnabled()
	{
		return this.enabled;
	}

	public void setEnabled(final boolean flag)
	{
		this.enabled = flag;
	}

	@Override
	public ModDim2D dim()
	{
		return this.dim;
	}

	@Override
	public void updateState()
	{

	}

	@Override
	public void drawDefaultBackground()
	{
		if (this.drawDefaultBackground)
		{
			super.drawDefaultBackground();
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
	{

	}

	public void addChildNoMods(final GuiFrame element)
	{
		this.addChild(element, false);
	}

	public void addChild(final GuiFrame element)
	{
		this.addChild(element, true);
	}

	private void addChild(final GuiFrame element, final boolean mods)
	{
		final RectHolder gui = ObjectFilter.cast(element, RectHolder.class);
		final RectHolder parentModifier = ObjectFilter.cast(this, RectHolder.class);

		if (mods && gui != null && gui.dim().mod() != null && parentModifier != null)
		{
			gui.dim().add(parentModifier, RectModifier.ModifierType.POS, RectModifier.ModifierType.SCALE);
		}

		if (!element.hasInit && this.mc != null)
		{
			element.setWorldAndResolution(this.mc, (int) InputHelper.getScreenWidth(), (int) InputHelper.getScreenHeight());
		}

		this.children.add(element);
	}

	public void removeChild(final GuiFrame gui)
	{
		this.children.remove(gui);
	}

	@Override
	public void initGui()
	{
		super.initGui();

		if (!this.hasInit)
		{
			this.init();

			this.hasInit = true;
		}
	}

	public void preDraw()
	{

	}

	public void draw()
	{

	}

	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float partialTicks)
	{
		if (!this.isVisible())
		{
			return;
		}

		if (InputHelper.isHovered(this))
		{
			if (!this.hoverEntered)
			{
				this.onHoverEnter();

				this.hoverEntered = true;
			}

			this.onHovered();
		}
		else if (this.hoverEntered)
		{
			this.onHoverExit();

			this.hoverEntered = false;
		}

		this.preDraw();

		super.drawScreen(mouseX, mouseY, partialTicks);

		/** Enable for debug rectangle rendering to see dimensions **/
		//Gui.drawRect((int) this.dim().x(), (int) this.dim().y(), (int) this.dim().maxX(), (int) this.dim().maxY(), Integer.MAX_VALUE);

		final float x = this.dim().x();
		final float y = this.dim().y();

		GlStateManager.pushMatrix();

		GlStateManager.disableLighting();

		GlStateManager.translate(x + this.dim().originX(), y + this.dim().originY(), 0);

		GlStateManager.translate(this.dim().isCenteredX() ? (this.dim().width() / 2) : 0, this.dim().isCenteredY() ? (this.dim().height() / 2) : 0, 0);

		GlStateManager.scale(this.dim().scale(), this.dim().scale(), 0);

		GlStateManager.rotate(this.dim().degrees(), 0.0F, 0.0F, 1.0F);

		GlStateManager.translate(this.dim().isCenteredX() ? -(this.dim().width() / 2) : 0, this.dim().isCenteredY() ? -(this.dim().height() / 2) : 0, 0);

		GlStateManager.translate(-x - this.dim().originX(), -y - this.dim().originY(), 0);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glEnable(GL11.GL_ALPHA_TEST);

		GlStateManager.enableAlpha();

		this.draw();

		for (final GuiFrame child : this.children)
		{
			child.drawScreen(mouseX, mouseY, partialTicks);
		}

		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GlStateManager.disableAlpha();

		GlStateManager.popMatrix();
	}

	public void publicMouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException
	{
		this.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);

		for (final GuiFrame frame : this.children)
		{
			frame.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick)
	{
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

		for (final GuiFrame frame : this.children)
		{
			frame.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		}
	}

	@Override
	protected void mouseReleased(final int mouseX, final int mouseY, final int state)
	{
		super.mouseReleased(mouseX, mouseY, state);

		for (final GuiFrame frame : this.children)
		{
			frame.mouseReleased(mouseX, mouseY, state);
		}
	}

	@Override
	protected void handleMouseClick(final Slot slotIn, final int slotId, final int mouseButton, final ClickType type)
	{
		super.handleMouseClick(slotIn, slotId, mouseButton, type);
	}

	protected void keyTypedInner(final char typedChar, final int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		final int i = Mouse.getEventDWheel();

		if (i != 0)
		{
			this.onMouseWheel(i);
		}
	}

	public void onMouseWheel(final int state)
	{
		for (final GuiFrame frame : this.children)
		{
			frame.onMouseWheel(state);
		}
	}

	@Override
	protected void keyTyped(final char typedChar, final int keyCode) throws IOException
	{
		if (!this.children.isEmpty())
		{
			this.keyTypedInner(typedChar, keyCode);
		}

		for (final GuiFrame frame : this.children)
		{
			frame.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		for (final GuiFrame frame : this.children)
		{
			frame.onGuiClosed();
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		for (final GuiFrame frame : this.children)
		{
			frame.updateScreen();
		}
	}

	@Override
	public void setWorldAndResolution(final Minecraft mc, final int width, final int height)
	{
		super.setWorldAndResolution(mc, width, height);

		for (final GuiFrame frame : this.children)
		{
			frame.setWorldAndResolution(mc, width, height);
		}
	}

	@Override
	protected void actionPerformed(final GuiButton button) throws IOException
	{
		super.actionPerformed(button);

		for (final GuiFrame frame : this.children)
		{
			frame.actionPerformed(button);
		}
	}

	@Override
	public void onResize(final Minecraft mcIn, final int w, final int h)
	{
		super.onResize(mcIn, w, h);

		for (final GuiFrame frame : this.children)
		{
			frame.onResize(mcIn, w, h);
		}
	}
}