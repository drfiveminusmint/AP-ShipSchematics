package com.github.drfiveminusmint.apshipschematics.impound;

import com.github.drfiveminusmint.apshipschematics.ShipSchematics;
import com.github.drfiveminusmint.apshipschematics.TextUtils;
import net.countercraft.movecraft.util.ComponentPaginator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public class ImpoundSearchTask implements ImpoundTask {
    private final UUID searchUUID;
    private final Player notifyPlayer;
    private String nameContains;

    public ImpoundSearchTask(@NotNull Player notifyPlayer, @Nullable UUID targetUUID, String... flags)
    {
        this.notifyPlayer = notifyPlayer;
        this.searchUUID = targetUUID;
        for (String s : flags) {
            String lc = s.toLowerCase();
            if (lc.contains("namecontains:")) {
                nameContains = lc.substring(lc.lastIndexOf("namecontains:")+13);
                continue;
            }
            if (lc.contains("nc:")) {
                nameContains = lc.substring(lc.lastIndexOf("nc:")+3);
                continue;
            }
        }
    }
    public void run() {
        File searchFile = new File(ShipSchematics.getInstance().getDataFolder().getAbsolutePath()
                + "/impounds/" + TextUtils.nullableUUIDToString(searchUUID) + ".yml");
        if (!searchFile.exists()) {
            notifyPlayer.sendMessage("Cannot find any impounds for UUID " + searchUUID);
            return;
        }
        ComponentPaginator results = new ComponentPaginator(
                Component.text("Impound Search"), (pageNumber) -> "/impound search " + pageNumber);
        for (Impound impound : ImpoundFileUtils.indexImpounds(searchFile))
        {
            if (nameContains != null && !impound.getSchematic().toLowerCase().contains(nameContains))
                continue;
            Component result = Component.text(impound.getSchematic())
                    .append(Component.text(" [Info]")
                    .color(NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand(new StringBuilder().append("/impound info ").append(searchUUID).append(" ").append(impound.getSchematic()).toString())));
            results.addLine(result);
        }
        if (results.isEmpty()) {
            notifyPlayer.sendMessage("No impounds found for this query.");
            return;
        }
        ShipSchematics.getImpoundManager().addSearchResult(notifyPlayer, results);
        for (Component line : results.getPage(1))
            notifyPlayer.sendMessage(line);
    }
}
