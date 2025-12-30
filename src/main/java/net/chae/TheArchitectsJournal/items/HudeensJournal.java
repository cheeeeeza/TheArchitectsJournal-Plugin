package net.chae.TheArchitectsJournal.items;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
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
                player.getInventory().addItem(new ItemStack(Material.NETHER_STAR));
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
                                "the knowledge of crafts, and the knowledge of chemistry this world " +
                                "holds. The one thing I am incapable of obtaining the knowledge of, " +
                                "is the amount of ridicule this team holds.\n\n"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "Any amount of movement from the rest of The Eight pushes me to the " +
                                "edge of the cliff. If I were blessed with that jester’s ability to " +
                                "alter reality, I would drown them all in the scorching sands of my " +
                                "ancestors’ desert.\n\n" +
                                "In any case that I am injured"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "or even killed by their sheer stupidity, this journal will stand as a " +
                                "record of my findings of those whose relics I had the fortune of studying " +
                                "firsthand.\n\n\n"
                ).color(NamedTextColor.DARK_GRAY),

                // THE FORSAKEN PRINCE EXTRACT
                Component.text()
                        .append(Component.text("Entry III: The Foresaken Prince\n\n", NamedTextColor.GOLD))
                        .append(Component.text(
                                "In all honesty, he’s only the most tolerable because he spares me the " +
                                        "trouble of conversation. At most, he communicates through grunts and " +
                                        "croaks. The rest persist in treating him as if he were human, but whatever",
                                NamedTextColor.DARK_GRAY
                        ))
                        .build(),

                Component.text(
                        "he is, is beyond that distinction- him, and the other half of whatever this team " +
                                "is meant to be.\n\n" +
                                "The prince, Arham, bears an unfortunate resemblance to a frog. He croaks incessantly. " +
                                "As much as it is an annoyance, I have come to fear the consequences of"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "his silence far more. One occasion the Dweller provoked him, the croaking stopped, " +
                                "and everything around dimmed and flickered dark. The ground itself started to protest, " +
                                "and sonic ruptures tore through the air. Had Cezar not intervened by touching his crown-"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "the Overworld, and my arm, would’ve ceased to exist.\n\n" +
                                "As for the crown, it appears as if he has some sort of fundamental incompatibility " +
                                "with it. For one, his “horns” spiralled out, locking it into place- as though he " +
                                "was forcefully crowned during"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "coronation, to be king of a kingdom that was never seen to be. " +
                                "It looks as if it fuels his anger. I’ve spoken with Emman once regarding " +
                                "the peculiar material of its construction. She hasn’t really given me an " +
                                "answer of sustenance, besides the underlying fear she tries to hide.\n\n"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                                "Naturally, I’ve conducted a bit of independent research and found " +
                                        "out that apparently, the Trailblazer is severely allergic to echo " +
                                        "shards. With that, replicating the crown itself was simple: three " +
                                        "echo shards and a silence armour trim in the centre; resembling the shape"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "of a crown. Replicating the power, however, isn’t as simple.\n" +
                                "\n" +
                                "I’ve yet to figure that out.\n"
                ).color(NamedTextColor.DARK_GRAY),

                // THE WANDERRING TRAILBLAZER EXTRACT
                Component.text()
                        .append(Component.text("Entry XVIII: The Wandering Trailblazer\n\n", NamedTextColor.GOLD))
                        .append(Component.text(
                                "She is, regrettably, only the most bearable, only because she is the most sane " +
                                        "among this group. Although, sane would be a generous overstatement. Normal " +
                                        "would be the more fitting term, as she is the only one within",
                                NamedTextColor.DARK_GRAY
                        ))
                        .build(),

                Component.text(
                        "the team whom I manage to sustain a conversation without immediate regret. Even so, " +
                                "she irritates me the least.\n" +
                                "\n" +
                                "Despite her innocent appearance, she knows far more than she lets on.\n" +
                                "\n" +
                                "Emman tends to conceal a lot of her"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "vulnerabilities, despite her incessant preaching about “trust” and “bond”. " +
                                "A Hypocrite at best. Tasked as the team’s scavenger, she always brings her lantern with " +
                                "her. I, for one, ridicule its purpose. Even a poorly lit torch would offer her much more " +
                                "illumination than the"

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "glow berries rotting inside that wooden casket.\n\n" +
                                "But of course, simply assuming isn’t very scholarly, and so I borrowed it while she slept.\n\n"+
                                "In retrospect, my greatest mistake during so was touching the lantern"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "itself. The sensation- an unusual surge coursing through my body- is not one I wish to experience " +
                                "again. And so I have since abandoned such plans and resorted to a much more refined " +
                                "methods of research: observation.\n" +
                                "\n" +
                                "Despite its monotonous nature, when done"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "properly, is truly enlightening.\n" +
                                "\n" +
                                "It is very subtle, but she moves more than any of the others and eats the least. " +
                                "Yet she remains inexplicably upright- even after near death encounters that I am sure " +
                                "should have ended each of our lives. Now, I am"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "increasingly convinced that her resilience is entirely the work of that lantern.\n\n" +
                                "An exceedingly simple one at that.\n" +
                                "\n" +
                                "With the princess’s assistance, surrounding a glow berry with a log with each " +
                                "required"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "species: oak, spruce, jungle acacia, cherry and bamboo, along with three " +
                                "anvils placed in the bottom row of a crafting table, the lantern comes to light. \n\n" +
                                "The result is deceptively simple. It’s mass, however, betrays its true nature."
                ).color(NamedTextColor.DARK_GRAY),

                // THE PROPHESEER 1 EXTRACT
                Component.text()
                        .append(Component.text("Entry ███: The Propheseer\n\n", NamedTextColor.GOLD))
                        .append(Component.text(
                                "In all honestly, I beseech him. Who does he believe he is? " +
                                        "Simply walking up to the greatest scholar of all Qamar, only " +
                                        "to conscript him into a troupe of fools?\n" +
                                        "\n" +
                                        "However, this arrangement is more ",
                                NamedTextColor.DARK_GRAY
                        ))
                        .build(),

                Component.text(
                        "bearable compared to enduring my father's pitiful imitation of a throne. " +
                                "I loathe the team, but under that seer’s leadership, I have been afforded " +
                                "freedom I couldn’t afford in my own home. And despite his… unfortunate, " +
                                "countenance, his knowledge is unexpectedly sound."
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "He’s mentioned about a mythical object he is determined to obtain with the " +
                                "help of this team: the Nether Star. I, for one, have never heard of such a " +
                                "fairytale before. No book in the libraries I’ve homed have listed such name. " +
                                "According to its legend, it houses the souls of those who yearned "
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "and cried. He claims it carries the wills of those long abandoned by time itself.\n" +
                                " \n" +
                                "He insists that it was the nether star that unveiled the prophecy to him, " +
                                "granting him the power to locate the Eight. Whether this makes him enlightened " +
                                "or merely dangerous remains to be seen.\n" +
                                "\n"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "████████ █████████ ██ ████ █████ ████████ █████ █ ██████ █████████ ██ █ " +
                                "███████ █ █████ ████ ██████ ███████████ █ ████ ████ ████████████ ██████ " +
                                "██ ██ ███ █████ ███ ███████ ████ █████ ████████████ ███ █████████ ██ "
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "█████ █ ██████ ██████\n" +
                                "\n" +
                                "I’ve yet to determine what his amulet actually does- " +
                                "assuming it does anything at all."
                ).color(NamedTextColor.DARK_GRAY),

                // THE HARROWING HARBINGER EXTRACT
                Component.text()
                        .append(Component.text("Entry XXV: The Harrowing Harbinger\n\n", NamedTextColor.GOLD))
                        .append(Component.text(
                                "The harbinger is silent. Her lack of emotion etched upon her face is worthy of " +
                                        "record. At first glance, one might assume she cares little, but my research " +
                                        "proves otherwise: Serina lives up to her title. She is, in every",
                                NamedTextColor.DARK_GRAY
                        ))
                        .build(),

                Component.text(
                        "sense, harrowing.\n\n" +
                                "Rarely she speaks, if at all. It is therefore surprising that she is related to that " +
                                "seer. Before he departed on his journey, Princess Serina had apparently vanished. " +
                                "That was the original purpose of his path. Now, having found her, Cezar redirected "

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "the team towards fulfilling the prophecy in order to save"+
                        "whatever is left of this barren wasteland. \n\n" +
                                "Her silence, however, is far from comforting. It is unnerving. Even the Catalyst who " +
                                "excels in annoyance lacks the ability to waver her."
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "One moment she disappears; the next she reappears. Keeping an eye on her is a greater " +
                                "challenge, akin to watching those lanky species in the wild. It even feels as if " +
                                "making eye contact with her would wager war. Being the fastest among all of us " +
                                "means she scouts the area"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "ahead along with the dweller. Whether her speed is a natural agility or aided by " +
                                "her cloak remains to be determined.\n" +
                                "\n" +
                                "She wears it constantly, the ends slightly ripped, recreating half of her kingdom’s " +
                                "crest. From what I can gather, the cloak"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "is, at least in material, is severely unremarkable- crafted from cowhide. " +
                                "Yet the configuration is notable, " +
                                "akin to making a leather chestplace with an Ender Eye embedded in the centre and an " +
                                "invisibility potion underneath. It is deceptively simple, yet results suggest"

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "enchantments beyond my " +
                        "understanding. I may need to seek out a conversation with Licht regarding so.\n" +
                                "\n" +
                                "Despite my initial expectation that she would remain aloof, Serina provides " +
                                "invaluable guidance regarding terrain, always scouting the area ahead along "

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "with the Dweller. It is, " +
                        "in fact, precisely this blend of silence and enigma that makes her slightly fascinating."
                ).color(NamedTextColor.DARK_GRAY),

                // THE ANGLER OF SKYE AND SEA EXTRACT
                Component.text()
                        .append(Component.text("Entry XXXII: The Angler of Sky and Sea\n\n", NamedTextColor.GOLD))
                        .append(Component.text(
                                "Before I was “captured” by Cezar, word had spread that the heir to the " +
                                        "Kingdom of the Skies had vanished. In truth, here she is, " +
                                        "accompanying this unusual group in their journey towards peace… ",
                                NamedTextColor.DARK_GRAY
                        ))
                        .build(),

                Component.text(
                        "if it can still be called such.\n" +
                                "\n" +
                                "Never did I imagine I would meet the heir to my nation's rival. While my father always " +
                                "fretted over how I should best their skies, in the contrary, the heir of sands is here " +
                                "along with her, following this ragtag team."

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "Conversations with her are surprisingly… soothing. Only she and I can truly relate, " +
                                "both shaped by families obsessed with pressuring the burden of a throne. Unlike me, " +
                                "her grace and patience are innate, made for such destiny. Ironic " +
                                "indeed that children of rival nations "
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "converse without constraint. I especially owe her, for she has saved me countess times " +
                                "from the Dweller’s incompetence. I also find it equally questionable that the clown " +
                                "pays her no need. It seems as if they have a history I have yet to discover.\n" +
                                "\n"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "Even the most indifferent of members treat her with caution. The prince, normally " +
                                "inattentive, seems unusually aware of her presence. Emman tells that it is due to a " +
                                "near death encounter in the past. Initially, I would have assumed she had saved them " +
                                "all. In contrary,"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "she admits she has no control over her prowess when her emotions stir. " +
                                "The angler mentions that very reason precisely is why she is grateful that Yeva " +
                                "remains at her side. Without her, she claims she would be forced to sever her own " +
                                "arm merely to disable the armlet "

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "controlling her abilities.\n" +
                                "\n" +
                                "As for the armlet itself, its properties remain a mystery. Out of all the relics I " +
                                "have studied, it is the most resistant to my replication. I’ve been forced to " +
                                "exhaust the last of my gold to construct blocks surrounding three "

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "hearts harvested from the sea. Creating it had me flying across trenches and drowning. " +
                                "Unfortunately, the jester had spared me from such fate. \n" +
                                "\n" +
                                "To my dismay, she takes endless amusement in reminding me of that incident. Tsk."
                ).color(NamedTextColor.DARK_GRAY),

                // THE CATALYST OF CHAOS EXTRACT
                Component.text()
                        .append(Component.text("Entry XLI: The Catalyst of Chaos\n\n", NamedTextColor.GOLD))
                        .append(Component.text(
                                "It’s a surprise she isn’t standing on my last nerves, given her condescending, " +
                                        "insufferable personality. Even if it would cost my whole kingdom’s fortune, " +
                                        "I would use it without a slither of doubt just to erase that ",
                                NamedTextColor.DARK_GRAY
                        ))
                        .build(),

                Component.text(
                        "perpetual smirk off her face. And yet… she is the only one whose opinion I am in " +
                                "agreement with, at least regarding the propheseer.\n" +
                                "\n" +
                                "Her abilities defy the laws of the world, beyond human comprehension. All historical " +
                                "and scientific knowledge gained "

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "by me is rendered useless in attempt of comprehending the logic behind what she can do. " +
                                "Her endless torment, never subtle, pushes me towards oblivion. I must admit, she is the " +
                                "most irritating when wielding her clown-like staff.\n" +
                                "\n" +
                                "The relic itself appears "
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "otherworldly. Crafting it required no small amount of ingenuity. However, " +
                                "to my misfortune, Yeva had discerned my intentions. From that point onward, " +
                                "she had subjected me into persistent mockery, supplying me false leads and " +
                                "transforming me into a rabbit "
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "whenever I faltered in her presence.\n" +
                                "\n" +
                                "Unfortunately for her, I have completed the recipe. Deceptively simple, " +
                                "though perhaps cruelly so. One is to combine a craft a totem made out of a beacon, " +
                                "a rabbit’s foot, and an ancient relic designed to  "
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "outmanoeuvre death (though it remains untested).\n" +
                                "\n" +
                                "Even so, upon its creation, Yeva appeared out of thin air and seized it from me once " +
                                "more. My stature proves insufficient to stop her before she flies away along " +
                                "with my makeshift staff. "

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "I have even risked my life repeatedly trying to obtain the materials for it.\n\n" +
                                "I’ll have to find another time to attempt the feat again."

                ).color(NamedTextColor.DARK_GRAY),

                // THE NETHERBORNE DWELLER EXTRACT
                Component.text()
                        .append(Component.text("Entry XLVII: The Netherborne Dweller\n\n", NamedTextColor.GOLD))
                        .append(Component.text(
                                "I, Hudeen, posses significant resolve- and for it to be so effortlessly " +
                                        "shattered by the tip of his blade is an indignity I have yet to forgive. " +
                                        "This damned hound does nothing but bark. I cannot entirely blame him- ",
                                NamedTextColor.DARK_GRAY
                        ))
                        .build(),

                Component.text(
                        "his comprehension is hardly sophisticated. His ability to run away from wisdom goes " +
                                "beyond me. It is unfortunate that we started on rough footing, as he was " +
                                "the tool used by which I was forced into joining this group.\n" +
                                "\n" +
                                "He embodies everything I find intolerable: "

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "explosive, uncalculating, and most offensively- gnawing.\n" +
                                "\n" +
                                "Wherever Yukimura goes, his greatsword follows. Frankly, I find this alarming. " +
                                "In what world does one simply carry such thing? Truly, he hails from another " +
                                "dimension. I would have tolerated "
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "him more if he had any concept of personal space. The circumstances of his birth are " +
                                "beyond me, for everything he touches ignites flames as hot as the Nether. Had " +
                                "he not barged into my office with curiosity, I would " +
                                "still possess thirty of my precious blueprints.\n" +
                                "\n"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "And that cursed greatsword.\n" +
                                "\n" +
                                "Despite his staggering incompetence, his aptitude for smithing is… admirable. " +
                                "Admirable in the sense that it borders whatever is considered inhumane. " +
                                "On one occasion, Emman once remarked-"
                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "rather enthusiastically- that it was “so cool” how " +
                        "he fused blocks of netherite ingots with blaze rods to fix his weapon with his " +
                                "bare hands. I chose not to ask how many laws of physics were violated in the progress.\n" +
                                "\n" +
                                "On another occasion, he insisted on "

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "taking me “swimming” to “cool down. I would not have objected had he not meant " +
                                "swimming in the fiery pits of the nether " +
                                "wastes. If Emman " +
                                "and Licht had not intervened, I fear this would have been my final contribution " +
                                "to this journal."

                ).color(NamedTextColor.DARK_GRAY),

                // THE PROPHESEER EXTRACT 2
                Component.text()
                        .append(Component.text("Entry LXVII\n\n", NamedTextColor.GOLD))
                        .append(Component.text(
                                "I was wronged. The Jester speaks the truth. If there was one regret I would " +
                                        "bring with me throughout my life and beyond, it would be mocking the jokes " +
                                        "of a jester with no king to entertain. The seer is corrupted " +
                                        "beyond redemption. ",
                                NamedTextColor.DARK_GRAY
                        ))
                        .build(),

                Component.text(
                        "This journey was never to save this nation- not while he leads it. \n" +
                                "\n" +
                                "Whoever lays their hand on this book, please, find the rest and warn them, for I " +
                                "can no longer reach the others. I am running out of time. I have tried, and if " +
                                "you are reading this: "

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "I have failed. For as long as the king lives, his kingdom is not safe. Do not trust his " +
                                "words. Do not fall for his will. Do not allow him to pursue you to make the same mistakes.\n" +
                                "\n" +
                                "All I have left to offer is a recipe of the sacred print. Use the Table, bind the"

                ).color(NamedTextColor.DARK_GRAY),

                Component.text(
                        "enchanted fruit " +
                        "with a map, and let it take form. When the last is forged and the collection is " +
                                "complete, it will create the one creation still capable of opposing him."
                ).color(NamedTextColor.DARK_GRAY)

        );

        journalItem.setItemMeta(meta);
        journalItem.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        journalItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Hudeen's Journal", NamedTextColor.YELLOW));

        return journalItem;
    }

    //special effects
    // -- ITEM SPECIAL EFFECTS --
    public void giveHudeensJournalEffect(Player player) {

        // Subtle, ancient / scholarly ambience
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 0.6f, 0.8f);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f, 0.7f);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 0.7f, 1.1f);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 0.2f, 0.6f);

        // Title
        player.showTitle(Title.title(
                Component.text("The Knowledge of Fate").color(NamedTextColor.DARK_PURPLE),
                Component.text("- The Realmcrafting Architect -").color(NamedTextColor.BLUE)
        ));
    }

    @EventHandler
    public void onJournalHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (isHudeensJournal(hand)) {
            giveHudeensJournalEffect(player);
        }
    }

    //item checker
    private boolean isHudeensJournal(ItemStack item) {
        if (item == null || item.getType() != Material.WRITTEN_BOOK) return false;
        if (!item.hasItemMeta()) return false;

        return Component.text("Hudeen's Journal", NamedTextColor.YELLOW)
                .equals(item.getData(DataComponentTypes.ITEM_NAME));
    }




}
