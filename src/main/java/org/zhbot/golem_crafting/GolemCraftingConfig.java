package org.zhbot.golem_crafting;

import net.runelite.client.config.*;
import org.zhbot.golem_crafting.enums.ProgressMode;
import org.zhbot.golem_crafting.enums.RenderMode;
import org.zhbot.golem_crafting.enums.SunstoneMode;

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
			keyName = "infoboxSunStoneMomentum",
			name = "Show sunstone momentum",
			description = "Show sunstone momentum",
			section = infoboxSection,
			position = 5
	)
	default boolean showInfoboxSunStoneMomentum()
	{
		return true;
	}

	@ConfigItem(
			keyName = "infoboxLoot",
			name = "Show loot",
			description = "Show loot counts",
			section = infoboxSection,
			position = 6
	)
	default boolean showInfoboxLoot()
	{
		return true;
	}

	@ConfigItem(
			keyName = "infoboxFurPouchUnknownTextColour",
			name = "Fur Pouch Unknown Text Colour",
			description = "The text colour for when the fur pouch contents are unknown",
			section = infoboxSection,
			position = 7
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
			position = 8
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
			position = 9
	)
	default Color infoboxFurPouchEmptyTextColour()
	{
		return new Color(255, 0, 0);
	}

	@ConfigSection(
			name = "Resource Warning Infobox",
			description = "Configure the resource warning settings",
			position = 2
	)
	String resourceWarningSection = "resourceWarningSection";

	@ConfigItem(
			keyName = "resourceInfoboxEnabled",
			name = "Enabled",
			description = "Enable the resource warning infobox",
			section = resourceWarningSection,
			position = 0
	)
	default boolean resourceInfoboxEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "resourceInfoboxFlash",
			name = "Flash on Empty",
			description = "Enable the resource warning infobox flashing when empty",
			section = resourceWarningSection,
			position = 1
	)
	default boolean resourceInfoboxFlash()
	{
		return true;
	}

	@ConfigItem(
			keyName = "resourceInfoboxWarnFur",
			name = "Warn on Fur",
			description = "Enable the resource warning infobox fur warning",
			section = resourceWarningSection,
			position = 2
	)
	default boolean resourceInfoboxWarnFur()
	{
		return true;
	}

	@ConfigItem(
			keyName = "resourceInfoboxFurThreshold",
			name = "Fur Threshold",
			description = "The low threshold for fur. Set to 0 to only warn on empty.",
			section = resourceWarningSection,
			position = 3
	)
	default int resourceInfoboxFurThreshold()
	{
		return 5;
	}

	@ConfigItem(
			keyName = "resourceInfoboxWarnSunstone",
			name = "Warn on Sunstone",
			description = "Enable the resource warning infobox sunstone warning",
			section = resourceWarningSection,
			position = 4
	)
	default boolean resourceInfoboxWarnSunstone()
	{
		return true;
	}

	@ConfigItem(
			keyName = "resourceInfoboxSunstoneThreshold",
			name = "Sunstone Threshold",
			description = "The low threshold for sunstone. Set to 0 to only warn on empty.",
			section = resourceWarningSection,
			position = 5
	)
	default int resourceInfoboxSunstoneThreshold()
	{
		return 4;
	}

	@ConfigItem(
			keyName = "resourceInfoboxWarnSunstoneCore",
			name = "Warn on Sunstone Core",
			description = "Enable the resource warning infobox sunstone warning",
			section = resourceWarningSection,
			position = 6
	)
	default boolean resourceInfoboxWarnSunstoneCore()
	{
		return true;
	}

	@ConfigItem(
			keyName = "resourceInfoboxCoreThreshold",
			name = "Core Threshold",
			description = "The low threshold for sunstone core. Set to 0 to only warn on empty.",
			section = resourceWarningSection,
			position = 7
	)
	default int resourceInfoboxCoreThreshold()
	{
		return 1;
	}

	@Alpha
	@ConfigItem(
			keyName = "resourceInfoboxLowColour",
			name = "Low Colour",
			description = "The infobox colour for when resources are low",
			section = resourceWarningSection,
			position = 8
	)
	default Color resourceInfoboxLowColour()
	{
		return new Color(255, 121, 0, 90);
	}

	@Alpha
	@ConfigItem(
			keyName = "resourceInfoboxEmptyColour1",
			name = "Empty Colour 1",
			description = "The infobox colour for when resources are empty",
			section = resourceWarningSection,
			position = 9
	)
	default Color resourceInfoboxEmptyColour1()
	{
		return new Color(255, 0, 0, 90);
	}

	@Alpha
	@ConfigItem(
			keyName = "resourceInfoboxEmptyColour2",
			name = "Empty Colour 2",
			description = "The second infobox colour for when resources are empty",
			section = resourceWarningSection,
			position = 10
	)
	default Color resourceInfoboxEmptyColour2()
	{
		return new Color(128, 128, 128, 90);
	}

	@ConfigSection(
			name = "Plinth",
			description = "Configure the plinth settings",
			position = 3
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
			keyName = "overlayPlinthRenderStyle",
			name = "Render style",
			description = "Choose style of overlay for the plinth",
			section = plinthSection,
			position = 1
	)
	default RenderMode overlayPlinthRenderStyle() { return RenderMode.CLICKBOX; }

	@ConfigItem(
			keyName = "overlayPlinthEfficiency",
			name = "Highlight Efficiency",
			description = "Highlight the golem plinth in a different colour when it's efficient to click",
			section = plinthSection,
			position = 2
	)
	default boolean showOverlayPlinthEfficiency()
	{
		return true;
	}

	@ConfigItem(
			keyName = "overlayPlinthCore",
			name = "Show Sunlight Core",
			description = "Render a sunlight core on final stage",
			section = plinthSection,
			position = 3
	)
	default boolean showOverlayPlinthCore()
	{
		return true;
	}

	@ConfigItem(
			keyName = "overlayPlinthZOffset",
			name = "Progress/Core Z Offset",
			description = "The Z offset for the progress/core overlay, relative to the plinth",
			section = plinthSection,
			position = 4
	)
	default int overlayZOffset()
	{
		return 290;
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayPlinthValidColour",
			name = "Valid Colour",
			description = "The highlight colour for plinth when action is valid",
			section = plinthSection,
			position = 5
	)
	default Color overlayPlinthValidColour()
	{
		return new Color(0, 255, 0, 75);
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayPlinthEfficiencyColour",
			name = "Efficiency Colour",
			description = "The highlight colour for plinth when it's efficient to click again",
			section = plinthSection,
			position = 6
	)
	default Color overlayPlinthEfficiencyColour()
	{
		return new Color(225, 223, 0, 75);
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayPlinthInvalidColour",
			name = "Invalid Colour",
			description = "The highlight colour for plinth when action is invalid",
			section = plinthSection,
			position = 7
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
			position = 8
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
			position = 9
	)
	default Color overlayPlinthInvalidCoreColour()
	{
		return new Color(255, 0, 0, 75);
	}

	@ConfigSection(
			name = "Tiles",
			description = "Configure tiles settings",
			position = 4
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

	@ConfigSection(
			name = "Progress",
			description = "Configure progress settings",
			position = 5
	)
	String progressSection = "progressSection";

	@ConfigItem(
			keyName = "progressMode",
			name = "Show Progress",
			description = "Where to show progress of each side",
			section = progressSection,
			position = 0
	)
	default ProgressMode showProgressMode()
	{
		return ProgressMode.BOTH;
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayProgressColour",
			name = "Colour",
			description = "The highlight colour for progress",
			section = progressSection,
			position = 1
	)
	default Color overlayProgressColour()
	{
		return Color.ORANGE;
	}

	@ConfigSection(
			name = "Sunstone",
			description = "Configure sunstone settings",
			position = 6
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

	@ConfigItem(
			keyName = "overlaySunstoneRenderStyle",
			name = "Render style",
			description = "Choose style of overlay for the sunstone",
			section = sunstoneSection,
			position = 1
	)
	default RenderMode overlaySunstoneRenderStyle() { return RenderMode.CLICKBOX; }

	@ConfigItem(
			keyName = "overlaySunstoneMomentum",
			name = "Show Momentum",
			description = "Show sunstone momentum",
			section = sunstoneSection,
			position = 2
	)
	default boolean overlaySunstoneMomentum()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "overlaySunstoneColour",
			name = "Sunstone Colour",
			description = "The highlight colour for sunstones",
			section = sunstoneSection,
			position = 3
	)
	default Color overlaySunstoneColour()
	{
		return new Color(0, 255, 0, 75);
	}

	@Alpha
	@ConfigItem(
			keyName = "overlaySunstoneMomentumColour",
			name = "Momentum Colour",
			description = "The colour for sunstone momentum",
			section = sunstoneSection,
			position = 4
	)
	default Color overlaySunstoneMomentumColour()
	{
		return new Color(255, 0, 0, 75);
	}

	@ConfigSection(
			name = "Fur Pouch",
			description = "Configure fur pouch settings",
			position = 7
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
			keyName = "overlayFurPouchAlways",
			name = "Always show",
			description = "Always show, even when not within golem crafting area",
			section = furPouchSection,
			position = 1
	)
	default boolean showFurPouchAlways()
	{
		return false;
	}

	@ConfigItem(
			keyName = "overlayFurPouch",
			name = "Highlight Inventory",
			description = "Highlight the fur pouch in inventory",
			section = furPouchSection,
			position = 2
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
			position = 3
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
			position = 4
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
			position = 5
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
			position = 6
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
			position = 7
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
			position = 8
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
			position = 9
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
			position = 10
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
			position = 11
	)
	default Color overlayFurPouchEmptyTextColour()
	{
		return new Color(255, 0, 0);
	}

	@ConfigSection(
			name = "Game Chat",
			description = "Configure game chat settings",
			position = 8
	)
	String gameChatSection = "gameChatSection";

	@ConfigItem(
			keyName = "gameChatHideAngle",
			name = "Hide finished angle",
			description = "Hide the game chat message when you finish an angle",
			section = gameChatSection,
			position = 0
	)
	default boolean gameChatHideAngle()
	{
		return true;
	}

	@ConfigItem(
			keyName = "gameChatHideRepeatedAngle",
			name = "Hide repeated angle",
			description = "Hide the game chat message when you click an angle already completed",
			section = gameChatSection,
			position = 0
	)
	default boolean gameChatHideRepeatedAngle()
	{
		return false;
	}

	@ConfigItem(
			keyName = "gameChatHideTotal",
			name = "Hide total",
			description = "Hide the game chat message with the total golems crafted",
			section = gameChatSection,
			position = 2
	)
	default boolean gameChatHideTotal()
	{
		return false;
	}

	@ConfigItem(
			keyName = "gameChatHideLoot",
			name = "Hide loot",
			description = "Hide the game chat message with the loot",
			section = gameChatSection,
			position = 3
	)
	default boolean gameChatHideLoot()
	{
		return false;
	}

	@ConfigItem(
			keyName = "gameChatHideLootExcludeChisel",
			name = "Exclude Jeweller's Chisel",
			description = "Exclude the Jeweller's Chisel from hiding the loot game chat message",
			section = gameChatSection,
			position = 4
	)
	default boolean gameChatHideLootExcludeChisel()
	{
		return true;
	}

	@ConfigSection(
			name = "Debug",
			description = "Debug settings",
			position = 9
	)
	String debugSection = "debugSection";

	@ConfigItem(
			keyName = "debug",
			name = "Debug",
			description = "Debug",
			section = debugSection,
			position = 0
	)
	default boolean debug()
	{
		return false;
	}
}
