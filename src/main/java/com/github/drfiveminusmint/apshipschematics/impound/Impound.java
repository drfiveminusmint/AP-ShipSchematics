package com.github.drfiveminusmint.apshipschematics.impound;


import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import java.util.Date;
import java.util.UUID;

public class Impound {
    private final UUID playerUUID;
    private final String schematic;
    private final Date dateCreated;
    private final UUID creatorUUID;
    private final String craftType;
    private final double unimpoundCost;
    private final Location location;

    public Impound(String schematic, @Nullable UUID playerUUID, @Nullable UUID creatorUUID, String craftType, double unimpoundCost, Location location) {
        this(schematic, playerUUID, creatorUUID, craftType, unimpoundCost, location, new Date());
    }

    public Impound(String schematic, @Nullable UUID playerUUID, @Nullable UUID creatorUUID, String craftType, double unimpoundCost, Location location, Date date) {
        this.schematic = schematic;
        this.playerUUID = playerUUID;
        this.creatorUUID = creatorUUID;
        this.craftType = craftType;
        this.dateCreated = date;
        this.unimpoundCost = unimpoundCost;
        this.location = location;
    }

    public Date getDateCreated() {return dateCreated;}
    public UUID getPlayerUUID() {return playerUUID;}
    public UUID getCreatorUUID() {return creatorUUID;}
    public String getSchematic() {return schematic;}
    public String getCraftType() {return craftType;}
    public double getUnimpoundCost() {return unimpoundCost;}
    public Location getLocation() {return location;}
}
