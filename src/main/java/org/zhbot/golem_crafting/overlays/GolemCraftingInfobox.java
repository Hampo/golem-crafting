package org.zhbot.golem_crafting.overlays;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.Text;
import org.zhbot.golem_crafting.GolemCraftingConfig;
import org.zhbot.golem_crafting.GolemCraftingPlugin;

import javax.inject.Inject;
import java.awt.*;
import java.util.Locale;

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
                .color(plugin.isCrafting() ? Color.GREEN : Color.RED)
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
                        .right(golem.getProgress() + "/6")
                        .build());
            }
        }

        if (config.showInfoboxFurPouch())
        {
            var furPouch = plugin.getFurPouch();
            var furPouchCount = furPouch.getCount();

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
                    .right(furPouch.hasFurPouch() ? furPouchCount + "/" + furPouch.getCapacity() : "N/A")
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

        if (config.showInfoboxSunStoneMomentum())
        {
            var ticks = plugin.getSunstoneOverlay().getMomentumTicks();
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Momentum:")
                    .right(String.valueOf(ticks))
                    .rightColor(ticks > 0 ? Color.GREEN : Color.RED)
                    .build());
        }

        if (config.showInfoboxLoot())
        {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Session Loot")
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

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE)
            return;

        var message = Text.removeTags(event.getMessage());

        var lootMatcher = GolemCraftingPlugin.LOOT_MESSAGE.matcher(message);
        if (!lootMatcher.matches())
            return;

        var lootAmount = Integer.parseInt(lootMatcher.group(1));
        var loot = lootMatcher.group(2).toLowerCase(Locale.ROOT);

        switch (loot)
        {
            case "uncut sapphire":
                sapphireCount += lootAmount;
                break;
            case "uncut emerald":
                emeraldCount += lootAmount;
                break;
            case "uncut ruby":
                rubyCount += lootAmount;
                break;
            case "uncut diamond":
                diamondCount += lootAmount;
                break;
            case "jeweller's chisel":
                jewellersChiselCount += lootAmount;
                break;
        }
    }
}
