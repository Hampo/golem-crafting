package org.zhbot.golem_crafting;

import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class GolemCraftingInfobox extends OverlayPanel {
    private static final int TOTAL_GOLEMS_ID = 15738;
    private static final int SUNSTONE_ID = 34020;
    private static final int SUNSTONE_CORE_ID = 34022;

    private final Client client;
    private final GolemCraftingPlugin plugin;
    private final GolemCraftingConfig config;

    @Inject
    private GolemCraftingInfobox(Client client, GolemCraftingPlugin plugin, GolemCraftingConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.TOP_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isWithinGolemArea())
            return super.render(graphics);

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Golem Crafting")
                .leftColor(Color.GREEN)
                .build());

        if (config.showInfoboxTotal())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total Golems:")
                    .right(String.valueOf(client.getVarbitValue(TOTAL_GOLEMS_ID)))
                    .build());
        }

        if (config.showInfoboxState())
        {
            for (var golem : plugin.getGolems())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(golem.getName() + ":")
                        .right(golem.getProgress(client) + "/6")
                        .build());
            }
        }

        if (config.showInfoboxFurPouch())
        {
            var furPouchCount = plugin.getFurPouchCount();
            Color color;
            if (furPouchCount == 0)
                color = config.infoboxFurPouchEmptyTextColour();
            else if (furPouchCount == -1)
                color = config.infoboxFurPouchUnknownTextColour();
            else if (furPouchCount <= config.furPouchLowThreshold())
                color = config.infoboxFurPouchLowTextColour();
            else
                color = Color.WHITE;
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Fur Pouch:")
                    .right(plugin.hasLargeFurPouch() ? furPouchCount + "/28" : "N/A")
                    .rightColor(color)
                    .build());
        }

        if (config.showInfoboxSunStone())
        {
            ItemContainer inventory = client.getItemContainer(InventoryID.INV);
            if (inventory != null)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Sunstone Core:")
                        .right(String.valueOf(inventory.count(SUNSTONE_CORE_ID)))
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Sunstone:")
                        .right(String.valueOf(inventory.count(SUNSTONE_ID)))
                        .build());
            }
        }

        return super.render(graphics);
    }
}
