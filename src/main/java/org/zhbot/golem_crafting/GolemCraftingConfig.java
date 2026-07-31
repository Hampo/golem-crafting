package org.zhbot.golem_crafting;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup(GolemCraftingConfig.group)
public interface GolemCraftingConfig extends Config
{
	String group = "golem-crafting";

	@ConfigSection(
			name = "Notifications",
			description = "Configure notifications",
			position = 0
	)
	String notificationsSection = "notificationsSection";

	@ConfigItem(
			keyName = "notifications",
			name = "Notifications",
			description = "Configures all notifications",
			section = notificationsSection,
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
			keyName = "infoboxSunStone",
			name = "Show sunstone",
			description = "Show sunstone count",
			section = infoboxSection,
			position = 4
	)
	default boolean showInfoboxSunStone()
	{
		return true;
	}

	@ConfigItem(
			keyName = "infoboxFurPouchUnknownTextColour",
			name = "Fur Pouch Unknown Text Colour",
			description = "The text colour for when the fur pouch contents are unknown",
			section = infoboxSection,
			position = 5
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
			position = 6
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
			position = 7
	)
	default Color infoboxFurPouchEmptyTextColour()
	{
		return new Color(255, 0, 0);
	}

	@ConfigSection(
			name = "Plinth",
			description = "Configure the plinth settings",
			position = 2
	)
	String plinthSection = "plinthSection";

	@ConfigItem(
			keyName = "overlayPlinth",
			name = "Highlight Plinth",
			description = "Highlight the golem plinths",
			section = plinthSection,
			position = 0
	)
	default boolean showOverlayPlinth()
	{
		return true;
	}

