package org.zhbot.golem_crafting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Notification;

@ConfigGroup(GolemCraftingConfig.group)
public interface GolemCraftingConfig extends Config
{
	String group = "golem-crafting";

	@ConfigItem(
			keyName = "notifications",
			name = "Notifications",
			description = "Configures all notifications"
	)
	default Notification notification()
	{
		return Notification.ON;
	}
}
