package net.chae.TheArchitectsJournal.items;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
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
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("UnstableAPIUsage")

public class SerinasCloak implements Listener {
    private final JavaPlugin plugin;
    private final Set<UUID> invisiblePlayers = new HashSet<>();

    private NamespacedKey SERINASCLOAK_RECIPE_KEY;
    private NamespacedKey SERINASCLOAKCOMPLETE_RECIPE_KEY;

    public SerinasCloak(JavaPlugin plugin) {
        this.plugin = plugin;
        this.SERINASCLOAK_RECIPE_KEY = new NamespacedKey(plugin, "serinascloak_recipe");
        this.SERINASCLOAKCOMPLETE_RECIPE_KEY = new NamespacedKey(plugin, "serinascloakcomplete_recipe");

        // 15 second invisibility cycle
        new BukkitRunnable() {
            final Map<UUID, Long> blinkStartTime = new HashMap<>();

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!isSerinasCloak(player.getInventory().getHelmet())) {
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        blinkStartTime.remove(player.getUniqueId());
                        continue;
                    }

                    UUID uuid = player.getUniqueId();
                    long now = System.currentTimeMillis();

                    if (!blinkStartTime.containsKey(uuid)) {
                        blinkStartTime.put(uuid, now);
                    }

                    long cycleStart = blinkStartTime.get(uuid);
                    long elapsed = now - cycleStart;

                    if (elapsed < 12000) { // 12s invisible
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0, true, false));
                    } else if (elapsed < 15000) { // 3s visible
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.01);
                    } else {
                        blinkStartTime.put(uuid, now); // Reset cycle
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
        //register recipes
        public void registerRecipes() {
        registerSerinasCloakRecipe();
        registerSerinasCloakRecipeComplete();
    }

    //ACHIEVEMENT ----------------------------------------------------------------------------------
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

    // RECIPES -------------------------------------------------------------------------------------
    public void registerSerinasCloakRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(SERINASCLOAK_RECIPE_KEY, SerinasCloak());
        recipe.shape(
                "C C",
                "CEC",
                "CPC");
        recipe.setIngredient('C', Material.LEATHER);
        recipe.setIngredient('E', Material.ENDER_EYE);
        recipe.setIngredient('P', Material.POTION);
        Bukkit.addRecipe(recipe);
    }

    public void registerSerinasCloakRecipeComplete() {
        ShapelessRecipe recipe = new ShapelessRecipe(SERINASCLOAKCOMPLETE_RECIPE_KEY, SerinasCloakComplete());
        recipe.addIngredient(new RecipeChoice.ExactChoice(SerinasCloak()));
        recipe.addIngredient(Material.NETHER_STAR);
        Bukkit.addRecipe(recipe);
    }

    // ITEMS ---------------------------------------------------------------------------------------
    public ItemStack SerinasCloak() {
        ItemStack cloak = ItemStack.of(Material.STICK);
        cloak.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "serinas_cloak"));
        cloak.setData(DataComponentTypes.ITEM_NAME, Component.text("Serina's Cloak", NamedTextColor.WHITE));
        cloak.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HEAD).build());
        cloak.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return cloak;
    }

    public ItemStack SerinasCloakComplete() {
        ItemStack cloak = ItemStack.of(Material.STICK);
        cloak.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "serinas_cloak"));
        cloak.setData(DataComponentTypes.ITEM_NAME, Component.text("Serina's Cloak", NamedTextColor.DARK_GREEN));
        cloak.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HEAD).build());
        cloak.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        cloak.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return cloak;
    }

    // TITLE EFFECT --------------------------------------------------------------------------------
    public void giveSerinasCloakEffect(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1f, 0.9f);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.9f);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.4f, 0.8f);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 0.4f, 0.8f);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.3f, 1.2f);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.3f, 1.2f);
        player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_AMBIENT_OMINOUS, 0.3f, 1.2f);


        player.showTitle(Title.title(
                Component.text("The Final Omen").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Harrowing Harbinger - ").color(NamedTextColor.DARK_GREEN)
        ));
    }

    @EventHandler
    public void onHelmetEquip(PlayerArmorChangeEvent event) {
        if (event.getSlotType() != PlayerArmorChangeEvent.SlotType.HEAD) return;
        ItemStack newItem = event.getNewItem();
        if (newItem == null) return;

        Component itemName = newItem.getData(DataComponentTypes.ITEM_NAME);
        if (itemName != null && itemName.equals(Component.text("Serina's Cloak", NamedTextColor.DARK_GREEN))) {
            giveSerinasCloakEffect(event.getPlayer());
        }
    }

    //TELEPORTATION -------------------------------------------------------------------------------

    private final Map<UUID, Location> lastTeleportLocation = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {


        Player player = event.getPlayer();
        if (!isSerinasCloak(player.getInventory().getHelmet())) return;

        Action action = event.getAction();
        Location loc = player.getLocation();
        World world = player.getWorld();

        // **LEFT CLICK** - Random 5-20 blocks forward
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            double distance = 5 + Math.random() * 15; // 5-20 range
            Location target = loc.clone().add(loc.getDirection().multiply(distance));
            target.setY(findSafeY(world, target));
            player.teleport(target);
            world.spawnParticle(Particle.PORTAL, loc, 20, 0.5, 0.5, 0.5, 0.2);
            world.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 1.5f);
            player.sendActionBar(Component.text("§a✦ The Future... §a✦", NamedTextColor.DARK_GREEN));
        }

        // **RIGHT CLICK** - Shift = Return to previous position
        else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                // **RETURN TO PREVIOUS POSITION**
                Location previous = lastTeleportLocation.get(player.getUniqueId());
                if (previous != null) {
                    player.teleport(previous);
                    world.spawnParticle(Particle.PORTAL, loc, 50, 1, 1, 1, 0.3);
                    world.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.2f);
                    player.sendActionBar(Component.text("§a✦ Returned to the Past §a✦", NamedTextColor.DARK_GREEN));
                } else {
                    player.sendActionBar(Component.text("§7No timeline stored", NamedTextColor.DARK_GREEN));
                }
            } else {
                // **ORIGINAL RANDOM TELEPORT** (no shift)
                double distance = 25 + Math.random() * 30; // 25-55 range
                Location target = loc.clone().add(loc.getDirection().multiply(distance));
                target.setY(findSafeY(world, target));

                // **STORE CURRENT POSITION** before teleport
                lastTeleportLocation.put(player.getUniqueId(), loc.clone());

                player.teleport(target);
                world.spawnParticle(Particle.PORTAL, loc, 50, 1, 1, 1, 0.7);
                world.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.8f);
                player.sendActionBar(Component.text("§b✦ The Past has been Marked §b✦", NamedTextColor.DARK_GREEN));
            }
        }
    }

    //safety net for tp
    private double findSafeY(World world, Location target) {
        // Scan up/down 20 blocks for safe landing
        for (int y = -20; y <= 20; y++) {
            Location check = target.clone();
            check.setY(target.getY() + y);
            if (check.getBlock().getType().isSolid() &&
                    check.clone().add(0, 1, 0).getBlock().getType().isAir() &&
                    check.clone().add(0, 2, 0).getBlock().getType().isAir()) {
                return check.getY() + 1;
            }
        }
        return target.getY(); // Fallback
    }

    //ENDERMAN IMMUNITY -----------------------------------------------------------------------------
    @EventHandler
    public void onEndermanTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;

        // Check cloak in helmet slot
        if (isSerinasCloak(player.getInventory().getHelmet())) {
            if (event.getEntity() instanceof Enderman) {
                event.setCancelled(true);
            }
        }
    }

    // CLOAK CHECKER --------------------------------------------------------------------------------
    private boolean isSerinasCloak(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(Component.text("Serina's Cloak", NamedTextColor.DARK_GREEN));
    }

}
