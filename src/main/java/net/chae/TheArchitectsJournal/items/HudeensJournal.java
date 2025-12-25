package net.chae.TheArchitectsJournal.items;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.BookMeta;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableAPIUsage")
public class HudeensJournal implements Listener {
    private final JavaPlugin plugin;
    private NamespacedKey JOURNAL_RECIPE_KEY;

    public HudeensJournal(JavaPlugin plugin) {
        this.plugin = plugin;
        this.JOURNAL_RECIPE_KEY = new NamespacedKey(plugin, "journal_recipe");
    }

    public void registerRecipes() {
        JournalRecipe();
    }

    // EVENT HANDLER - Achievement on craft
    @EventHandler
    public void onJournalCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack result = event.getRecipe().getResult();
        if (result == null) return;

        if (!result.isSimilar(Journal())) return;

        grantJournalAdvancement(player);
    }

    private void grantJournalAdvancement(Player player) {
        NamespacedKey key = new NamespacedKey("chae", "custom/obtain_journal");
        Advancement advancement = Bukkit.getAdvancement(key);

        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                progress.awardCriteria("has_journal");
            }
        }
    }

    // RECIPE
    public void JournalRecipe() {
        ShapelessRecipe recipe = new ShapelessRecipe(JOURNAL_RECIPE_KEY, Journal());
        recipe.addIngredient(Material.COPPER_PICKAXE);
        recipe.addIngredient(Material.NETHER_STAR);
        recipe.addIngredient(Material.BOOK);
        Bukkit.addRecipe(recipe);
    }

    // ITEM STACK
    public ItemStack Journal() {
        ItemStack journalItem = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) journalItem.getItemMeta();

        meta.setTitle("Hudeen's Logs");
        meta.setAuthor("The Realmcrafting Architect");

        meta.addPages(
                Component.text(
                        "I, Hudeen, am a scholar. I excel at the knowledge of materials, " +
                                "the knowledge of crafts, and the knowledge of chemistry this world holds. " +
                                "The one thing i am incapable of obtaining the knowledge of, is the amount of " +
                                "ridicule this team holds- and what they're holding.\n\n"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "Any amount of movement from the rest of The Eight pushes me " +
                                "to the edge of the cliff. If I had that jester's ability to alter reality, " +
                                "I would drown them all in the scorching sands of my ancestors' desert.\n\n" +
                                "In any case that I am injured or even killed by their sheer "
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "stupidity, I, Hudeen, am taking note of my killers and their peculiarity. " +
                                "It is my hope that I shall discern the full extent of their capabilities and " +
                                "remake them with the most precision. Starting with the most bearable " +
                                "of them all:\n\n"
                ).color(NamedTextColor.DARK_GRAY),

                // THE FORSAKEN PRINCE EXTRACT
                Component.text()
                        .append(Component.text("Specimen 1: The Forsaken Prince\n\n", NamedTextColor.DARK_AQUA))
                        .append(Component.text(
                                "In all honesty, he's only the most bearable because of his inability " +
                                        "to speak. The most he does is makes grunting sounds and croaks in reply. " +
                                        "The rest treat him as if he's human, but whatever he is, is beyond that. " +
                                        "Him and ",
                                NamedTextColor.DARK_GRAY
                        ))
                        .build(),

                Component.text(
                        "the other half of whatever team this is.\n\n" +
                                "The prince, Arham, is like a frog. If anything he croaks almost all the time. " +
                                "As much as it is an annoyance, I fear more of what would happen if he doesnt. " +
                                "One time the Dweller angered him, the croaking stopped, and everything"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "around turned and flickered dark. " +
                                "The ground even started rumbling and sonic explosions echoed through. " +
                                "If Severin hadn't calmed things down by touching his crown, " +
                                "the overworld and my arm would've disappeared by now.\n\n" +
                                "Speaking of his crown, it seems as if he has "
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "some sort of fundamental " +
                                "incompatibility with it. Well, for starters his \"horns\" spiralled out " +
                                "of his head locking it in, as if he was forcefully crowned during coronation, " +
                                "to be king of a kingdom that was never seen to be. "
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "It looks as if it fuels his anger. I've spoken with Emman regarding it " +
                                "once on the peculiar material the crown is made out of. She hasn't " +
                                "really given me an answer besides the underlying fear she tries to hide.\n\n" +
                                "I've done a bit of independent research and found out that apparently, "
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "the trailblazer is severely allergic to echo shards. With that, " +
                                "replicating the crown itself was simple: three echo shards and a netherite " +
                                "ingot in the centre; all in the shape of a crown. However, replicating " +
                                "the power isn't as simple. I've yet to figure that out.\n\n "
                ).color(NamedTextColor.DARK_GRAY)
        );

        journalItem.setItemMeta(meta);
        journalItem.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        journalItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Hudeen's Journal", NamedTextColor.YELLOW));

        return journalItem;
    }
}
