package com.kammoun.API.Configuration;

import com.kammoun.API.Utils.Chat.ChatFormater;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class KConfigLoader {

    protected final JavaPlugin plugin;
    private final File file;
    private final String fileName;

    @Getter
    protected FileConfiguration config;


    public KConfigLoader(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
        load();
    }


    public final void load() {
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource(fileName, false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }


    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config file: " + fileName);
            e.printStackTrace();
        }
    }

    // --- Utility Methods ---

    public String getString(String path) {
        return config.getString(path, "Path not found: " + path);
    }

    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    public String getFormattedString(String path) {
        return ChatFormater.color(getString(path));
    }

    public int getInt(String path) {
        return config.getInt(path, 0);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path, false);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public List<String> getFormattedStringList(String path) {
        return getStringList(path).stream()
                .map(ChatFormater::color)
                .collect(Collectors.toList());
    }
}