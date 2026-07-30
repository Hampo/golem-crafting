package org.zhbot.golem_crafting;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
	name = "Golem Crafting"
)
public class GolemCraftingPlugin extends Plugin
{
	private static final int SOUTH_GOLEM_PROGRESS_ID = 15733;
	private static final int SOUTH_GOLEM_NORTH_STATE_ID = 15734;
	private static final int SOUTH_GOLEM_EAST_STATE_ID = 15735;
	private static final int SOUTH_GOLEM_SOUTH_STATE_ID = 15736;
	private static final int SOUTH_GOLEM_WEST_STATE_ID = 15737;
	//private static final int GOLEMS_CRAFTED_TOTAL_ID = 15738;

	private static final Pattern FUR_POUCH_PATTERN = Pattern.compile("Your fur pouch is currently holding (\\d+) fur\\.");
	private static final Set<Integer> FUR_ITEM_IDS = Set.of(
			ItemID.HUNTINGBEAST_POLAR_FUR,
			ItemID.HUNTINGBEAST_WOODLAND_FUR,
			ItemID.HUNTINGBEAST_JUNGLE_FUR,
			ItemID.HUNTINGBEAST_DESERT_FUR,
			ItemID.HUNTING_FENNECFOX_FUR,
			ItemID.HUNTING_FUR_JAGUAR_PERFECT,
			ItemID.HUNTING_FUR_LEOPARD_PERFECT,
			ItemID.HUNTING_FUR_TIGER_PERFECT,
			ItemID.HUNTING_ANTELOPESUN_FUR,
			ItemID.HUNTING_ANTELOPEMOON_FUR,
			ItemID.HUNTINGBEAST_SPEEDY_FUR,
			ItemID.HUNTINGBEAST_SILENT_FUR,
			ItemID.HUNTINGBEAST_SPEEDY2_FUR,
			/*ItemID.GOAT_PIT_FUR*/34017
	);

	@Inject
	private Client client;

	@Inject
	private GolemCraftingConfig config;

	@Inject ConfigManager configManager;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SouthGolemOverlay southGolemOverlay;

	@Inject
	private FurPouchOverlay furPouchOverlay;

	@Getter
	private int southGolemProgress;

	@Getter
	private int southGolemProgressTick;

	@Getter
	private boolean southGolemNorthDone;

	@Getter
	private boolean southGolemEastDone;

	@Getter
	private boolean southGolemSouthDone;

	@Getter
	private boolean southGolemWestDone;

	private static final String FUR_POUCH_KEY = "furPouchCount";
	public int getFurPouchCount()
	{
		var accountHash = client.getAccountHash();
		if (accountHash == -1)
			return -1;

		Integer value = configManager.getConfiguration(GolemCraftingConfig.group, FUR_POUCH_KEY + "_" + accountHash, Integer.class);
		return value != null ? value : -1;
	}
	private void setFurPouchCount(int value)
	{
		var accountHash = client.getAccountHash();
		if (accountHash == -1)
			return;

		configManager.setConfiguration(GolemCraftingConfig.group, FUR_POUCH_KEY + "_" + accountHash, value);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(southGolemOverlay);
		overlayManager.add(furPouchOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(southGolemOverlay);
		overlayManager.remove(furPouchOverlay);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		switch (event.getVarbitId())
		{
			case SOUTH_GOLEM_PROGRESS_ID:
				southGolemProgress = event.getValue();
				southGolemProgressTick = client.getTickCount();
				if (event.getValue() > 1)
					notifier.notify(config.notification(), "Golem stage complete");
				else if (event.getValue() == 0 && getFurPouchCount() > 0 && hasLargeFurPouch())
					setFurPouchCount(getFurPouchCount() - 1);
				break;
			case SOUTH_GOLEM_NORTH_STATE_ID:
				southGolemNorthDone = event.getValue() != 0;
				break;
			case SOUTH_GOLEM_EAST_STATE_ID:
				southGolemEastDone = event.getValue() != 0;
				break;
			case SOUTH_GOLEM_SOUTH_STATE_ID:
				southGolemSouthDone = event.getValue() != 0;
				break;
			case SOUTH_GOLEM_WEST_STATE_ID:
				southGolemWestDone = event.getValue() != 0;
				break;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
			return;
		if (!hasLargeFurPouch())
			return;

		var message = event.getMessage();

		if (message.contains("You need to dress the golem in furs from hunted creatures.") ||
			message.contains("Your fur pouch is empty."))
		{
			setFurPouchCount(0);
			return;
		}

		var matcher = FUR_POUCH_PATTERN.matcher(message);
		if (!matcher.find())
			return;

		setFurPouchCount(Integer.parseInt(matcher.group(1)));
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		var itemId = event.getItemId();
		if (itemId != ItemID.HG_FURPOUCH_LARGE && itemId != ItemID.HG_FURPOUCH_LARGE_OPEN)
			return;

		ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		if (inventory == null)
			return;

		switch (event.getMenuOption())
		{
			case "Fill":
				var furCount = 0;
				for (var item : inventory.getItems())
					if (FUR_ITEM_IDS.contains(item.getId()))
						furCount++;

				setFurPouchCount(Math.min(28, getFurPouchCount() + furCount));
				break;
			case "Empty":
				if (isBankOpen())
				{
					setFurPouchCount(0);
					break;
				}

				var freeSpots = 0;
				for (var item : inventory.getItems())
					if (item.getId() == -1)
						freeSpots++;

				setFurPouchCount(Math.max(0, getFurPouchCount() - freeSpots));
			case "Empty-to-bank": // Item Charges Improved
				setFurPouchCount(0);
		}
	}

	@Provides
	GolemCraftingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GolemCraftingConfig.class);
	}

	public boolean hasGolemMaterials()
	{
		ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		if (inventory == null)
			return false;
		ItemContainer equipment = client.getItemContainer(InventoryID.WORN);

		if (!(inventory.contains(ItemID.CHISEL) || inventory.contains(34024/*Jewellers' Chisel*/)))
			return false;

		if (!(inventory.contains(ItemID.HAMMER) || inventory.contains(ItemID.IMCANDO_HAMMER) || inventory.contains(ItemID.IMCANDO_HAMMER_OFFHAND)))
		{
			if (equipment == null)
				return false;

			if (!(equipment.contains(ItemID.IMCANDO_HAMMER) || equipment.contains(ItemID.IMCANDO_HAMMER_OFFHAND)))
				return false;
		}

		if (!inventory.contains(34022/*Sunstone Core*/))
			return false;

		if (inventory.count(34020/*Sunstone*/) < 4)
			return false;

		if (hasLargeFurPouch() && getFurPouchCount() == 0)
			return false;

		return true;
	}

	public boolean hasLargeFurPouch()
	{
		ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		if (inventory == null)
			return false;

		return inventory.contains(ItemID.HG_FURPOUCH_LARGE) || inventory.contains(ItemID.HG_FURPOUCH_LARGE_OPEN);
	}

	public boolean isBankOpen()
	{
		Widget bankWidget = client.getWidget(InterfaceID.BANKMAIN);

		return bankWidget != null && !bankWidget.isHidden();
	}
}
