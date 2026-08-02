package org.zhbot.golem_crafting.enums;

import lombok.Getter;

public enum ProgressMode {
    NONE("None", false, false),
    TILES("Tiles", true, false),
    PLINTH("Plinth", false, true),
    BOTH("Both", true, true);

    private final String name;

    @Getter
    private final boolean showTiles;

    @Getter
    private final boolean showPlinth;

    ProgressMode(String name, boolean showTiles, boolean showPlinth)
    {
        this.name = name;
        this.showTiles = showTiles;
        this.showPlinth = showPlinth;
    }

    @Override
    public String toString() {
        return name;
    }
}
