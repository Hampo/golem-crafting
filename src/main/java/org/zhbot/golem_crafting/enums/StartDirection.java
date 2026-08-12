package org.zhbot.golem_crafting.enums;

public enum StartDirection {
    EAST,
    WEST;

    public CardinalDirection getDirection()
    {
        switch (this)
        {
            case EAST:
                return CardinalDirection.EAST;
            case WEST:
                return CardinalDirection.WEST;
            default:
                return CardinalDirection.NONE;
        }
    }
}
