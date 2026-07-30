package org.zhbot.golem_crafting;

import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class FurPouchOverlay extends WidgetItemOverlay {
    private static final WorldPoint CENTER = new WorldPoint(2590, 2250, 0);
    private static final int MAX_DISTANCE = 40;

    private final Client client;
    private final GolemCraftingPlugin plugin;
    //private final GolemCraftingConfig config;

    @Inject
    private FurPouchOverlay(Client client, GolemCraftingPlugin plugin/*, GolemCraftingConfig config*/)
    {
        this.client = client;
        this.plugin = plugin;
        //this.config = config;

        showOnInventory();
        setPriority(2f);
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
        if (itemId != ItemID.HG_FURPOUCH_LARGE && itemId != ItemID.HG_FURPOUCH_LARGE_OPEN)
            return;

        var furPouchCount = plugin.getFurPouchCount();
        if (furPouchCount > 5)
            return;

        var bounds = widgetItem.getCanvasBounds();
        if (bounds == null || bounds.width <= 0 || bounds.height <= 0)
            return;

        var player = client.getLocalPlayer();
        if (player == null)
            return;
        var playerLocation = player.getWorldLocation();
        if (playerLocation.distanceTo(CENTER) > MAX_DISTANCE)
            return;

        Color color;
        if (furPouchCount == 0)
            color = Color.RED;
        else if (furPouchCount == -1)
            color = Color.YELLOW;
        else
            color = new Color(204, 102, 0);

        graphics.setColor(color);
        graphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
