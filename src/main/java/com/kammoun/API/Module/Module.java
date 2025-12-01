package com.kammoun.API.Module;

import org.bukkit.plugin.java.JavaPlugin;

public interface Module {


    void onEnable(JavaPlugin plugin);
    void onDisable(JavaPlugin plugin);


    String getName();


}
