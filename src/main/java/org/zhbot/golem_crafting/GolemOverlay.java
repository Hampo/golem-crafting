package org.zhbot.golem_crafting;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import java.awt.*;

public class GolemOverlay extends Overlay {
    private static final int SUNSTONE_CORE_ID = 34022;

    private final Client client;
    private final GolemCraftingPlugin plugin;
    private final GolemCraftingConfig config;
    private final ItemManager itemManager;
    private final Golem golem;

    public GolemOverlay(Client client, GolemCraftingPlugin plugin, GolemCraftingConfig config, ItemManager itemManager, Golem golem)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        this.golem = golem;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        var progress = golem.getProgress(client);
        if (progress == 0)
        {
            if (!plugin.hasGolemMaterials())
                return null;

            if (plugin.isAnyGolemActive())
                return null;

            var station = golem.getStationGameObject(client);
            if (station == null)
                return null;

            var ticksSinceProgress = client.getTickCount() - golem.getLastProgressTick();
            if (ticksSinceProgress < Golem.RESPAWN_DELAY)
            {
                renderPie(graphics, station.getCanvasLocation(0), (float)ticksSinceProgress / Golem.RESPAWN_DELAY, Color.ORANGE);
            }
            else
            {
                if (config.showOverlayPlinth())
                    renderObject(graphics, station, config.overlayPlinthRenderStyle(), config.overlayPlinthValidColour());
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
                renderTile(graphics, golem.getFinalTile(), config.overlayTileColour());
        }
        else
        {
            if (!golem.isNorthDone(client))
            {
                if (playerLocation.distanceTo(golem.getNorthTile()) == 0)
                {
                    onValidTile = true;
                    currentSide = CardinalDirection.NORTH;
                }

                if (config.showOverlayTiles())
                    renderTile(graphics, golem.getNorthTile(), config.overlayTileColour());

                if (config.showProgressMode().isShowTiles())
                    renderPie(graphics, golem.getNorthTile(), golem.getNorthProgress(client), config.overlayProgressColour());
            }
            else if (config.showOverlayCompleteTiles())
            {
                renderTile(graphics, golem.getNorthTile(), config.overlayTileCompleteColour());
            }

            if (!golem.isEastDone(client))
            {
                if (playerLocation.distanceTo(golem.getEastTile()) == 0)
                {
                    onValidTile = true;
                    currentSide = CardinalDirection.EAST;
                }

                if (config.showOverlayTiles())
                    renderTile(graphics, golem.getEastTile(), config.overlayTileColour());

                if (config.showProgressMode().isShowTiles())
                    renderPie(graphics, golem.getEastTile(), golem.getEastProgress(client), config.overlayProgressColour());
            }
            else if (config.showOverlayCompleteTiles())
            {
                renderTile(graphics, golem.getEastTile(), config.overlayTileCompleteColour());
            }

            if (!golem.isSouthDone(client))
            {
                if (playerLocation.distanceTo(golem.getSouthTile()) == 0)
                {
                    onValidTile = true;
                    currentSide = CardinalDirection.SOUTH;
                }

                if (config.showOverlayTiles())
                    renderTile(graphics, golem.getSouthTile(), config.overlayTileColour());

                if (config.showProgressMode().isShowTiles())
                    renderPie(graphics, golem.getSouthTile(), golem.getSouthProgress(client), config.overlayProgressColour());
            }
            else if (config.showOverlayCompleteTiles())
            {
                renderTile(graphics, golem.getSouthTile(), config.overlayTileCompleteColour());
            }

            if (!golem.isWestDone(client))
            {
                if (playerLocation.distanceTo(golem.getWestTile()) == 0)
                {
                    onValidTile = true;
                    currentSide = CardinalDirection.WEST;
                }

                if (config.showOverlayTiles())
                    renderTile(graphics, golem.getWestTile(), config.overlayTileColour());

                if (config.showProgressMode().isShowTiles())
                    renderPie(graphics, golem.getWestTile(), golem.getWestProgress(client), config.overlayProgressColour());
            }
            else if (config.showOverlayCompleteTiles())
            {
                renderTile(graphics, golem.getWestTile(), config.overlayTileCompleteColour());
            }
        }

        var station = golem.getStationGameObject(client);
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
            renderObject(graphics, station, config.overlayPlinthRenderStyle(), color);
        }

        if (isFinalStep && config.showOverlayPlinthCore())
            renderItem(graphics, station, SUNSTONE_CORE_ID);
        else if (!isFinalStep && currentSide != CardinalDirection.NONE && config.showProgressMode().isShowPlinth())
        {
            var currentProgress = 0f;
            switch (currentSide)
            {
                case NORTH:
                    currentProgress = golem.getNorthProgress(client);
                    break;
                case EAST:
                    currentProgress = golem.getEastProgress(client);
                    break;
                case SOUTH:
                    currentProgress = golem.getSouthProgress(client);
                    break;
                case WEST:
                    currentProgress = golem.getWestProgress(client);
                    break;
            }

            renderPie(graphics, station, currentProgress, config.overlayProgressColour());
        }


        return null;
    }

    private void renderTile(Graphics2D graphics, WorldPoint worldPoint, Color color)
    {
        var localPoint = LocalPoint.fromWorld(client, worldPoint);
        if (localPoint == null)
            return;

        var tilePoly = Perspective.getCanvasTilePoly(client, localPoint);
        OverlayUtil.renderPolygon(graphics, tilePoly, color);
    }

    private void renderObject(Graphics2D graphics, GameObject gameObject, RenderMode renderMode, Color color)
    {
        var area = renderMode == RenderMode.CLICKBOX ? gameObject.getClickbox() : gameObject.getConvexHull();
        var mousePosition = client.getMouseCanvasPosition();

        OverlayUtil.renderHoverableArea(graphics, area, mousePosition, color, color, color.darker());
    }

    private void renderPie(Graphics2D graphics, WorldPoint worldPoint, float progress, Color color)
    {
        if (worldPoint == null)
            return;

        var localPoint = LocalPoint.fromWorld(client, worldPoint);
        if (localPoint == null)
            return;

        var point = Perspective.localToCanvas(client, localPoint, worldPoint.getPlane());
        if (point == null)
            return;

        renderPie(graphics, point, progress, color);
    }

    private void renderPie(Graphics2D graphics, GameObject gameObject, float progress, Color color)
    {
        if (gameObject == null)
            return;

        var localPoint = gameObject.getLocalLocation();

        var point = Perspective.localToCanvas(client, localPoint, gameObject.getPlane(), config.overlayZOffset());
        if (point == null)
            return;

        renderPie(graphics, point, progress, color);
    }

    private void renderPie(Graphics2D graphics, Point position, float progress, Color color)
    {
        ProgressPieComponent pie = new ProgressPieComponent();
        pie.setPosition(position);
        pie.setProgress(progress);
        pie.setBorderColor(color.darker());
        pie.setFill(color);
        pie.render(graphics);
    }

    private void renderItem(Graphics2D graphics, GameObject gameObject, int itemId)
    {
        var itemImage = itemManager.getImage(itemId);
        if (itemImage == null)
            return;

        OverlayUtil.renderImageLocation(client, graphics, gameObject.getLocalLocation(), itemImage, config.overlayZOffset());
    }
}
