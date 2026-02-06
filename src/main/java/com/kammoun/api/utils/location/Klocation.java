package com.kammoun.api.utils.location;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
public class Klocation {

    private final Vector vector;
    private final String worldName;
    @Setter
    private float yaw;
    @Setter
    private float pitch;

    public Klocation(Vector vector, String worldName) {
        this.vector = vector;
        this.worldName = worldName;
    }

    public Klocation(Vector vector, String worldName, float yaw, float pitch) {
        this.vector = vector;
        this.worldName = worldName;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    public Klocation(Location location){
        this.vector = new Vector(location.getX(), location.getY(), location.getZ());
        this.worldName = location.getWorld().getName();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }



    public double getX(){
        return vector.getX();
    }
    public double getY(){
        return vector.getY();
    }
    public double getZ(){
        return vector.getZ();
    }


    @Nullable
    public Location getLocation(){
        if(Bukkit.getWorld(worldName) == null){
            Bukkit.getConsoleSender().sendMessage("§cWorld with the name "+ worldName + "not found");
            return null;
        }
        Location loc = new Location(Bukkit.getWorld(worldName),vector.getX(),vector.getY(),vector.getZ());
        loc.setYaw(yaw);
        loc.setPitch(pitch);
        return loc;
    }


    public static Klocation getLocationFromConfig(@NotNull ConfigurationSection section){
        double x = section.getDouble("x", 0.0);
        double y = section.getDouble("y", 0.0);
        double z = section.getDouble("z", 0.0);
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");
        return new Klocation(new Vector(x, y, z), section.getString("world","world"), yaw, pitch);
    }

}
