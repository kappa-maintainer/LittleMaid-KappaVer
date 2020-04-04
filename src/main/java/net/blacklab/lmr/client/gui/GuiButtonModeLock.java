package net.blacklab.lmr.client.gui;

import net.blacklab.lmr.LittleMaidReengaged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;;

public class GuiButtonModeLock extends GuiButton {
	
	public static final ResourceLocation GUI_LOCKBUTTON_RESOURCE = new ResourceLocation(LittleMaidReengaged.DOMAIN+":textures/gui/container/buttons/lockbutton.png");
	
	public boolean locked;
		
	protected String translateKey;

	public GuiButtonModeLock(int buttonId, int x, int y, String buttonText, boolean locked, boolean isvisible) {
		super(buttonId, x, y, 16, 16, buttonText);
		this.locked = locked;
		translateKey = buttonText;
		this.visible = isvisible;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(!visible) return;
		handleHovered(mouseX, mouseY);
		GlStateManager.pushMatrix();
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		mc.getTextureManager().bindTexture(GUI_LOCKBUTTON_RESOURCE);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		drawTexturedModalRect(x, y, 0, locked?0:16, 16, 16);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
//		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
	
	protected void handleHovered(int mouseX, int mouseY) {
		hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + width && mouseY < this.y + height;
	}

	@Override
	public void drawButtonForegroundLayer(int mouseX, int mouseY) {
		// TODO 自動生成されたメソッド・スタブ
		super.drawButtonForegroundLayer(mouseX, mouseY);
		showHoverText(Minecraft.getMinecraft(), mouseX, mouseY);
	}
	
	protected void showHoverText(Minecraft mcMinecraft, int mx, int my){
		if(hovered){
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			GlStateManager.colorMask(true, true, true, false);
			FontRenderer fRenderer = mcMinecraft.getRenderManager().getFontRenderer();
			int lcolor = 0xc0000000;
			String viewString = I18n.format(translateKey);
			int fx = fRenderer.getStringWidth(viewString);
			drawGradientRect(mx+4, my+4, mx+4+fx+4, my+4+8+4, lcolor, lcolor);
			drawCenteredString(fRenderer, viewString, mx+fx/2+6, my+6, 0xffffffff);
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			GlStateManager.colorMask(true, true, true, true);
		}
	}

}
