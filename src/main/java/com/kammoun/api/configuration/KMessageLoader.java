package com.kammoun.api.configuration;

import com.kammoun.api.utils.chat.ChatFormater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kammoun.api.utils.PlaceHolderHelper.parsePlaceholders;

public class KMessageLoader<T extends Enum<T> & MessageKey> extends KConfigLoader {

    private final Class<T> enumClass;
    private final Map<T, List<String>> messages = new HashMap<>();

    public KMessageLoader(JavaPlugin plugin, String fileName, Class<T> enumClass) {
        super(plugin, fileName);
        this.enumClass = enumClass;
        loadMessages();
        fillMissingMessages();
    }

    private void loadMessages() {
        for (T key : enumClass.getEnumConstants()) {
            if (config.contains(key.getPath())) {
                Object raw = config.get(key.getPath());
                if (raw instanceof List<?> list) {
                    messages.put(key, list.stream().map(Object::toString).collect(Collectors.toList()));
                } else if (raw instanceof String s) {
                    messages.put(key, List.of(s));
                } else {
                    messages.put(key, Arrays.asList(key.getDefault()));
                }
            } else {
                messages.put(key, Arrays.asList(key.getDefault()));
            }
        }
    }

    private void fillMissingMessages() {
        boolean changed = false;
        for (T key : enumClass.getEnumConstants()) {
            if (!config.contains(key.getPath())) {
                String[] def = key.getDefault();
                if (def.length == 1) {
                    config.set(key.getPath(), def[0]);
                } else {
                    config.set(key.getPath(), Arrays.asList(def));
                }
                changed = true;
            }
        }
        if (changed) save();
    }

    public String get(T key, String... placeholders) {
        List<String> lines = messages.getOrDefault(key, Arrays.asList(key.getDefault()));
        String line = lines.isEmpty() ? "" : lines.get(0);
        return ChatFormater.color(parsePlaceholders(line, placeholders));
    }

    public List<String> getList(T key, String... placeholders) {
        List<String> lines = messages.getOrDefault(key, Arrays.asList(key.getDefault()));
        return lines.stream()
                .map(l -> ChatFormater.color(parsePlaceholders(l, placeholders)))
                .collect(Collectors.toList());
    }

    public void send(Player player, T key, String... placeholders) {
        getList(key, placeholders).stream().filter(l -> !l.isEmpty()).forEach(player::sendMessage);
    }

    public void broadcast(T key, String... placeholders) {
        getList(key, placeholders).stream().filter(l -> !l.isEmpty()).forEach(line -> Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(line)));
    }

    public void reload() {
        load();
        messages.clear();
        loadMessages();
        fillMissingMessages();
    }
}
