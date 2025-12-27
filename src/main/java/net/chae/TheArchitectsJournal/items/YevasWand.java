package net.chae.TheArchitectsJournal.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;  // For rabbit.getPersistentDataContainer()
import org.bukkit.NamespacedKey;


import java.time.Duration;
import java.util.*;

@SuppressWarnings("UnstableAPIUsage")
public class YevasWand implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, Integer> stepCount = new HashMap<>();

    private NamespacedKey YEVASWAND_RECIPE_KEY;
    private NamespacedKey YEVASWANDCOMPLETE_RECIPE_KEY;

    public YevasWand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.YEVASWAND_RECIPE_KEY = new NamespacedKey(plugin, "yevaswand_recipe");
        this.YEVASWANDCOMPLETE_RECIPE_KEY = new NamespacedKey(plugin, "yevaswandcomplete_recipe");
        this.rabbitKey = new NamespacedKey(plugin, "original_uuid");
    }

    public void registerRecipes() {
        registerYevasWandRecipe();
        registerYevasWandRecipeComplete();
    }

    // -- ITEM ACHIEVEMENT --
    @EventHandler
    public void onWandCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack result = event.getRecipe().getResult();
        if (result == null || !result.isSimilar(YevasWandComplete())) return;
        grantWandAdvancement(player);
    }

    private void grantWandAdvancement(Player player) {
        NamespacedKey key = new NamespacedKey("chae", "custom/obtain_wand");
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                progress.awardCriteria("has_wand");
            }
        }
    }

    // --ITEM RECIPES--
    public void registerYevasWandRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(YEVASWAND_RECIPE_KEY, YevasWand());
        recipe.shape(
                " N ",
                " R ",
                " T ");
        recipe.setIngredient('N', Material.BEACON);
        recipe.setIngredient('R', Material.RABBIT_FOOT);
        recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
        Bukkit.addRecipe(recipe);
    }

    public void registerYevasWandRecipeComplete() {
        ShapelessRecipe recipe = new ShapelessRecipe(YEVASWANDCOMPLETE_RECIPE_KEY, YevasWandComplete());
        recipe.addIngredient(new RecipeChoice.ExactChoice(YevasWand()));
        recipe.addIngredient(Material.NETHER_STAR);
        Bukkit.addRecipe(recipe);
    }

    // --ITEM CREATION--
    public ItemStack YevasWand() {
        ItemStack wand = ItemStack.of(Material.STICK);
        wand.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "yevas_wand"));
        wand.setData(DataComponentTypes.ITEM_NAME, Component.text("Yeva's Wand", NamedTextColor.WHITE));
        wand.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());
        wand.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return wand;
    }

    public ItemStack YevasWandComplete() {
        ItemStack wand = ItemStack.of(Material.STICK);
        wand.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "yevas_wand"));
        wand.setData(DataComponentTypes.ITEM_NAME, Component.text("Yeva's Wand", NamedTextColor.DARK_GRAY));
        wand.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        wand.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());
        wand.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return wand;
    }

    // -- ITEM SPECIAL EFFECTS --
    public void giveYevasWandEffect(Player player) {
        player.playSound(player.getLocation(), "minecraft:entity.phantom.ambient", 1f, 1f);
        player.playSound(player.getLocation(), Sound.BLOCK_BASALT_BREAK, 0.3f, 0.7f);       // Rock cracks
        player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 0.4f, 0.9f);           // Lava bubbles
        player.playSound(player.getLocation(), Sound.ENTITY_MAGMA_CUBE_SQUISH, 0.2f, 1.1f); // Magma squish
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.1f, 0.6f);   // Distant boom
        player.showTitle(Title.title(
                Component.text("The Final Show").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Catalyst of Chaos - ").color(NamedTextColor.DARK_GRAY)
        ));
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (isYevasWand(offhand)) {
            startLevitationEffect(player); // Base effects
        }
    }

    // **OFFHAND EQUIP** - FIXED!
    @EventHandler
    public void onOffhandEquip(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getRawSlot() != 45) return; // Offhand slot

        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack offhand = player.getInventory().getItemInOffHand();
                if (isYevasWand(offhand)) {
                    stepCount.put(player.getUniqueId(), 0); // Reset steps
                    startLevitationEffect(player);
                    giveYevasWandEffect(player);
                } else {
                    stopLevitationEffect(player);
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Stop if wand not held
        if (!isYevasWand(player.getInventory().getItemInOffHand())) {
            stopLevitationEffect(player);
            return;
        }

        // Stop if sneaking
        if (player.isSneaking()) {
            stopLevitationEffect(player);
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;

        // Only count horizontal movement
        if (Math.abs(to.getX() - from.getX()) < 0.1 &&
                Math.abs(to.getZ() - from.getZ()) < 0.1) {
            return;
        }

        int steps = stepCount.getOrDefault(uuid, 0) + 1;
        stepCount.put(uuid, steps);

        // Every 5 steps = stronger levitation
        if (steps % 5 == 0) {
            int level = Math.min(1, steps / 5);

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.LEVITATION,
                    100, // 1 second
                    level,
                    false,
                    false
            ));

            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.SPEED,
                    20,   // 1 second
                    1,    // Speed II
                    false,
                    false
            ));

            // sound effect
            if (steps % 40 == 0) {
                player.playSound(player.getLocation(),
                        Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 0.5f, 2f);
            }
        }
    }

    private void startLevitationEffect(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 8, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 3, 1, false, false));
    }

    private void stopLevitationEffect(Player player) {
        stepCount.remove(player.getUniqueId());
        player.removePotionEffect(PotionEffectType.LEVITATION);
        //player.removePotionEffect(PotionEffectType.SLOW_FALLING);
    }

    //immortality curse

    // Tracks last time each player used the immortality effect
    private final Map<UUID, Long> lastTotemUse = new HashMap<>();
    // 10 minutes in milliseconds
    private static final long TOTEM_COOLDOWN = 10L * 60L * 1000L;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ItemStack offhand = player.getInventory().getItemInOffHand();

        if (!isYevasWand(offhand)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long lastUse = lastTotemUse.getOrDefault(uuid, 0L);

        // Cooldown check (10 minutes)
        if (now - lastUse < TOTEM_COOLDOWN) {
            long remaining = (TOTEM_COOLDOWN - (now - lastUse)) / 1000;
            player.sendMessage(Component.text("Immortality's a curse! Not a Blessing. Did you really think it'll save you? HAHAHAHHAHAHAHA (" + remaining + "s)")
                    .color(NamedTextColor.RED));
            return; // Let them die
        }

        //grant wand death achievement
        NamespacedKey key = new NamespacedKey("chae", "wand/wand_death");
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                progress.awardCriteria("wand_death");
            }
        }

        // death cancelled
        event.setCancelled(true);  // Prevents death entirely
        lastTotemUse.put(uuid, now);

        // resseruction totem effects
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 900, 1)); // 45s Regen II
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 1));   // 5s Absorption II

        // invisibility
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0)); // 10s invis

        // Spawn 67 rabbits
        Location loc = player.getLocation();
        World world = player.getWorld();

        for (int i = 0; i < 67; i++) {
            Location spawnLoc = loc.clone().add(
                    (Math.random() - 0.5) * 6,  // -3 to +3 X
                    0,                          // Same Y
                    (Math.random() - 0.5) * 6   // -3 to +3 Z
            );

            Rabbit rabbit = (Rabbit) world.spawnEntity(spawnLoc, EntityType.RABBIT);
            rabbit.setRabbitType(i % 2 == 0 ? Rabbit.Type.BLACK : Rabbit.Type.THE_KILLER_BUNNY);
        }

        // Visual/audio effects for resseruction
        world.spawnParticle(Particle.TOTEM_OF_UNDYING, loc, 50, 1, 1, 1, 0.5);
        world.spawnParticle(Particle.SCRAPE, loc, 100, 1.5, 1.5, 1.5, 0.05);
        world.spawnParticle(Particle.SQUID_INK, loc, 25, 2, 2, 2, 0.1);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1f, 1f);
        world.playSound(loc, Sound.ENTITY_ENDERMAN_SCREAM, 0.5f, 0.8f);

        player.sendMessage("You think you can simply die?");

    }

    // turn into rabbit effect

    private final Map<UUID, PlayerData> transformedPlayers = new HashMap<>();

    private NamespacedKey rabbitKey;

    private void restoreOriginal(UUID victimUUID, Location loc) {
        PlayerData data = transformedPlayers.remove(victimUUID);
        if (data != null && data.isPlayer) {
            Player player = Bukkit.getPlayer(victimUUID);
            if (player != null) {
                player.teleport(loc);
                player.setHealth(data.health);
                player.setFoodLevel(data.foodLevel);
            }
        }
    }

    private static class PlayerData {
        final double health;
        final int foodLevel;  // Only for players
        final boolean isPlayer;
        final UUID originalUUID;  // Store UUID properly

        PlayerData(double health, int foodLevel, boolean isPlayer, UUID originalUUID) {
            this.health = health;
            this.foodLevel = foodLevel;
            this.isPlayer = isPlayer;
            this.originalUUID = originalUUID;
        }
    }

    private final Map<UUID, EntityRevertData> mobReverts = new HashMap<>();

    private static class EntityRevertData {
        final LivingEntity original;
        final Location location;

        EntityRevertData(LivingEntity original, Location location) {
            this.original = original;
            this.location = location;
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;

        ItemStack mainHand = attacker.getInventory().getItemInMainHand();
        if (!isYevasWand(mainHand)) return;

        if (event.getEntity() instanceof Player victim) {
            rabbitCursePlayer(victim, attacker, event);  // Players get potion effects
        } else if (event.getEntity() instanceof LivingEntity mob) {
            rabbitTransformMob(mob, attacker, event);    // Mobs spawn rabbits
        }
    }

    private void rabbitCursePlayer(Player victim, Player attacker, EntityDamageByEntityEvent event) {
        if (Math.random() < 0.5) {
            event.setCancelled(true);

            // RABBIT CURSE TITLE
            victim.showTitle(
                    Title.title(
                            Component.text("Zimzalabim!").color(NamedTextColor.RED),
                            Component.text("Hop away little bunny!").color(NamedTextColor.WHITE)
                    ));

            // PLAYER RABBIT STATS
            victim.setMaxHealth(3.0);
            victim.setHealth(3.0);
            victim.setFoodLevel(10);
            victim.setWalkSpeed(0.25f);

            victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 200, 4));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 0));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));

            // REVERT with title
            new BukkitRunnable() {
                @Override
                public void run() {
                    victim.showTitle(
                            Title.title(
                                    Component.text("Poof!").color(NamedTextColor.GREEN),
                                    Component.text("No more hopping!").color(NamedTextColor.GRAY)
                            )
                    );

                    victim.setMaxHealth(20.0);
                    victim.setHealth(Math.min(20.0, victim.getHealth() * (20.0/3.0)));
                    victim.setFoodLevel(20);
                    victim.setWalkSpeed(0.2f);
                    victim.removePotionEffect(PotionEffectType.JUMP_BOOST);
                    victim.removePotionEffect(PotionEffectType.SLOWNESS);
                    victim.removePotionEffect(PotionEffectType.WEAKNESS);
                }
            }.runTaskLater(plugin, 200L);

            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1f, 0.5f);
        }
    }

    private void rabbitTransformMob(LivingEntity mob, Player attacker, EntityDamageByEntityEvent event) {
        if (Math.random() < 0.5) {
            event.setCancelled(true);

            Location loc = mob.getLocation();
            World world = mob.getWorld();

            mob.remove();
            Rabbit rabbit = (Rabbit) world.spawnEntity(loc, EntityType.RABBIT);

            // STORE original UUID on rabbit
            rabbit.getPersistentDataContainer().set(rabbitKey, PersistentDataType.STRING, mob.getUniqueId().toString());
            rabbit.setRabbitType(Rabbit.Type.SALT_AND_PEPPER);
            rabbit.setHealth(3.0);

            // Auto-revert ONLY if rabbit alive
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!rabbit.isDead()) {
                        World revertWorld = loc.getWorld();
                        LivingEntity original = (LivingEntity) revertWorld.spawnEntity(loc, mob.getType());
                        original.setHealth(mob.getHealth());

                    }
                }
            }.runTaskLater(plugin, 200L);


        }
    }


    private void revertMob(UUID mobUUID, Location loc) {
        EntityRevertData data = mobReverts.remove(mobUUID);
        if (data != null) {
            // Find the rabbit by UUID (from PersistentDataContainer or tracker)
            Rabbit rabbit = getRabbitByUUID(mobUUID);

            // ONLY revert if rabbit still exists
            if (rabbit != null && !rabbit.isDead()) {
                World world = loc.getWorld();
                LivingEntity original = (LivingEntity) world.spawnEntity(loc, data.original.getType());
                original.setHealth(data.original.getHealth());

            }
        }
    }

    private Rabbit getRabbitByUUID(UUID originalUUID) {
        for (World world : Bukkit.getWorlds()) {
            for (Rabbit rabbit : world.getEntitiesByClass(Rabbit.class)) {
                // Check if this rabbit stores the original UUID
                if (rabbit.getPersistentDataContainer().has(rabbitKey, PersistentDataType.STRING)) {
                    String storedUUID = rabbit.getPersistentDataContainer().get(rabbitKey, PersistentDataType.STRING);
                    if (storedUUID.equals(originalUUID.toString())) {
                        return rabbit;
                    }
                }
            }
        }
        return null;
    }


    // wand checker
    private boolean isYevasWand(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(Component.text("Yeva's Wand", NamedTextColor.DARK_GRAY));
    }

}

