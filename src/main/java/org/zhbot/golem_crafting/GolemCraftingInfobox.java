package org.zhbot.golem_crafting;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class GolemCraftingInfobox extends OverlayPanel {
    private static final int TOTAL_GOLEMS_ID = 15738;
    private static final int SUNSTONE_ID = 34020;
    private static final int SUNSTONE_CORE_ID = 34022;

    private final Client client;
    private final GolemCraftingPlugin plugin;
    private final GolemCraftingConfig config;

    private int sapphireCount = 0;
    private int emeraldCount = 0;
    private int rubyCount = 0;
    private int diamondCount = 0;
    private int jewellersChiselCount = 0;

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

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Golem Crafting")
                .color(Color.GREEN)
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

        if (config.showInfoboxLoot())
        {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Loot")
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Uncut Sapphire:")
                    .right(String.valueOf(sapphireCount))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Uncut Emerald:")
                    .right(String.valueOf(emeraldCount))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Uncut Ruby:")
                    .right(String.valueOf(rubyCount))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Uncut Diamond:")
                    .right(String.valueOf(diamondCount))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Jeweller's Chisel:")
                    .right(String.valueOf(jewellersChiselCount))
                    .build());
        }

        return super.render(graphics);
    }

    public void incrementSapphireCount(int value)
    {
        sapphireCount += value;
    }

    public void incrementEmeraldCount(int value)
    {
        emeraldCount += value;
    }

    public void incrementRubyCount(int value)
    {
        rubyCount += value;
    }

    public void incrementDiamondCount(int value)
    {
        diamondCount += value;
    }

    public void incrementJewellersChiselCount(int value)
    {
        jewellersChiselCount += value;
    }
}
