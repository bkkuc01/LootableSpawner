package pl.bkkuc.lootablespawner.data;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.bkkuc.lootablespawner.Plugin;

import java.util.stream.Collectors;

public class ItemBuilder {

    public static ItemStack getSpawner(EntityType entityType){
        ItemStack item = Plugin.getInstance().getConfigData().entities.getOrDefault(entityType, null);
        if(item == null){
            item = Plugin.getInstance().getConfigData().spawnerItem.clone();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replace("{mob}", entityType.name()));
            if(meta.getLore() != null && !meta.getLore().isEmpty()){
                meta.setLore(meta.getLore().stream().map(s -> s.replace("{mob}", entityType.name())).collect(Collectors.toList()));
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
