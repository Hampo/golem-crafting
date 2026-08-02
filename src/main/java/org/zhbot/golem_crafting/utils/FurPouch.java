package org.zhbot.golem_crafting.utils;

import com.google.common.collect.Sets;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;
import org.zhbot.golem_crafting.GolemCraftingConfig;
import org.zhbot.golem_crafting.GolemCraftingPlugin;
import org.zhbot.golem_crafting.enums.FurPouchType;

import javax.inject.Inject;
import java.util.Set;
import java.util.regex.Pattern;

public class FurPouch {
    private static final Set<Integer> CLOSED_POUCH_IDS = Set.of(
            ItemID.HG_FURPOUCH_SMALL,
            ItemID.HG_FURPOUCH_MED,
            ItemID.HG_FURPOUCH_LARGE
    );
    private static final Set<Integer> OPEN_POUCH_IDS = Set.of(
            ItemID.HG_FURPOUCH_SMALL_OPEN,
            ItemID.HG_FURPOUCH_MED_OPEN,
            ItemID.HG_FURPOUCH_LARGE_OPEN
    );
    public static final Set<Integer> ALL_POUCH_IDS = Sets.union(CLOSED_POUCH_IDS, OPEN_POUCH_IDS);
    private static final Set<Integer> SMALL_POUCH_IDS = Set.of(
            ItemID.HG_FURPOUCH_SMALL,
            ItemID.HG_FURPOUCH_SMALL_OPEN
    );
    private static final Set<Integer> MED_POUCH_IDS = Set.of(
            ItemID.HG_FURPOUCH_MED,
            ItemID.HG_FURPOUCH_MED_OPEN
    );
    private static final Set<Integer> LARGE_POUCH_IDS = Set.of(
            ItemID.HG_FURPOUCH_LARGE,
            ItemID.HG_FURPOUCH_LARGE_OPEN
    );
    private static final int SMALL_POUCH_CAPACITY = 14;
    private static final int MED_POUCH_CAPACITY = 21;
    private static final int LARGE_POUCH_CAPACITY = 28;

    public static final Set<Integer> FUR_ITEM_IDS = Set.of(
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

    private static final Pattern FUR_POUCH_PATTERN = Pattern.compile("Your fur pouch is currently holding (\\d+) fur\\.");

    private final Client client;
    private final ConfigManager configManager;
    private final GolemCraftingPlugin plugin;

    @Inject
    private FurPouch(Client client, ConfigManager configManager, GolemCraftingPlugin plugin)
    {
        this.client = client;
        this.configManager = configManager;
        this.plugin = plugin;
    }

    public boolean hasClosedFurPouch()
    {
        var inventory = client.getItemContainer(InventoryID.INV);
        if (inventory == null)
            return false;

        for (var closedPouchID : CLOSED_POUCH_IDS)
            if (inventory.contains(closedPouchID))
                return true;

        return false;
    }

    public boolean hasOpenFurPouch()
    {
        var inventory = client.getItemContainer(InventoryID.INV);
        if (inventory == null)
            return false;

        for (var openPouchID : OPEN_POUCH_IDS)
            if (inventory.contains(openPouchID))
                return true;

        return false;
    }

    public boolean hasFurPouch()
    {
        return hasClosedFurPouch() || hasOpenFurPouch();
    }

    public FurPouchType getFurPouchType()
    {
        var inventory = client.getItemContainer(InventoryID.INV);
        if (inventory == null)
            return FurPouchType.INVALID;

        for (var smallPouchID : SMALL_POUCH_IDS)
            if (inventory.contains(smallPouchID))
                return FurPouchType.SMALL;

        for (var medPouchID : MED_POUCH_IDS)
            if (inventory.contains(medPouchID))
                return FurPouchType.MED;

        for (var largePouchID : LARGE_POUCH_IDS)
            if (inventory.contains(largePouchID))
                return FurPouchType.LARGE;

        return FurPouchType.INVALID;
    }

    public int getCapacity()
    {
        var pouchType = getFurPouchType();

        switch (pouchType)
        {
            case SMALL:
                return SMALL_POUCH_CAPACITY;
            case MED:
                return MED_POUCH_CAPACITY;
            case LARGE:
                return LARGE_POUCH_CAPACITY;
            case INVALID:
            default:
                return 0;
        }
    }

    private static final String FUR_POUCH_KEY = "furPouchCount";
    public int getCount()
    {
        var accountHash = client.getAccountHash();
        if (accountHash == -1)
            return -1;

        Integer value = configManager.getConfiguration(GolemCraftingConfig.group, FUR_POUCH_KEY + "_" + accountHash, Integer.class);
        return value != null ? value : -1;
    }
    private void setCount(int value)
    {
        var accountHash = client.getAccountHash();
        if (accountHash == -1)
            return;

        configManager.setConfiguration(GolemCraftingConfig.group, FUR_POUCH_KEY + "_" + accountHash, value);
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        if (!hasFurPouch())
            return;

        var furPouchCount = getCount();
        if (furPouchCount == 0)
            return;

        var varbitId = event.getVarbitId();

        for (var golem : plugin.getGolems())
            if (varbitId == golem.getProgressID() && event.getValue() == 0)
                    setCount(furPouchCount - 1);
    }

    @Subscribe
    private void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE)
            return;

        if (!hasFurPouch())
            return;

        var message = Text.removeTags(event.getMessage());

        if (message.contains("You need to dress the golem in furs from hunted creatures.") ||
                message.contains("Your fur pouch is empty."))
        {
            setCount(0);
            return;
        }

        var matcher = FUR_POUCH_PATTERN.matcher(message);
        if (!matcher.find())
            return;

        setCount(Integer.parseInt(matcher.group(1)));
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
            if (ALL_POUCH_IDS.contains(sourceItemId) && FUR_ITEM_IDS.contains(targetItemId))
                furId = targetItemId;
            else if (FUR_ITEM_IDS.contains(sourceItemId) && ALL_POUCH_IDS.contains(targetItemId))
                furId = sourceItemId;
            else
                return;

            ItemContainer inventory = client.getItemContainer(InventoryID.INV);
            if (inventory == null)
                return;

            var furCount = inventory.count(furId);
            setCount(Math.min(getCapacity(), getCount() + furCount));

            return;
        }

        var itemId = event.getItemId();
        if (!ALL_POUCH_IDS.contains(itemId))
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

                setCount(Math.min(getCapacity(), getCount() + furCount));
                break;
            case "Empty":
                if (isBankOpen())
                {
                    setCount(0);
                    break;
                }

                var freeSpots = 0;
                for (var item : inventory.getItems())
                    if (item.getId() == -1)
                        freeSpots++;

                setCount(Math.max(0, getCount() - freeSpots));
                break;
            case "Empty-to-bank": // Item Charges Improved
                setCount(0);
                break;
        }
    }

    public boolean isBankOpen()
    {
        Widget bankWidget = client.getWidget(InterfaceID.BANKMAIN);

        return bankWidget != null && !bankWidget.isHidden();
    }
}
