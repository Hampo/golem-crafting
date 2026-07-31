package org.zhbot.golem_crafting;

import net.runelite.api.Client;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class FurPouchOverlay extends WidgetItemOverlay {
    private final Client client;
    private final GolemCraftingPlugin plugin;
    private final GolemCraftingConfig config;

    @Inject
    private FurPouchOverlay(Client client, GolemCraftingPlugin plugin, GolemCraftingConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        showOnInventory();
        setPriority(2f);
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
        if (!GolemCraftingPlugin.LARGE_FUR_POUCH_IDS.contains((itemId)))
            return;

        var bounds = widgetItem.getCanvasBounds();
        if (bounds == null || bounds.width <= 0 || bounds.height <= 0)
            return;

        if (!plugin.isWithinGolemArea())
            return;

        var furPouchCount = plugin.getFurPouchCount();

        if (config.showOverlayFurPouch())
            renderBox(graphics, bounds, furPouchCount);

        if (!config.showOverlayFurPouchCount())
            return;

        String text = (furPouchCount == -1) ? "?" : String.valueOf(furPouchCount);
        graphics.setFont(net.runelite.client.ui.FontManager.getRunescapeSmallFont());

        Color color;
        if (furPouchCount == 0)
            color = config.overlayFurPouchEmptyTextColour();
        else if (furPouchCount == -1)
            color = config.overlayFurPouchUnknownTextColour();
        else if (furPouchCount <= config.furPouchLowThreshold())
            color = config.overlayFurPouchLowTextColour();
        else
            color = config.overlayFurPouchTextColour();
        graphics.setColor(color);

        graphics.drawString(text, bounds.x + 2, bounds.y + 12);
    }

    private void renderBox(Graphics2D graphics, Rectangle bounds, int furPouchCount)
    {
        Color color;
        if (furPouchCount == 0)
            color = config.overlayFurPouchEmptyColour();
        else if (furPouchCount == -1)
            color = config.overlayFurPouchUnknownColour();
        else if (furPouchCount <= config.furPouchLowThreshold())
            color = config.overlayFurPouchLowColour();
        else
            color = config.overlayFurPouchColour();

        graphics.setColor(color);
        graphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
