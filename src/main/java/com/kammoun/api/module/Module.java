package com.kammoun.api.module;

import org.bukkit.plugin.java.JavaPlugin;

public interface Module {


    void onEnable(JavaPlugin plugin);
    void onDisable(JavaPlugin plugin);


    String getName();


}
