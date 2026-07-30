package org.zhbot.golem_crafting;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import java.awt.*;

public class GolemOverlay extends Overlay {
    private final Client client;
    private final GolemCraftingPlugin plugin;
    private final GolemCraftingConfig config;
    private final Golem golem;

    public GolemOverlay(Client client, GolemCraftingPlugin plugin, GolemCraftingConfig config, Golem golem)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
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
                    renderObject(graphics, station, config.overlayPlinthValidColour());
            }

            return null;
        }

        var player = client.getLocalPlayer();
        if (player == null)
            return null;
        var playerLocation = player.getWorldLocation();
        var onValidTile = false;

        if (progress == 5)
        {
            onValidTile = playerLocation.distanceTo(golem.getFinalTile()) == 0;
            if (config.showOverlayTiles())
                renderTile(graphics, golem.getFinalTile());
        }
        else
        {
            if (!golem.isNorthDone(client))
            {
                onValidTile = playerLocation.distanceTo(golem.getNorthTile()) == 0;
                if (config.showOverlayTiles())
                    renderTile(graphics, golem.getNorthTile());

                if (config.showOverlayTileProgress())
                    renderPie(graphics, golem.getNorthTile(), golem.getNorthProgress(client), Color.ORANGE);
            }

            if (!golem.isEastDone(client))
            {
                onValidTile = onValidTile || playerLocation.distanceTo(golem.getEastTile()) == 0;
                if (config.showOverlayTiles())
                    renderTile(graphics, golem.getEastTile());

                if (config.showOverlayTileProgress())
                    renderPie(graphics, golem.getEastTile(), golem.getEastProgress(client), Color.ORANGE);
            }

            if (!golem.isSouthDone(client))
            {
                onValidTile = onValidTile || playerLocation.distanceTo(golem.getSouthTile()) == 0;
                if (config.showOverlayTiles())
                    renderTile(graphics, golem.getSouthTile());

                if (config.showOverlayTileProgress())
                    renderPie(graphics, golem.getSouthTile(), golem.getSouthProgress(client), Color.ORANGE);
            }

            if (!golem.isWestDone(client))
            {
                onValidTile = onValidTile || playerLocation.distanceTo(golem.getWestTile()) == 0;
                if (config.showOverlayTiles())
                    renderTile(graphics, golem.getWestTile());

                if (config.showOverlayTileProgress())
                    renderPie(graphics, golem.getWestTile(), golem.getWestProgress(client), Color.ORANGE);
            }
        }

        var station = golem.getStationGameObject(client);
        if (station == null)
            return null;

        if (config.showOverlayPlinth())
            renderObject(graphics, station, onValidTile ? (progress == 5 ? config.overlayPlinthValidCoreColour() : config.overlayPlinthValidColour()) : (progress == 5 ? config.overlayPlinthInvalidCoreColour() : config.overlayPlinthInvalidColour()));

        return null;
    }

    private void renderTile(Graphics2D graphics, WorldPoint worldPoint)
    {
        var localPoint = LocalPoint.fromWorld(client, worldPoint);
        if (localPoint == null)
            return;

        var tilePoly = Perspective.getCanvasTilePoly(client, localPoint);
        OverlayUtil.renderPolygon(graphics, tilePoly, config.overlayTileColour());
    }

    private void renderObject(Graphics2D graphics, GameObject gameObject, Color color)
    {
        var clickbox = gameObject.getClickbox();
        var mousePosition = client.getMouseCanvasPosition();

        OverlayUtil.renderHoverableArea(graphics, clickbox, mousePosition, color, color, color.darker());
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

    private void renderPie(Graphics2D graphics, Point position, float progress, Color color)
    {
        ProgressPieComponent pie = new ProgressPieComponent();
        pie.setPosition(position);
        pie.setProgress(progress);
        pie.setBorderColor(color.darker());
        pie.setFill(color);
        pie.render(graphics);
    }
}
