package net.chae.TheArchitectsJournal.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import java.util.*;

@SuppressWarnings("UnstableAPIUsage")

public class LichtsArmlet implements Listener {
    private final JavaPlugin plugin;

    private NamespacedKey LICHTSARMLET_RECIPE_KEY;
    private NamespacedKey LICHTSARMLETCOMPLETE_RECIPE_KEY;

    public LichtsArmlet(JavaPlugin plugin) {
        this.plugin = plugin;
        this.LICHTSARMLET_RECIPE_KEY = new NamespacedKey(plugin, "lichtsarmlet_recipe");
            this.LICHTSARMLETCOMPLETE_RECIPE_KEY = new NamespacedKey(plugin, "lichtsarmletcomplete_recipe");

        startOceanProtectionTask();

    }

    public void registerRecipes() {
        registerLichtsArmletRecipe();
        registerLichtsArmletRecipeComplete();
    }

    // -- ITEM ACHIEVEMENT -------------------------------------------------------------------------
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

    // --ITEM RECIPES------------------------------------------------------------------------------
    public void registerLichtsArmletRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(LICHTSARMLET_RECIPE_KEY, LichtsArmlet());
        recipe.shape(
                "GGG",
                "HHH",
                "GGG"
        );
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('H', Material.HEART_OF_THE_SEA);
        Bukkit.addRecipe(recipe);
    }

    public void registerLichtsArmletRecipeComplete() {
        ShapelessRecipe recipe = new ShapelessRecipe(LICHTSARMLETCOMPLETE_RECIPE_KEY, LichtsArmletComplete());
        recipe.addIngredient(new RecipeChoice.ExactChoice(LichtsArmlet()));
        recipe.addIngredient(Material.NETHER_STAR);
        Bukkit.addRecipe(recipe);
    }

    // --ITEM CREATION-----------------------------------------------------------------------------
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

        // name and colour
        armlet.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "lichts_armlet"));
        armlet.setData(DataComponentTypes.ITEM_NAME, Component.text("Licht's Armlet", NamedTextColor.GOLD));
        armlet.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        //equipment slot
        armlet.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());

        //item stack size
        armlet.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return armlet;
    }

    // -- ITEM SPECIAL EFFECTS --------------------------------------------------------------------
    public void giveLichtsArmletEffect(Player player) {
        Location loc = player.getLocation();

        // Ocean ambience
        player.playSound(loc, Sound.ENTITY_AXOLOTL_IDLE_WATER, 1f, 1.0f);
        player.playSound(loc, Sound.BLOCK_WATER_AMBIENT, 0.8f, 1.0f);
        player.playSound(loc, Sound.ENTITY_DOLPHIN_AMBIENT, 1f, 1.2f);
        player.playSound(loc, Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, 0.7f, 1.1f);
        player.playSound(loc, Sound.BLOCK_CONDUIT_AMBIENT, 0.6f, 0.9f);

        player.showTitle(Title.title(
                Component.text("Guidance of the Wind and Waves").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Angler of Sky and Sea - ").color(NamedTextColor.GOLD)
        ));
    }

    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack newOffhand = event.getOffHandItem();
        if (isLichtsArmlet(newOffhand)) {
            giveLichtsArmletEffect(player);
        }
    }

    // ITEM CHECKER --------------------------------------------------------------------------------
    private boolean isLichtsArmlet(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!item.hasData(DataComponentTypes.ITEM_NAME)) return false;

        Component name = item.getData(DataComponentTypes.ITEM_NAME);  // MOVE UP

        return name != null && name.equals(
                Component.text("Licht's Armlet", NamedTextColor.GOLD)
        );
    }

    //HOSTILE OCEAN MOBS NEUTRAL -------------------------------------------------------------------

    private final Map<UUID, Boolean> armletHolders = new HashMap<>();

    private void startOceanProtectionTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {

                // Check offhand armlet
                ItemStack offhand = player.getInventory().getItemInOffHand();
                boolean hasArmlet = isLichtsArmlet(offhand);
                armletHolders.put(player.getUniqueId(), hasArmlet);

                if (!hasArmlet) continue;

                // Constantly clear nearby ocean hostile targets
                for (Entity nearby : player.getNearbyEntities(67, 67, 67)) {
                    if (!(nearby instanceof Mob mob)) continue;

                    if (mob instanceof Drowned ||
                            mob instanceof Guardian ||
                            mob instanceof ElderGuardian) {

                        if (player.equals(mob.getTarget())) {
                            mob.setTarget(null);  // hard clear target [web:85]
                            mob.setAggressive(false);
                        }
                    }
                }
            }
        }, 0L, 3L); // every 0.25s
    }


    // SEA MASTERY ---------------------------------------------------------------------------------
    public void startLichtsArmletPassives() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {

                ItemStack hand = player.getInventory().getItemInOffHand();
                if (!isLichtsArmlet(hand)) continue;

                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.WATER_BREATHING, 60, 0, true, false
                ));
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.DOLPHINS_GRACE, 60, 0, true, false
                ));

            }
        }, 0L, 10L);
    }

    //AIR BUCKET CLUTCH AND BOOST ---------------------------------------------------------------------------

    private final Map<UUID, Long> glideCooldown = new HashMap<>();

    @EventHandler
    public void onCloudlineGlide(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInOffHand();

        if (!isLichtsArmlet(hand)) return;
        if (player.isOnGround()) return;
        if (!player.isSneaking()) return;

        long now = System.currentTimeMillis();
        if (glideCooldown.containsKey(player.getUniqueId())
                && now - glideCooldown.get(player.getUniqueId()) < 500) return;

        glideCooldown.put(player.getUniqueId(), now);

        Vector dir = player.getLocation().getDirection().multiply(0.8);
        dir.setY(0.35);
        player.setVelocity(dir);

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW_FALLING, 60, 0, false, false
        ));

        player.playSound(player.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, 1f, 1.2f);
    }

    //AIR NET-----------------------------------------------------------------------------------------------

    private final Map<UUID, Long> netCooldown = new HashMap<>();

    @EventHandler
    public void onSkyfishersNet(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInOffHand();

        if (!isLichtsArmlet(hand)) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR) return;

        long now = System.currentTimeMillis();
        if (netCooldown.containsKey(player.getUniqueId())
                && now - netCooldown.get(player.getUniqueId()) < 10000) {
            player.sendActionBar(Component.text("§9✦ Armlet Recovering... §9✦", NamedTextColor.GRAY));
            return;
        }

        netCooldown.put(player.getUniqueId(), now);

        Location center = player.getLocation()
                .add(player.getLocation().getDirection().multiply(8));

        List<LivingEntity> affectedMobs = new ArrayList<>();

        for (Entity entity : center.getWorld().getNearbyEntities(center, 6, 6, 6)) {
            if (!(entity instanceof LivingEntity le)) continue;
            if (entity.equals(player)) continue;

            affectedMobs.add(le);

            le.addPotionEffect(new PotionEffect(
                    PotionEffectType.LEVITATION, 100, 4
            ));
        }

        // Pull mobs together over 20 ticks (1 second), then slam down
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (task.getTaskId() % 10 == 0) {  // Every 0.5s update pull strength
                for (LivingEntity le : affectedMobs) {
                    if (le.isDead() || !le.isValid()) continue;

                    // Calculate direction to center
                    Vector toCenter = center.toVector().subtract(le.getLocation().toVector()).normalize();
                    // Stronger pull as time progresses
                    double pullStrength = Math.min(0.4 + (task.getTaskId() * 0.02), 0.8);
                    Vector pullVelocity = toCenter.multiply(pullStrength);
                    pullVelocity.setY(0.3);

                    le.setVelocity(pullVelocity);
                }
            }

            // At 40 ticks: Final slam down
            if (task.getTaskId() >= 40) {
                for (LivingEntity le : affectedMobs) {
                    if (le.isDead() || !le.isValid()) continue;
                    
                    le.removePotionEffect(PotionEffectType.LEVITATION);

                    le.setVelocity(new Vector(0, -2.6, 0));
                }
                task.cancel();
            }
        }, 0L, 1L);

        center.getWorld().spawnParticle(Particle.CLOUD, center, 60);
        player.playSound(center, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 0.8f);
    }

    // ULTIMATE (AIRNET BUT FOR PLAYERS) ----------------------------------------------------------

    private final Map<UUID, Long> ultimateCooldown = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.OFF_HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        event.setCancelled(true);
        activateUltimate(player);
    }

    public void activateUltimate(Player player) {
        ItemStack hand = player.getInventory().getItemInOffHand();
        if (!isLichtsArmlet(hand)) return;

        long now = System.currentTimeMillis();
        if (ultimateCooldown.containsKey(player.getUniqueId())
                && now - ultimateCooldown.get(player.getUniqueId()) < 30000) {
            player.sendActionBar(Component.text("§9✦ Armlet Recovering... §9✦", NamedTextColor.GRAY));
            return;
        }

        ultimateCooldown.put(player.getUniqueId(), now);

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.equals(player)) continue;
            if (target.getLocation().distance(player.getLocation()) > 12) continue;

            target.addPotionEffect(new PotionEffect(
                    PotionEffectType.LEVITATION, 40, 5
            ));

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                target.setVelocity(new Vector(0, -2.5, 0));
            }, 40L);
        }

        player.getWorld().playSound(
                player.getLocation(),
                Sound.ENTITY_LIGHTNING_BOLT_THUNDER,
                1f, 0.8f
        );
    }

}
