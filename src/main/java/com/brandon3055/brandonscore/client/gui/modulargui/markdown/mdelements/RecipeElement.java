package com.brandon3055.brandonscore.client.gui.modulargui.markdown.mdelements;

import com.brandon3055.brandonscore.client.gui.modulargui.markdown.LayoutHelper;
import com.brandon3055.brandonscore.integration.IRecipeRenderer;
import com.brandon3055.brandonscore.integration.JeiHelper;
import com.brandon3055.brandonscore.lib.StackReference;
import com.brandon3055.projectintelligence.api.PiAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import static com.brandon3055.brandonscore.client.gui.modulargui.markdown.mdelements.MarkerElement.Type.NEW_LINE;

/**
 * Created by brandon3055 on 5/31/2018.
 */
public class RecipeElement extends MDElementBase<RecipeElement> {

    private List<IRecipeRenderer> renderers;
    public int spacing = 1;

    public RecipeElement(String stackString) {
        this.hasSubParts = true;
        this.boundless = true;
        this.colour = 0xc6c6c6;
        this.colourHover = -1;
        StackReference stackRef = StackReference.fromString(stackString);
        ItemStack stack;

        if (stackRef == null || (stack = stackRef.createStack()).isEmpty()) {
            error("[Broken recipe. Specified Item or Block could not be found!]");
            return;
        }

        if (!JeiHelper.jeiAvailable()) {
            error("[Broken recipe. The mod JEI (Just Enough Items) is required to display recipes!]");
            return;
        }

        renderers = JeiHelper.getRecipeRenderers(stack);
        if (renderers == null) {
            error("[Broken recipe. No recipe's were found for " + stackString + "]");
        }
        else if (renderers.isEmpty() && false) {
            error(I18n.format("pi.md.no_recipe_for_item.txt") + " (This message will only show in edit mode)");
        }
    }

    @Override
    public void layoutElement(LayoutHelper layout, List<MDElementBase> lineElement) {
        toRemove.addAll(subParts);
        subParts.clear();

        for (int i = 0; i < renderers.size(); i++) {
            IRecipeRenderer renderer = renderers.get(i);
            RecipeElementPart element = new RecipeElementPart(this, renderer);

            int avalibleWidth = layout.getWidth() - layout.getCaretXOffset();
            int nextWidth = renderer.getWidth() + leftPad + rightPad;
            if (avalibleWidth - spacing < nextWidth && layout.getCaretXOffset() > 0) {
                layout.newLine(0, subParts.isEmpty() ? 0 : 0);
                MarkerElement marker = new MarkerElement(NEW_LINE);
                subParts.add(marker);
            }
            else if (lineElement.isEmpty() || !(lineElement.get(lineElement.size() - 1) instanceof RecipeElement)) {
                MarkerElement spacer = new MarkerElement(spacing, 0);
                spacer.layoutElement(layout, lineElement);
                subParts.add(spacer);
            }

            addChild(element);
            subParts.add(element);
            element.layoutElement(layout, lineElement);
            if (i == 0) {
                setXPosMod((element1, integer) -> element.xPos());
                setYPosMod((element1, integer) -> element.yPos());
            }
        }

        if (!renderers.isEmpty()) {
            MarkerElement spacer = new MarkerElement(spacing, 0);
            spacer.layoutElement(layout, lineElement);
            subParts.add(spacer);
        }
    }

    private static class RecipeElementPart extends MDElementBase<RecipeElementPart> {
        private RecipeElement parent;
        private IRecipeRenderer renderer;

        public RecipeElementPart(RecipeElement parent, IRecipeRenderer renderer) {
            this.parent = parent;
            this.renderer = renderer;
            this.setSize(renderer.getWidth() + parent.leftPad + parent.rightPad + 8, renderer.getHeight() + parent.topPad + parent.bottomPad + 8);
        }

        @Override
        public void renderElement(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            boolean mouseOver = isMouseOver(mouseX, mouseY);
            if (parent.hasColourBorder) {
                drawColouredRect(xPos(), yPos(), xSize(), ySize(), 0xFF000000 | parent.getColourBorder(mouseOver));
            }
            else if (parent.hasColourBorderHover && mouseOver) {
                drawColouredRect(xPos(), yPos(), xSize(), ySize(), 0xFF000000 | parent.colourBorderHover);
            }

            renderer.render(minecraft, xPos() + parent.leftPad + 4, yPos() + parent.topPad + 4, mouseX, mouseY);
            super.renderElement(minecraft, mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean renderOverlayLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            if (isMouseOver(mouseX, mouseY)) {
                renderer.renderOverlay(mc, mouseX, mouseY);
                return true;
            }
            return super.renderOverlayLayer(minecraft, mouseX, mouseY, partialTicks);
        }

        public void layoutElement(LayoutHelper layout, List<MDElementBase> lineElement) {
            Point pos = layout.nextElementPos(xSize(), ySize() + parent.spacing);
            setPos(pos.x, pos.y);
        }

        @Override
        public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            if (isMouseOver(mouseX, mouseY) && (mouseButton == 0 || mouseButton == 1)) {
                renderer.handleRecipeClick(mc, mouseX, mouseY, mouseButton == 1);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        protected boolean keyTyped(char typedChar, int keyCode) throws IOException {
            int mouseX = Mouse.getX() * parent.screenWidth / this.mc.displayWidth;
            int mouseY = parent.screenHeight - Mouse.getY() * parent.screenHeight / this.mc.displayHeight - 1;

            if (isMouseOver(mouseX, mouseY)) {
                Object o = renderer.getIngredientUnderMouse(mouseX, mouseY);
                if (keyCode == JeiHelper.getRecipeKey(false)) {
                    renderer.handleRecipeClick(mc, mouseX, mouseY, false);
                    return true;
                }
                else if (keyCode == JeiHelper.getRecipeKey(true)) {
                    renderer.handleRecipeClick(mc, mouseX, mouseY, true);
                    return true;
                }
                else if (o instanceof ItemStack && !((ItemStack) o).isEmpty() && PiAPI.isAPIAvalible() && keyCode == PiAPI.getETGuiKey().getKeyCode()) {
                    List<String> pages = PiAPI.getRelatedPages((ItemStack) o);
                    if (!pages.isEmpty()) {
                        PiAPI.openGui(modularGui.getScreen(), pages);
                        return true;
                    }
                }
            }

            return super.keyTyped(typedChar, keyCode);
        }
    }
}
