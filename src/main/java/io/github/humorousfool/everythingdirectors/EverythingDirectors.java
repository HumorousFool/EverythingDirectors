package io.github.humorousfool.everythingdirectors;

import io.github.humorousfool.everythingdirectors.directors.ProjectileDirector;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public final class EverythingDirectors extends JavaPlugin {

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        Config.DirectorTool = Material.getMaterial(getConfig().getString("DirectorTool", "STICK"));

        getServer().getPluginManager().registerEvents(new ProjectileDirector("arrows", "Arrow Director", "arrow", EntityType.ARROW, EntityType.SPECTRAL_ARROW), this);
        getServer().getPluginManager().registerEvents(new ProjectileDirector("eggs", "Egg Director", "egg", EntityType.EGG), this);
        getServer().getPluginManager().registerEvents(new ProjectileDirector("snowballs", "Snowball Director", "snowball", EntityType.SNOWBALL), this);
        getServer().getPluginManager().registerEvents(new ProjectileDirector("potions", "Potion Director", "potion", EntityType.SPLASH_POTION, EntityType.THROWN_EXP_BOTTLE), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
