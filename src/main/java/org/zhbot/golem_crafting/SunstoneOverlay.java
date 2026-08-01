package org.zhbot.golem_crafting;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Set;

public class SunstoneOverlay extends Overlay {
    private static final int SUNSTONE_MONOLITH_ID = 62216;
    private static final Set<Integer> SUNSTONE_ROCK_IDS = Set.of(
            62393,
            62394
    );

    private final Client client;
    private final GolemCraftingPlugin plugin;
    private final GolemCraftingConfig config;

    @Inject
    public SunstoneOverlay(Client client, GolemCraftingPlugin plugin, GolemCraftingConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        var sunstoneMode = config.overlaySunstoneMode();
        if (sunstoneMode == SunstoneMode.NONE)
            return null;

        if (!plugin.isWithinGolemArea())
            return null;

        if (plugin.hasGolemMaterials())
            return null;

        var worldView = client.getTopLevelWorldView();
        var scene = worldView.getScene();
        var tiles = scene.getTiles()[0];

        var hasMomentum = config.overlaySunstoneMomentum() && plugin.hasMomentum();

        for (var xTiles : tiles) {
            for (var tile : xTiles) {
                if (tile == null)
                    continue;

                var gameObjects = tile.getGameObjects();
                if (gameObjects == null)
                    continue;

                for (var gameObject : gameObjects)
                {
                    if (gameObject == null)
                        continue;

                    if (sunstoneMode == SunstoneMode.MONOLITH && gameObject.getId() == SUNSTONE_MONOLITH_ID)
                    {
                        renderObject(graphics, gameObject, config.overlaySunstoneRenderStyle(), config.overlaySunstoneColour());
                        return null;
                    }

                    if (sunstoneMode == SunstoneMode.ROCKS && SUNSTONE_ROCK_IDS.contains(gameObject.getId()))
                        renderObject(graphics, gameObject, config.overlaySunstoneRenderStyle(), hasMomentum ? config.overlaySunstoneMomentumColour() : config.overlaySunstoneColour());
                }
            }
        }

        return null;
    }

    private void renderObject(Graphics2D graphics, GameObject gameObject, RenderMode renderMode, Color color)
    {
        var area = renderMode == RenderMode.CLICKBOX ? gameObject.getClickbox() : gameObject.getConvexHull();
        var mousePosition = client.getMouseCanvasPosition();

        OverlayUtil.renderHoverableArea(graphics, area, mousePosition, color, color, color.darker());
    }
}
