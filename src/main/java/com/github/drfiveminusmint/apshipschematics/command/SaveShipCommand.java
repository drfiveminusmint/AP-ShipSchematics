package com.github.drfiveminusmint.apshipschematics.command;

import com.github.drfiveminusmint.apshipschematics.ShipSaver;
import net.countercraft.movecraft.util.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SaveShipCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!command.getName().equalsIgnoreCase("saveship"))
            return false;
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatUtils.errorPrefix() + "Only players can use this command.");
            return true;
        }

        if (strings.length < 1) {
            commandSender.sendMessage(ChatUtils.errorPrefix() + "Please provide a name for the schematic.");
            return true;
        }

        if (!ShipSaver.trySaveShip((Player) commandSender, strings[0])) {
            commandSender.sendMessage(ChatUtils.errorPrefix() + "Something went wrong saving your schematic.");
            return true;
        }
        return false;
    }
}
