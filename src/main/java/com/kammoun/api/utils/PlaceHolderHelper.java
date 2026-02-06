package com.kammoun.api.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlaceHolderHelper {

    public static String parsePlaceholders(String message, String... placeholders) {
        if (placeholders == null || placeholders.length == 0) {
            return message;
        }
        if (placeholders.length % 2 != 0) {
            return message;
        }

        for (int i = 0; i < placeholders.length; i += 2) {
            String placeholder = placeholders[i];
            String replacement = placeholders[i + 1];

            if (placeholder != null && replacement != null) {
                message = message.replace(placeholder, replacement);
            }
        }
        return message;
    }

    public static ItemStack parsePlaceholders(ItemStack itemStack, String... placeholders) {
        if (itemStack == null || placeholders == null || placeholders.length == 0 || placeholders.length % 2 != 0) {
            return itemStack;
        }

        ItemStack newItem = itemStack.clone();
        if (!newItem.hasItemMeta()) {
            return newItem;
        }

        ItemMeta meta = newItem.getItemMeta();

        if (meta.hasDisplayName()) {
            String name = meta.getDisplayName();
            for (int i = 0; i < placeholders.length; i += 2) {
                name = name.replaceAll( placeholders[i], placeholders[i + 1]);
            }
            meta.setDisplayName(name);
        }
        List<String> lore = meta.getLore();
        if (meta.hasLore()) {
            List<String> newLore = new ArrayList<>();
            for (String line : lore) {
                String updatedLine = line;
                for (int i = 0; i < placeholders.length; i += 2) {
                    updatedLine = updatedLine.replace(placeholders[i], placeholders[i + 1]);
                }
                newLore.add(updatedLine);
            }
            meta.setLore(newLore);
        }

        newItem.setItemMeta(meta);
        return newItem;
    }

}
