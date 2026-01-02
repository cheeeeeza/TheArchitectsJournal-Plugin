package net.chae.TheArchitectsJournal.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.util.Vector;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Blaze;  // Nether mobs
import org.bukkit.entity.Ghast;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Enderman;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
        startNetherProtectionTask();
        startPassiveEffects();
    }

    public void registerRecipes() {
        registerYukimurasGreatswordRecipe();
        registerYukimurasGreatswordRecipeComplete();
    }

    // ITEM ACHIEVEMENT ---------------------------------------------------------------------------------
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

    // --ITEM RECIPES---------------------------------------------------------------------------------
    public void registerYukimurasGreatswordRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(YUKIMURASGREATSWORD_RECIPE_KEY, YukimurasGreatsword());
        recipe.shape(
                "N",
                "N",
                "B"
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
    private final Map<UUID, Long> netherTpCooldowns = new HashMap<>();  // ← NEW

    // ITEM CREATION -------------------------------------------------------------------------------
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
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.OFFHAND));

        builder.addModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(new NamespacedKey(plugin, "attack_damage"), 10,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));

        builder.addModifier(Attribute.BLOCK_BREAK_SPEED,
                new AttributeModifier(new NamespacedKey(plugin, "block_break_speed"), 7,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.OFFHAND));

        greatsword.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());

        // Hide default attribute tooltip
        TooltipDisplay.Builder tooltipBuilder = TooltipDisplay.tooltipDisplay();
        tooltipBuilder.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        greatsword.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipBuilder.build());

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

    // ITEM TITLE EFFECTS ---------------------------------------------------------------------------
    public void giveYukimurasGreatswordEffect(Player player) {
        //EDIT
        player.playSound(player.getLocation(), "minecraft:entity.blaze.ambient", 1f, 1f);
        player.playSound(player.getLocation(), Sound.AMBIENT_CRIMSON_FOREST_MOOD, 0.5f, 0.8f);  // Deep rumble
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 0.3f, 1.2f);
        player.playSound(player.getLocation(), Sound.AMBIENT_NETHER_WASTES_MOOD, 1f, 0.9f);
        player.playSound(player.getLocation(), Sound.AMBIENT_NETHER_WASTES_ADDITIONS, 1f, 0.9f);
        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.6f, 0.9f);


        player.showTitle(Title.title(
                Component.text("The Everlasting War").color(NamedTextColor.DARK_PURPLE),
                Component.text(" - The Netherborne Dweller - ").color(NamedTextColor.DARK_RED)
        ));
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        // Item being switched TO
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        boolean hasSword = isYukimurasGreatsword(newItem);


        // Item being switched FROM
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
        boolean hadSword = isYukimurasGreatsword(oldItem);

        if (hasSword && !hadSword) {
            giveYukimurasGreatswordEffect(player);
        }
    }

    //NETHER MOB IMMUNITY ------------------------------------------------------------------------

    private final Map<UUID, Boolean> swordHolders = new HashMap<>();


    private void startNetherProtectionTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {

                // Check main hand sword
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                boolean hasSword = isYukimurasGreatsword(mainHand);
                swordHolders.put(player.getUniqueId(), hasSword);

                if (!hasSword) continue;

                // Constantly clear nearby nether hostile targets
                for (Entity nearby : player.getNearbyEntities(67, 67, 67)) {
                    if (!(nearby instanceof Mob mob)) continue;

                    if (mob instanceof Blaze ||
                            mob instanceof Ghast ||
                            mob instanceof WitherSkeleton ||
                            mob instanceof PiglinBrute ||
                            mob instanceof PigZombie ||
                            mob instanceof MagmaCube ||
                            mob instanceof Skeleton ||
                            mob instanceof Enderman) {

                        if (player.equals(mob.getTarget())) {
                            mob.setTarget(null);
                            mob.setAggressive(false);
                        }
                    }
                }
            }
        }, 0L, 3L); // every 0.25s
    }


    // TELEPORTATION DIMENSION ---------------------------------------------------------------------------
    private int getSafeY(World world, int x, int z) {
        for (int y = 120; y > 0; y--) {
            Block block = world.getBlockAt(x, y, z);
            if (block.getType() != Material.AIR) {
                return y + 1;  // Spawn above highest block
            }
        }
        return 80;  // Fallback
    }

    @EventHandler
    public void onNetherTeleport(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!isYukimurasGreatsword(player.getInventory().getItemInMainHand())) return;
        if (!player.isSneaking() || event.getAction() != Action.RIGHT_CLICK_AIR) return;

        // cd
        Long lastTp = netherTpCooldowns.get(player.getUniqueId());
        if (lastTp != null && System.currentTimeMillis() - lastTp < 60000) {
            player.sendActionBar(Component.text("§9✦ Greatsword Recharging... §9✦", NamedTextColor.GRAY));
            return;
        }
        netherTpCooldowns.put(player.getUniqueId(), System.currentTimeMillis());

        World currentWorld = player.getWorld();
        World targetWorld = null;

        // vice versa logic
        if (currentWorld.getEnvironment() == World.Environment.NETHER) {
            // Nether → Overworld (multiply coords x8)
            targetWorld = Bukkit.getWorlds().stream()
                    .filter(w -> w.getEnvironment() == World.Environment.NORMAL)
                    .findFirst()
                    .orElse(null);
        } else {
            // Overworld → Nether (divide coords /8)
            targetWorld = Bukkit.getWorlds().stream()
                    .filter(w -> w.getEnvironment() == World.Environment.NETHER)
                    .findFirst()
                    .orElse(null);
        }

        if (targetWorld == null) {
            player.sendMessage(Component.text("AIZA !!!! DEBUG !!! IF YOUR NOT AIZA AND YOU SEE THIS PLEASE TELL HER: Target dimension not found!", NamedTextColor.RED));
            return;
        }

        // coordinates
        Location currentLoc = player.getLocation();
        double targetX = currentWorld.getEnvironment() == World.Environment.NETHER
                ? currentLoc.getX() * 8 : currentLoc.getX() / 8.0;
        double targetZ = currentWorld.getEnvironment() == World.Environment.NETHER
                ? currentLoc.getZ() * 8 : currentLoc.getZ() / 8.0;

        // Find safe Y
        int safeY = getSafeY(targetWorld, (int) targetX, (int) targetZ);

        Location targetLoc = new Location(targetWorld, targetX, safeY, targetZ,
                currentLoc.getYaw(), currentLoc.getPitch());

        // Portal effects (no title)
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 100, 1, 1, 1, 0.5);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.2f);

        player.teleport(targetLoc);
    }

    // EXPLOSION --------------------------------------------------------------------------------------------

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
        if (lastExplosionTime.getOrDefault(uuid, 0L) > now - 5000) {  // 0.5 sec CD
            player.sendActionBar(Component.text("§9✦ Greatsword Recharging... §9✦", NamedTextColor.GRAY));
            return;
        }

        lastExplosionTime.put(uuid, now);
        event.setCancelled(true);

        // VFX
        player.getWorld().createExplosion(loc, 4.0f, true, true);  // Block damage + fire
        player.getWorld().spawnParticle(Particle.EXPLOSION, loc, 1);
        player.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.8f);

    }

    // FIRE ASCPECT---------------------------------------------------------
    @EventHandler
    public void onGreatswordKill(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) return;

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!isYukimurasGreatsword(mainHand)) return;

        LivingEntity victim = event.getEntity();

        // FIRE ASPECT + COOKED MEAT
        victim.setFireTicks(100);

        // **PASS PLAYER TO METHOD**
        List<ItemStack> cookedDrops = getCookedDrops(victim, player);
        for (ItemStack cookedMeat : cookedDrops) {
            victim.getWorld().dropItemNaturally(victim.getLocation(), cookedMeat);
        }

        // Fire effects
        Location loc = victim.getLocation();
        victim.getWorld().spawnParticle(Particle.FLAME, loc, 30, 0.5, 0.5, 0.5, 0.1);
        victim.getWorld().playSound(loc, Sound.ITEM_FIRECHARGE_USE, 1f, 1.2f);
    }

    private List<ItemStack> getCookedDrops(LivingEntity entity, Player player) {
        List<ItemStack> cookedDrops = new ArrayList<>();

        // Animal → Cooked meat
        if (entity instanceof Cow || entity instanceof MushroomCow) {
            cookedDrops.add(new ItemStack(Material.COOKED_BEEF, 1));
        } else if (entity instanceof Pig) {
            cookedDrops.add(new ItemStack(Material.COOKED_PORKCHOP, 1));
        } else if (entity instanceof Sheep) {
            cookedDrops.add(new ItemStack(Material.COOKED_MUTTON, 1));
        } else if (entity instanceof Chicken) {
            cookedDrops.add(new ItemStack(Material.COOKED_CHICKEN, 1));
        }

        return cookedDrops;
    }

    // -- ITEM CHECKER ---------------------------------------------------------------------------------------------------------

    private final Map<UUID, Long> swimCooldown = new HashMap<>();

    @EventHandler
    public void onLavaSwimBoost(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        if (!isYukimurasGreatsword(mainHand) && !isYukimurasGreatsword(offHand)) return;

        // Check if in lava (head or body submerged)
        Location loc = player.getLocation();
        Block feetBlock = loc.clone().subtract(0, 0.3, 0).getBlock();
        Block headBlock = loc.clone().add(0, 0.5, 0).getBlock();
        if (feetBlock.getType() != Material.LAVA && headBlock.getType() != Material.LAVA) return;

        if (player.isOnGround()) return;
        if (player.isSneaking()) return;

        long now = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();
        if (swimCooldown.containsKey(uuid) && now - swimCooldown.get(uuid) < 300) return;  // 0.3s cooldown for smooth

        swimCooldown.put(uuid, now);

        // Boost forward + slight up for lava swimming propulsion
        Vector dir = player.getLocation().getDirection().multiply(1.0);
        dir.setY(0.0);
        player.setVelocity(dir);

        player.playSound(player.getLocation(), Sound.ENTITY_STRIDER_STEP_LAVA, 0.8f, 1.5f);
    }


    private void startPassiveEffects() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ItemStack mainHand = player.getInventory().getItemInMainHand();
                    ItemStack offHand = player.getInventory().getItemInOffHand();

                    boolean hasSword = isYukimurasGreatsword(mainHand) || isYukimurasGreatsword(offHand);

                    if (hasSword) {
                        // Fire res + resistance (both hands)
                        player.addPotionEffect(new PotionEffect(
                                PotionEffectType.FIRE_RESISTANCE, 40, 0, true, false));

                    } else {
                        player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                        player.removePotionEffect(PotionEffectType.LEVITATION);  // Clear lava walk
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }



    private boolean isYukimurasGreatsword(ItemStack item) {
        if (item == null) return false;
        Component name = item.getData(DataComponentTypes.ITEM_NAME);
        return name != null && name.equals(Component.text("Yukimura's Greatsword", NamedTextColor.DARK_RED));
    }



}
