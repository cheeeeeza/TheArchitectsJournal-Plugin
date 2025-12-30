package net.chae.TheArchitectsJournal;

import net.chae.TheArchitectsJournal.items.*;
import org.bukkit.plugin.java.JavaPlugin;

public class TheArchitectsJournal extends JavaPlugin {

    private ArhamsCrown crown;
    private HudeensJournal journal;
    private YevasWand wand;
    private YukimurasGreatsword greatsword;
    private LichtsArmlet armlet;
    private EmmansLantern lantern;
    private HudeensBlueprint blueprint;
    private SerinasCloak cloak;
    private CezarsAmulet amulet;

    @Override
    public void onEnable() {
        getLogger().info("TheArchitectsJournal loaded!");

        // Initialize items
        crown = new ArhamsCrown(this);
        journal = new HudeensJournal(this);
        wand = new YevasWand(this);
        greatsword = new YukimurasGreatsword(this);
        armlet = new LichtsArmlet(this);
        lantern = new EmmansLantern(this);
        blueprint = new HudeensBlueprint(this);
        cloak = new SerinasCloak(this);
        amulet = new CezarsAmulet(this);

        // Register recipes
        crown.registerRecipes();
        journal.registerRecipes();
        wand.registerRecipes();
        greatsword.registerRecipes();
        armlet.registerRecipes();
        lantern.registerRecipes();
        blueprint.registerRecipes();
        cloak.registerRecipes();
        amulet.registerRecipes();

        // Register events
        getServer().getPluginManager().registerEvents(crown, this);
        getServer().getPluginManager().registerEvents(journal, this);
        getServer().getPluginManager().registerEvents(wand, this);
        getServer().getPluginManager().registerEvents(greatsword, this);
        getServer().getPluginManager().registerEvents(armlet, this);
        getServer().getPluginManager().registerEvents(lantern, this);
        getServer().getPluginManager().registerEvents(blueprint, this);
        getServer().getPluginManager().registerEvents(cloak, this);
        getServer().getPluginManager().registerEvents(amulet, this);

        //lichts armlet effects
        armlet.startLichtsArmletPassives();

    }

    @Override
    public void onDisable() {
        getLogger().info("TheArchitectsJournal disabled!");
    }
}
