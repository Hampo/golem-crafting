package org.zhbot.golem_crafting.utils;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.ColorUtil;
import org.zhbot.golem_crafting.enums.RenderMode;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class GraphicsUtils {
    private final Client client;
    private final ItemManager itemManager;

    @Inject
    public GraphicsUtils(Client client, ItemManager itemManager)
    {
        this.client = client;
        this.itemManager = itemManager;
    }

    public void renderBox(Graphics2D graphics, Rectangle bounds, Color color)
    {
        var mousePosition = client.getMouseCanvasPosition();

        var fillColour = ColorUtil.colorWithAlpha(color, 50);
        OverlayUtil.renderHoverableArea(graphics, bounds, mousePosition, fillColour, color, color.darker());
    }

    public void renderText(Graphics2D graphics, int x, int y, String text, Color color)
    {
        var textComponent = new TextComponent();
        textComponent.setFont(FontManager.getRunescapeSmallFont());
        textComponent.setPosition(x, y);
        textComponent.setText(text);
        textComponent.setColor(color);
        textComponent.render(graphics);
    }

    public void renderTile(Graphics2D graphics, WorldPoint worldPoint, Color color)
    {
        var localPoint = LocalPoint.fromWorld(client, worldPoint);
        if (localPoint == null)
            return;

        var tilePoly = Perspective.getCanvasTilePoly(client, localPoint);
        OverlayUtil.renderPolygon(graphics, tilePoly, color);
    }

    public void renderObject(Graphics2D graphics, GameObject gameObject, RenderMode renderMode, Color color)
    {
        var area = renderMode == RenderMode.CLICKBOX ? gameObject.getClickbox() : gameObject.getConvexHull();
        var mousePosition = client.getMouseCanvasPosition();

        var borderColour = ColorUtil.colorWithAlpha(color, 255);
        OverlayUtil.renderHoverableArea(graphics, area, mousePosition, color, borderColour, borderColour.darker());
    }

    public void renderPie(Graphics2D graphics, WorldPoint worldPoint, float progress, Color color)
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

    public void renderPie(Graphics2D graphics, GameObject gameObject, float progress, Color color, int zOffset)
    {
        if (gameObject == null)
            return;

        var localPoint = gameObject.getLocalLocation();

        var point = Perspective.localToCanvas(client, localPoint, gameObject.getPlane(), zOffset);
        if (point == null)
            return;

        renderPie(graphics, point, progress, color);
    }

    public void renderPie(Graphics2D graphics, Point position, float progress, Color color)
    {
        ProgressPieComponent pie = new ProgressPieComponent();
        pie.setPosition(position);
        pie.setProgress(progress);
        pie.setBorderColor(color.darker());
        pie.setFill(color);
        pie.render(graphics);
    }

    public void renderItem(Graphics2D graphics, GameObject gameObject, int itemId, int zOffset)
    {
        var itemImage = itemManager.getImage(itemId);
        if (itemImage == null)
            return;

        OverlayUtil.renderImageLocation(client, graphics, gameObject.getLocalLocation(), itemImage, zOffset);
    }
}
