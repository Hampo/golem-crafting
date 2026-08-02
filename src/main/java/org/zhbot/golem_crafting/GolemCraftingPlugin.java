package org.zhbot.golem_crafting;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.zhbot.golem_crafting.enums.SunstoneMode;
import org.zhbot.golem_crafting.golems.Golem;
import org.zhbot.golem_crafting.golems.NorthGolem;
import org.zhbot.golem_crafting.golems.SouthGolem;
import org.zhbot.golem_crafting.overlays.*;
import org.zhbot.golem_crafting.utils.FurPouch;
import org.zhbot.golem_crafting.utils.GraphicsUtils;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
	name = "Golem Crafting"
)
public class GolemCraftingPlugin extends Plugin
{
	private static final WorldPoint CENTER = new WorldPoint(2590, 2250, 0);
	private static final int MAX_DISTANCE = 15;

	private static final String FINISH_ANGLE_MESSAGE = "You finish crafting the golem from this angle.";
	private static final String REPEATED_ANGLE_MESSAGE = "You've already crafted this side of the golem.";
	private static final Pattern TOTAL_GOLEMS_MESSAGE = Pattern.compile("You have crafted \\d+ golems on Wyrmscraig\\.");
	public static final Pattern LOOT_MESSAGE = Pattern.compile("As you complete the golem it leaves a gift on the ground for you: (\\d+) x (.*)\\.");

	private static final int CRAFTING_ANIMATION_ID = 14458;

	@Inject
	private Client client;

	@Inject
	private EventBus eventBus;

	@Inject
	private GolemCraftingConfig config;

	@Inject
	private GraphicsUtils graphicsUtils;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private FurPouchOverlay furPouchOverlay;

	@Inject
	@Getter
	private SunstoneOverlay sunstoneOverlay;

	@Inject
	private GolemCraftingInfobox infobox;

	@Inject
	private ResourceWarningInfobox resourceWarningInfobox;

	@Inject
	@Getter
	private FurPouch furPouch;

	@Getter
	private final List<Golem> golems = new ArrayList<>();
	private final Set<GolemOverlay> golemOverlays = new HashSet<>();

	@Getter
	private int lastBusyTick;

	@Override
	protected void startUp() throws Exception
	{
		eventBus.register(furPouch);
		eventBus.register(infobox);
		eventBus.register(sunstoneOverlay);

		golems.add(new NorthGolem(client, notifier, config));
		golems.add(new SouthGolem(client, notifier, config));

		for (var golem : golems)
		{
			eventBus.register(golem);

			var overlay = new GolemOverlay(client, this, config, graphicsUtils, golem);
			overlayManager.add(overlay);
			golemOverlays.add(overlay);
		}

		updateConfig();
	}

	@Override
	protected void shutDown() throws Exception
	{
		eventBus.unregister(furPouch);
		eventBus.unregister(infobox);
		eventBus.unregister(sunstoneOverlay);
		for (var golem : golems)
			eventBus.unregister(golem);

		for (var golemOverlay : golemOverlays)
			overlayManager.remove(golemOverlay);
		overlayManager.remove(furPouchOverlay);
		overlayManager.remove(sunstoneOverlay);
		overlayManager.remove(infobox);
		overlayManager.remove(resourceWarningInfobox);
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

		if (config.resourceInfoboxEnabled())
			overlayManager.add(resourceWarningInfobox);
		else
			overlayManager.remove(resourceWarningInfobox);

		if(config.showOverlayFurPouch() || config.showOverlayFurPouchCount())
			overlayManager.add(furPouchOverlay);
		else
			overlayManager.remove(furPouchOverlay);

		if (config.overlaySunstoneMode() != SunstoneMode.NONE)
			overlayManager.add(sunstoneOverlay);
		else
			overlayManager.remove(sunstoneOverlay);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		var varbitId = event.getVarbitId();

		if (varbitId == VarbitID.BUSY)
			lastBusyTick = client.getTickCount();
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
			var loot = lootMatcher.group(2).toLowerCase(Locale.ROOT);

			hideMessage = config.gameChatHideLoot() && (!config.gameChatHideLootExcludeChisel() || !loot.equalsIgnoreCase("Jeweller's Chisel"));
		}

		hideMessage = hideMessage ||
						(config.gameChatHideAngle() && message.contains(FINISH_ANGLE_MESSAGE)) ||
						(config.gameChatHideRepeatedAngle() && message.contains(REPEATED_ANGLE_MESSAGE)) ||
						(config.gameChatHideTotal() && TOTAL_GOLEMS_MESSAGE.matcher(message).matches());

		if (hideMessage)
		{
			final ChatLineBuffer lineBuffer = client.getChatLineMap().get(ChatMessageType.GAMEMESSAGE.getType());
			if (lineBuffer == null)
				return;

			lineBuffer.removeMessageNode(event.getMessageNode());
			client.refreshChat();
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

		if (getFurCount() == 0)
			return false;

		return true;
	}

	public int getFurCount()
	{
		var inventory = client.getItemContainer(InventoryID.INV);
		if (inventory == null)
			return 0;

		var furCount = furPouch.hasFurPouch() ? furPouch.getCount() : 0;
		if (furCount == -1)
			furCount = 0;

		for (var item : inventory.getItems())
			if (FurPouch.FUR_ITEM_IDS.contains(item.getId()))
				furCount++;

		return furCount;
	}

	public boolean isAnyGolemActive()
	{
		for (var golem : golems)
			if (golem.getProgress() > 0)
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
