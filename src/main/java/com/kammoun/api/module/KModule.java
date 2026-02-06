package com.kammoun.api.module;

import com.kammoun.api.commands.KCommand;
import com.kammoun.api.utils.bungee.BungeeHelper;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public abstract class KModule<T extends JavaPlugin> implements Module {

    private final String moduleName;
    protected T api;
    protected File moduleDataFolder;
    private BungeeHelper bungeeHelper;
    private FileConfiguration config;

    public KModule(String moduleName) {
        if (moduleName == null || moduleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Module name cannot be null or empty");
        }
        this.moduleName = moduleName;
    }

    @Override
    public final void onEnable(JavaPlugin api) {
        if (api == null) {
            throw new IllegalArgumentException("JavaPlugin instance cannot be null");
        }

        this.api = (T) api;
        this.moduleDataFolder = new File(api.getDataFolder(), moduleName);
        this.bungeeHelper = new BungeeHelper(api);


        if (!moduleDataFolder.exists() && !moduleDataFolder.mkdirs()) {
            getLogger().warning("Failed to create module data folder: " + moduleDataFolder.getPath());
        }

        saveDefaultConfig("config.yml");
        loadConfig();

        try {
            onModuleEnable();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error enabling module " + moduleName, e);
        }
    }

    @Override
    public final void onDisable(JavaPlugin api) {
        try {
            onModuleDisable();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error disabling module " + moduleName, e);
        }

        this.config = null;
        this.api = null;
    }

    @Override
    public final String getName() {
        return this.moduleName;
    }

    /**
     * Called when the module is enabled
     */
    public abstract void onModuleEnable();

    /**
     * Called when the module is disabled
     */
    public abstract void onModuleDisable();

    /**
     * Reloads the module configuration and state
     */
    public void reload() {
        reloadConfig();
    }

    /**
     * Gets the module's logger
     */
    public Logger getLogger() {
        return api != null ? api.getLogger() : Logger.getLogger(moduleName);
    }

    /**
     * Saves the default config file if it doesn't exist
     */
    public void saveDefaultConfig(String configName) {
        if (configName == null || configName.trim().isEmpty()) {
            getLogger().warning("Config name cannot be null or empty");
            return;
        }

        File configFile = new File(moduleDataFolder, configName);
        if (configFile.exists()) {
            return;
        }

        String resourcePath = moduleName + "/" + configName;
        try (InputStream in = api.getResource(resourcePath)) {
            if (in == null) {
                getLogger().warning("Could not find default config: " + resourcePath);
                return;
            }

            Files.copy(in, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            getLogger().info("Created default config: " + configName);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save default config: " + configName, e);
        }
    }

    /**
     * Loads the module configuration
     */
    private void loadConfig() {
        File configFile = new File(moduleDataFolder, "config.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Reloads the module configuration from disk
     */
    public void reloadConfig() {
        loadConfig();
    }

    /**
     * Saves the current configuration to disk
     */
    public void saveConfig() {
        if (config == null) {
            getLogger().warning("Cannot save null configuration");
            return;
        }

        File configFile = new File(moduleDataFolder, "config.yml");
        try {
            config.save(configFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
        }
    }

    /**
     * Registers a command executor
     */
    public boolean registerCommand(CommandExecutor command, String commandName) {
        if (commandName == null || commandName.trim().isEmpty()) {
            getLogger().warning("Command name cannot be null or empty");
            return false;
        }

        if (command == null) {
            getLogger().warning("Command executor cannot be null for: " + commandName);
            return false;
        }

        PluginCommand pluginCommand = api.getCommand(commandName);
        if (pluginCommand == null) {
            getLogger().warning("Command not found in plugin.yml: " + commandName);
            return false;
        }

        pluginCommand.setExecutor(command);
        return true;
    }

    /**
     * Registers a KCommand with both executor and tab completer
     */
    public boolean registerCommand(KCommand command, String commandName) {
        if (commandName == null || commandName.trim().isEmpty()) {
            getLogger().warning("Command name cannot be null or empty");
            return false;
        }

        if (command == null) {
            getLogger().warning("KCommand cannot be null for: " + commandName);
            return false;
        }

        PluginCommand pluginCommand = api.getCommand(commandName);
        if (pluginCommand == null) {
            getLogger().warning("Command not found in plugin.yml: " + commandName);
            return false;
        }

        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);
        return true;
    }

    /**
     * Registers an event listener
     */
    public void registerEvent(Listener listener) {
        if (listener == null) {
            getLogger().warning("Listener cannot be null");
            return;
        }

        Bukkit.getPluginManager().registerEvents(listener, api);
    }

    /**
     * Gets the module's data folder
     */
    public File getDataFolder() {
        return moduleDataFolder;
    }

    /**
     * Gets the module's configuration
     */
    public FileConfiguration getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }
}