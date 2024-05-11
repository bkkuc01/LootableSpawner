package pl.bkkuc.lootablespawner;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import pl.bkkuc.lootablespawner.data.ConfigData;
import pl.bkkuc.lootablespawner.listeners.PlayerListener;

@Getter
public final class Plugin extends JavaPlugin {

    @Getter
    private static Plugin instance;

    private ConfigData configData;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        configData = new ConfigData(getConfig());

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
