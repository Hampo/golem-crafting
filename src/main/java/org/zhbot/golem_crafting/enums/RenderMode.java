package org.zhbot.golem_crafting.enums;

public enum RenderMode {
    CLICKBOX("Clickbox"),
    HULL("Hull");

    private final String name;

    RenderMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
