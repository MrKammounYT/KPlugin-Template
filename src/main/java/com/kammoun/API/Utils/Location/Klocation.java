package com.kammoun.API.Utils.Location;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
public class Klocation {

    private final Vector vector;
    private final String WorldName;
    @Setter
    private float yaw;
    @Setter
    private float pitch;

    public Klocation(Vector vector, String worldName) {
        this.vector = vector;
        this.WorldName = worldName;
    }

    public Klocation(Vector vector, String worldName, float yaw, float pitch) {
        this.vector = vector;
        this.WorldName = worldName;
        this.yaw = yaw;
        this.pitch = pitch;
    }




    @Nullable
    public Location getLocation(){
        if(Bukkit.getWorld(WorldName) == null){
            Bukkit.getConsoleSender().sendMessage("§cWorld with the name "+ WorldName + "not found");
            return null;
        }
        Location loc = new Location(Bukkit.getWorld(WorldName),vector.getX(),vector.getY(),vector.getZ());
        loc.setYaw(yaw);
        loc.setPitch(pitch);
        return loc;
    }


    public static Klocation getLocationFromConfig(@NotNull ConfigurationSection section){
        int x = section.getInt("x",0);
        int y = section.getInt("y",0);
        int z = section.getInt("z",0);
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");
        return new Klocation(new Vector(x, y, z), section.getString("world","world"), yaw, pitch);
    }

}
