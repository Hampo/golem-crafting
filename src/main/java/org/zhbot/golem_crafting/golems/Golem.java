package org.zhbot.golem_crafting.golems;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.Subscribe;
import org.zhbot.golem_crafting.GolemCraftingConfig;
import org.zhbot.golem_crafting.GolemCraftingPlugin;
import org.zhbot.golem_crafting.enums.CardinalDirection;

import java.awt.*;

public abstract class Golem {
    public static final int RESPAWN_DELAY = 10;
    private static final int MAX_PROGRESS = 10;

    private final Client client;
    private final Notifier notifier;
    private final GolemCraftingPlugin plugin;
    private final GolemCraftingConfig config;

    @Getter
    private final String name;

    @Getter
    private final int progressID;
    @Getter
    private int lastProgressTick = -RESPAWN_DELAY;
    private boolean firstProgressTick = true;
    @Getter
    private int lastShapeClickTick = -1;

    private final int northStateID;
    private final int eastStateID;
    private final int southStateID;
    private final int westStateID;

    private final int northProgressID;
    private final int eastProgressID;
    private final int southProgressID;
    private final int westProgressID;

    @Getter
    private final int stationID;
    @Getter
    private final WorldPoint golemTile;
    @Getter
    private final WorldPoint northTile;
    @Getter
    private final WorldPoint eastTile;
    @Getter
    private final WorldPoint southTile;
    @Getter
    private final WorldPoint westTile;

    private final CardinalDirection finalTile;

    public Golem(Client client, Notifier notifier, GolemCraftingPlugin plugin, GolemCraftingConfig config, String name, int stationID, int progressID, int northStateID, int eastStateID, int southStateID, int westStateID, int northProgressID, int eastProgressID, int southProgressID, int westProgressID, WorldPoint golemTile, CardinalDirection finalTile)
    {
        this.client = client;
        this.notifier = notifier;
        this.plugin = plugin;
        this.config = config;

        this.name = name;
        this.stationID = stationID;
        this.progressID = progressID;
        this.northStateID = northStateID;
        this.eastStateID = eastStateID;
        this.southStateID = southStateID;
        this.westStateID = westStateID;
        this.northProgressID = northProgressID;
        this.eastProgressID = eastProgressID;
        this.southProgressID = southProgressID;
        this.westProgressID = westProgressID;
        this.golemTile = golemTile;
        this.northTile = new WorldPoint(golemTile.getX(), golemTile.getY() + 1, golemTile.getPlane());
        this.eastTile = new WorldPoint(golemTile.getX() + 1, golemTile.getY(), golemTile.getPlane());
        this.southTile = new WorldPoint(golemTile.getX(), golemTile.getY() - 1, golemTile.getPlane());
        this.westTile = new WorldPoint(golemTile.getX() - 1, golemTile.getY(), golemTile.getPlane());
        this.finalTile = finalTile;
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        var varbitId = event.getVarbitId();

        if (varbitId != progressID)
            return;

        if (firstProgressTick)
            firstProgressTick = false;
        else
            lastProgressTick = client.getTickCount();

        if (event.getValue() > 1)
            notifier.notify(config.notification(), name + " stage complete");
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (event.getMenuAction() == MenuAction.GAME_OBJECT_FIRST_OPTION && event.getMenuOption().equalsIgnoreCase("Shape-golem")) {
            var sceneX = event.getParam0();
            var sceneY = event.getParam1();

            var worldView = client.getTopLevelWorldView();
            var worldPoint = WorldPoint.fromScene(worldView.getScene(), sceneX, sceneY, worldView.getPlane());

            if (golemTile.distanceTo(worldPoint) == 0)
                lastShapeClickTick = client.getTickCount();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() != GameState.LOGGED_IN)
            return;

        firstProgressTick = true;
        lastProgressTick = -RESPAWN_DELAY;
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if (!config.plinthRemoveMenuOptions())
            return;

        var entry = event.getMenuEntry();
        if (entry.getType() != MenuAction.GAME_OBJECT_FIRST_OPTION)
            return;

        var sceneX = entry.getParam0();
        var sceneY = entry.getParam1();

        var worldView = client.getTopLevelWorldView();
        var worldPoint = WorldPoint.fromScene(worldView.getScene(), sceneX, sceneY, worldView.getPlane());

        if (golemTile.distanceTo(worldPoint) != 0)
            return;

        var option = entry.getOption();
        if (option.equalsIgnoreCase("Start-golem"))
        {
            if (!plugin.hasGolemMaterials()
                || client.getTickCount() - lastProgressTick < RESPAWN_DELAY)
                client.getMenu().removeMenuEntry(entry);

            return;
        }

        var localPlayer = client.getLocalPlayer();
        if (localPlayer == null)
            return;
        var playerTile = localPlayer.getWorldLocation();

        var side = CardinalDirection.NONE;
        if (playerTile.distanceTo(northTile) == 0)
            side = CardinalDirection.NORTH;
        else if (playerTile.distanceTo(eastTile) == 0)
            side = CardinalDirection.EAST;
        else if (playerTile.distanceTo(southTile) == 0)
            side = CardinalDirection.SOUTH;
        else if (playerTile.distanceTo(westTile) == 0)
            side = CardinalDirection.WEST;

        if (side == CardinalDirection.NONE)
            return;

        switch (option)
        {
            case "Insert-core":
                if (side != finalTile)
                    client.getMenu().removeMenuEntry(entry);
                break;
            case "Shape-golem":
                switch (side)
                {
                    case NORTH:
                        if (isNorthDone())
                            client.getMenu().removeMenuEntry(entry);
                        break;
                    case EAST:
                        if (isEastDone())
                            client.getMenu().removeMenuEntry(entry);
                        break;
                    case SOUTH:
                        if (isSouthDone())
                            client.getMenu().removeMenuEntry(entry);
                        break;
                    case WEST:
                        if (isWestDone())
                            client.getMenu().removeMenuEntry(entry);
                        break;
                }
        }
    }

