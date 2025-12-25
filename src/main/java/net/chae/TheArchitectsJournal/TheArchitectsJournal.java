package net.chae.TheArchitectsJournal;

import net.chae.TheArchitectsJournal.items.*;
import org.bukkit.plugin.java.JavaPlugin;

public class TheArchitectsJournal extends JavaPlugin {

    private ArhamsCrown crown;
    private HudeensJournal journal;
    private YevasWand wand;
    private YukimurasGreatsword greatsword;
    private LichtsArmlet armlet;

    @Override
    public void onEnable() {
        getLogger().info("TheArchitectsJournal loaded!");

        // Initialize items
        crown = new ArhamsCrown(this);
        journal = new HudeensJournal(this);
        wand = new YevasWand(this);
        greatsword = new YukimurasGreatsword(this);
        armlet = new LichtsArmlet(this);

        // Register recipes
        crown.registerRecipes();
        journal.registerRecipes();
        wand.registerRecipes();
        greatsword.registerRecipes();
        armlet.registerRecipes();

        // Register events
        getServer().getPluginManager().registerEvents(crown, this);
        getServer().getPluginManager().registerEvents(journal, this);
        getServer().getPluginManager().registerEvents(wand, this);
        getServer().getPluginManager().registerEvents(greatsword, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("TheArchitectsJournal disabled!");
    }
}
