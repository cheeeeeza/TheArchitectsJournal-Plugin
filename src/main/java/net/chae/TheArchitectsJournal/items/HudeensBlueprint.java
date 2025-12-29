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
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("UnstableAPIUsage")
public class HudeensBlueprint implements Listener {
    private final JavaPlugin plugin;

    private NamespacedKey HUDEENSBLUEPRINT_RECIPE_KEY;
    private NamespacedKey HUDEENSBLUEPRINTCOMPLETE_RECIPE_KEY;

    public HudeensBlueprint(JavaPlugin plugin) {
        this.plugin = plugin;
        this.HUDEENSBLUEPRINT_RECIPE_KEY = new NamespacedKey(plugin, "hudeensblueprint_recipe");
        this.HUDEENSBLUEPRINTCOMPLETE_RECIPE_KEY = new NamespacedKey(plugin, "hudeensblueprintcomplete_recipe");
    }

    public void registerRecipes() {
        registerHudeensBlueprintRecipe();
        registerHudeensBlueprintRecipeComplete();
    }

    // ACHIEVEMENT - blueprint craft
    @EventHandler
    public void onBlueprintCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack result = event.getRecipe().getResult();
        if (result == null || !result.isSimilar(HudeensBlueprintComplete())) return;
        grantBlueprintAdvancement(player);
    }

    private void grantBlueprintAdvancement(Player player) {
        NamespacedKey key = new NamespacedKey("chae", "custom/obtain_blueprint");
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                progress.awardCriteria("has_blueprint");
            }
        }
    }

    // RECIPES -------------------------------------------------------
    public void registerHudeensBlueprintRecipe() {
        ShapelessRecipe recipe = new ShapelessRecipe(HUDEENSBLUEPRINT_RECIPE_KEY, HudeensBlueprint());
        recipe.addIngredient(Material.ENCHANTED_GOLDEN_APPLE);
        recipe.addIngredient(Material.MAP);
        Bukkit.addRecipe(recipe);
    }

    public void registerHudeensBlueprintRecipeComplete() {
        ShapelessRecipe recipe = new ShapelessRecipe(HUDEENSBLUEPRINTCOMPLETE_RECIPE_KEY, HudeensBlueprintComplete());
        recipe.addIngredient(new RecipeChoice.ExactChoice(HudeensBlueprint()));
        recipe.addIngredient(Material.NETHER_STAR);
        Bukkit.addRecipe(recipe);
    }

    // ITEMS ------------------------------------------------------------
    public ItemStack HudeensBlueprint() {
        ItemStack blueprint = ItemStack.of(Material.STICK);

        blueprint.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "hudeens_blueprint"));
        blueprint.setData(DataComponentTypes.ITEM_NAME, Component.text("Hudeen's Blueprint", NamedTextColor.WHITE));
        blueprint.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());
        blueprint.setData(DataComponentTypes.MAX_STACK_SIZE, 1);

        return blueprint;
    }

    public ItemStack HudeensBlueprintComplete() {
        ItemStack blueprint = ItemStack.of(Material.STICK);

        // Attributes (FIXED NamespacedKey - uses plugin instance)
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();

        blueprint.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "hudeens_blueprint"));

        // descriptions

        // Hide default attribute tooltip
        TooltipDisplay.Builder tooltipBuilder = TooltipDisplay.tooltipDisplay();
        tooltipBuilder.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        blueprint.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipBuilder.build());

        blueprint.setData(DataComponentTypes.ITEM_NAME, Component.text("Hudeen's Blueprint", NamedTextColor.DARK_BLUE));

        blueprint.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        blueprint.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());

        blueprint.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return blueprint;
    }


    // SPECIAL TITLE EFFECTS -----------------------------------------------
    public void giveHudeensBlueprintEffect(Player player) {

        player.playSound(player.getLocation(),"minecraft:block.respawn_anchor.charge", 0.4f, 0.9f);
        player.playSound(player.getLocation(),"minecraft:block.enchantment_table.use", 0.4f, 0.9f);
        player.playSound(player.getLocation(),"minecraft:block.anvil.use", 0.2f, 1.8f);
        player.playSound(player.getLocation(),"minecraft:block.beacon.activate", 0.35f, 1.1f);
        player.playSound(player.getLocation(),"minecraft:block.lightning_rod.power_on", 0.4f, 1.1f);
        player.playSound(player.getLocation(),"minecraft:block.lightning_rod.power_on", 0.4f, 1.1f);
        player.playSound(player.getLocation(),"minecraft:block.lightning_rod.power_on", 0.4f, 1.1f);
        player.playSound(player.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK, 0.4f, 0.9f);
        player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_AMBIENT_OMINOUS, 1f, 0.9f);

        player.showTitle(Title.title(
                Component.text("The Pinnacle of Creation").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Realmcrafting Architect - ").color(NamedTextColor.DARK_BLUE)
        ));
    }

    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack newOffhand = event.getOffHandItem();
        if (isHudeensBlueprint(newOffhand)) {
            giveHudeensBlueprintEffect(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getSlot() != 40 || event.getCurrentItem() == null) return; // 40 = offhand slot
        if (isHudeensBlueprint(event.getCurrentItem())) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (isHudeensBlueprint(player.getInventory().getItemInOffHand())) {
                    giveHudeensBlueprintEffect(player);
                }
            }, 1L);
        }
    }

    // BLUEPRINT CHECKER -----------------------------------------------------------
    private boolean isHudeensBlueprint(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(Component.text("Hudeen's Blueprint", NamedTextColor.DARK_BLUE));
    }

    // DUPLICATION EFFECT --------------------------------------

    private final Map<UUID, Long> duplicateCooldowns = new HashMap<>();


    @EventHandler
    public void onBlueprintDuplicate(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // blueprint in offhand check
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (!isHudeensBlueprint(offhand)) return;

        // left click air/block
        if (event.getAction() != Action.LEFT_CLICK_AIR &&
                event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand == null || mainHand.getType().isAir()) return;

        // 60 second cooldown
        Long lastDup = duplicateCooldowns.get(player.getUniqueId());
        if (lastDup != null && System.currentTimeMillis() - lastDup < 60000) {
            player.sendActionBar(Component.text("§7Blueprint recharging...", NamedTextColor.GRAY));
            return;
        }
        duplicateCooldowns.put(player.getUniqueId(), System.currentTimeMillis());

        // Duplicate main hand item (full stack)
        ItemStack duplicate = mainHand.clone();
        duplicate.setAmount(Math.min(duplicate.getMaxStackSize(), 64));

        // Give to inventory
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(duplicate);
        if (!leftover.isEmpty()) {
            player.getWorld().dropItem(player.getLocation(), leftover.values().iterator().next());
        }

        // visual effects
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 0.8f, 1.8f);
        player.getWorld().spawnParticle(Particle.ENCHANTED_HIT, player.getLocation().add(0, 1.5, 0), 15, 0.4, 0.4, 0.4, 0.05);
        player.sendActionBar(Component.text("§9✦ Duplicated §b" + mainHand.getType().name() + " §9✦", NamedTextColor.DARK_BLUE));
    }



}
