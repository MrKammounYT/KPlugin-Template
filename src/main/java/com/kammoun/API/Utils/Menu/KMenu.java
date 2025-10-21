package com.kammoun.API.Utils.Menu;

import com.kammoun.API.Configuration.KConfigLoader;
import com.kammoun.API.Utils.Chat.ChatFormater;
import com.kammoun.API.Utils.Items.KItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static com.kammoun.API.Utils.PlaceHolderHelper.parsePlaceholders;

@Getter
public abstract class KMenu extends KConfigLoader implements InventoryHolder {

    protected Inventory inventory;

    private final String title;
    private final int size;
    private KItem goBack;
    private KItem nextPage;
    private KItem prevPage;
    private KItem closePage;
    private KItem fillerItem;

    public KMenu(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
        this.title = config.getString("Title", "Default title");
        this.size = config.getInt("Size", 9);
        loadDefaultItems(config);
    }

    private void loadDefaultItems(@NotNull FileConfiguration config) {
        goBack = KItem.fromConfig(config.getConfigurationSection("Navigation.Go-Back"));
        nextPage = KItem.fromConfig(config.getConfigurationSection("Navigation.Next-Page"));
        prevPage = KItem.fromConfig(config.getConfigurationSection("Navigation.Prev-Page"));
        closePage = KItem.fromConfig(config.getConfigurationSection("Navigation.Close-Page"));
        fillerItem = KItem.fromConfig(config.getConfigurationSection("Navigation.Filler-Item"));
    }

    public abstract void handleClick(InventoryClickEvent event);

    protected abstract void setMenuItems(String... placeholders);

    public void open(Player player, String... placeholders) {
        open(player, 1, 1, placeholders);
    }

    public void open(Player player, int currentPage, int maxPage, String... placeholders) {
        String formattedTitle = ChatFormater.color(parsePlaceholders(title, placeholders));
        inventory = Bukkit.createInventory(this, size, formattedTitle);

        this.setNavigationItems(currentPage, maxPage);
        this.setMenuItems(placeholders);
        this.addFillerItem();

        player.openInventory(inventory);
    }

    protected void setNavigationItems(int currentPage, int maxPage) {
        if (goBack != null) {
            inventory.setItem(goBack.getSlot(), goBack.getItemStack());
        }
        if (nextPage != null && currentPage < maxPage) {
            inventory.setItem(nextPage.getSlot(), nextPage.getItemStack());
        }
        if (prevPage != null && currentPage > 1) {
            inventory.setItem(prevPage.getSlot(), prevPage.getItemStack());
        }
        if (closePage != null) {
            inventory.setItem(closePage.getSlot(), closePage.getItemStack());
        }
    }

    private void addFillerItem() {
        if (fillerItem == null) return;

        ItemStack filler = fillerItem.getItemStack();
        for (int slot = 0; slot < size; slot++) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}