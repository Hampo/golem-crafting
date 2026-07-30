package org.zhbot.golem_crafting;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;

public class SouthGolemOverlay extends Overlay {
    private static final int RESPAWN_DELAY = 10;

    private final Client client;
    private final GolemCraftingPlugin plugin;
    //private final GolemCraftingConfig config;

    private static final int SOUTH_GOLEM_STATION_ID = 62352;
    private static final WorldPoint SOUTH_GOLEM_TILE = new WorldPoint(2595, 2253, 0);
    private static final WorldPoint SOUTH_GOLEM_NORTH_TILE = new WorldPoint(SOUTH_GOLEM_TILE.getX(), SOUTH_GOLEM_TILE.getY() + 1, 0);
    private static final WorldPoint SOUTH_GOLEM_EAST_TILE = new WorldPoint(SOUTH_GOLEM_TILE.getX() + 1, SOUTH_GOLEM_TILE.getY(), 0);
    private static final WorldPoint SOUTH_GOLEM_SOUTH_TILE = new WorldPoint(SOUTH_GOLEM_TILE.getX(), SOUTH_GOLEM_TILE.getY() - 1, 0);
    private static final WorldPoint SOUTH_GOLEM_WEST_TILE = new WorldPoint(SOUTH_GOLEM_TILE.getX() - 1, SOUTH_GOLEM_TILE.getY(), 0);

    @Inject
    private SouthGolemOverlay(Client client, GolemCraftingPlugin plugin/*, GolemCraftingConfig config*/)
    {
        this.client = client;
        this.plugin = plugin;
        //this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.getSouthGolemProgress() == 0 && !plugin.hasGolemMaterials())
            return null;

        var localTile = LocalPoint.fromWorld(client, SOUTH_GOLEM_TILE);
        if (localTile == null)
            return null;

        var worldView = client.getWorldView(localTile.getWorldView());
        if (worldView == null)
            return null;

        var scene = worldView.getScene();
        if (scene == null)
            return null;

        var tile = scene.getTiles()[SOUTH_GOLEM_TILE.getPlane()][localTile.getSceneX()][localTile.getSceneY()];
        if (tile == null)
            return null;

        GameObject southStation = null;
        for (var gameObject : tile.getGameObjects())
        {
            if (gameObject != null && gameObject.getId() == SOUTH_GOLEM_STATION_ID)
            {
                southStation = gameObject;
                break;
            }
        }
        if (southStation == null)
            return null;

        if (plugin.getSouthGolemProgress() == 0)
        {
            renderTile(graphics, southStation, SOUTH_GOLEM_NORTH_TILE);
            return null;
        }

        if (!plugin.isSouthGolemNorthDone())
        {
            renderTile(graphics, southStation, SOUTH_GOLEM_NORTH_TILE);
            return null;
        }

        if (!plugin.isSouthGolemEastDone())
        {
            renderTile(graphics, southStation, SOUTH_GOLEM_EAST_TILE);
            return null;
        }

        if (!plugin.isSouthGolemSouthDone())
        {
            renderTile(graphics, southStation, SOUTH_GOLEM_SOUTH_TILE);
            return null;
        }

        if (!plugin.isSouthGolemWestDone())
        {
            renderTile(graphics, southStation, SOUTH_GOLEM_WEST_TILE);
            return null;
        }

        renderTile(graphics, southStation, SOUTH_GOLEM_NORTH_TILE);

        return null;
    }

    private void renderTile(Graphics2D graphics, GameObject gameObject, WorldPoint worldPoint)
    {
        var localPoint = LocalPoint.fromWorld(client, worldPoint);
        if (localPoint == null)
            return;

        var tilePoly = Perspective.getCanvasTilePoly(client, localPoint);
        OverlayUtil.renderPolygon(graphics, tilePoly, Color.GREEN);

        if (gameObject != null)
        {
            var ticksSinceProgress = client.getTickCount() - plugin.getSouthGolemProgressTick();
            if (plugin.getSouthGolemProgress() == 0 && ticksSinceProgress < RESPAWN_DELAY)
            {
                ProgressPieComponent pie = new ProgressPieComponent();
                pie.setPosition(gameObject.getCanvasLocation(0));
                pie.setProgress((float)ticksSinceProgress / RESPAWN_DELAY);
                pie.setBorderColor(Color.ORANGE.darker());
                pie.setFill(Color.ORANGE);
                pie.render(graphics);
            }
            else
            {
                var player = client.getLocalPlayer();
                var playerLocation = player.getWorldLocation();
                var color = (playerLocation.getX() == worldPoint.getX() && playerLocation.getY() == worldPoint.getY()) ? new Color(0, 255, 0, 75) : new Color(255, 0, 0, 75);
                renderObject(graphics, gameObject, color);
            }
        }
    }

    private void renderObject(Graphics2D graphics, GameObject gameObject, Color color)
    {
        var clickbox = gameObject.getClickbox();
        var mousePosition = client.getMouseCanvasPosition();

        OverlayUtil.renderHoverableArea(graphics, clickbox, mousePosition, color, color, color.darker());
    }
}
