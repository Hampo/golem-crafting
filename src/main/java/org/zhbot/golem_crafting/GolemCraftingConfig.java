package org.zhbot.golem_crafting;

import net.runelite.client.config.*;

import java.awt.*;

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

	@ConfigItem(
			keyName = "furPouchLowThreshold",
			name = "Fur Pouch Low Threshold",
			description = "The threshold for fur pouch contents to be deemed low",
			position = 1
	)
	default int furPouchLowThreshold()
	{
		return 5;
	}

	@ConfigSection(
			name = "Infobox",
			description = "Configure the infobox",
			position = 2
	)
	String infoboxSection = "infoboxSection";

	@ConfigItem(
			keyName = "infobox",
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
			keyName = "infoboxTotal",
			name = "Show total golems",
			description = "Shows the total number of golems crafted",
			section = infoboxSection,
			position = 1
	)
	default boolean showInfoboxTotal()
	{
		return true;
	}

	@ConfigItem(
			keyName = "infoboxState",
			name = "Show state",
			description = "Show current state of crafting",
			section = infoboxSection,
			position = 2
	)
	default boolean showInfoboxState()
	{
		return true;
	}

	@ConfigItem(
			keyName = "infoboxFurPouch",
			name = "Show fur pouch",
			description = "Show fur pouch count",
			section = infoboxSection,
			position = 3
	)
	default boolean showInfoboxFurPouch()
	{
		return true;
	}

	@ConfigItem(
			keyName = "infoboxFurPouchUnknownTextColour",
			name = "Fur Pouch Unknown Text Colour",
			description = "The text colour for when the fur pouch contents are unknown",
			section = infoboxSection,
			position = 4
	)
	default Color infoboxFurPouchUnknownTextColour()
	{
		return new Color(255, 255, 0);
	}

	@ConfigItem(
			keyName = "infoboxFurPouchLowTextColour",
			name = "Fur Pouch Low Text Colour",
			description = "The text colour for when the fur pouch contents are low",
			section = infoboxSection,
			position = 5
	)
	default Color infoboxFurPouchLowTextColour()
	{
		return new Color(255, 121, 0);
	}

	@ConfigItem(
			keyName = "infoboxFurPouchEmptyTextColour",
			name = "Fur Pouch Empty Text Colour",
			description = "The text colour for when the fur pouch is empty",
			section = infoboxSection,
			position = 6
	)
	default Color infoboxFurPouchEmptyTextColour()
	{
		return new Color(255, 0, 0);
	}

	@ConfigSection(
			name = "Overlays",
			description = "Configure the various overlays",
			position = 3
	)
	String overlaysSection = "overlaysSection";

	@ConfigItem(
			keyName = "overlayPlinth",
			name = "Highlight Plinth",
			description = "Highlight the golem plinths",
			section = overlaysSection,
			position = 0
	)
	default boolean showOverlayPlinth()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayPlinthValidColour",
			name = "Plinth Valid Colour",
			description = "The highlight colour for plinth when action is valid",
			section = overlaysSection,
			position = 1
	)
	default Color overlayPlinthValidColour()
	{
		return new Color(0, 255, 0, 75);
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayPlinthInvalidColour",
			name = "Plinth Invalid Colour",
			description = "The highlight colour for plinth when action is invalid",
			section = overlaysSection,
			position = 2
	)
	default Color overlayPlinthInvalidColour()
	{
		return new Color(255, 0, 0, 75);
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayPlinthValidCoreColour",
			name = "Plinth Valid Core Colour",
			description = "The highlight colour for plinth when you're on the right tile to insert the core",
			section = overlaysSection,
			position = 3
	)
	default Color overlayPlinthValidCoreColour()
	{
		return new Color(0, 255, 0, 75);
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayPlinthInvalidCoreColour",
			name = "Plinth Invalid Core Colour",
			description = "The highlight colour for plinth when you're on the wrong tile to insert the core",
			section = overlaysSection,
			position = 4
	)
	default Color overlayPlinthInvalidCoreColour()
	{
		return new Color(255, 0, 0, 75);
	}

	@ConfigItem(
			keyName = "overlayTiles",
			name = "Highlight Tiles",
			description = "Highlight the valid tiles",
			section = overlaysSection,
			position = 5
	)
	default boolean showOverlayTiles()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayTileColour",
			name = "Tile Colour",
			description = "The highlight colour for tiles",
			section = overlaysSection,
			position = 6
	)
	default Color overlayTileColour()
	{
		return Color.GREEN;
	}

	@ConfigItem(
			keyName = "overlayFurPouch",
			name = "Highlight Fur Pouch",
			description = "Highlight the fur pouch",
			section = overlaysSection,
			position = 7
	)
	default boolean showOverlayFurPouch()
	{
		return true;
	}

	@ConfigItem(
			keyName = "overlayFurPouchUnknownColour",
			name = "Fur Pouch Unknown Colour",
			description = "The highlight colour for when the fur pouch contents are unknown",
			section = overlaysSection,
			position = 8
	)
	default Color overlayFurPouchUnknownColour()
	{
		return new Color(255, 255, 0);
	}

	@ConfigItem(
			keyName = "overlayFurPouchLowColour",
			name = "Fur Pouch Low Colour",
			description = "The highlight colour for when the fur pouch contents are low",
			section = overlaysSection,
			position = 9
	)
	default Color overlayFurPouchLowColour()
	{
		return new Color(255, 121, 0);
	}

	@ConfigItem(
			keyName = "overlayFurPouchEmptyColour",
			name = "Fur Pouch Empty Colour",
			description = "The highlight colour for when the fur pouch is empty",
			section = overlaysSection,
			position = 10
	)
	default Color overlayFurPouchEmptyColour()
	{
		return new Color(255, 0, 0);
	}

	@ConfigItem(
			keyName = "overlayFurPouchCount",
			name = "Show Fur Pouch Count",
			description = "Renders the count of furs in the pouch",
			section = overlaysSection,
			position = 11
	)
	default boolean showOverlayFurPouchCount()
	{
		return true;
	}

	@ConfigItem(
			keyName = "overlayFurPouchTextColour",
			name = "Fur Pouch Text Colour",
			description = "The default text colour for the fur pouch",
			section = overlaysSection,
			position = 12
	)
	default Color overlayFurPouchTextColour()
	{
		return new Color(255, 255, 255);
	}

	@ConfigItem(
			keyName = "overlayFurPouchUnknownTextColour",
			name = "Fur Pouch Unknown Text Colour",
			description = "The text colour for when the fur pouch contents are unknown",
			section = overlaysSection,
			position = 13
	)
	default Color overlayFurPouchUnknownTextColour()
	{
		return new Color(255, 255, 0);
	}

	@ConfigItem(
			keyName = "overlayFurPouchLowTextColour",
			name = "Fur Pouch Low Text Colour",
			description = "The text colour for when the fur pouch contents are low",
			section = overlaysSection,
			position = 14
	)
	default Color overlayFurPouchLowTextColour()
	{
		return new Color(255, 121, 0);
	}

	@ConfigItem(
			keyName = "overlayFurPouchEmptyTextColour",
			name = "Fur Pouch Empty Text Colour",
			description = "The text colour for when the fur pouch is empty",
			section = overlaysSection,
			position = 15
	)
	default Color overlayFurPouchEmptyTextColour()
	{
		return new Color(255, 0, 0);
	}
}
