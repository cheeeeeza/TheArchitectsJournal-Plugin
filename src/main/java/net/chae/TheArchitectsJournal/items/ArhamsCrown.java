package net.chae.TheArchitectsJournal.items;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
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
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("UnstableAPIUsage")
public class ArhamsCrown implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, Long> sonicCooldowns = new HashMap<>();
    private final Map<UUID, Long> wardenCooldowns = new HashMap<>();

    private NamespacedKey ARHAMSCROWN_RECIPE_KEY;
    private NamespacedKey ARHAMSCROWNCOMPLETE_RECIPE_KEY;

    public ArhamsCrown(JavaPlugin plugin) {
        this.plugin = plugin;
        this.ARHAMSCROWN_RECIPE_KEY = new NamespacedKey(plugin, "arhamscrown_recipe");
        this.ARHAMSCROWNCOMPLETE_RECIPE_KEY = new NamespacedKey(plugin, "arhamscrowncomplete_recipe");
    }

    public void registerRecipes() {
        registerArhamsCrownRecipe();
        registerArhamsCrownRecipeComplete();
    }

    // ACHIEVEMENT - Crown craft
    @EventHandler
    public void onCrownCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack result = event.getRecipe().getResult();
        if (result == null || !result.isSimilar(ArhamsCrownComplete())) return;
        grantCrownAdvancement(player);
    }

    private void grantCrownAdvancement(Player player) {
        NamespacedKey key = new NamespacedKey("chae", "custom/obtain_crown");
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                progress.awardCriteria("has_crown");
            }
        }
    }

    // RECIPES
    public void registerArhamsCrownRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(ARHAMSCROWN_RECIPE_KEY, ArhamsCrown());
        recipe.shape(" E ", "ENE");
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('E', Material.ECHO_SHARD);
        Bukkit.addRecipe(recipe);
    }

    public void registerArhamsCrownRecipeComplete() {
        ShapelessRecipe recipe = new ShapelessRecipe(ARHAMSCROWNCOMPLETE_RECIPE_KEY, ArhamsCrownComplete());
        recipe.addIngredient(new RecipeChoice.ExactChoice(ArhamsCrown()));
        recipe.addIngredient(Material.NETHER_STAR);
        Bukkit.addRecipe(recipe);
    }

    // ITEMS
    public ItemStack ArhamsCrown() {
        ItemStack crown = ItemStack.of(Material.STICK);
        crown.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "arhams_crown"));
        crown.setData(DataComponentTypes.ITEM_NAME, Component.text("Arham's Crown", NamedTextColor.WHITE));
        crown.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HEAD).build());
        crown.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return crown;
    }

    public ItemStack ArhamsCrownComplete() {
        ItemStack crown = ItemStack.of(Material.STICK);

        // Attributes (FIXED NamespacedKey - uses plugin instance)
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
        builder.addModifier(Attribute.MAX_HEALTH,
                new AttributeModifier(new NamespacedKey(plugin, "max_health"), 20,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD));
        builder.addModifier(Attribute.ATTACK_KNOCKBACK,
                new AttributeModifier(new NamespacedKey(plugin, "attack_knockback"), 1.5,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD));
        builder.addModifier(Attribute.ENTITY_INTERACTION_RANGE,
                new AttributeModifier(new NamespacedKey(plugin, "entity_interaction_range"), 2,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD));
        builder.addModifier(Attribute.KNOCKBACK_RESISTANCE,
                new AttributeModifier(new NamespacedKey(plugin, "knockback_resistance"), 1,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD));
        crown.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());

        crown.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "arhams_crown"));
        crown.setData(DataComponentTypes.ITEM_NAME, Component.text("Arham's Crown", NamedTextColor.DARK_AQUA));
        crown.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        crown.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HEAD).build());
        crown.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        return crown;
    }

    // EFFECTS
    public void giveArhamsCrownEffect(Player player) {
        player.playSound(player.getLocation(), "minecraft:entity.warden.emerge", 1f, 1f);
        player.showTitle(Title.title(
                Component.text("The Coronation").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Forsaken Prince - ").color(NamedTextColor.DARK_AQUA)
        ));
    }

    @EventHandler
    public void onHelmetEquip(PlayerArmorChangeEvent event) {
        if (event.getSlotType() != PlayerArmorChangeEvent.SlotType.HEAD) return;
        ItemStack newItem = event.getNewItem();
        if (newItem == null) return;

        Component itemName = newItem.getData(DataComponentTypes.ITEM_NAME);
        if (itemName != null && itemName.equals(Component.text("Arham's Crown", NamedTextColor.DARK_AQUA))) {
            giveArhamsCrownEffect(event.getPlayer());
        }
    }

    // CROWN CHECKER
    private boolean isArhamsCrown(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(Component.text("Arham's Crown", NamedTextColor.DARK_AQUA));
    }

    // SONIC BOOM
    @EventHandler
    public void onPlayerPunch(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;

        UUID uuid = attacker.getUniqueId();
        long now = System.currentTimeMillis();
        if (sonicCooldowns.getOrDefault(uuid, 0L) > now - 10000) return;

        ItemStack helmet = attacker.getInventory().getHelmet();
        if (!isArhamsCrown(helmet) || !attacker.getInventory().getItemInMainHand().getType().isAir()) return;

        sonicCooldowns.put(uuid, now);
        event.setCancelled(true);
        doSonicBoom(attacker, event.getEntity());
    }

    private void doSonicBoom(Player player, Entity targetEntity) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        if (world == null || !(targetEntity instanceof LivingEntity victim) || victim == player) return;

        Location playerLoc = player.getLocation();
        Location victimLoc = victim.getLocation();
        Vector direction = victimLoc.toVector().subtract(playerLoc.toVector()).normalize();
        double distance = playerLoc.distance(victimLoc);
        int segments = (int) Math.max(5, distance * 3);

        for (int i = 1; i <= segments; i++) {
            double progress = (double) i / segments;
            Location segmentLoc = playerLoc.clone().add(direction.clone().multiply(distance * progress));
            world.spawnParticle(Particle.SONIC_BOOM, segmentLoc, 3, 0.1, 1.0, 0.1, 0.05);
        }

        world.playSound(playerLoc, Sound.ENTITY_WARDEN_SONIC_BOOM, 2f, 1f);
        victim.damage(15, player);
        victim.setVelocity(direction.multiply(2.0).setY(0.5));
    }

    // WARDEN SPAWN
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player) || !(event.getDamager() instanceof Player attacker)) return;

        if (!isArhamsCrown(player.getInventory().getHelmet())) return;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (wardenCooldowns.getOrDefault(uuid, 0L) > now - 10000) return;

        wardenCooldowns.put(uuid, now);
        spawnWarden(player, attacker);
    }

    @EventHandler
    public void onWardenTarget(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof Warden) || !(event.getTarget() instanceof Player player)) return;

        if (isArhamsCrown(player.getInventory().getHelmet())) {
            event.setCancelled(true);
        }
    }

    private void spawnWarden(Player wearer, Player attacker) {
        Location loc = attacker.getLocation();
        World world = loc.getWorld();
        if (world == null) return;

        Warden warden = (Warden) world.spawnEntity(loc, EntityType.WARDEN);
        warden.setTarget(attacker);
        world.playSound(loc, Sound.ENTITY_WARDEN_EMERGE, 1f, 1f);
        world.spawnParticle(Particle.SQUID_INK, loc, 50, 1, 2, 1, 0.1);
    }
}
