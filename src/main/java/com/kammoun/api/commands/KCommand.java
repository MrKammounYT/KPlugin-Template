package com.kammoun.api.commands;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public abstract class KCommand implements CommandExecutor, TabCompleter {

    private final String commandName;
    private final String permission;
    private final boolean playerOnly;
    private final List<KSubCommand> subCommands = new ArrayList<>();


    public KCommand(String commandName, String permission, boolean playerOnly) {
        this.commandName = commandName;
        this.permission = permission;
        this.playerOnly = playerOnly;
    }

    public KCommand(String commandName) {
        this(commandName, null, true);
    }


    public abstract void execute(CommandSender sender, String[] args);


    public abstract List<String> onTab(CommandSender sender, String[] args);


    protected void addSubCommand(KSubCommand... commands) {
        subCommands.addAll(Arrays.asList(commands));
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (playerOnly && !(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by a player.");
            return true;
        }

        if (permission != null && !sender.hasPermission(permission)) {
            return true;
        }

        if (args.length > 0) {
            for (KSubCommand subCommand : subCommands) {
                if (subCommand.getName().equalsIgnoreCase(args[0])) {
                    subCommand.perform(sender, Arrays.copyOfRange(args, 1, args.length));
                    return true;
                }
            }
        }

        execute(sender, args);
        return true;
    }

    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return subCommands.stream()
                    .map(KSubCommand::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .filter(name -> {
                        KSubCommand sub = subCommands.stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
                        return sub == null || sub.getPermission() == null || sender.hasPermission(sub.getPermission());
                    })
                    .collect(Collectors.toList());
        }

        if (args.length > 1) {
            for (KSubCommand subCommand : subCommands) {
                if (subCommand.getName().equalsIgnoreCase(args[0])) {
                    return subCommand.getSubCommandTab(sender, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }

        return onTab(sender, args);
    }
}
