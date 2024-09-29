package com.github.drfiveminusmint.apshipschematics;

import com.github.drfiveminusmint.apshipschematics.command.ImpoundCommand;
import com.github.drfiveminusmint.apshipschematics.command.SaveShipCommand;
import com.github.drfiveminusmint.apshipschematics.impound.ImpoundManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ShipSchematics extends JavaPlugin {
    private static ShipSchematics instance;
    private static ImpoundManager impoundManager;
    private static File impoundFolder;

    @Override
    public void onEnable() {
        instance = this;
        impoundFolder = new File(getDataFolder(),"impounds");
        if (!impoundFolder.exists())
            impoundFolder.mkdirs();

        getCommand("saveship").setExecutor(new SaveShipCommand());
        getCommand("impound").setExecutor(new ImpoundCommand());

        impoundManager = new ImpoundManager();
        impoundManager.runTaskTimerAsynchronously(this,0,20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ShipSchematics getInstance() {return instance;}
    public static ImpoundManager getImpoundManager() {return impoundManager;}
    public static File getImpoundFolder() {return impoundFolder;}
}
