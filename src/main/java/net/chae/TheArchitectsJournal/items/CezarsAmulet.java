package net.chae.TheArchitectsJournal.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableAPIUsage")
public class CezarsAmulet implements Listener {
    private final JavaPlugin plugin;

    private NamespacedKey CEZARSAMULET_RECIPE_KEY;
    private NamespacedKey CEZARSAMULETCOMPLETE_RECIPE_KEY;

    public CezarsAmulet(JavaPlugin plugin) {
        this.plugin = plugin;
        this.CEZARSAMULET_RECIPE_KEY = new NamespacedKey(plugin, "cezarsamulet_recipe");
        this.CEZARSAMULETCOMPLETE_RECIPE_KEY = new NamespacedKey(plugin, "cezarsamuletcomplete_recipe");
    }

    public void registerRecipes() {
        registerCezarsAmuletRecipeComplete();
    }

    // ACHIEVEMENT - lantern craft
    @EventHandler
    public void onAmuletCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack result = event.getRecipe().getResult();
        if (result == null || !result.isSimilar(CezarsAmuletComplete())) return;
        grantAmuletAdvancement(player);
    }

    private void grantAmuletAdvancement(Player player) {
        NamespacedKey key = new NamespacedKey("chae", "custom/obtain_amulet");
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                progress.awardCriteria("has_amulet");
            }
        }
    }

    // RECIPES -------------------------------------------------------

    public void registerCezarsAmuletRecipeComplete() {
        ShapelessRecipe recipe = new ShapelessRecipe(CEZARSAMULETCOMPLETE_RECIPE_KEY, CezarsAmuletComplete());
        recipe.addIngredient(Material.NETHER_STAR);
        Bukkit.addRecipe(recipe);
    }

    // ITEMS ------------------------------------------------------------

    public ItemStack CezarsAmuletComplete() {
        ItemStack amulet = ItemStack.of(Material.STICK);

        // Attributes (FIXED NamespacedKey - uses plugin instance)
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();

        builder.addModifier(Attribute.LUCK,
                new AttributeModifier(new NamespacedKey(plugin, "luck"), 1024,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.OFFHAND));

        amulet.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());


        amulet.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "cezars_amulet"));

        // descriptions

        // Hide default attribute tooltip
        TooltipDisplay.Builder tooltipBuilder = TooltipDisplay.tooltipDisplay();
        tooltipBuilder.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        amulet.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipBuilder.build());

        amulet.setData(DataComponentTypes.ITEM_NAME, Component.text("Cezar's Amulet", NamedTextColor.DARK_PURPLE));

        amulet.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        amulet.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());

        amulet.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return amulet;
    }


    // SPECIAL TITLE EFFECTS -----------------------------------------------
    public void giveCezarsAmuletEffect(Player player) {

        player.playSound(player.getLocation(),"minecraft:block.respawn_anchor.charge", 0.4f, 0.9f);
        player.playSound(player.getLocation(),"minecraft:block.enchantment_table.use", 0.4f, 0.9f);
        player.playSound(player.getLocation(),"minecraft:block.anvil.use", 0.2f, 1.8f);
        player.playSound(player.getLocation(),"minecraft:block.beacon.activate", 0.35f, 1.1f);
        player.playSound(player.getLocation(),"minecraft:block.lightning_rod.power_on", 0.4f, 1.1f);
        player.playSound(player.getLocation(),"minecraft:block.lightning_rod.power_on", 0.4f, 1.1f);
        player.playSound(player.getLocation(),"minecraft:block.lightning_rod.power_on", 0.4f, 1.1f);

        player.showTitle(Title.title(
                Component.text("The Vision of Time").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Propheseer - ").color(NamedTextColor.WHITE)
        ));
    }

    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack newOffhand = event.getOffHandItem();
        if (isCezarsAmulet(newOffhand)) {
            giveCezarsAmuletEffect(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getSlot() != 40 || event.getCurrentItem() == null) return; // 40 = offhand slot
        if (isCezarsAmulet(event.getCurrentItem())) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (isCezarsAmulet(player.getInventory().getItemInOffHand())) {
                    giveCezarsAmuletEffect(player);
                }
            }, 1L);
        }
    }


    // LANTERN CHECKER -----------------------------------------------------------
    private boolean isCezarsAmulet(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(Component.text("Cezar's Amulet", NamedTextColor.DARK_PURPLE));
    }



}