    public int getProgress()
    {
        return client.getVarbitValue(progressID);
    }

    public boolean isNorthDone()
    {
        return client.getVarbitValue(northStateID) != 0;
    }

    public boolean isEastDone()
    {
        return client.getVarbitValue(eastStateID) != 0;
    }

    public boolean isSouthDone()
    {
        return client.getVarbitValue(southStateID) != 0;
    }

    public boolean isWestDone()
    {
        return client.getVarbitValue(westStateID) != 0;
    }

    public float getNorthProgress() { return (float)client.getVarbitValue(northProgressID) / MAX_PROGRESS; }

    public float getEastProgress() { return (float)client.getVarbitValue(eastProgressID) / MAX_PROGRESS; }

    public float getSouthProgress() { return (float)client.getVarbitValue(southProgressID) / MAX_PROGRESS; }

    public float getWestProgress() { return (float)client.getVarbitValue(westProgressID) / MAX_PROGRESS; }

    public WorldPoint getFinalTile() {
        switch (finalTile)
        {
            case NORTH:
                return northTile;
            case EAST:
                return eastTile;
            case SOUTH:
                return southTile;
            case WEST:
                return westTile;
        }

        return null;
    }

    public GameObject getStationGameObject()
    {
        var localTile = LocalPoint.fromWorld(client, golemTile);
        if (localTile == null)
            return null;

        var worldView = client.getWorldView(localTile.getWorldView());
        if (worldView == null)
            return null;

        var scene = worldView.getScene();
        if (scene == null)
            return null;

        var tile = scene.getTiles()[golemTile.getPlane()][localTile.getSceneX()][localTile.getSceneY()];
        if (tile == null)
            return null;

        for (var gameObject : tile.getGameObjects())
            if (gameObject != null && gameObject.getId() == stationID)
                return gameObject;

        return null;
    }
}

