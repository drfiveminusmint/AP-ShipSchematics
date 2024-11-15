package com.github.drfiveminusmint.apshipschematics.impound;

import com.github.drfiveminusmint.apshipschematics.Settings;
import com.github.drfiveminusmint.apshipschematics.ShipSchematics;
import com.github.drfiveminusmint.apshipschematics.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public class ImpoundInfoTask implements ImpoundTask {
    private final Player notifyPlayer;
    private final UUID searchUUID;
    private final String impoundName;

    public ImpoundInfoTask(@NotNull Player notifyPlayer, @Nullable UUID searchUUID, String name) {
        this.notifyPlayer = notifyPlayer;
        this.searchUUID = searchUUID;
        this.impoundName = name;
    }

    @Override
    public void run() {
        File searchFile = new File(ShipSchematics.getInstance().getDataFolder().getAbsolutePath()
                + "/impounds/" + TextUtils.nullableUUIDToString(searchUUID) + ".yml");
        if (!searchFile.exists()) {
            notifyPlayer.sendMessage("Cannot find any impounds for UUID " + TextUtils.nullableUUIDToString(searchUUID));
            return;
        }
        for (Impound impound : ImpoundFileUtils.indexImpounds(searchFile)) {
            if (impound.getSchematic().equals(impoundName)) {
                displayImpoundInfo(impound);
                return;
            }
        }
        notifyPlayer.sendMessage("Cannot find any impounds matching the name " + impoundName);
    }

    private void displayImpoundInfo(Impound impound) {
        notifyPlayer.sendMessage(Component.text("==Impound Info==").color(NamedTextColor.GOLD));
        notifyPlayer.sendMessage(buildComponent("Name/Schematic",impoundName));
        if (impound.getCreatorUUID() != null && Bukkit.getServer().getOfflinePlayer(impound.getPlayerUUID()).getName() != null)
            notifyPlayer.sendMessage(buildComponent("Owner", Bukkit.getServer().getOfflinePlayer(impound.getPlayerUUID()).getName()));
        else
            notifyPlayer.sendMessage(buildComponent("Owner", TextUtils.nullableUUIDToString(impound.getPlayerUUID())));
        if (impound.getCreatorUUID() != null && Bukkit.getServer().getOfflinePlayer(impound.getCreatorUUID()).getName() != null)
            notifyPlayer.sendMessage(buildComponent("Creator", Bukkit.getServer().getOfflinePlayer(impound.getCreatorUUID()).getName()));
        else
            notifyPlayer.sendMessage(buildComponent("Creator", TextUtils.nullableUUIDToString(impound.getCreatorUUID())));
        notifyPlayer.sendMessage(buildComponent("Date Created", impound.getDateCreated().toString()));
        notifyPlayer.sendMessage(buildComponent("Craft Type", impound.getCraftType()));
        if (impound.getLocation() != null)
            notifyPlayer.sendMessage(buildComponent("Location", String.format("[X:%d Y:%d Z:%d] ", impound.getLocation().getBlockX(), impound.getLocation().getBlockY(), impound.getLocation().getBlockZ())));
        else
            notifyPlayer.sendMessage(buildComponent("Location","(Unknown)"));
        notifyPlayer.sendMessage(buildComponent("Unimpound Cost", String.valueOf(impound.getUnimpoundCost())));
        notifyPlayer.sendMessage(Component.text("[Load Schematic]").clickEvent(ClickEvent.runCommand("/impound load " + impound.getPlayerUUID() + " " + impound.getSchematic()))
                        .append(Component.text(" [Charge Unimpound Cost] ").clickEvent(ClickEvent.runCommand("/eco take " + impound.getPlayerUUID() + " " + impound.getUnimpoundCost())))
                        .append(Component.text("[Remove]").clickEvent(ClickEvent.runCommand("/impound remove " + impound.getPlayerUUID() + " " + impound.getSchematic()))).color(NamedTextColor.AQUA));
    }

    private Component buildComponent(String label, String value) {
        return Component.text(label+": ").color(NamedTextColor.GRAY).append(Component.text(value).color(NamedTextColor.WHITE));
    }
}
