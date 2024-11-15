package com.github.drfiveminusmint.apshipschematics.impound;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.countercraft.movecraft.repair.util.WEUtils;
import net.countercraft.movecraft.util.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;

public class ImpoundLoadTask implements ImpoundTask {
    private final File directory;
    private final Player executingPlayer;
    private final String name;

    public ImpoundLoadTask(Player p, File f, String s) {
        directory = f;
        executingPlayer = p;
        name = s;
    }

    @Override
    public void run() {
        // We can hook into Repair for this
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(new BukkitPlayer(executingPlayer));
        try {
            Clipboard clipboard = WEUtils.loadSchematic(directory, name);
            session.setClipboard(new ClipboardHolder(clipboard));
        } catch (FileNotFoundException e) {
            executingPlayer.sendMessage(ChatUtils.errorPrefix().append(Component.text("File not found")));
            return;
        }
        executingPlayer.sendMessage("Successfully loaded schematic " + name);
    }
}
