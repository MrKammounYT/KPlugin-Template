package com.kammoun.api.utils.items;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@Getter
public class MenuItem {

    private final ItemBuilder builder;
    private int slot = -1;
    private double price = 0;
    private String permission;
    private ClickAction action;
    private Consumer<InventoryClickEvent> clickHandler;

    public MenuItem(ItemBuilder builder) {
        this.builder = builder;
    }

    public static MenuItem of(ItemBuilder builder) {
        return new MenuItem(builder);
    }

    public MenuItem slot(int slot) {
        this.slot = slot;
        return this;
    }

    public MenuItem price(double price) {
        this.price = price;
        return this;
    }

    public MenuItem permission(String permission) {
        this.permission = permission;
        return this;
    }

    public MenuItem action(ClickAction action) {
        this.action = action;
        return this;
    }

    public MenuItem onClick(Consumer<InventoryClickEvent> handler) {
        this.clickHandler = handler;
        return this;
    }

    public void handleClick(InventoryClickEvent event) {
        if (clickHandler != null) clickHandler.accept(event);
    }

    public boolean hasPermission(org.bukkit.entity.Player player) {
        return permission == null || permission.isEmpty() || player.hasPermission(permission);
    }


    public ItemStack getItemStack() { return builder.build(); }

}