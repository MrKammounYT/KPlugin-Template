package com.kammoun.api.utils.menu;

import com.kammoun.api.configuration.KConfigLoader;
import com.kammoun.api.utils.chat.ChatFormater;
import com.kammoun.api.utils.items.ItemReader;
import com.kammoun.api.utils.items.MenuItem;
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

import java.util.HashMap;
import java.util.Map;

import static com.kammoun.api.utils.PlaceHolderHelper.parsePlaceholders;

@Getter
public abstract class KMenu extends KConfigLoader implements InventoryHolder {

    protected Inventory inventory;
    protected final Map<Integer, MenuItem> menuItems = new HashMap<>();

    private final String title;
    private final int size;
    private MenuItem goBack;
    private MenuItem nextPage;
    private MenuItem prevPage;
    private MenuItem closePage;
    private MenuItem fillerItem;

    public KMenu(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
        this.title = config.getString("Title", "Default title");
        this.size = config.getInt("Size", 9);
        loadDefaultItems(config);
    }

    private void loadDefaultItems(@NotNull FileConfiguration config) {
        goBack     = ItemReader.readMenuItem(config.getConfigurationSection("Navigation.Go-Back"));
        nextPage   = ItemReader.readMenuItem(config.getConfigurationSection("Navigation.Next-Page"));
        prevPage   = ItemReader.readMenuItem(config.getConfigurationSection("Navigation.Prev-Page"));
        closePage  = ItemReader.readMenuItem(config.getConfigurationSection("Navigation.Close-Page"));
        fillerItem = ItemReader.readMenuItem(config.getConfigurationSection("Navigation.Filler-Item"));
    }

    protected abstract void setMenuItems(String... placeholders);

    protected void setItem(MenuItem item) {
        if (item == null || item.getSlot() < 0) return;
        menuItems.put(item.getSlot(), item);
        inventory.setItem(item.getSlot(), item.getItemStack());
    }

    public void handleClick(InventoryClickEvent event) {
        MenuItem item = menuItems.get(event.getSlot());
        if (item != null) item.handleClick(event);
    }

    public void open(Player player, String... placeholders) {
        open(player, 1, 1, placeholders);
    }

    public void open(Player player, int currentPage, int maxPage, String... placeholders) {
        String formattedTitle = ChatFormater.color(parsePlaceholders(title, placeholders));
        inventory = Bukkit.createInventory(this, size, formattedTitle);
        menuItems.clear();

        setNavigationItems(currentPage, maxPage);
        setMenuItems(placeholders);
        addFillerItem();

        player.openInventory(inventory);
    }

    protected void setNavigationItems(int currentPage, int maxPage) {
        if (goBack != null)                             setItem(goBack);
        if (nextPage != null && currentPage < maxPage)  setItem(nextPage);
        if (prevPage != null && currentPage > 1)        setItem(prevPage);
        if (closePage != null)                          setItem(closePage);
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
