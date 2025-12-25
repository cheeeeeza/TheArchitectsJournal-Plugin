package net.chae.TheArchitectsJournal;

import net.chae.TheArchitectsJournal.items.YevasWand;
import org.bukkit.plugin.java.JavaPlugin;
import net.chae.TheArchitectsJournal.items.ArhamsCrown;
import net.chae.TheArchitectsJournal.items.HudeensJournal;

public class TheArchitectsJournal extends JavaPlugin {

    private ArhamsCrown crown;
    private HudeensJournal journal;
    private YevasWand wand;

    @Override
    public void onEnable() {
        getLogger().info("TheArchitectsJournal loaded!");

        // Initialize items
        crown = new ArhamsCrown(this);
        journal = new HudeensJournal(this);
        wand = new YevasWand(this);

        // Register recipes
        crown.registerRecipes();
        journal.registerRecipes();
        wand.registerRecipes();

        // Register events
        getServer().getPluginManager().registerEvents(crown, this);
        getServer().getPluginManager().registerEvents(journal, this);
        getServer().getPluginManager().registerEvents(wand, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("TheArchitectsJournal disabled!");
    }
}
