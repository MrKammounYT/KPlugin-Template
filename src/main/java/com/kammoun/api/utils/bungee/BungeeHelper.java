package com.kammoun.api.utils.bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BungeeHelper {


    private final Plugin plugin;

    public BungeeHelper(Plugin plugin) {
        this.plugin = plugin;

        // Register plugin messaging channel
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }


    public void sendBungeeCommand(Player player, String command) {
        if (player == null || !player.isOnline()) {
            plugin.getLogger().warning("Cannot send BungeeCord command: Player is null or offline");
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ForwardToServer");
        out.writeUTF("ALL");

        ByteArrayDataOutput innerData = ByteStreams.newDataOutput();
        innerData.writeUTF("DispatchCommand");
        innerData.writeUTF(command);

        byte[] innerBytes = innerData.toByteArray();
        out.writeShort(innerBytes.length);
        out.write(innerBytes);

        // Send the plugin message
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    /**
     * Connects a player to another server in the BungeeCord network
     * @param player The player to connect
     * @param server The server name to connect to
     */
    public void connectToServer(Player player, String server) {
        if (player == null || !player.isOnline()) {
            plugin.getLogger().warning("Cannot connect player to server: Player is null or offline");
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    /**
     * Sends a message to a specific player on the BungeeCord network
     * @param player The player sending the message
     * @param targetPlayer The player to receive the message
     * @param message The message to send
     */
    public void sendPlayerMessage(Player player, String targetPlayer, String message) {
        if (player == null || !player.isOnline()) {
            plugin.getLogger().warning("Cannot send player message: Player is null or offline");
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Message");
        out.writeUTF(targetPlayer);
        out.writeUTF(message);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    /**
     * Kick a player from the BungeeCord network with a reason
     * @param player The player sending the kick request
     * @param targetPlayer The player to kick
     * @param reason The reason for the kick
     */
    public void kickPlayer(Player player, String targetPlayer, String reason) {
        if (player == null || !player.isOnline()) {
            plugin.getLogger().warning("Cannot kick player: Player is null or offline");
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("KickPlayer");
        out.writeUTF(targetPlayer);
        out.writeUTF(reason);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }


    public void cleanup() {
        plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin);
    }
}
