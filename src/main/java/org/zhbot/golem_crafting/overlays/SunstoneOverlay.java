package org.zhbot.golem_crafting.overlays;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.Text;
import org.zhbot.golem_crafting.GolemCraftingConfig;
import org.zhbot.golem_crafting.GolemCraftingPlugin;
import org.zhbot.golem_crafting.enums.SunstoneMode;
import org.zhbot.golem_crafting.utils.GraphicsUtils;

import javax.inject.Inject;
import java.awt.*;
import java.util.Set;

public class SunstoneOverlay extends Overlay {
    private static final int SUNSTONE_MONOLITH_ID = 62216;
    private static final Set<Integer> SUNSTONE_ROCK_IDS = Set.of(
            62393,
            62394
    );

    private static final String MINING_ROCK_MESSAGE = "You swing your pick at the rock.";
    private static final String MINING_MONOLITH_MESSAGE = "You swing your pick at the monolith.";
    private static final String MINED_SUNSTONE_MESSAGE = "You manage to mine some sunstone.";
    private static final int MOMENTUM_TICKS = 5;
    private boolean miningSunstoneRock = false;
    private int lastSunstoneMinedTick = -MOMENTUM_TICKS;

    private final Client client;
    private final GolemCraftingPlugin plugin;
    private final GolemCraftingConfig config;
    private final GraphicsUtils graphicsUtils;

    @Inject
    public SunstoneOverlay(Client client, GolemCraftingPlugin plugin, GolemCraftingConfig config, GraphicsUtils graphicsUtils)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.graphicsUtils = graphicsUtils;

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
        var tiles = scene.getTiles()[worldView.getPlane()];

        var hasMomentum = config.overlaySunstoneMomentum() && hasMomentum();

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
                        graphicsUtils.renderObject(graphics, gameObject, config.overlaySunstoneRenderStyle(), config.overlaySunstoneColour());
                        return null;
                    }

                    if (sunstoneMode == SunstoneMode.ROCKS && SUNSTONE_ROCK_IDS.contains(gameObject.getId()))
                        graphicsUtils.renderObject(graphics, gameObject, config.overlaySunstoneRenderStyle(), hasMomentum ? config.overlaySunstoneMomentumColour() : config.overlaySunstoneColour());
                }
            }
        }

        return null;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.SPAM)
            return;

        var message = Text.removeTags(event.getMessage());

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
        if (event.getGameState() != GameState.LOGGED_IN)
            return;

        lastSunstoneMinedTick = -MOMENTUM_TICKS;
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
