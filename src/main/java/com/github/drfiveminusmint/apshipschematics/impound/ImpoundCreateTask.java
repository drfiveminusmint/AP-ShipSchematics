package com.github.drfiveminusmint.apshipschematics.impound;

import com.github.drfiveminusmint.apshipschematics.ShipSchematics;
import com.github.drfiveminusmint.apshipschematics.TextUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ImpoundCreateTask implements ImpoundTask {
    private final Impound impound;
    private final Player notifyPlayer;
    public ImpoundCreateTask (@NotNull Player notifyPlayer, Impound impoundToWrite)
    {
        this.impound = impoundToWrite;
        this.notifyPlayer = notifyPlayer;
    }
    @Override
    public void run() {
        File impoundsFile = new File(ShipSchematics.getInstance().getDataFolder().getAbsolutePath()
                + "/impounds/" + TextUtils.nullableUUIDToString(impound.getPlayerUUID()) + ".yml");
        if (!impoundsFile.exists())
            try {
                impoundsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                notifyPlayer.sendMessage("Error adding impound to list; your schematic has still been saved.");
                return;
            }
        File tempFile = new File(ShipSchematics.getInstance().getDataFolder().getAbsolutePath()
                + "/impounds/" + TextUtils.nullableUUIDToString(impound.getPlayerUUID()) + "-new.yml");
        try {
            Files.copy(impoundsFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            e.printStackTrace();
            notifyPlayer.sendMessage("Error adding impound to list; your schematic has still been saved.");
            return;
        }
        if (ImpoundFileUtils.writeImpoundToFile(tempFile, impound) && impoundsFile.delete() && tempFile.renameTo(impoundsFile)) {
            notifyPlayer.sendMessage("Successfully added impound to list.");
        } else
            notifyPlayer.sendMessage("Error adding impound to list; your schematic has still been saved.");
    }
}
