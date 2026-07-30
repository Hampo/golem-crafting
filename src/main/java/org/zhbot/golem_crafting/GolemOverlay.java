package org.zhbot.golem_crafting;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
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
    private final Golem golem;

    public GolemOverlay(Client client, GolemCraftingPlugin plugin, Golem golem)
    {
        this.client = client;
        this.plugin = plugin;
        this.golem = golem;
        //this.config = config;

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
                ProgressPieComponent pie = new ProgressPieComponent();
                pie.setPosition(station.getCanvasLocation(0));
                pie.setProgress((float)ticksSinceProgress / Golem.RESPAWN_DELAY);
                pie.setBorderColor(Color.ORANGE.darker());
                pie.setFill(Color.ORANGE);
                pie.render(graphics);
            }
            else
            {
                renderObject(graphics, station, new Color(0, 255, 0, 75));
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
            renderTile(graphics, golem.getFinalTile());
        }
        else
        {
            if (!golem.isNorthDone(client))
            {
                onValidTile = playerLocation.distanceTo(golem.getNorthTile()) == 0;
                renderTile(graphics, golem.getNorthTile());
            }

            if (!golem.isEastDone(client))
            {
                onValidTile = onValidTile || playerLocation.distanceTo(golem.getEastTile()) == 0;
                renderTile(graphics, golem.getEastTile());
            }

            if (!golem.isSouthDone(client))
            {
                onValidTile = onValidTile || playerLocation.distanceTo(golem.getSouthTile()) == 0;
                renderTile(graphics, golem.getSouthTile());
            }

            if (!golem.isWestDone(client))
            {
                onValidTile = onValidTile || playerLocation.distanceTo(golem.getWestTile()) == 0;
                renderTile(graphics, golem.getWestTile());
            }
        }

        var station = golem.getStationGameObject(client);
        if (station == null)
            return null;

        renderObject(graphics, station, onValidTile ? new Color(0, 255, 0, 75) : new Color(255, 0, 0, 75));

        return null;
    }

    private void renderTile(Graphics2D graphics, WorldPoint worldPoint)
    {
        var localPoint = LocalPoint.fromWorld(client, worldPoint);
        if (localPoint == null)
            return;

        var tilePoly = Perspective.getCanvasTilePoly(client, localPoint);
        OverlayUtil.renderPolygon(graphics, tilePoly, Color.GREEN);
    }

    private void renderObject(Graphics2D graphics, GameObject gameObject, Color color)
    {
        var clickbox = gameObject.getClickbox();
        var mousePosition = client.getMouseCanvasPosition();

        OverlayUtil.renderHoverableArea(graphics, clickbox, mousePosition, color, color, color.darker());
    }
}
