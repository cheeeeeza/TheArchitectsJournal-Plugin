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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import net.chae.TheArchitectsJournal.items.ArhamsCrown;
import net.chae.TheArchitectsJournal.items.LichtsArmlet;
import net.chae.TheArchitectsJournal.items.YevasWand;
import net.chae.TheArchitectsJournal.items.YukimurasGreatsword;
import net.chae.TheArchitectsJournal.items.EmmansLantern;
import net.chae.TheArchitectsJournal.items.HudeensBlueprint;
import net.chae.TheArchitectsJournal.items.SerinasCloak;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@SuppressWarnings("UnstableAPIUsage")
public class CezarsAmulet implements Listener {
    private final JavaPlugin plugin;

    private NamespacedKey CEZARSAMULETCOMPLETE_RECIPE_KEY;

    private ArhamsCrown crown;
    private LichtsArmlet armlet;
    private YevasWand wand;
    private YukimurasGreatsword greatsword;
    private EmmansLantern lantern;
    private HudeensBlueprint blueprint;
    private SerinasCloak cloak;

    public CezarsAmulet(JavaPlugin plugin) {
        this.plugin = plugin;
        this.CEZARSAMULETCOMPLETE_RECIPE_KEY = new NamespacedKey(plugin, "cezarsamuletcomplete_recipe");


        this.crown = new ArhamsCrown(plugin);
        this.armlet = new LichtsArmlet(plugin);
        this.wand = new YevasWand(plugin);
        this.greatsword = new YukimurasGreatsword(plugin);
        this.lantern = new EmmansLantern(plugin);
        this.blueprint = new HudeensBlueprint(plugin);
        this.cloak = new SerinasCloak(plugin);


        startFreezeTask();

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
        recipe.addIngredient(new RecipeChoice.ExactChoice(crown.ArhamsCrownComplete()));
        recipe.addIngredient(new RecipeChoice.ExactChoice(armlet.LichtsArmletComplete()));
        recipe.addIngredient(new RecipeChoice.ExactChoice(wand.YevasWandComplete()));
        recipe.addIngredient(new RecipeChoice.ExactChoice(greatsword.YukimurasGreatswordComplete()));
        recipe.addIngredient(new RecipeChoice.ExactChoice(lantern.EmmansLanternComplete()));
        recipe.addIngredient(new RecipeChoice.ExactChoice(blueprint.HudeensBlueprintComplete()));
        recipe.addIngredient(new RecipeChoice.ExactChoice(cloak.SerinasCloakComplete()));
        Bukkit.addRecipe(recipe);
    }

    // ITEMS ------------------------------------------------------------

    public ItemStack CezarsAmuletComplete() {
        ItemStack amulet = ItemStack.of(Material.STICK);

        // Attributes (FIXED NamespacedKey - uses plugin instance)
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();

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
        player.playSound(player.getLocation(),"minecraft:block.beacon.activate", 0.35f, 1.1f);
        player.playSound(player.getLocation(),"minecraft:block.lightning_rod.power_on", 0.4f, 1.1f);
        player.playSound(player.getLocation(),"minecraft:block.lightning_rod.power_on", 0.4f, 1.1f);
        player.playSound(player.getLocation(),"minecraft:block.lightning_rod.power_on", 0.4f, 1.1f);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.9f);

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
        } else {
            // restart task when off hold
            stopFreezeTask();
            startFreezeTask();
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


    // AMULET CHECKER -----------------------------------------------------------
    private boolean isCezarsAmulet(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(Component.text("Cezar's Amulet", NamedTextColor.DARK_PURPLE));
    }

    // AURA FREEZE --------------------------------------------------------------
    private int freezeTaskId = -1;
    public void startFreezeTask() {
        if (freezeTaskId != -1) return;
        freezeTaskId = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player holder : Bukkit.getOnlinePlayers()) {
                    if (!isCezarsAmulet(holder.getInventory().getItemInOffHand())) continue;
                    Location loc = holder.getLocation();
                    for (Entity entity : loc.getNearbyEntities(15.0, 15.0, 15.0)) {
                        if (entity instanceof Player p && p == holder) continue;
                        entity.setVelocity(new Vector(0, 0, 0));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L).getTaskId();
    }

    public void stopFreezeTask() {
        if (freezeTaskId != -1) {
            Bukkit.getScheduler().cancelTask(freezeTaskId);
            freezeTaskId = -1;
        }
    }

    // PLAYER UNIVERSAL FREEZE -------------------------------------------------------

    private final Set<UUID> frozen = new HashSet<>();

    @EventHandler
    public void onAmuletLeftClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.OFF_HAND) return; // offhand only [web:115]
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Player caster = event.getPlayer();
        if (!isCezarsAmulet(caster.getInventory().getItemInOffHand())) return;

        // Optional: prevent block breaking / interactions
        event.setCancelled(true);

        freezeAllPlayersFor15s(caster);
    }

    private void freezeAllPlayersFor15s(Player caster) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            frozen.add(p.getUniqueId());
            p.setVelocity(new Vector(0, 0, 0));
        }

        caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 0.5f);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                frozen.remove(p.getUniqueId());
            }
        }, 20L * 10); // 10 seconds
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!frozen.contains(event.getPlayer().getUniqueId())) return;
        if (event.getTo() == null) return;

        // allow head turning, block translation
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            event.setTo(from);
        }
    }







}
