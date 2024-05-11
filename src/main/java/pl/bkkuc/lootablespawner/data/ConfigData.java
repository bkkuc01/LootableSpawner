package pl.bkkuc.lootablespawner.data;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pl.bkkuc.lootablespawner.Plugin;
import pl.bkkuc.purutils.ColorUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class ConfigData {

    @NonFinal
    @Nullable List<Material> allowedItems;
    int chance;
    @Nullable String itemName;
    List<String> itemLore;
    Map<EntityType, ItemStack> entities;

    ItemStack spawnerItem;

    public ConfigData(FileConfiguration configuration){
        this.allowedItems = new ArrayList<>();
        List<String> AI = configuration.getStringList("allowed-items");
        if(!AI.isEmpty() && !AI.contains("*")) {
            for (String materialName : AI) {
                Material material;
                try {
                    material = Material.valueOf(materialName);
                } catch (IllegalArgumentException e) {
                    Plugin.getInstance().getLogger().warning("Material '" + materialName + "' is not found.");
                    continue;
                }
                allowedItems.add(material);
            }
        }
        else {
            allowedItems = null;
        }

        this.chance = configuration.getInt("chance", 100);
        this.itemName = configuration.getString("item.name");
        this.itemLore = configuration.getStringList("item.lore");


        this.spawnerItem = new ItemStack(Material.SPAWNER);
        ItemMeta meta = spawnerItem.getItemMeta();

        if(itemName != null){
            meta.setDisplayName(ColorUtility.colorize(itemName));
        }
        if(!itemLore.isEmpty()) {
            meta.setLore(itemLore.stream().map(ColorUtility::colorize).collect(Collectors.toList()));
        }

        spawnerItem.setItemMeta(meta);

        this.entities = new HashMap<>();
        ConfigurationSection translateSection = configuration.getConfigurationSection("translate");
        if(translateSection != null){
            for(Map.Entry<String, Object> entry: translateSection.getValues(false).entrySet()) {
                String k = entry.getKey();
                Object v = entry.getValue();
                if(v instanceof String) {
                    k = k.toUpperCase();
                    EntityType entityType = null;
                    try {
                        entityType = EntityType.valueOf(k);
                    } catch (IllegalArgumentException e) {
                        Plugin.getInstance().getLogger().warning("Entity '" + k + "' is not found.");
                    }
                    if (entityType != null) {

                        ItemStack item = spawnerItem.clone();
                        ItemMeta itemMeta = item.getItemMeta();

                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{mob}", String.valueOf(v)));
                        if(itemMeta.getLore() != null && !itemMeta.getLore().isEmpty()) {
                            itemMeta.setLore(itemMeta.getLore().stream().map(s -> s.replace("{mob}", String.valueOf(v))).collect(Collectors.toList()));
                        }

                        item.setItemMeta(itemMeta);
                        entities.put(entityType, item);
                    }
                }
            }
        }
    }
}
