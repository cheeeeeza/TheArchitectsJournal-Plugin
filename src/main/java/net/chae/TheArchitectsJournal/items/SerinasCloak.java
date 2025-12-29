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
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


@SuppressWarnings("UnstableAPIUsage")
public class SerinasCloak implements Listener {
    private final JavaPlugin plugin;

    private NamespacedKey SERINASCLOAK_RECIPE_KEY;
    private NamespacedKey SERINASCLOAKCOMPLETE_RECIPE_KEY;

    public SerinasCloak(JavaPlugin plugin) {
        this.plugin = plugin;
        this.SERINASCLOAK_RECIPE_KEY = new NamespacedKey(plugin, "serinascloak_recipe");
        this.SERINASCLOAKCOMPLETE_RECIPE_KEY = new NamespacedKey(plugin, "serinascloakcomplete_recipe");
    }

    public void registerRecipes() {
        registerSerinasCloakRecipe();
        registerSerinasCloakRecipeComplete();
    }

    // ACHIEVEMENT - lantern craft
    @EventHandler
    public void onCloakCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack result = event.getRecipe().getResult();
        if (result == null || !result.isSimilar(SerinasCloakComplete())) return;
        grantCloakAdvancement(player);
    }

    private void grantCloakAdvancement(Player player) {
        NamespacedKey key = new NamespacedKey("chae", "custom/obtain_cloak");
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                progress.awardCriteria("has_cloak");
            }
        }
    }

    // RECIPES -------------------------------------------------------
    public void registerSerinasCloakRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(SERINASCLOAK_RECIPE_KEY, SerinasCloak());
        recipe.shape(
                "SJC",
                "OGB",
                "AAA");
        recipe.setIngredient('O', Material.OAK_LOG);
        recipe.setIngredient('S', Material.SPRUCE_LOG);
        recipe.setIngredient('J', Material.JUNGLE_LOG);
        recipe.setIngredient('C', Material.CHERRY_LOG);
        recipe.setIngredient('B', Material.BAMBOO_BLOCK);
        recipe.setIngredient('G', Material.GLOW_BERRIES);
        recipe.setIngredient('A', Material.ANVIL);
        Bukkit.addRecipe(recipe);
    }

    public void registerSerinasCloakRecipeComplete() {
        ShapelessRecipe recipe = new ShapelessRecipe(SERINASCLOAKCOMPLETE_RECIPE_KEY, SerinasCloakComplete());
        recipe.addIngredient(new RecipeChoice.ExactChoice(SerinasCloak()));
        recipe.addIngredient(Material.NETHER_STAR);
        Bukkit.addRecipe(recipe);
    }

    // RECIPES -------------------------------------------------------

    // ITEMS ------------------------------------------------------------
    public ItemStack SerinasCloak() {
        ItemStack crown = ItemStack.of(Material.GLOW_BERRIES);
        crown.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "emmans_lantern"));
        crown.setData(DataComponentTypes.ITEM_NAME, Component.text("Emman's Lantern", NamedTextColor.WHITE));
        crown.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());
        crown.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return crown;
    }

    public ItemStack SerinasCloakComplete() {

        ItemStack cloak = ItemStack.of(Material.STICK);

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();

        cloak.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());

        cloak.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "emmans_lantern"));

        // descriptions

        // Hide default attribute tooltip
        TooltipDisplay.Builder tooltipBuilder = TooltipDisplay.tooltipDisplay();
        tooltipBuilder.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        cloak.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipBuilder.build());

        cloak.setData(DataComponentTypes.ITEM_NAME, Component.text("Serina's Cloak", NamedTextColor.DARK_GREEN));

        cloak.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        cloak.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());

        cloak.setData(DataComponentTypes.MAX_STACK_SIZE, 1);

        return cloak;
    }

    // ITEMS ------------------------------------------------------------

    // SPECIAL TITLE EFFECTS -----------------------------------------------
    public void giveEmmansLanternEffect(Player player) {

        player.playSound(player.getLocation(),"minecraft:block.spore_blossom.place", 0.6f, 1.1f);
        player.playSound(player.getLocation(),"minecraft:block.amethyst_block.chime", 0.8f, 1.3f);
        player.playSound(player.getLocation(),"minecraft:block.cave_vines.pick_berries", 1f, 1.3f);
        player.playSound(player.getLocation(),"minecraft:item.glow_ink_sac.use", 0.7f, 1.0f);
        player.playSound(player.getLocation(),"minecraft:entity.axolotl.idle_air", 1f, 1.2f);
        player.playSound(player.getLocation(),"minecraft:block.enchantment_table.use", 0.4f, 0.9f);
        player.playSound(player.getLocation(), Sound.AMBIENT_CRIMSON_FOREST_ADDITIONS, 1f, 0.9f);

        player.showTitle(Title.title(
                Component.text("The Impending Doom").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Harrowing Harbinger - ").color(NamedTextColor.DARK_GREEN)
        ));
    }

    // CLOAK CHECKER -----------------------------------------------------------
    private boolean isSerinasCloak(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(Component.text("Serina's Cloak", NamedTextColor.DARK_GREEN));
    }




}
