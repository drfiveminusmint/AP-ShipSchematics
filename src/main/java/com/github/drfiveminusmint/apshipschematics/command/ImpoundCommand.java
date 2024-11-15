package com.github.drfiveminusmint.apshipschematics.command;

import com.github.drfiveminusmint.apshipschematics.ShipSaver;
import com.github.drfiveminusmint.apshipschematics.ShipSchematics;
import com.github.drfiveminusmint.apshipschematics.impound.*;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.util.ComponentPaginator;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class ImpoundCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("impound") || args.length == 0)
            return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return true;
        }
        if (args[0].equalsIgnoreCase("search"))
            return searchCommand((Player)sender, args);
        if (args[0].equalsIgnoreCase("debug"))
            return tempDebugCommand((Player) sender);
        if (args[0].equalsIgnoreCase("info"))
            return infoCommand(((Player) sender), args);
        if (args[0].equalsIgnoreCase("create"))
            return createCommand(((Player) sender), args);
        if (args[0].equalsIgnoreCase("remove"))
            return removeCommand(((Player) sender), args);
        if (args[0].equalsIgnoreCase("load"))
            return loadCommand(((Player) sender), args);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }

    private boolean searchCommand(@NotNull Player sender, @NotNull String[] args) {
        if (args.length == 1) {
            sender.sendMessage("Please provide a player, UUID, or 'unknown'.");
            return true;
        }
        ComponentPaginator results = ShipSchematics.getImpoundManager().getSearchResults(sender);
        if (results != null)
            try {
                int page = Integer.parseInt(args[1]);
                if (!results.isInBounds(page)) {
                    sender.sendMessage("Invalid page: " + args[1]);
                    return true;
                }
                if (results.isEmpty()) {
                    sender.sendMessage("Error: Results Expired.");
                    return true;
                }
                for (Component line : results.getPage(page))
                    sender.sendMessage(line);
                return true;
            }
            catch (NumberFormatException e) {}
        UUID searchUUID;
        try {
            searchUUID = getNullableUUID(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Could not resolve player or UUID " + args[1]);
            return true;
        }
        sender.sendMessage("Indexing file, please wait...");
        ShipSchematics.getImpoundManager().addTask(new ImpoundSearchTask(sender, searchUUID, args));
        return true;
    }

    private boolean infoCommand(@NotNull Player sender, String[] args) {
        if (args.length <= 2) {
            sender.sendMessage("Insufficient arguments. Use /impound info (<player>/unknown) <name>");
            return true;
        }
        UUID searchUUID;
        try {
            searchUUID = getNullableUUID(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Could not resolve player or UUID " + args[1]);
            return true;
        }
        sender.sendMessage("Indexing file, please wait...");
        ShipSchematics.getImpoundManager().addTask(new ImpoundInfoTask(sender, searchUUID, args[2]));
        return true;
    }

    private boolean createCommand(Player sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage("Please supply a schematic name.");
            return true;
        }
        Craft craftToSave = CraftManager.getInstance().getCraftByPlayer(sender);
        if (craftToSave == null) {
            sender.sendMessage("Pilot the craft you want to save first.");
            return true;
        }
        UUID ownerUUID = null;
        for (MovecraftLocation location : craftToSave.getHitBox()) {
            BlockState blockState = craftToSave.getWorld().getBlockAt(location.toBukkit(craftToSave.getWorld())).getState();
            if (!(blockState instanceof Sign)) continue;
            // Prioritize pilot signs
            if (((Sign)blockState).getLine(0).equalsIgnoreCase("Pilot:")) {
                if (Bukkit.getServer().getPlayerUniqueId(((Sign) blockState).getLine(1)) != null) {
                    ownerUUID = Bukkit.getServer().getPlayerUniqueId(((Sign) blockState).getLine(1));
                    break;
                }
            } else if (((Sign) blockState).getLine(0).equalsIgnoreCase("Crew:")
                        || ((Sign) blockState).getLine(0).toString().equalsIgnoreCase("[Private]")) {
                if (Bukkit.getServer().getPlayerUniqueId(((Sign) blockState).getLine(1)) != null) {
                    ownerUUID = Bukkit.getServer().getPlayerUniqueId(((Sign) blockState).getLine(1));
                    break;
                }
            }
        }
        double unimpoundCost = 0d;
        for (int i = 2; i < args.length; i++) {
            if (args[i].toLowerCase().contains("cost:") || args[i].toLowerCase().contains("c:"))
                try {
                    unimpoundCost = Double.parseDouble(args[i].substring(args[i].indexOf(':') + 1));
                } catch (NumberFormatException ex) {
                    unimpoundCost = 0d;
                }
            else if (args[i].toLowerCase().contains("player:") || args[i].toLowerCase().contains("p:"))
                ownerUUID = Bukkit.getServer().getPlayerUniqueId(args[i].substring(args[i].indexOf(':') + 1));
        }
        String schemName = args[1];
        //TODO save schematic
        if (new File(ShipSchematics.getImpoundFolder(), ownerUUID + "/" + schemName + ".schem").exists())
        {
            sender.sendMessage("This schematic already exists.");
            return true;
        }
        if (!ShipSaver.trySaveShip(sender,  schemName, "AP-ShipSchematics/impounds/" + ownerUUID))
        {
            sender.sendMessage("Error saving your schematic.");
            return true;
        }
        Impound resultImpound = new Impound(
                schemName,
                ownerUUID,
                sender.getUniqueId(),
                craftToSave.getType().getStringProperty(CraftType.NAME),
                unimpoundCost,
                sender.getLocation().toBlockLocation()
        );
        ShipSchematics.getImpoundManager().addTask(new ImpoundCreateTask(sender, resultImpound));
        return true;
    }

    private boolean removeCommand(Player sender, String[] args) {
        if (args.length <= 2) {
            sender.sendMessage("Insufficient arguments. Use /impound remove (<player>/unknown) <name>");
            return true;
        }
        UUID searchUUID;
        try {
            searchUUID = getNullableUUID(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Could not resolve player or UUID " + args[1]);
            return true;
        }
        ShipSchematics.getImpoundManager().addTask(new ImpoundRemoveTask(sender, searchUUID, args[2]));
        return true;
    }

    private boolean loadCommand(Player sender, String[] args) {
        //loading logic
        if (args.length < 3)
        {
            sender.sendMessage("Insufficient arguments. Use /impound load (<player>/unknown) <name>");
        }
        File searchFile;
        try {
            UUID searchUUID = getNullableUUID(args[1]);
            searchFile = new File(ShipSchematics.getImpoundFolder(), searchUUID.toString());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Could not resolve player or UUID " + args[1]);
            return true;
        }
        ShipSchematics.getImpoundManager().addTask(new ImpoundLoadTask(sender, searchFile, args[2]));
        return true;
    }

    @Nullable
    private UUID getNullableUUID(String s) {
        UUID temp;
        if (s.equalsIgnoreCase("unknown"))
            temp = null;
        else {
            temp = Bukkit.getServer().getPlayerUniqueId(s);
            if (temp == null)
                temp = UUID.fromString(s);
        }
        return temp;
    }

    private boolean tempDebugCommand(Player sender) {
        sender.sendMessage(sender.getLocation().toString());
        return true;
    }
}
