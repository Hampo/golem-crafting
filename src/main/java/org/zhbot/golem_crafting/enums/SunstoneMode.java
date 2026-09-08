package org.zhbot.golem_crafting.enums;

public enum SunstoneMode {
    NONE("None"),
    MONOLITH("Monolith"),
    ROCKS("Upper Rocks"),
    ROCKS_LOWER("Lower Rocks");

    private final String name;

    SunstoneMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
