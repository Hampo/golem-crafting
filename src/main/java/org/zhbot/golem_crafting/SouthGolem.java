package org.zhbot.golem_crafting;

import net.runelite.api.coords.WorldPoint;

public class SouthGolem extends Golem {
    private static final int STATION_ID = 62352;

    private static final int PROGRESS_ID = 15733;
    private static final int NORTH_STATE_ID = 15734;
    private static final int EAST_STATE_ID = 15735;
    private static final int SOUTH_STATE_ID = 15736;
    private static final int WEST_STATE_ID = 15737;

    private static final WorldPoint GOLEM_TILE = new WorldPoint(2595, 2253, 0);
    private static final CardinalDirection FINAL_TILE = CardinalDirection.NORTH;

    public SouthGolem()
    {
        super("South Golem", STATION_ID, PROGRESS_ID, NORTH_STATE_ID, EAST_STATE_ID, SOUTH_STATE_ID, WEST_STATE_ID, GOLEM_TILE, FINAL_TILE);
    }
}
