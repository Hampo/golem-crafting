package org.zhbot.golem_crafting;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GolemCraftingPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GolemCraftingPlugin.class);
		RuneLite.main(args);
	}
}