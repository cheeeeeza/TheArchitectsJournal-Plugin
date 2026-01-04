package net.chae.TheArchitectsJournal.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.*;
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
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
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
        startWandProtectionTask();

    }

    public void registerRecipes() {
        registerYevasWandRecipe();
        registerYevasWandRecipeComplete();
    }

    // -- ITEM ACHIEVEMENT -----------------------------------------------------------------
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

    // --ITEM RECIPES----------------------------------------------------------------------------
    public void registerYevasWandRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(YEVASWAND_RECIPE_KEY, YevasWand());
        recipe.shape(
                "N",
                "R",
                "T");
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

    // --ITEM CREATION------------------------------------------------------------------------
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

    // -- ITEM SPECIAL EFFECTS ------------------------------------------------------------------
    public void giveYevasWandEffect(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.9f, 1.2f);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.4f, 1.6f);
        player.playSound(player.getLocation(), Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM, 0.6f, 1.4f);
        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1f, 0.6f);
        player.playSound(player.getLocation(), Sound.AMBIENT_BASALT_DELTAS_MOOD, 1f, 1f);
        player.playSound(player.getLocation(), Sound.AMBIENT_CRIMSON_FOREST_MOOD, 1f, 0.9f);

        player.showTitle(Title.title(
                Component.text("The Final Show").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Catalyst of Chaos - ").color(NamedTextColor.DARK_GRAY)
        ));
    }

    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack newOffhand = event.getOffHandItem();
        if (isYevasWand(newOffhand)) {
            giveYevasWandEffect(player);
        }
    }

    // LEVITATION -------------------------------------------------------------------------------
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (isYevasWand(hand)) {
            startLevitationEffect(player); // Base effects
        }
    }

    // offhand disable levi
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

    // IMMORTALITY CURSE ---------------------------------------------------------------------------

    // Tracks last time each player used the immortality effect
    private final Map<UUID, Long> lastTotemUse = new HashMap<>();
    // 10 minutes in milliseconds
    private static final long TOTEM_COOLDOWN = 3L * 60L * 1000L;

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
            player.sendActionBar(Component.text("§9✦ Immortality is a Curse, not a Blessing §9✦", NamedTextColor.DARK_GRAY));
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

        player.sendActionBar(Component.text("§9✦ Death has Refused You §9✦", NamedTextColor.DARK_GRAY));

    }

    // RABBIT TURNING EFFECT -----------------------------------------------------------------------------

    private final Map<UUID, PlayerData> transformedPlayers = new HashMap<>();

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

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;

        ItemStack mainHand = attacker.getInventory().getItemInMainHand();
        if (!isYevasWand(mainHand)) return;

        if (event.getEntity() instanceof Player victim) {
            CursePlayer(victim, attacker, event);  // Players get potion effects
        } else if (event.getEntity() instanceof LivingEntity mob) {
            rabbitTransformMob(mob, attacker, event);    // Mobs spawn rabbits
        }
    }

    private Location findSafeLocation(World world, Location target, int maxScan) {
        Location safeLoc = target.clone();

        for (int yOffset = -maxScan; yOffset <= maxScan; yOffset++) {
            safeLoc.setY(target.getY() + yOffset);
            if (safeLoc.getBlock().getType().isSolid() &&
                    safeLoc.clone().add(0, 1, 0).getBlock().getType().isAir() &&
                    safeLoc.clone().add(0, 2, 0).getBlock().getType().isAir()) {
                return safeLoc.clone().add(0, 1, 0);
            }
        }
        return target;
    }

    private void CursePlayer(Player victim, Player attacker, EntityDamageByEntityEvent event) {
        if (Math.random() < 0.5) {
            event.setCancelled(true);

            // Store original stats
            double originalHealth = victim.getHealth();
            int originalFood = victim.getFoodLevel();
            float originalSpeed = victim.getWalkSpeed();
            Location originalLoc = victim.getLocation().clone();

            // RABBIT CURSE TITLE + IMMEDIATE TELEPORT
            victim.showTitle(
                    Title.title(
                            Component.text("POOF!").color(NamedTextColor.RED),
                            Component.text("Where'd you go?").color(NamedTextColor.WHITE)
                    ));

            // IMMEDIATE RANDOM TELEPORT within 167 block radius
            double angle = Math.random() * 2 * Math.PI;
            double distance = Math.random() * 167;
            Location teleportLoc = originalLoc.clone().add(
                    Math.cos(angle) * distance,
                    0,
                    Math.sin(angle) * distance
            );

            // Find safe Y level
            World world = victim.getWorld();
            teleportLoc = findSafeLocation(world, teleportLoc, 20);
            victim.teleport(teleportLoc);

            // Apply bunny debuffs for 5 seconds (shorter duration)
            victim.setMaxHealth(3.0);
            victim.setHealth(3.0);
            victim.setFoodLevel(10);
            victim.setWalkSpeed(0.25f);

            victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 100, 4));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));

            // REVERT debuffs after 5 seconds
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!victim.isOnline()) return;

                    // Restore original stats
                    victim.setMaxHealth(20.0);
                    victim.setHealth(Math.min(20.0, originalHealth));
                    victim.setFoodLevel(originalFood);
                    victim.setWalkSpeed(originalSpeed);
                    victim.removePotionEffect(PotionEffectType.JUMP_BOOST);
                    victim.removePotionEffect(PotionEffectType.SLOWNESS);
                    victim.removePotionEffect(PotionEffectType.WEAKNESS);
                }
            }.runTaskLater(plugin, 100L);  // 5 seconds

            // Teleport + bunny sounds
            victim.getWorld().playSound(teleportLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.2f);
            victim.getWorld().playSound(originalLoc, Sound.ENTITY_CHICKEN_EGG, 1f, 0.5f);
            victim.getWorld().spawnParticle(Particle.PORTAL, teleportLoc, 50, 1, 1, 1, 0.5);
        }
    }

    //MOB PLAYER CURSE ------------------------------------------------------------------------------------

    private NamespacedKey rabbitKey;
    private final Map<UUID, EntityRevertData> mobReverts = new HashMap<>();

    private void rabbitTransformMob(LivingEntity mob, Player attacker, EntityDamageByEntityEvent event) {
        if (Math.random() < 0.5) {
            event.setCancelled(true);

            Location loc = mob.getLocation();
            World world = mob.getWorld();

            // saving mob data
            boolean wasTamed = false;
            UUID ownerUUID = null;
            if (mob instanceof Tameable tameable) {
                wasTamed = tameable.isTamed();
                if (wasTamed) ownerUUID = tameable.getOwnerUniqueId();
            }

            // Store for revert
            mobReverts.put(mob.getUniqueId(), new EntityRevertData(mob, loc, wasTamed, ownerUUID));
            mob.remove();

            Rabbit rabbit = (Rabbit) world.spawnEntity(loc, EntityType.RABBIT);
            rabbit.getPersistentDataContainer().set(rabbitKey, PersistentDataType.STRING, mob.getUniqueId().toString());
            rabbit.setRabbitType(Rabbit.Type.SALT_AND_PEPPER);
            rabbit.setHealth(3.0);

            // **REVERT AFTER 10s - PRESERVES TAMING**
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!rabbit.isDead()) {
                        revertMob(mob.getUniqueId(), loc);
                    }
                }
            }.runTaskLater(plugin, 200L);
        }
    }

    // revert ent data
    private static class EntityRevertData {
        final LivingEntity original;
        final Location location;
        final boolean wasTamed;
        final UUID ownerUUID;

        EntityRevertData(LivingEntity original, Location location, boolean wasTamed, UUID ownerUUID) {
            this.original = original;
            this.location = location;
            this.wasTamed = wasTamed;
            this.ownerUUID = ownerUUID;
        }
    }

    private void revertMob(UUID originalUUID, Location loc) {
        EntityRevertData data = mobReverts.remove(originalUUID);
        if (data != null) {
            Rabbit rabbit = getRabbitByUUID(originalUUID);
            if (rabbit != null && !rabbit.isDead()) {
                rabbit.remove();

                World world = loc.getWorld();
                LivingEntity original = (LivingEntity) world.spawnEntity(loc, data.original.getType());
                original.setHealth(data.original.getHealth());

                // taming restoration kept
                if (data.wasTamed && data.ownerUUID != null && original instanceof Tameable tameable) {
                    tameable.setTamed(true);
                    tameable.setOwner(Bukkit.getOfflinePlayer(data.ownerUUID));
                }
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

    // KILLER RABBIT  & NETHER MOBS & PHANTOM IMMUNITY -------------------------------------------------------------

    private final Map<UUID, Boolean> wandHolders = new HashMap<>();

    private void startWandProtectionTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {

                ItemStack offhand = player.getInventory().getItemInOffHand();
                ItemStack mainhand = player.getInventory().getItemInMainHand();
                boolean hasWand = isYevasWand(offhand) || isYevasWand(mainhand);
                wandHolders.put(player.getUniqueId(), hasWand);

                if (!hasWand) continue;

                for (Entity nearby : player.getNearbyEntities(67, 67, 67)) {
                    if (!(nearby instanceof Mob mob)) continue;

                    if (player.equals(mob.getTarget())) {
                        mob.setTarget(null);

                        if (mob instanceof Phantom ||
                                mob instanceof Blaze ||
                                mob instanceof Ghast ||
                                mob instanceof WitherSkeleton ||
                                mob instanceof PiglinBrute ||
                                mob instanceof PigZombie ||
                                mob instanceof MagmaCube ||
                                mob instanceof Enderman ||
                                mob instanceof Skeleton ||
                                (mob instanceof Rabbit rabbit && rabbit.getRabbitType() == Rabbit.Type.THE_KILLER_BUNNY)) {

                            mob.setTarget(null);
                        }
                    }
                }
            }
        }, 0L, 1L);
    }


    // WAND CHECKER --------------------------------------------------------------------------------------------
    private boolean isYevasWand(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(Component.text("Yeva's Wand", NamedTextColor.DARK_GRAY));
    }

}