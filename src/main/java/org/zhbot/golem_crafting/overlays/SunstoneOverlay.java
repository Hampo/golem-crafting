package org.zhbot.golem_crafting.overlays;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import org.zhbot.golem_crafting.GolemCraftingConfig;
import org.zhbot.golem_crafting.GolemCraftingPlugin;
import org.zhbot.golem_crafting.enums.SunstoneMode;
import org.zhbot.golem_crafting.utils.GraphicsUtils;
import org.zhbot.golem_crafting.utils.TextUtils;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class SunstoneOverlay extends Overlay {
    private static final String MINING_ROCK_MESSAGE = "You swing your pick at the rock.";
    private static final String MINING_MONOLITH_MESSAGE = "You swing your pick at the monolith.";
    private static final String MINED_SUNSTONE_MESSAGE = "You manage to mine some sunstone.";
    private static final int MOMENTUM_TICKS = 5;

    private final Client client;
    private final GolemCraftingPlugin plugin;
    private final GolemCraftingConfig config;
    private final GraphicsUtils graphicsUtils;
    private final TextUtils textUtils;

    private boolean miningSunstoneRock = false;
    private int lastSunstoneMinedTick = -MOMENTUM_TICKS;

    private GameObject monolith;
    private final List<GameObject> upperRocks = new ArrayList<>();
    private final List<GameObject> lowerRocks = new ArrayList<>();

    @Inject
    public SunstoneOverlay(Client client, GolemCraftingPlugin plugin, GolemCraftingConfig config, GraphicsUtils graphicsUtils, TextUtils textUtils)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.graphicsUtils = graphicsUtils;
        this.textUtils = textUtils;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public void startup()
    {
        var worldView = client.getTopLevelWorldView();
        if (worldView == null)
            return;

        var scene = worldView.getScene();
        if (scene == null)
            return;

        var tiles = scene.getTiles();
        if (tiles == null || tiles.length == 0)
            return;

        var zTiles = tiles[worldView.getPlane()];

        for (var xTiles : zTiles) {
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

                    switch (gameObject.getId())
                    {
                        case ObjectID.WYRMSCRAIG_SUNSTONE01:
                            monolith = gameObject;
                            break;
                        case ObjectID.SUNSTONEROCK1:
                        case ObjectID.SUNSTONEROCK2:
                            upperRocks.add(gameObject);
                            break;
                    }
                }
            }
        }
    }

    public void shutdown()
    {
        monolith = null;
        upperRocks.clear();
        lowerRocks.clear();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        var sunstoneMode = config.overlaySunstoneMode();
        if (sunstoneMode == SunstoneMode.NONE)
            return null;

        if (plugin.outsideGolemArea())
            return null;

        if (plugin.hasGolemMaterials())
            return null;

        var hasMomentum = config.overlaySunstoneMomentum() && hasMomentum();

        switch (sunstoneMode)
        {
            case MONOLITH:
                if (monolith == null)
                    break;

                graphicsUtils.renderObject(graphics, monolith, config.overlaySunstoneRenderStyle(), config.overlaySunstoneColour());
                break;
            case ROCKS:
                for (var rock : upperRocks)
                    graphicsUtils.renderObject(graphics, rock, config.overlaySunstoneRenderStyle(), hasMomentum ? config.overlaySunstoneMomentumColour() : config.overlaySunstoneColour());

                break;
            case ROCKS_LOWER:
                for (var rock : lowerRocks)
                    graphicsUtils.renderObject(graphics, rock, config.overlaySunstoneRenderStyle(), hasMomentum ? config.overlaySunstoneMomentumColour() : config.overlaySunstoneColour());

                break;
        }

        return null;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.SPAM)
            return;

        var message = textUtils.Clean(event.getMessage());

        if (message.contains(MINING_MONOLITH_MESSAGE)) {
            miningSunstoneRock = false;
            return;
        }

        if (message.contains(MINING_ROCK_MESSAGE)) {
            miningSunstoneRock = true;
            return;
        }

        if (miningSunstoneRock && message.contains(MINED_SUNSTONE_MESSAGE)) {
            lastSunstoneMinedTick = client.getTickCount();
            return;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState())
        {
            case LOADING:
                monolith = null;
                upperRocks.clear();
                lowerRocks.clear();

                break;
            case LOGGED_IN:
                lastSunstoneMinedTick = -MOMENTUM_TICKS;

                break;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        var object = event.getGameObject();

        switch (object.getId())
        {
            case ObjectID.WYRMSCRAIG_SUNSTONE01:
                monolith = object;

                break;
            case ObjectID.SUNSTONEROCK1:
            case ObjectID.SUNSTONEROCK2:
                if (object.getWorldLocation().getX() < 2605)
                    upperRocks.add(object);
                else
                    lowerRocks.add(object);

                break;
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        var object = event.getGameObject();

        switch (object.getId())
        {
            case ObjectID.WYRMSCRAIG_SUNSTONE01:
                if (object == monolith)
                    monolith = null;

                break;
            case ObjectID.SUNSTONEROCK1:
            case ObjectID.SUNSTONEROCK2:
                upperRocks.remove(object);
                lowerRocks.remove(object);

                break;
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if (!config.sunstoneDeprioritiseOther())
            return;

        var sunstoneMode = config.overlaySunstoneMode();
        if (sunstoneMode == SunstoneMode.NONE)
            return;

        if (plugin.outsideGolemArea())
            return;

        var entry = event.getMenuEntry();
        if (entry.getType() != MenuAction.GAME_OBJECT_FIRST_OPTION)
            return;

        var option = textUtils.Clean(event.getOption());
        if (!option.equals("Mine"))
            return;

        var target = textUtils.Clean(event.getTarget());
        switch (target)
        {
            case "Sunstone monolith":
                if (sunstoneMode == SunstoneMode.MONOLITH)
                    return;

                break;
            case "Sunstone rocks":
                var sceneX = entry.getParam0();
                var sceneY = entry.getParam1();

                var worldView = client.getTopLevelWorldView();
                var worldPoint = WorldPoint.fromScene(worldView.getScene(), sceneX, sceneY, worldView.getPlane());

                if (worldPoint.getX() < 2605)
                {
                    if (sunstoneMode == SunstoneMode.ROCKS)
                        return;
                }
                else
                {
                    if (sunstoneMode == SunstoneMode.ROCKS_LOWER)
                        return;
                }

                break;
            default:
                return;
        }

        event.getMenuEntry().setDeprioritized(true);
    }

    private boolean hasMomentum()
    {
        var ticksSinceMined = client.getTickCount() - lastSunstoneMinedTick;
        return ticksSinceMined < MOMENTUM_TICKS;
    }

    public int getMomentumTicks()
    {
        var momentumTicks = lastSunstoneMinedTick + MOMENTUM_TICKS - client.getTickCount();
        return Math.max(momentumTicks, 0);
    }
}
