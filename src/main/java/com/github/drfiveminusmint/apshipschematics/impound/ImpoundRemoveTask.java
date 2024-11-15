package com.github.drfiveminusmint.apshipschematics.impound;

import com.github.drfiveminusmint.apshipschematics.ShipSchematics;
import com.github.drfiveminusmint.apshipschematics.TextUtils;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ImpoundRemoveTask implements ImpoundTask {
    private final Player notifyPlayer;
    private final UUID targetUUID;
    private final String impoundName;

    public ImpoundRemoveTask(Player notifyPlayer, UUID targetUUID, String impoundName) {
        this.notifyPlayer = notifyPlayer;
        this.targetUUID = targetUUID;
        this.impoundName = impoundName;
    }

    public void run() {
        File impoundsFile = new File(ShipSchematics.getImpoundFolder(), TextUtils.nullableUUIDToString(targetUUID) + ".yml");
        File tempFile = new File(ShipSchematics.getImpoundFolder(), TextUtils.nullableUUIDToString(targetUUID) + "-new.yml");
        try {
           if (tempFile.exists())
               tempFile.delete();
           tempFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            notifyPlayer.sendMessage("Error removing impound from list: Couldn't create temp file.");
            return;
        }
        boolean found = false;
        for (Impound impound : ImpoundFileUtils.indexImpounds(impoundsFile)) {
            if (!impound.getSchematic().equalsIgnoreCase(impoundName) && !ImpoundFileUtils.writeImpoundToFile(tempFile, impound)) {
                notifyPlayer.sendMessage("Error removing impound from list: Couldn't write to temp file.");
                tempFile.delete();
                return;
            } else
                found = true;
        }
        if (!found) {
            notifyPlayer.sendMessage("Could not find target impound " + impoundName);
            tempFile.delete();
            return;
        }
        if (impoundsFile.delete() && tempFile.renameTo(impoundsFile))
            notifyPlayer.sendMessage("Successfully removed impound " + impoundName);
        else {
            notifyPlayer.sendMessage("Error removing impound from list: Couldn't replace impound file.");
            return;
        }

        // Next, delete the schematic
        File schematicFile = new File(ShipSchematics.getImpoundFolder(), targetUUID + "/" + impoundName + ".schem");
        if (schematicFile.exists())
            schematicFile.delete();
    }
}
