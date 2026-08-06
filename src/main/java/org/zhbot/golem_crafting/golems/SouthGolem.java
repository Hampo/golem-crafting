package org.zhbot.golem_crafting.golems;

import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.Notifier;
import org.zhbot.golem_crafting.GolemCraftingConfig;
import org.zhbot.golem_crafting.GolemCraftingPlugin;
import org.zhbot.golem_crafting.enums.CardinalDirection;

public class SouthGolem extends Golem {
    private static final int STATION_ID = ObjectID.GOLEM_STATION_2;

    private static final int PROGRESS_ID = VarbitID.GOLEM_CRAFTING_STATION_2;
    private static final int NORTH_STATE_ID = VarbitID.GOLEM_CRAFTING_STATION_2_NORTH;
    private static final int EAST_STATE_ID = VarbitID.GOLEM_CRAFTING_STATION_2_EAST;
    private static final int SOUTH_STATE_ID = VarbitID.GOLEM_CRAFTING_STATION_2_SOUTH;
    private static final int WEST_STATE_ID = VarbitID.GOLEM_CRAFTING_STATION_2_WEST;

    private static final int NORTH_PROGRESS_ID = VarbitID.GOLEM_2_NORTH_PROGRESS;
    private static final int EAST_PROGRESS_ID = VarbitID.GOLEM_2_EAST_PROGRESS;
    private static final int SOUTH_PROGRESS_ID = VarbitID.GOLEM_2_SOUTH_PROGRESS;
    private static final int WEST_PROGRESS_ID = VarbitID.GOLEM_2_WEST_PROGRESS;

    private static final WorldPoint GOLEM_TILE = new WorldPoint(2595, 2253, 0);
    private static final CardinalDirection FINAL_TILE = CardinalDirection.NORTH;

    public SouthGolem(Client client, Notifier notifier, GolemCraftingPlugin plugin, GolemCraftingConfig config)
    {
        super(client, notifier, plugin, config, "South Golem", STATION_ID, PROGRESS_ID, NORTH_STATE_ID, EAST_STATE_ID, SOUTH_STATE_ID, WEST_STATE_ID, NORTH_PROGRESS_ID, EAST_PROGRESS_ID, SOUTH_PROGRESS_ID, WEST_PROGRESS_ID, GOLEM_TILE, FINAL_TILE);
    }
}
