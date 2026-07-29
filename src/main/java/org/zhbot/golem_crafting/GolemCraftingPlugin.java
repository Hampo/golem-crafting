package org.zhbot.golem_crafting;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

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

	@Inject
	private Client client;

	@Inject
	private GolemCraftingConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SouthGolemOverlay southGolemOverlay;

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

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(southGolemOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(southGolemOverlay);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		switch (event.getVarbitId())
		{
			case SOUTH_GOLEM_PROGRESS_ID:
				southGolemProgress = event.getValue();
				southGolemProgressTick = client.getTickCount();
				if (event.getValue() > 0)
					notifier.notify(config.notification(), "Golem stage complete");
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

		// TODO: Check fur pouch

		return true;
	}
}
