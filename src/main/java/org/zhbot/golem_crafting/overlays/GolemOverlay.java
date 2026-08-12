package org.zhbot.golem_crafting.overlays;

import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import org.zhbot.golem_crafting.GolemCraftingConfig;
import org.zhbot.golem_crafting.GolemCraftingPlugin;
import org.zhbot.golem_crafting.enums.CardinalDirection;
import org.zhbot.golem_crafting.golems.Golem;
import org.zhbot.golem_crafting.utils.GraphicsUtils;

import java.awt.*;

public class GolemOverlay extends Overlay {
    private final Client client;
    private final GolemCraftingPlugin plugin;
    private final GolemCraftingConfig config;
    private final GraphicsUtils graphicsUtils;
    private final Golem golem;

    public GolemOverlay(Client client, GolemCraftingPlugin plugin, GolemCraftingConfig config, GraphicsUtils graphicsUtils, Golem golem)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.graphicsUtils = graphicsUtils;
        this.golem = golem;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        var progress = golem.getProgress();
        if (progress == 0)
        {
            if (!plugin.hasGolemMaterials())
                return null;

            if (plugin.isAnyGolemActive())
                return null;

            var station = golem.getStationGameObject();
            if (station == null)
                return null;

            var ticksSinceProgress = client.getTickCount() - golem.getLastProgressTick();
            if (ticksSinceProgress < Golem.RESPAWN_DELAY)
            {
                graphicsUtils.renderPie(graphics, station.getCanvasLocation(0), (double)ticksSinceProgress / Golem.RESPAWN_DELAY, Color.ORANGE);
            }
            else
            {
                if (config.showOverlayPlinth())
                    graphicsUtils.renderObject(graphics, station, config.overlayPlinthRenderStyle(), config.overlayPlinthValidColour());
            }

            return null;
        }

        var player = client.getLocalPlayer();
        if (player == null)
            return null;
        var playerLocation = player.getWorldLocation();
        var onValidTile = false;

        var currentSide = CardinalDirection.NONE;
        var isFinalStep = progress == 5;
        if (isFinalStep)
        {
            onValidTile = playerLocation.distanceTo(golem.getFinalTile()) == 0;

            if (config.showOverlayTiles())
                graphicsUtils.renderTile(graphics, golem.getFinalTile(), config.overlayTileColour());
        }
        else
        {
            if (!golem.isNorthDone())
            {
                if (playerLocation.distanceTo(golem.getNorthTile()) == 0)
                {
                    onValidTile = true;
                    currentSide = CardinalDirection.NORTH;
                }

                if (config.showOverlayTiles())
                    graphicsUtils.renderTile(graphics, golem.getNorthTile(), config.overlayTileColour());

                if (config.showProgressMode().isShowTiles())
                    graphicsUtils.renderPie(graphics, golem.getNorthTile(), golem.getNorthProgress(), config.overlayProgressColour());
            }
            else if (config.showOverlayCompleteTiles())
            {
                graphicsUtils.renderTile(graphics, golem.getNorthTile(), config.overlayTileCompleteColour());
            }

            if (!golem.isEastDone())
            {
                if (playerLocation.distanceTo(golem.getEastTile()) == 0)
                {
                    onValidTile = true;
                    currentSide = CardinalDirection.EAST;
                }

                if (config.showOverlayTiles())
                    graphicsUtils.renderTile(graphics, golem.getEastTile(), config.overlayTileColour());

                if (config.showProgressMode().isShowTiles())
                    graphicsUtils.renderPie(graphics, golem.getEastTile(), golem.getEastProgress(), config.overlayProgressColour());
            }
            else if (config.showOverlayCompleteTiles())
            {
                graphicsUtils.renderTile(graphics, golem.getEastTile(), config.overlayTileCompleteColour());
            }

            if (!golem.isSouthDone())
            {
                if (playerLocation.distanceTo(golem.getSouthTile()) == 0)
                {
                    onValidTile = true;
                    currentSide = CardinalDirection.SOUTH;
                }

                if (config.showOverlayTiles())
                    graphicsUtils.renderTile(graphics, golem.getSouthTile(), config.overlayTileColour());

                if (config.showProgressMode().isShowTiles())
                    graphicsUtils.renderPie(graphics, golem.getSouthTile(), golem.getSouthProgress(), config.overlayProgressColour());
            }
            else if (config.showOverlayCompleteTiles())
            {
                graphicsUtils.renderTile(graphics, golem.getSouthTile(), config.overlayTileCompleteColour());
            }

            if (!golem.isWestDone())
            {
                if (playerLocation.distanceTo(golem.getWestTile()) == 0)
                {
                    onValidTile = true;
                    currentSide = CardinalDirection.WEST;
                }

                if (config.showOverlayTiles())
                    graphicsUtils.renderTile(graphics, golem.getWestTile(), config.overlayTileColour());

                if (config.showProgressMode().isShowTiles())
                    graphicsUtils.renderPie(graphics, golem.getWestTile(), golem.getWestProgress(), config.overlayProgressColour());
            }
            else if (config.showOverlayCompleteTiles())
            {
                graphicsUtils.renderTile(graphics, golem.getWestTile(), config.overlayTileCompleteColour());
            }
        }

        var station = golem.getStationGameObject();
        if (station == null)
            return null;

        if (config.showOverlayPlinth())
        {
            Color color;
            if (!onValidTile)
                color = isFinalStep ? config.overlayPlinthInvalidCoreColour() : config.overlayPlinthInvalidColour();
            else if (config.showOverlayPlinthEfficiency() && progress > 0 && progress < 5 && plugin.isCrafting() && client.getVarbitValue(VarbitID.BUSY) == 0 && plugin.getLastBusyTick() >= golem.getLastShapeClickTick())
                color = config.overlayPlinthEfficiencyColour();
            else
                color = (isFinalStep ? config.overlayPlinthValidCoreColour() : config.overlayPlinthValidColour());
            graphicsUtils.renderObject(graphics, station, config.overlayPlinthRenderStyle(), color);
        }

        if (isFinalStep)
        {
            if (config.showOverlayPlinthCore())
                graphicsUtils.renderItem(graphics, station, ItemID.SUNSTONE_CORE, config.overlayZOffset());

            if (config.showOverlayGolemOnInsertCore())
                graphicsUtils.renderTile(graphics, golem.getGolemTile(), config.overlayTileGolemOnInsertColour());
        }
        else if (currentSide != CardinalDirection.NONE && config.showProgressMode().isShowPlinth())
        {
            var currentProgress = 0d;
            switch (currentSide)
            {
                case NORTH:
                    currentProgress = golem.getNorthProgress();
                    break;
                case EAST:
                    currentProgress = golem.getEastProgress();
                    break;
                case SOUTH:
                    currentProgress = golem.getSouthProgress();
                    break;
                case WEST:
                    currentProgress = golem.getWestProgress();
                    break;
            }

            graphicsUtils.renderPie(graphics, station, currentProgress, config.overlayProgressColour(), config.overlayZOffset());
        }


        return null;
    }
}
