package com.kammoun.API.Utils.Menu;

import com.kammoun.API.Utils.Items.KItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class KMenu {


    private final String Title;
    private final int Size;
    private KItem GoBack;
    private KItem NextPage;
    private KItem PrevPage;
    private KItem ClosePage;
    private KItem FillerItem;


    public KMenu(@NotNull FileConfiguration config){
        Title = config.getString("Title","Default title");
        Size = config.getInt("Size",9);
        loadDefaultItems(config);
    }

    public KMenu(String title, int size) {
        Title = title;
        Size = size %9 !=0 ? 9 : size;

    }

    private void loadDefaultItems(@NotNull FileConfiguration config){
        GoBack = KItem.fromConfig(config.getConfigurationSection("Navigation.Go-Back"));
        NextPage = KItem.fromConfig(config.getConfigurationSection("Navigation.Next-Page"));
        PrevPage = KItem.fromConfig(config.getConfigurationSection("Navigation.Prev-Page"));
        ClosePage = KItem.fromConfig(config.getConfigurationSection("Navigation.Close-Page"));
        FillerItem = KItem.fromConfig(config.getConfigurationSection("Navigation.Filler-Item"));
    }

    public Inventory build(@Nullable InventoryHolder holder){
        Inventory inventory = Bukkit.createInventory(holder, Size, Title);
        if(GoBack != null){inventory.setItem(GoBack.getSlot(),GoBack.build());}
        if(NextPage != null){inventory.setItem(NextPage.getSlot(),NextPage.build());}
        if(PrevPage != null){inventory.setItem(PrevPage.getSlot(),PrevPage.build());}
        if(ClosePage != null){inventory.setItem(ClosePage.getSlot(),ClosePage.build());}

        if(FillerItem != null){
            ItemStack filler = FillerItem.getItemStack();
            for(int slot=0;slot<Size;slot++)
            {
                if(inventory.getItem(slot)==null){
                    inventory.setItem(slot,filler);
                }
            }
        }
        return inventory;
    }
    public Inventory build(){
        return build(null);
    }


}
