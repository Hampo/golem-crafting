package org.zhbot.golem_crafting.enums;

import lombok.Getter;

@Getter
public enum TileMode {
    NONE("None", false, false, false),
    ALL("All", true, true, false),
    ALL_WITH_OPTIMAL("All w/ Optimal", true, true, true),
    INCOMPLETE("Incomplete", false, true, false),
    INCOMPLETE_WITH_OPTIMAL("Incomplete w/ Optimal", false, true, true),
    OPTIMAL("Optimal", false, false, true);

    private final String name;
    private final boolean showComplete;
    private final boolean showIncomplete;
    private final boolean showOptimal;

    TileMode(String name, boolean showComplete, boolean showIncomplete, boolean showOptimal)
    {
        this.name = name;
        this.showComplete = showComplete;
        this.showIncomplete = showIncomplete;
        this.showOptimal = showOptimal;
    }

    @Override
    public String toString() {
        return name;
    }
}
