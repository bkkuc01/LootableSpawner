package pl.bkkuc.lootablespawner.listeners;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import pl.bkkuc.lootablespawner.Plugin;
import pl.bkkuc.lootablespawner.data.ItemBuilder;

import java.util.concurrent.ThreadLocalRandom;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent e){
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        Block block = e.getBlock();
        if(!(block.getState() instanceof CreatureSpawner)) return;
        e.setDropItems(false);

        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if(Plugin.getInstance().getConfigData().allowedItems != null) {
            boolean found = false;
            for(Material material: Plugin.getInstance().getConfigData().allowedItems){
                if(item != null && item.getType() == material){
                    found = true;
                    break;
                }
            }
            if(!found) return;
        }

        if(Plugin.getInstance().getConfigData().chance < ThreadLocalRandom.current().nextInt(100)) {
            return;
        }

        CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
        ItemStack itemStack = ItemBuilder.getSpawner(creatureSpawner.getSpawnedType());
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("spawner", creatureSpawner.getSpawnedType().name());
        nbtItem.applyNBT(itemStack);

        block.getWorld().dropItem(block.getLocation().clone().toCenterLocation(), itemStack);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent e){
        if (e.getBlockPlaced().getType() != Material.SPAWNER) return;
        ItemStack item = e.getItemInHand();
        NBTItem nbtItem = new NBTItem(item);
        if(!nbtItem.hasTag("spawner")) return;

        BlockState blockState = e.getBlockPlaced().getState();
        if (blockState instanceof CreatureSpawner) {
            CreatureSpawner spawner = (CreatureSpawner) blockState;
            spawner.setSpawnedType(EntityType.valueOf(nbtItem.getString("spawner")));
            spawner.update(true);
        }
    }
}
