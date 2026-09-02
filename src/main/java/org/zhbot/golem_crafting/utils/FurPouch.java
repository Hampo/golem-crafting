package org.zhbot.golem_crafting.utils;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import org.zhbot.golem_crafting.GolemCraftingConfig;
import org.zhbot.golem_crafting.GolemCraftingPlugin;
import org.zhbot.golem_crafting.enums.FurPouchType;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

// TODO:
// - Support Empty in deposit box
// - Handle tattered fur
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
            ItemID.GOAT_PIT_FUR
    );

    private static final Pattern FUR_POUCH_PATTERN = Pattern.compile("Your fur pouch is currently holding (\\d+) fur\\.");
    private static final List<String> FUR_POUCH_EMPTY_MESSAGES = List.of(
            "You need to dress the golem in furs from hunted creatures.",
            "Your fur pouch is empty.",
            "You empty all of your containers into the bank."
    );
    private static final List<String> CAUGHT_CREATURE_MESSAGES = List.of(
            "You manage to noose a polar kebbit that is hiding in the snowdrift.",
            "You manage to noose a common kebbit that is hiding in the bush.",
            "You manage to noose a Feldip weasel that is hiding in the bush.",
            "You manage to noose a desert devil that is hiding in the sand.",
            "You've caught a pyre fox.",
            "You've caught a spined larupia!",
            "You've caught a horned graahk!",
            "You've caught a sabre-toothed kyatt!",
            "You've caught a sabretoothed kyatt!",
            "You've caught a sunlight antelope!",
            "You've caught a moonlight antelope!",
            "You retrieve the falcon as well as the fur of the dead kebbit."
    );

    private final Client client;
    private final ConfigManager configManager;
    private final GolemCraftingPlugin plugin;
    private final TextUtils textUtils;

    @Getter
    private boolean hasOpenFurPouch = false;
    @Getter
    private boolean hasClosedFurPouch = false;
    @Getter
    private FurPouchType furPouchType = FurPouchType.INVALID;
    private int invFurCount = 0;
    private int invSpaceCount = 0;

    @Inject
    private FurPouch(Client client, ClientThread clientThread, ConfigManager configManager, GolemCraftingPlugin plugin, TextUtils textUtils)
    {
        this.client = client;
        this.configManager = configManager;
        this.plugin = plugin;
        this.textUtils = textUtils;

        if (client.getGameState() == GameState.LOGGED_IN)
        {
            clientThread.invoke(() ->
            {
                hunterXP = client.getSkillExperience(Skill.HUNTER);
                checkInventory(client.getItemContainer(InventoryID.INV));
            });
        }
    }

    public boolean hasFurPouch()
    {
        return hasClosedFurPouch || hasOpenFurPouch;
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
        if (furPouchCount == 0 || furPouchCount == -1)
            return;

        var varbitId = event.getVarbitId();

        for (var golem : plugin.getGolems())
            if (varbitId == golem.getProgressID() && event.getValue() == 0)
                    setCount(furPouchCount - 1);
    }

    @Subscribe
    private void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM)
            return;

        if (!hasFurPouch())
            return;

        var message = textUtils.Clean(event.getMessage());

        var matcher = FUR_POUCH_PATTERN.matcher(message);
        if (matcher.find())
        {
            setCount(Integer.parseInt(matcher.group(1)));
            return;
        }

        for (var emptyMessage : FUR_POUCH_EMPTY_MESSAGES)
        {
            if (!message.contains(emptyMessage))
                continue;

            setCount(0);
            return;
        }

        if (!hasOpenFurPouch || getCount() == -1)
            return;

        for (var caughtMessage : CAUGHT_CREATURE_MESSAGES)
        {
            if (!message.contains(caughtMessage))
                continue;

            setCount(Math.min(getCount() + 1, getCapacity()));
            return;
        }
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

        switch (event.getMenuOption())
        {
            case "Fill":
                var count = getCount();
                var capacity = getCapacity();
                var space = capacity - count;
                if (space < 1)
                    return;

                setCount(Math.min(capacity, count + invFurCount));
                break;
            case "Empty":
                if (isBankOpen())
                {
                    setCount(0);
                    break;
                }

                if (invSpaceCount < 1)
                    return;

                setCount(Math.max(0, getCount() - invSpaceCount));
                break;
            case "Empty-to-bank": // Item Charges Improved
                setCount(0);
                break;
        }
    }

    private static final WorldPoint GOAT_PIT_TILE = new WorldPoint(2572, 2195, 0);
    private static final int GOAT_PIT_RANGE = 2;
    private static final int GOAT_PIT_MIN_XP = 100;
    private static final int GOAT_PIT_MAX_XP = 179;

    private int hunterXP = -1;
    private int goatHornCount = 0;

    @Subscribe
    private void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() != GameState.LOGGED_IN)
            return;

        hunterXP = client.getSkillExperience(Skill.HUNTER);
        checkInventory(client.getItemContainer(InventoryID.INV));
    }

    @Subscribe
    private void onStatChanged(StatChanged event)
    {
        var skill = event.getSkill();
        if (skill != Skill.HUNTER)
            return;

        var xp = event.getXp();
        if (hunterXP == -1)
        {
            hunterXP = xp;
            return;
        }

        var xpGained = xp - hunterXP;
        hunterXP = xp;

        if (!hasOpenFurPouch || getCount() == -1)
            return;

        if (xpGained < GOAT_PIT_MIN_XP || xpGained > GOAT_PIT_MAX_XP)
            return;

        var localPlayer = client.getLocalPlayer();
        if (localPlayer == null)
            return;

        if (localPlayer.getWorldLocation().distanceTo(GOAT_PIT_TILE) > GOAT_PIT_RANGE)
            return;

        var inventory = client.getItemContainer(InventoryID.INV);
        if (inventory == null)
            return;

        var newGoatHornCount = inventory.count(ItemID.DESERT_GOAT_HORN);
        if (goatHornCount != newGoatHornCount)
            return;

        setCount(Math.min(getCount() + 1, getCapacity()));
    }

    @Subscribe
    private void onItemContainerChanged(ItemContainerChanged event)
    {
        if (event.getContainerId() != InventoryID.INV)
            return;

        checkInventory(event.getItemContainer());
    }

    public boolean isBankOpen()
    {
        Widget bankWidget = client.getWidget(InterfaceID.BANKMAIN);

        return bankWidget != null && !bankWidget.isHidden();
    }

    private void checkInventory(ItemContainer inventory)
    {
        if (inventory == null)
            return;

        var occupiedSlots = 0;

        var goatHornCount = 0;
        var invFurCount = 0;
        var hasClosedFurPouch = false;
        var hasOpenFurPouch = false;
        var furPouchType = FurPouchType.INVALID;
        for (var item : inventory.getItems())
        {
            if (item == null || item.getId() == -1)
                continue;

            occupiedSlots++;

            switch (item.getId()) {
                case ItemID.DESERT_GOAT_HORN:
                    goatHornCount++;
                    break;
                case ItemID.HG_FURPOUCH_SMALL:
                    hasClosedFurPouch = true;
                    furPouchType = FurPouchType.SMALL;
                    break;
                case ItemID.HG_FURPOUCH_SMALL_OPEN:
                    hasOpenFurPouch = true;
                    furPouchType = FurPouchType.SMALL;
                    break;
                case ItemID.HG_FURPOUCH_MED:
                    hasClosedFurPouch = true;
                    furPouchType = FurPouchType.MED;
                    break;
                case ItemID.HG_FURPOUCH_MED_OPEN:
                    hasOpenFurPouch = true;
                    furPouchType = FurPouchType.MED;
                    break;
                case ItemID.HG_FURPOUCH_LARGE:
                    hasClosedFurPouch = true;
                    furPouchType = FurPouchType.LARGE;
                    break;
                case ItemID.HG_FURPOUCH_LARGE_OPEN:
                    hasOpenFurPouch = true;
                    furPouchType = FurPouchType.LARGE;
                    break;
                default:
                    if (FUR_ITEM_IDS.contains(item.getId()))
                        invFurCount++;
                    break;
            }
        }
        this.goatHornCount = goatHornCount;
        this.invFurCount = invFurCount;
        this.invSpaceCount = 28 - occupiedSlots;
        this.hasClosedFurPouch = hasClosedFurPouch;
        this.hasOpenFurPouch = hasOpenFurPouch;
        this.furPouchType = furPouchType;
    }
}
