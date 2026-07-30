package org.zhbot.golem_crafting;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class GolemCraftingInfobox extends OverlayPanel {
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
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Fur pouch:")
                    .right(plugin.hasLargeFurPouch() ? plugin.getFurPouchCount() + "/28" : "N/A")
                    .build());
        }

        return super.render(graphics);
    }
}
