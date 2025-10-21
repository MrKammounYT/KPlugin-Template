package com.kammoun.API.Utils.InventoryHolder;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;


@Getter
public class MultiPageHolder implements InventoryHolder {


    private final int currentPage;
    private final int maxPage;

    public MultiPageHolder(int currentPage, int maxPage) {
        this.currentPage = currentPage;
        this.maxPage = maxPage;
    }



    private Inventory inventory;
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
