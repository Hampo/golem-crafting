package org.zhbot.golem_crafting.utils;

import net.runelite.api.Client;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TextUtils {
    private final Client client;

    @Inject
    private TextUtils(Client client)
    {
        this.client = client;
    }

    public String Clean(String str)
    {
        final var expanded = client.macroExpand(str);
        return Text.removeTags(expanded);
    }
}
