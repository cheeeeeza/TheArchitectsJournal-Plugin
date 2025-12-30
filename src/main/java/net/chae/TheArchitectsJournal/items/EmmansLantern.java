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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("UnstableAPIUsage")

public class EmmansLantern implements Listener {
    private final JavaPlugin plugin;

    private NamespacedKey EMMANSLANTERN_RECIPE_KEY;
    private NamespacedKey EMMANSLANTERNCOMPLETE_RECIPE_KEY;

    public EmmansLantern(JavaPlugin plugin) {
        this.plugin = plugin;
        this.EMMANSLANTERN_RECIPE_KEY = new NamespacedKey(plugin, "emmanslanter_recipe");
        this.EMMANSLANTERNCOMPLETE_RECIPE_KEY = new NamespacedKey(plugin, "emmanslanterncomplete_recipe");

        // SUPER SNEAK SPEED ---------------------------------------------------------------------------
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (isEmmansLantern(p.getInventory().getItemInOffHand()) && p.isSneaking()) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 9, true, false));
                        //p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 40, 0, true, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1, true, false)); // Regen II
                        p.setFoodLevel(Math.min(20, p.getFoodLevel() + 1));
                        p.setSaturation(10.0f);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void registerRecipes() {
        registerEmmansLanternRecipe();
        registerEmmansLanternRecipeComplete();
    }

    // ACHIEVEMENT ----------------------------------------------------------------------------------
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

    // RECIPES ------------------------------------------------------------------------------------
    public void registerEmmansLanternRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(EMMANSLANTERN_RECIPE_KEY, EmmansLantern());
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

    public void registerEmmansLanternRecipeComplete() {
        ShapelessRecipe recipe = new ShapelessRecipe(EMMANSLANTERNCOMPLETE_RECIPE_KEY, EmmansLanternComplete());
        recipe.addIngredient(new RecipeChoice.ExactChoice(EmmansLantern()));
        recipe.addIngredient(Material.NETHER_STAR);
        Bukkit.addRecipe(recipe);
    }

    // ITEMS ---------------------------------------------------------------------------------------
    public ItemStack EmmansLantern() {
        ItemStack crown = ItemStack.of(Material.GLOW_BERRIES);
        crown.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "emmans_lantern"));
        crown.setData(DataComponentTypes.ITEM_NAME, Component.text("Emman's Lantern", NamedTextColor.WHITE));
        crown.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());
        crown.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return crown;
    }

    public ItemStack EmmansLanternComplete() {
        ItemStack lantern = ItemStack.of(Material.GLOW_BERRIES);

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();

        builder.addModifier(Attribute.LUCK,
                new AttributeModifier(new NamespacedKey(plugin, "luck"), 100,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.OFFHAND));

        builder.addModifier(Attribute.BLOCK_INTERACTION_RANGE,
                new AttributeModifier(new NamespacedKey(plugin, "block_interaction_range"), 3,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.OFFHAND));

        builder.addModifier(Attribute.SNEAKING_SPEED,
                new AttributeModifier(new NamespacedKey(plugin, "sneaking_speed"), 4,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.OFFHAND));


        lantern.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());

        lantern.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "emmans_lantern"));

        // descriptions

        // Hide default attribute tooltip
        TooltipDisplay.Builder tooltipBuilder = TooltipDisplay.tooltipDisplay();
        tooltipBuilder.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        lantern.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipBuilder.build());

        lantern.setData(DataComponentTypes.ITEM_NAME, Component.text("Emman's Lantern", NamedTextColor.LIGHT_PURPLE));

        lantern.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        lantern.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());

        lantern.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return lantern;
    }

    // SPECIAL TITLE EFFECTS ----------------------------------------------------------------------------
    public void giveEmmansLanternEffect(Player player) {

        player.playSound(player.getLocation(),"minecraft:block.spore_blossom.place", 0.6f, 1.1f);
        player.playSound(player.getLocation(),"minecraft:block.amethyst_block.chime", 0.8f, 1.3f);
        player.playSound(player.getLocation(),"minecraft:block.cave_vines.pick_berries", 1f, 1.3f);
        player.playSound(player.getLocation(),"minecraft:item.glow_ink_sac.use", 0.7f, 1.0f);
        player.playSound(player.getLocation(),"minecraft:entity.axolotl.idle_air", 1f, 1.2f);
        player.playSound(player.getLocation(),"minecraft:block.enchantment_table.use", 0.4f, 0.9f);
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.9f);
        player.playSound(player.getLocation(), Sound.ENTITY_TURTLE_AMBIENT_LAND, 1f, 0.9f);

        player.showTitle(Title.title(
                Component.text("The Light of Way").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Wandering Trailblazer - ").color(NamedTextColor.LIGHT_PURPLE)
        ));
    }

    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack newOffhand = event.getOffHandItem();
        if (isEmmansLantern(newOffhand)) {
            giveEmmansLanternEffect(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getSlot() != 40 || event.getCurrentItem() == null) return; // 40 = offhand slot
        if (isEmmansLantern(event.getCurrentItem())) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (isEmmansLantern(player.getInventory().getItemInOffHand())) {
                    giveEmmansLanternEffect(player);
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onOffhandEquip(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getRawSlot() != 45) return; // Offhand slot

        // **CORRECT: Use runTaskLater (ONE TIME)**
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack offhand = player.getInventory().getItemInOffHand();
                if (isEmmansLantern(offhand)) {
                    giveEmmansLanternEffect(player);
                }
            }
        }.runTaskLater(plugin, 1L); // ONE TIME next tick
    }


    // LANTERN CHECKER ----------------------------------------------------------------------------------------
    private boolean isEmmansLantern(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(Component.text("Emman's Lantern", NamedTextColor.LIGHT_PURPLE));
    }

    // OW MOBS IMMUNITY --------------------------------------------------------------------------------------
    @EventHandler
    public void onOverworldMobTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;

        ItemStack hand = player.getInventory().getItemInOffHand();

        if (!isEmmansLantern(hand)) return;

        Entity e = event.getEntity();
        if (e instanceof Zombie ||
                e instanceof Creeper ||
                e instanceof Spider ||
                e instanceof CaveSpider ||
                e instanceof Skeleton ||
                e instanceof Pillager) {
            event.setCancelled(true);
        }
    }

    // NO HUNGER CONSUMED RAHHH -------------------------------------------------------------------------------
    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // Only protect players with lantern in offhand
        if (!isEmmansLantern(player.getInventory().getItemInOffHand())) return;

        // Keep hunger at current level (no drain)
        event.setCancelled(true);
    }

    // SPEED STEALER ---------------------------------------------------------------------------------------
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;

        // Check lantern in offhand
        if (!isEmmansLantern(player.getInventory().getItemInOffHand())) return;

        // Apply slowness retaliation
        attacker.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOWNESS,
                100,  // 5 seconds (20 ticks/sec)
                2      // Amplifier 2 = Slowness 3
        ));

        // Visual feedback
        player.getWorld().spawnParticle(Particle.NOTE,
                attacker.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);

        // Sound effect
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 0.3f, 1.2f);
    }

}
