package org.zhbot.golem_crafting.overlays;

import net.runelite.api.Client;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import org.zhbot.golem_crafting.GolemCraftingConfig;
import org.zhbot.golem_crafting.GolemCraftingPlugin;

import javax.inject.Inject;
import java.awt.*;

public class ResourceWarningInfobox extends OverlayPanel {
    private static final int SUNSTONE_ID = 34020;
    private static final int SUNSTONE_CORE_ID = 34022;

    private final Client client;
    private final GolemCraftingPlugin plugin;
    private final GolemCraftingConfig config;

    @Inject
    private ResourceWarningInfobox(Client client, GolemCraftingPlugin plugin, GolemCraftingConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isWithinGolemArea())
            return null;

        var inventory = client.getItemContainer(InventoryID.INV);
        if (inventory == null)
            return null;

        panelComponent.getChildren().clear();

        var empty = false;
        var low = false;

        if (config.resourceInfoboxWarnFur())
        {
            var furCount = plugin.getTotalFurCount();

            if (furCount == 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Fur")
                        .right("Empty")
                        .build());
                empty = true;
            }
            else if (furCount <= config.resourceInfoboxFurThreshold())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Fur")
                        .right("Low")
                        .build());
                low = true;
            }
        }

        if (config.resourceInfoboxWarnSunstone())
        {
            var sunstoneCount = inventory.count(SUNSTONE_ID);

            if (sunstoneCount == 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Sunstone")
                        .right("Empty")
                        .build());
                empty = true;
            }
            else if (sunstoneCount <= config.resourceInfoboxSunstoneThreshold())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Sunstone")
                        .right("Low")
                        .build());
                low = true;
            }
        }

        if (config.resourceInfoboxWarnSunstoneCore())
        {
            var sunstoneCoreCount = inventory.count(SUNSTONE_CORE_ID);

            if (sunstoneCoreCount == 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Cores")
                        .right("Empty")
                        .build());
                empty = true;
            }
            else if (sunstoneCoreCount <= config.resourceInfoboxCoreThreshold())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Cores")
                        .right("Low")
                        .build());
                low = true;
            }
        }

        if (!empty && !low)
            return null;

        if (empty)
            panelComponent.setBackgroundColor((!config.resourceInfoboxFlash() || client.getGameCycle() % 40 < 20) ? config.resourceInfoboxEmptyColour1() : config.resourceInfoboxEmptyColour2());
        else
            panelComponent.setBackgroundColor(config.resourceInfoboxLowColour());

        return panelComponent.render(graphics);
    }
}
