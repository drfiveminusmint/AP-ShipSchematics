package com.github.drfiveminusmint.apshipschematics.impound;

import com.github.drfiveminusmint.apshipschematics.ShipSchematics;
import com.github.drfiveminusmint.apshipschematics.TextUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ImpoundFileUtils {
    //This class contains all methods the Impounds feature of this plugin uses to read and write files.
    //Only ImpoundTasks should call any of these methods.

    public static Set<Impound> indexImpounds(File f) {
        //get the impounds from a file
        Set<Impound> results = new HashSet<>();
        UUID playerUUID;
        try {
            playerUUID = UUID.fromString(f.getName().substring(0, f.getName().length()-4));
        } catch (IllegalArgumentException e) {
            playerUUID = null;
        }
        ConfigurationSection globalSection = YamlConfiguration.loadConfiguration(f);
        for (String name : globalSection.getKeys(false)) {
            if(!globalSection.isConfigurationSection(name)) {
                ShipSchematics.getInstance().getLogger().severe("Failed to load impound '" + name + "': Impropper formatting.");
                continue;
            }
            ConfigurationSection subsection = globalSection.getConfigurationSection(name);
            UUID creatorUUID;
            try {
                String temp = subsection.getString("CreatorUUID");
                if (temp == null)
                    creatorUUID = null;
                else
                    creatorUUID = UUID.fromString(temp);
            } catch (IllegalArgumentException e) {
                creatorUUID = null;
            }
            results.add(new Impound(
                    name,
                    playerUUID,
                    creatorUUID,
                    subsection.getString("CraftType", "Unknown"),
                    subsection.getDouble("UnimpoundCost", 0d),
                    TextUtils.locationFromString(subsection.getString("Location", "(Unknown)")),
                    new Date(subsection.getString("DateCreated", new Date().toString()))
            ));
        }
        return results;
    }

    public static boolean writeImpoundToFile(File f, Impound i) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(f,true));
            writer.write(inQuotes(i.getSchematic()) + ":");
            writer.write('\n');
            writer.write("      CreatorUUID: ");
            writer.write(inQuotes((i.getCreatorUUID() != null) ? i.getCreatorUUID().toString() : "(Unknown)"));
            writer.write('\n');
            writer.write("      DateCreated: ");
            writer.write(inQuotes(i.getDateCreated().toString()));
            writer.write('\n');
            writer.write("      CraftType: ");
            writer.write(inQuotes(i.getCraftType()));
            writer.write('\n');
            writer.write("      Location: ");
            writer.write(inQuotes(TextUtils.locationToStringReadable(i.getLocation())));
            writer.write('\n');
            writer.write("      UnimpoundCost: ");
            writer.write(Double.toString(i.getUnimpoundCost()));
            writer.write('\n');
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static String inQuotes (String s) {return new StringBuilder().append('\'').append(s).append('\'').toString();}
}