	@ConfigItem(
			keyName = "overlayPlinth",
			name = "Show Sunlight Core",
			description = "Render a sunlight core on final stage",
			section = plinthSection,
			position = 1
	)
	default boolean showOverlayPlinthCore()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayPlinthValidColour",
			name = "Valid Colour",
			description = "The highlight colour for plinth when action is valid",
			section = plinthSection,
			position = 2
	)
	default Color overlayPlinthValidColour()
	{
		return new Color(0, 255, 0, 75);
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayPlinthInvalidColour",
			name = "Invalid Colour",
			description = "The highlight colour for plinth when action is invalid",
			section = plinthSection,
			position = 3
	)
	default Color overlayPlinthInvalidColour()
	{
		return new Color(255, 0, 0, 75);
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayPlinthValidCoreColour",
			name = "Valid Core Colour",
			description = "The highlight colour for plinth when you're on the right tile to insert the core",
			section = plinthSection,
			position = 4
	)
	default Color overlayPlinthValidCoreColour()
	{
		return new Color(0, 255, 0, 75);
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayPlinthInvalidCoreColour",
			name = "Invalid Core Colour",
			description = "The highlight colour for plinth when you're on the wrong tile to insert the core",
			section = plinthSection,
			position = 5
	)
	default Color overlayPlinthInvalidCoreColour()
	{
		return new Color(255, 0, 0, 75);
	}

	@ConfigSection(
			name = "Tiles",
			description = "Configure tiles settings",
			position = 3
	)
	String tilesSection = "tilesSection";

	@ConfigItem(
			keyName = "overlayTiles",
			name = "Highlight Incomplete",
			description = "Highlight the incomplete tiles",
			section = tilesSection,
			position = 0
	)
	default boolean showOverlayTiles()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayTileColour",
			name = "Incomplete Colour",
			description = "The highlight colour for incomplete tiles",
			section = tilesSection,
			position = 1
	)
	default Color overlayTileColour()
	{
		return Color.GREEN;
	}

	@ConfigItem(
			keyName = "overlayCompleteTiles",
			name = "Highlight Complete",
			description = "Highlight the complete tiles",
			section = tilesSection,
			position = 2
	)
	default boolean showOverlayCompleteTiles()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayTileCompleteColour",
			name = "Complete Colour",
			description = "The highlight colour for complete tiles",
			section = tilesSection,
			position = 3
	)
	default Color overlayTileCompleteColour()
	{
		return Color.RED;
	}

	@ConfigItem(
			keyName = "overlayTileProgress",
			name = "Show Tile Progress",
			description = "Shows progress of each side on the tile",
			section = tilesSection,
			position = 4
	)
	default boolean showOverlayTileProgress()
	{
		return true;
	}

	@ConfigSection(
			name = "Sunstone",
			description = "Configure sunstone settings",
			position = 4
	)
	String sunstoneSection = "sunstoneSection";

	@ConfigItem(
			keyName = "overlaySunstoneMode",
			name = "Highlight Sunstones",
			description = "Highlight sunstones when inventory is empty",
			section = sunstoneSection,
			position = 0
	)
	default SunstoneMode overlaySunstoneMode()
	{
		return SunstoneMode.ROCKS;
	}

	@Alpha
	@ConfigItem(
			keyName = "overlaySunstoneColour",
			name = "Sunstone Colour",
			description = "The highlight colour for sunstones",
			section = sunstoneSection,
			position = 1
	)
	default Color overlaySunstoneColour()
	{
		return new Color(0, 255, 0, 75);
	}

	@ConfigSection(
			name = "Fur Pouch",
			description = "Configure fur pouch settings",
			position = 5
	)
	String furPouchSection = "furPouchSection";

	@ConfigItem(
			keyName = "furPouchLowThreshold",
			name = "Low Threshold",
			description = "The threshold for fur pouch contents to be deemed low",
			section = furPouchSection,
			position = 0
	)
	default int furPouchLowThreshold()
	{
		return 5;
	}

	@ConfigItem(
			keyName = "overlayFurPouch",
			name = "Highlight Inventory",
			description = "Highlight the fur pouch in inventory",
			section = furPouchSection,
			position = 0
	)
	default boolean showOverlayFurPouch()
	{
		return true;
	}

	@ConfigItem(
			keyName = "overlayFurPouchColour",
			name = "Default Colour",
			description = "The default highlight colour for the fur pouch",
			section = furPouchSection,
			position = 1
	)
	default Color overlayFurPouchColour()
	{
		return new Color(0, 255, 0);
	}

	@ConfigItem(
			keyName = "overlayFurPouchLowColour",
			name = "Low Colour",
			description = "The highlight colour for when the fur pouch contents are low",
			section = furPouchSection,
			position = 2
	)
	default Color overlayFurPouchLowColour()
	{
		return new Color(255, 121, 0);
	}

	@ConfigItem(
			keyName = "overlayFurPouchEmptyColour",
			name = "Empty Colour",
			description = "The highlight colour for when the fur pouch is empty",
			section = furPouchSection,
			position = 3
	)
	default Color overlayFurPouchEmptyColour()
	{
		return new Color(255, 0, 0);
	}

	@ConfigItem(
			keyName = "overlayFurPouchUnknownColour",
			name = "Unknown Colour",
			description = "The highlight colour for when the fur pouch contents are unknown",
			section = furPouchSection,
			position = 4
	)
	default Color overlayFurPouchUnknownColour()
	{
		return new Color(255, 255, 0);
	}

	@ConfigItem(
			keyName = "overlayFurPouchCount",
			name = "Show Count",
			description = "Renders the count of furs in the pouch",
			section = furPouchSection,
			position = 5
	)
	default boolean showOverlayFurPouchCount()
	{
		return true;
	}

	@ConfigItem(
			keyName = "overlayFurPouchTextColour",
			name = "Default Text Colour",
			description = "The default text colour for the fur pouch",
			section = furPouchSection,
			position = 6
	)
	default Color overlayFurPouchTextColour()
	{
		return new Color(255, 255, 255);
	}

	@ConfigItem(
			keyName = "overlayFurPouchUnknownTextColour",
			name = "Unknown Text Colour",
			description = "The text colour for when the fur pouch contents are unknown",
			section = furPouchSection,
			position = 7
	)
	default Color overlayFurPouchUnknownTextColour()
	{
		return new Color(255, 255, 0);
	}

	@ConfigItem(
			keyName = "overlayFurPouchLowTextColour",
			name = "Low Text Colour",
			description = "The text colour for when the fur pouch contents are low",
			section = furPouchSection,
			position = 8
	)
	default Color overlayFurPouchLowTextColour()
	{
		return new Color(255, 121, 0);
	}

	@ConfigItem(
			keyName = "overlayFurPouchEmptyTextColour",
			name = "Empty Text Colour",
			description = "The text colour for when the fur pouch is empty",
			section = furPouchSection,
			position = 16
	)
	default Color overlayFurPouchEmptyTextColour()
	{
		return new Color(255, 0, 0);
	}
}
