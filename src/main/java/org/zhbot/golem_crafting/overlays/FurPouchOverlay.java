package org.zhbot.golem_crafting.overlays;

import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.*;
import org.zhbot.golem_crafting.utils.FurPouch;
import org.zhbot.golem_crafting.GolemCraftingConfig;
import org.zhbot.golem_crafting.GolemCraftingPlugin;
import org.zhbot.golem_crafting.utils.GraphicsUtils;

import javax.inject.Inject;
import java.awt.*;

public class FurPouchOverlay extends WidgetItemOverlay {
    private final GolemCraftingPlugin plugin;
    private final GolemCraftingConfig config;
    private final GraphicsUtils graphicsUtils;

    @Inject
    private FurPouchOverlay(GolemCraftingPlugin plugin, GolemCraftingConfig config, GraphicsUtils graphicsUtils)
    {
        this.plugin = plugin;
        this.config = config;
        this.graphicsUtils = graphicsUtils;

        showOnInventory();
        setPriority(2f);
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
        if (!FurPouch.ALL_POUCH_IDS.contains((itemId)))
            return;

        var bounds = widgetItem.getCanvasBounds();
        if (bounds == null || bounds.width <= 0 || bounds.height <= 0)
            return;

        if (!config.showFurPouchAlways() && !plugin.isWithinGolemArea())
            return;

        var furPouch = plugin.getFurPouch();
        var furPouchCount = furPouch.getCount();

        if (config.showOverlayFurPouch())
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
            graphicsUtils.renderBox(graphics, bounds, color);
        }

        if (!config.showOverlayFurPouchCount())
            return;

        Color color;
        if (furPouchCount == 0)
            color = config.overlayFurPouchEmptyTextColour();
        else if (furPouchCount == -1)
            color = config.overlayFurPouchUnknownTextColour();
        else if (furPouchCount <= config.furPouchLowThreshold())
            color = config.overlayFurPouchLowTextColour();
        else
            color = config.overlayFurPouchTextColour();

        graphicsUtils.renderText(graphics, bounds.x + 1, bounds.y + 13, (furPouchCount == -1) ? "?" : String.valueOf(furPouchCount), color);
    }
}
