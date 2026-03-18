package kr.guinnessgroup.serverLog;

import kr.guinnessgroup.serverLog.events.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class ServerLog extends JavaPlugin {
    private ServerInfoListener serverInfoListener;

    @Override
    public void onEnable() {
        // Plugin startup logic
        serverInfoListener = new ServerInfoListener(this);

        saveDefaultConfig();
        reloadConfig();

        registerEvents();
        registerServerInfo();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BucketListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CommandListener(this), this);
    }

    private void registerServerInfo() {
        // period 20 ticks = 1 second
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            List<World> worlds = Bukkit.getWorlds();
            serverInfoListener.countEntityInWorlds(worlds);
            serverInfoListener.countChunksLoadedInWorlds(worlds);
            serverInfoListener.countPlayerInWorlds(worlds);
        }, 0, getConfig().getLong("serverInfo.interval") * 60 * 20);
    }
}