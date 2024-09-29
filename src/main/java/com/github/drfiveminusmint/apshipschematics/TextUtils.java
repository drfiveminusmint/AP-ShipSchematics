package com.github.drfiveminusmint.apshipschematics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class TextUtils {
    @NotNull
    public static String locationToStringReadable(@Nullable Location location) {
        if (location == null)
            return "(Unknown)";
        return new StringBuilder().append("{")
                .append("World: ").append(location.getWorld().getName()).append(", ")
                .append("X: ").append(location.getBlockX()).append(", ")
                .append("Y: ").append(location.getBlockY()).append(", ")
                .append("Z: ").append(location.getBlockZ()).append(", ")
                .append("}").toString();
    }

    @Nullable
    public static Location locationFromString(String string) {
        if (string.equalsIgnoreCase("(Unknown)"))
            return null;
        World world = (string.contains("World: ")) ? Bukkit.getWorld(string.substring(string.indexOf("World: ") + 7, string.indexOf(','))) : null;

        String[] elements = string.split(",");
        if (elements.length < 4)
            return null;
        int x,y,z;
        try {
            x = Integer.parseInt(elements[1].substring(elements[1].indexOf("X: ") + 3));
            y = Integer.parseInt(elements[2].substring(elements[2].indexOf("Y: ") + 3));
            z = Integer.parseInt(elements[3].substring(elements[3].indexOf("Z: ") + 3));
        } catch (NumberFormatException e) {
            return null;
        }
        return new Location(world, x, y, z);
    }

    @NotNull
    public static String nullableUUIDToString(@Nullable UUID uuid)
    {
        return (uuid != null) ? uuid.toString() : "unknown";
    }
}
