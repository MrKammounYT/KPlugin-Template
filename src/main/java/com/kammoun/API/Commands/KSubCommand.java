package com.kammoun.API.Commands;

import org.bukkit.command.CommandSender;
import java.util.List;

public interface KSubCommand {


    String getName();


    String getPermission();


    void perform(CommandSender sender, String[] args);


    List<String> getSubCommandTab(CommandSender sender, String[] args);
}