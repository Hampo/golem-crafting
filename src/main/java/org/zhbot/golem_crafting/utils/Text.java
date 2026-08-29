package org.zhbot.golem_crafting.utils;

// TODO: Replace this with RuneLite's macro handling when added.
public class Text {
    public static String Clean(String str)
    {
        return net.runelite.client.util.Text.removeTags(str).replaceAll("@[^@]*@", "");
    }
}
