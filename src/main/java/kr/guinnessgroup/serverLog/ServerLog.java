package kr.guinnessgroup.serverLog;

import kr.guinnessgroup.serverLog.events.*;
import kr.guinnessgroup.serverLog.tasks.ServerInfoTask;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class ServerLog extends JavaPlugin {
    private ServerLogUtils logUtils;
    private ServerInfoTask serverInfoTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        logUtils = new ServerLogUtils(this);

        registerEvents();
        registerServerInfo();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(logUtils), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(logUtils), this);
        Bukkit.getPluginManager().registerEvents(new BucketListener(logUtils), this);
        Bukkit.getPluginManager().registerEvents(new ItemListener(logUtils), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this, logUtils), this);
        Bukkit.getPluginManager().registerEvents(new CommandListener(logUtils), this);
    }

    private void registerServerInfo() {
        serverInfoTask = new ServerInfoTask(logUtils);
        // period 20 ticks = 1 second
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            List<World> worlds = Bukkit.getWorlds();
            serverInfoTask.countEntityInWorlds(worlds);
            serverInfoTask.countChunksLoadedInWorlds(worlds);
            serverInfoTask.countPlayerInWorlds(worlds);
        }, 0, getConfig().getLong("serverInfo.interval") * 60 * 20);
    }
}
