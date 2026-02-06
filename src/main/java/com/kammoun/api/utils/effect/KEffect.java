package com.kammoun.api.utils.effect;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class KEffect {

    private final PotionEffectType type;
    private int duration; // in seconds!
    private int amplifier;
    private boolean ambient;
    private boolean particles;

    public KEffect(@NotNull PotionEffectType type) {
        this.type = type;
        this.duration = 30; // Default duration of 30 seconds
        this.amplifier = 0; // Default amplifier (level 1)
        this.ambient = false;
        this.particles = true;
    }

    public void duration(int seconds) {
        this.duration = seconds;
    }

    public void amplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public void ambient(boolean ambient) {
        this.ambient = ambient;
    }

    public void particles(boolean particles) {
        this.particles = particles;
    }

    public PotionEffect build() {
        return new PotionEffect(type, duration * 20, amplifier, ambient, particles);
    }

    public void apply(@NotNull Player player) {
        player.addPotionEffect(build());
    }

    public static void clearAll(@NotNull Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    public static KEffect fromConfig(@NotNull ConfigurationSection config) {
        PotionEffectType type = PotionEffectType.getByName(config.getString("type", ""));
        if (type == null) {
            return null;
        }

        KEffect kEffect = new KEffect(type);
        kEffect.duration(config.getInt("duration", 30));
        kEffect.amplifier(config.getInt("amplifier", 0));
        kEffect.ambient(config.getBoolean("ambient", false));
        kEffect.particles(config.getBoolean("particles", true));

        return kEffect;
    }
}