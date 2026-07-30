package org.zhbot.golem_crafting;

import net.runelite.api.coords.WorldPoint;

public class NorthGolem extends Golem {
    private static final int STATION_ID = 62351;

    private static final int PROGRESS_ID = 15728;
    private static final int NORTH_STATE_ID = 15729;
    private static final int EAST_STATE_ID = 15730;
    private static final int SOUTH_STATE_ID = 15731;
    private static final int WEST_STATE_ID = 15732;

    private static final WorldPoint GOLEM_TILE = new WorldPoint(2596, 2257, 0);
    private static final CardinalDirection FINAL_TILE = CardinalDirection.SOUTH;

    public NorthGolem()
    {
        super("North Golem", STATION_ID, PROGRESS_ID, NORTH_STATE_ID, EAST_STATE_ID, SOUTH_STATE_ID, WEST_STATE_ID, GOLEM_TILE, FINAL_TILE);
    }
}
