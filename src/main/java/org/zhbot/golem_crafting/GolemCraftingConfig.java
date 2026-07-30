package org.zhbot.golem_crafting;

import net.runelite.client.config.*;

@ConfigGroup(GolemCraftingConfig.group)
public interface GolemCraftingConfig extends Config
{
	String group = "golem-crafting";

	@ConfigItem(
			keyName = "notifications",
			name = "Notifications",
			description = "Configures all notifications",
			position = 0
	)
	default Notification notification()
	{
		return Notification.ON;
	}

	@ConfigSection(
			name = "Infobox",
			description = "Configure the infobox",
			position = 1
	)
	String infoboxSection = "infoboxSection";

	@ConfigItem(
			keyName="infobox",
			name = "Show infobox",
			description = "Show infobox with details on the state of golem crafting",
			section = infoboxSection,
			position = 0
	)
	default boolean showInfobox()
	{
		return true;
	}

	@ConfigItem(
			keyName="infoboxState",
			name = "Show state",
			description = "Show current state of crafting",
			section = infoboxSection,
			position = 1
	)
	default boolean showInfoboxState()
	{
		return true;
	}

	@ConfigItem(
			keyName="infoboxFurPouch",
			name = "Show fur pouch",
			description = "Show fur pouch count",
			section = infoboxSection,
			position = 2
	)
	default boolean showInfoboxFurPouch()
	{
		return true;
	}
}
