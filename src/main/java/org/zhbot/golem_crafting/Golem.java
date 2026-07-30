package org.zhbot.golem_crafting;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.awt.*;

public abstract class Golem {
    public static final int RESPAWN_DELAY = 10;

    @Getter
    private String name;

    @Getter
    private final int progressID;
    @Getter
    @Setter
    private int lastProgressTick;

    private final int northStateID;
    private final int eastStateID;
    private final int southStateID;
    private final int westStateID;

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

    public Golem(String name, int stationID, int progressID, int northStateID, int eastStateID, int southStateID, int westStateID, WorldPoint golemTile, CardinalDirection finalTile)
    {
        this.name = name;
        this.stationID = stationID;
        this.progressID = progressID;
        this.northStateID = northStateID;
        this.eastStateID = eastStateID;
        this.southStateID = southStateID;
        this.westStateID = westStateID;
        this.golemTile = golemTile;
        this.northTile = new WorldPoint(golemTile.getX(), golemTile.getY() + 1, golemTile.getPlane());
        this.eastTile = new WorldPoint(golemTile.getX() + 1, golemTile.getY(), golemTile.getPlane());
        this.southTile = new WorldPoint(golemTile.getX(), golemTile.getY() - 1, golemTile.getPlane());
        this.westTile = new WorldPoint(golemTile.getX() - 1, golemTile.getY(), golemTile.getPlane());
        this.finalTile = finalTile;
    }

    public int getProgress(final Client client)
    {
        return client.getVarbitValue(progressID);
    }

    public boolean isNorthDone(final Client client)
    {
        return client.getVarbitValue(northStateID) != 0;
    }

    public boolean isEastDone(final Client client)
    {
        return client.getVarbitValue(eastStateID) != 0;
    }

    public boolean isSouthDone(final Client client)
    {
        return client.getVarbitValue(southStateID) != 0;
    }

    public boolean isWestDone(final Client client)
    {
        return client.getVarbitValue(westStateID) != 0;
    }

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

    public GameObject getStationGameObject(final Client client)
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

