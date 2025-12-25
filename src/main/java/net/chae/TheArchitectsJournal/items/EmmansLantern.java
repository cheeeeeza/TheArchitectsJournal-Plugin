package net.chae.TheArchitectsJournal.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kyori.adventure.text.format.TextColor.color;


@SuppressWarnings("UnstableAPIUsage")

public class EmmansLantern implements Listener {
    private final JavaPlugin plugin;

    private NamespacedKey EMMANSLANTERN_RECIPE_KEY;
    private NamespacedKey EMMANSLANTERNCOMPLETE_RECIPE_KEY;

    public EmmansLantern(JavaPlugin plugin) {
        this.plugin = plugin;
        this.EMMANSLANTERN_RECIPE_KEY = new NamespacedKey(plugin, "emmanslantern_recipe");
            this.EMMANSLANTERNCOMPLETE_RECIPE_KEY = new NamespacedKey(plugin, "emmanslantern_recipe");
    }

    public void registerRecipes() {
        registerEmmansLanternRecipe();
        registerEmmansLanternRecipeComplete();
    }

    // -- ITEM ACHIEVEMENT --
    @EventHandler
    public void onLanternCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack result = event.getRecipe().getResult();
        if (result == null || !result.isSimilar(EmmansLanternComplete())) return;
        grantLanternAdvancement(player);
    }

    private void grantLanternAdvancement(Player player) {
        NamespacedKey key = new NamespacedKey("chae", "custom/obtain_lantern");
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                progress.awardCriteria("has_lantern");
            }
        }
    }

    // --ITEM RECIPES--
    public void registerEmmansLanternRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(EMMANSLANTERN_RECIPE_KEY, EmmansLantern());
        recipe.shape(
                " N ",
                " E ",
                " T "
        );
        recipe.setIngredient('N', Material.BEACON);
        recipe.setIngredient('E', Material.ENDER_EYE);
        recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
        Bukkit.addRecipe(recipe);
    }

    public void registerEmmansLanternRecipeComplete() {
        ShapelessRecipe recipe = new ShapelessRecipe(EMMANSLANTERNCOMPLETE_RECIPE_KEY, EmmansLanternComplete());
        recipe.addIngredient(new RecipeChoice.ExactChoice(EmmansLantern()));
        recipe.addIngredient(Material.NETHER_STAR);
        Bukkit.addRecipe(recipe);
    }

    // --ITEM CREATION--
    public ItemStack EmmansLantern() {

        ItemStack wand = ItemStack.of(Material.STICK);

        // name and colour
        wand.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "yevas_wand"));
        wand.setData(DataComponentTypes.ITEM_NAME, Component.text("Yeva's Wand", NamedTextColor.WHITE));

        //equipment slot
        wand.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());

        //item stack size
        wand.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return wand;
    }

    public ItemStack EmmansLanternComplete() {
        ItemStack lantern = ItemStack.of(Material.STICK);

        // item attributes
        //ADD HERE AIZA

        // name and colour
        lantern.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "emmans_lantern"));
        lantern.setData(DataComponentTypes.ITEM_NAME, Component.text("Emman's Lantern", NamedTextColor.YELLOW));
        lantern.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        //equipment slot
        lantern.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());

        //item stack size
        lantern.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return lantern;
    }

    // -- ITEM SPECIAL EFFECTS --
    public void giveEmmansLanternEffect(Player player) {
        //EDIT
        player.playSound(player.getLocation(), "minecraft:entity.warden.emerge", 1f, 1f);
        player.showTitle(Title.title(
                Component.text("Light up the Way!").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Wandering Trailblazer - ").color(NamedTextColor.YELLOW)
        ));
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        // Activate if wand is in offhand (runs frequently)
        if (isEmmansLantern(offhand)) {
            giveEmmansLanternEffect(player);
        }
    }

    // -- ITEM CHECKER --
    private boolean isEmmansLantern(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(
                Component.text("Emman's Lantern", TextColor.color(0xFFFF69B4))
        );
    }


    //ITEM SPECIAL EFFECTS


}
