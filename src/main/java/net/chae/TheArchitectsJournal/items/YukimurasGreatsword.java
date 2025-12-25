package net.chae.TheArchitectsJournal.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.format.TextColor.color;


@SuppressWarnings("UnstableAPIUsage")

public class YukimurasGreatsword implements Listener {
    private final JavaPlugin plugin;

    private NamespacedKey YUKIMURASGREATSWORD_RECIPE_KEY;
    private NamespacedKey YUKIMURASGREATSWORDCOMPLETE_RECIPE_KEY;

    public YukimurasGreatsword(JavaPlugin plugin) {
        this.plugin = plugin;
        this.YUKIMURASGREATSWORD_RECIPE_KEY = new NamespacedKey(plugin, "yukimurasgreatsword_recipe");
        this.YUKIMURASGREATSWORDCOMPLETE_RECIPE_KEY = new NamespacedKey(plugin, "yukimurasgreatswordcomplete_recipe");
    }

    public void registerRecipes() {
        registerYukimurasGreatswordRecipe();
        registerYukimurasGreatswordRecipeComplete();
    }

    // -- ITEM ACHIEVEMENT --
    @EventHandler
    public void onGreatswordCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack result = event.getRecipe().getResult();
        if (result == null || !result.isSimilar(YukimurasGreatswordComplete())) return;
        grantGreatswordAdvancement(player);
    }

    private void grantGreatswordAdvancement(Player player) {
        NamespacedKey key = new NamespacedKey("chae", "custom/obtain_greatsword");
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                progress.awardCriteria("has_greatsword");
            }
        }
    }

    // --ITEM RECIPES--
    public void registerYukimurasGreatswordRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(YUKIMURASGREATSWORD_RECIPE_KEY, YukimurasGreatsword());
        recipe.shape(
                " N ",
                " N ",
                " B "
        );
        recipe.setIngredient('N', Material.NETHERITE_BLOCK);
        recipe.setIngredient('B', Material.BLAZE_ROD);
        Bukkit.addRecipe(recipe);
    }

    public void registerYukimurasGreatswordRecipeComplete() {
        ShapelessRecipe recipe = new ShapelessRecipe(YUKIMURASGREATSWORDCOMPLETE_RECIPE_KEY, YukimurasGreatswordComplete());
        recipe.addIngredient(new RecipeChoice.ExactChoice(YukimurasGreatsword()));
        recipe.addIngredient(Material.NETHER_STAR);
        Bukkit.addRecipe(recipe);

    }

    private final Map<UUID, Long> lastEffectTime = new HashMap<>();
    private final Map<UUID, Boolean> fireImmunePlayers = new HashMap<>();
    private final Map<UUID, Long> lastExplosionTime = new HashMap<>();


    // --ITEM CREATION--
    public ItemStack YukimurasGreatsword() {

        ItemStack greatsword = ItemStack.of(Material.STICK);

        // name and colour
        greatsword.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "yukimuras_greatsword"));
        greatsword.setData(DataComponentTypes.ITEM_NAME, Component.text("Yukimura's Greatsword", NamedTextColor.WHITE));

        //equipment slot
        greatsword.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());

        //item stack size
        greatsword.setData(DataComponentTypes.MAX_STACK_SIZE, 1);

        return greatsword;
    }

    public ItemStack YukimurasGreatswordComplete() {
        ItemStack greatsword = ItemStack.of(Material.NETHERITE_SCRAP);

        // item attributes
        //attack speed boost
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();

        builder.addModifier(Attribute.ATTACK_SPEED,
                new AttributeModifier(new NamespacedKey(plugin, "attack_speed"), 7,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));

        builder.addModifier(Attribute.KNOCKBACK_RESISTANCE,
                new AttributeModifier(new NamespacedKey(plugin, "knockback_resistance"), 7,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));

        builder.addModifier(Attribute.MINING_EFFICIENCY,
                new AttributeModifier(new NamespacedKey(plugin, "mining_efficiency"), 7,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.OFFHAND));

        builder.addModifier(Attribute.SUBMERGED_MINING_SPEED,
                new AttributeModifier(new NamespacedKey(plugin, "submerged_mining_speed"), 7,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.OFFHAND));

        builder.addModifier(Attribute.SAFE_FALL_DISTANCE,
                new AttributeModifier(new NamespacedKey(plugin, "safe_fall_distance"), 7,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));

        builder.addModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(new NamespacedKey(plugin, "attack_damage"), 15,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));

        builder.addModifier(Attribute.BLOCK_BREAK_SPEED,
                new AttributeModifier(new NamespacedKey(plugin, "block_break_speed"), 7,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.OFFHAND));

        greatsword.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());

        // name and colour
        greatsword.setData(DataComponentTypes.ITEM_MODEL, Key.key("chae", "yukimuras_greatsword"));
        greatsword.setData(DataComponentTypes.ITEM_NAME, Component.text("Yukimura's Greatsword", NamedTextColor.DARK_RED));

        greatsword.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        //equipment slot
        greatsword.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HAND).build());

        //item stack size
        greatsword.setData(DataComponentTypes.MAX_STACK_SIZE, 1);

        return greatsword;
    }

    // -- ITEM SPECIAL EFFECTS --
    public void giveYukimurasGreatswordEffect(Player player) {
        //EDIT
        player.playSound(player.getLocation(), "minecraft:entity.blaze.ambient", 1f, 1f);
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 0.5f, 0.8f);  // Deep rumble
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 0.3f, 1.2f);
        player.showTitle(Title.title(
                Component.text("The Everlasting War").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Netherborne Dweller - ").color(NamedTextColor.DARK_RED)
        ));
    }

    // what happens when the item is being held
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        boolean hasSword = isYukimurasGreatsword(mainHand);

        // title vfx
        if (hasSword && lastEffectTime.getOrDefault(uuid, 0L) < now - 5000) {
            giveYukimurasGreatswordEffect(player);
            lastEffectTime.put(uuid, now);
        }

        // fire n explosion res
        if (hasSword) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999999, 5, true, false)); // Level 6 = explosion proof
        } else {
            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
            player.removePotionEffect(PotionEffectType.RESISTANCE);
        }
    }

    // exuplosion

    @EventHandler
    public void onRightClickBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        if (!isYukimurasGreatsword(mainHand)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        Block block = event.getClickedBlock();
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (lastExplosionTime.getOrDefault(uuid, 0L) > now - 500) {  // ✅ 5 SECONDS
            player.sendMessage(Component.text("Slow down there Dweller").color(NamedTextColor.RED));
            return;
        }

        lastExplosionTime.put(uuid, now);
        event.setCancelled(true);

        // ✅ FIXED PARTICLE + FULL DESTRUCTION
        player.getWorld().createExplosion(loc, 4.0f, true, true);  // Block damage + fire
        player.getWorld().spawnParticle(Particle.EXPLOSION, loc, 1);  // ✅ HUGE explosion
        player.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.8f);

    }

    // -- ITEM CHECKER --
    private boolean isYukimurasGreatsword(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(Component.text("Yukimura's Greatsword", NamedTextColor.DARK_RED));
    }



}
