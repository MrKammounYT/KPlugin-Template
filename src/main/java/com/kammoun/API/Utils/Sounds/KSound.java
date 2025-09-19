package com.kammoun.API.Utils.Sounds;


import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@Getter
public class KSound {


    private final Sound sound;
    private final float pitch;
    private final float volume;
    public KSound(Sound sound) {
        this.sound = sound;
        this.volume = 1;
        this.pitch = 1;
    }

    public KSound(Sound sound, float pitch, float volume) {
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
    }


    public void playSound(Player player){
        player.playSound(player.getLocation(), sound, volume, pitch);
    }


    @Nullable
    public static KSound getSoundFromConfig(ConfigurationSection section){
        if(section == null) return null;
        try {
            Sound sound = Sound.valueOf(section.getString("sound","NULL").toUpperCase());
            float pitch = Float.parseFloat(section.getString("pitch","1.0"));
            float volume = Float.parseFloat(section.getString("volume","1.0"));
            return new KSound(sound, pitch, volume);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
