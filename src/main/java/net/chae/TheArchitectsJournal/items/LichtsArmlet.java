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

public class LichtsArmlet implements Listener {
    private final JavaPlugin plugin;

    private NamespacedKey LICHTSARMLET_RECIPE_KEY;
    private NamespacedKey LICHTSARMLETCOMPLETE_RECIPE_KEY;

    public LichtsArmlet(JavaPlugin plugin) {
        this.plugin = plugin;
        this.LICHTSARMLET_RECIPE_KEY = new NamespacedKey(plugin, "lichtsarmlet_recipe");
            this.LICHTSARMLETCOMPLETE_RECIPE_KEY = new NamespacedKey(plugin, "lichtsarmletcomplete_recipe");
    }

    public void registerRecipes() {
        registerLichtsArmletRecipe();
        registerLichtsArmletRecipeComplete();
    }

    // -- ITEM ACHIEVEMENT --
    @EventHandler
    public void onArmletCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack result = event.getRecipe().getResult();
        if (result == null || !result.isSimilar(LichtsArmletComplete())) return;
        grantArmletAdvancement(player);
    }

    private void grantArmletAdvancement(Player player) {
        NamespacedKey key = new NamespacedKey("chae", "custom/obtain_armlet");
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                progress.awardCriteria("has_armlet");
            }
        }
    }

    // --ITEM RECIPES--
    public void registerLichtsArmletRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(LICHTSARMLET_RECIPE_KEY, LichtsArmlet());
        recipe.shape(
                "GGG",
                "NTH",
                "GGG"
        );
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('N', Material.NAUTILUS_SHELL);
        recipe.setIngredient('T', Material.TRIDENT);
        recipe.setIngredient('H', Material.HEART_OF_THE_SEA);
        Bukkit.addRecipe(recipe);
    }

    public void registerLichtsArmletRecipeComplete() {
        ShapelessRecipe recipe = new ShapelessRecipe(LICHTSARMLETCOMPLETE_RECIPE_KEY, LichtsArmletComplete());
        recipe.addIngredient(new RecipeChoice.ExactChoice(LichtsArmlet()));
        recipe.addIngredient(Material.NETHER_STAR);
        Bukkit.addRecipe(recipe);
    }

    // --ITEM CREATION--
    public ItemStack LichtsArmlet() {

        ItemStack armlet = ItemStack.of(Material.STICK);

        // name and colour
        armlet.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "lichts_armlet"));
        armlet.setData(DataComponentTypes.ITEM_NAME, Component.text("Licht's Armlet", NamedTextColor.WHITE));

        //equipment slot
        armlet.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());

        //item stack size
        armlet.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return armlet;
    }

    public ItemStack LichtsArmletComplete() {
        ItemStack armlet = ItemStack.of(Material.STICK);

        // item attributes
        //ADD HERE AIZA

        // name and colour
        armlet.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "lichts_armlet"));
        armlet.setData(DataComponentTypes.ITEM_NAME, Component.text("Lichts Armlet", NamedTextColor.YELLOW));
        armlet.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        //equipment slot
        armlet.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());

        //item stack size
        armlet.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return armlet;
    }

    // -- ITEM SPECIAL EFFECTS --
    public void giveLichtsArmletEffect(Player player) {
        Location loc = player.getLocation();

        // Ocean ambience
        player.playSound(loc, Sound.AMBIENT_UNDERWATER_LOOP, 0.6f, 1.0f);          // Constant underwater hum
        player.playSound(loc, Sound.BLOCK_WATER_AMBIENT, 0.8f, 1.0f);             // Gentle water movement
        player.playSound(loc, Sound.ENTITY_DOLPHIN_AMBIENT, 0.5f, 1.2f);          // Dolphin call
        player.playSound(loc, Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, 0.7f, 1.1f); // Rising bubbles
        player.playSound(loc, Sound.BLOCK_CONDUIT_AMBIENT, 0.4f, 0.9f);           // Mystical ocean tone

        player.showTitle(Title.title(
                Component.text("Guidance of the Wind and Waves").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Angler of Sky and Sea - ").color(NamedTextColor.YELLOW)
        ));
    }


    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        // Activate if wand is in offhand (runs frequently)
        if (isLichtsArmlet(offhand)) {
            giveLichtsArmletEffect(player);
        }
    }

    // -- ITEM CHECKER --
    private boolean isLichtsArmlet(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(
                Component.text("Lichts Armlet", NamedTextColor.YELLOW)
        );
    }


    //ITEM SPECIAL EFFECTS


}
