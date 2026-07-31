package org.zhbot.golem_crafting;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
	name = "Golem Crafting"
)
public class GolemCraftingPlugin extends Plugin
{
	private static final WorldPoint CENTER = new WorldPoint(2590, 2250, 0);
	private static final int MAX_DISTANCE = 15;

	private static final Pattern FUR_POUCH_PATTERN = Pattern.compile("Your fur pouch is currently holding (\\d+) fur\\.");
	public static final Set<Integer> LARGE_FUR_POUCH_IDS = Set.of(
			ItemID.HG_FURPOUCH_LARGE,
			ItemID.HG_FURPOUCH_LARGE_OPEN
	);
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

	private static final String FINISH_ANGLE_MESSAGE = "You finish crafting the golem from this angle.";
	private static final String REPEATED_ANGLE_MESSAGE = "You've already crafted this side of the golem.";
	private static final Pattern TOTAL_GOLEMS_MESSAGE = Pattern.compile("You have crafted \\d+ golems on Wyrmscraig\\.");
	private static final Pattern LOOT_MESSAGE = Pattern.compile("As you complete the golem it leaves a gift on the ground for you: (\\d+) x (.*)\\.");

	private static final int CRAFTING_ANIMATION_ID = 14458;

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
	private ItemManager itemManager;

	@Inject
	private FurPouchOverlay furPouchOverlay;

	@Inject
	private SunstoneOverlay sunstoneOverlay;

	@Inject
	private GolemCraftingInfobox infobox;

	@Getter
	private final List<Golem> golems = List.of(new NorthGolem(), new SouthGolem());
	private final Set<GolemOverlay> golemOverlays = new HashSet<>();

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
		for (var golem : golems)
		{
			var overlay = new GolemOverlay(client, this, config, itemManager, golem);
			overlayManager.add(overlay);
			golemOverlays.add(overlay);
		}
		overlayManager.add(sunstoneOverlay);

		updateConfig();
	}

	@Override
	protected void shutDown() throws Exception
	{
		for (var golemOverlay : golemOverlays)
			overlayManager.remove(golemOverlay);
		overlayManager.remove(furPouchOverlay);
		overlayManager.remove(sunstoneOverlay);
		overlayManager.remove(infobox);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(GolemCraftingConfig.group))
			updateConfig();
	}

	private void updateConfig()
	{
		if (config.showInfobox())
			overlayManager.add(infobox);
		else
			overlayManager.remove(infobox);

		if(config.showOverlayFurPouch())
			overlayManager.add(furPouchOverlay);
		else
			overlayManager.remove(furPouchOverlay);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		var varbitId = event.getVarbitId();
		for (var golem : golems)
		{
			if (varbitId == golem.getProgressID())
			{
				golem.setLastProgressTick(client.getTickCount());
				if (event.getValue() > 1)
					notifier.notify(config.notification(), "Golem stage complete");
				else if (event.getValue() == 0 && getFurPouchCount() > 0 && hasLargeFurPouch())
					setFurPouchCount(getFurPouchCount() - 1);
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
			return;

		var message = Text.removeTags(event.getMessage());

		var hideMessage = false;

		var lootMatcher = LOOT_MESSAGE.matcher(message);
		if (lootMatcher.matches())
		{
			var lootAmount = Integer.parseInt(lootMatcher.group(1));
			var loot = lootMatcher.group(2).toLowerCase(Locale.ROOT);

			hideMessage = config.gameChatHideLoot() && (!config.gameChatHideLootExcludeChisel() || !loot.equalsIgnoreCase("Jeweller's Chisel"));

			switch (loot)
			{
				case "uncut sapphire":
					infobox.incrementSapphireCount(lootAmount);
					break;
				case "uncut emerald":
					infobox.incrementEmeraldCount(lootAmount);
					break;
				case "uncut ruby":
					infobox.incrementRubyCount(lootAmount);
					break;
				case "uncut diamond":
					infobox.incrementDiamondCount(lootAmount);
					break;
				case "jeweller's chisel":
					infobox.incrementJewellersChiselCount(lootAmount);
					break;
			}
		}

		hideMessage = hideMessage ||
						(config.gameChatHideAngle() && message.contains(FINISH_ANGLE_MESSAGE)) ||
						(config.gameChatHideRepeatedAngle() && message.contains(REPEATED_ANGLE_MESSAGE)) ||
						(config.gameChatHideTotal() && TOTAL_GOLEMS_MESSAGE.matcher(message).matches());

		if (!hideMessage && config.gameChatHideLoot())
		{
			var matcher = LOOT_MESSAGE.matcher(message);
			if (matcher.matches())
				hideMessage = !config.gameChatHideLootExcludeChisel() || !matcher.group(1).toLowerCase(Locale.ROOT).contains("jeweller's chisel");
		}
		if (hideMessage)
		{
			final ChatLineBuffer lineBuffer = client.getChatLineMap().get(ChatMessageType.GAMEMESSAGE.getType());
			if (lineBuffer == null)
				return;

			lineBuffer.removeMessageNode(event.getMessageNode());
			client.refreshChat();

			return;
		}

		if (!hasLargeFurPouch())
			return;

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
		if (event.getMenuAction() == MenuAction.WIDGET_TARGET_ON_WIDGET)
		{
			var selectedWidget = client.getSelectedWidget();
			if (selectedWidget == null)
				return;

			var sourceItemId = selectedWidget.getItemId();
			var targetItemId = event.getItemId();

			var furId = -1;
			if (LARGE_FUR_POUCH_IDS.contains(sourceItemId) && FUR_ITEM_IDS.contains(targetItemId))
				furId = targetItemId;
			else if (FUR_ITEM_IDS.contains(sourceItemId) && LARGE_FUR_POUCH_IDS.contains(targetItemId))
				furId = sourceItemId;
			else
				return;

			ItemContainer inventory = client.getItemContainer(InventoryID.INV);
			if (inventory == null)
				return;

			var furCount = inventory.count(furId);
			setFurPouchCount(Math.min(28, getFurPouchCount() + furCount));

			return;
		}

		var itemId = event.getItemId();
		if (!LARGE_FUR_POUCH_IDS.contains(itemId))
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
				break;
			case "Empty-to-bank": // Item Charges Improved
				setFurPouchCount(0);
				break;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGGED_IN)
			for (var golem : golems)
				golem.onLogin();
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

		for (var furPouchId : LARGE_FUR_POUCH_IDS)
			if (inventory.contains(furPouchId))
				return true;

		return false;
	}

	public boolean isBankOpen()
	{
		Widget bankWidget = client.getWidget(InterfaceID.BANKMAIN);

		return bankWidget != null && !bankWidget.isHidden();
	}

	public boolean isAnyGolemActive()
	{
		for (var golem : golems)
			if (golem.getProgress(client) > 0)
				return true;

		return false;
	}

	public boolean isWithinGolemArea()
	{
		var player = client.getLocalPlayer();
		if (player == null)
			return false;

		var playerLocation = player.getWorldLocation();
		return playerLocation.distanceTo(CENTER) <= MAX_DISTANCE;
	}

	public boolean isCrafting()
	{
		return client.getLocalPlayer().getAnimation() == CRAFTING_ANIMATION_ID;
	}
}
