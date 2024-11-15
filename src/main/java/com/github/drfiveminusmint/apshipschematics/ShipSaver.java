package com.github.drfiveminusmint.apshipschematics;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.repair.util.WEUtils;
import org.bukkit.entity.Player;

import java.io.File;

public class ShipSaver {

    public static boolean trySaveShip(Player player, String name)
    {
        return trySaveShip(player, name, Settings.schematicFolderPath);
    }
    public static boolean trySaveShip(Player player, String name, String dir)
    {
        Craft craft = CraftManager.getInstance().getCraftByPlayer(player);
        if (craft == null)
        {
            player.sendMessage("You must be piloting a craft.");
            return false;
        }
        String pluginsFolderPath = ShipSchematics.getPlugin(ShipSchematics.class).getDataFolder().getParentFile().getAbsolutePath();
        File target = new File(pluginsFolderPath + "/" + dir);
        if (!target.exists()) target.mkdirs();
        return WEUtils.saveCraftSchematic(target, name, craft.getWorld(), craft.getHitBox(), player.getLocation());

    }
}
