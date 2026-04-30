package kr.guinnessgroup.serverLog;

import kr.guinnessgroup.serverLog.db.DatabaseManager;
import kr.guinnessgroup.serverLog.db.EventRepository;
import kr.guinnessgroup.serverLog.events.*;
import kr.guinnessgroup.serverLog.tasks.ServerInfoTask;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

public final class ServerLog extends JavaPlugin {
    private ServerLogUtils logUtils;
    private DatabaseManager databaseManager;
    private EventRepository eventRepository;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        logUtils = new ServerLogUtils(this);

        if (logUtils.isDatabaseEnabled()) {
            try {
                databaseManager = new DatabaseManager(this);
                eventRepository = new EventRepository(databaseManager, getLogger());
                getLogger().info("Database connected.");
            } catch (SQLException e) {
                getLogger().log(Level.SEVERE, "Database connection failed. DB logging disabled.", e);
                databaseManager = null;
                eventRepository = null;
            }
        }

        registerEvents();
        registerServerInfo();
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this, logUtils, eventRepository), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(this, logUtils, eventRepository), this);
        Bukkit.getPluginManager().registerEvents(new BucketListener(this, logUtils, eventRepository), this);
        Bukkit.getPluginManager().registerEvents(new ItemListener(this, logUtils, eventRepository), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this, logUtils, eventRepository), this);
        Bukkit.getPluginManager().registerEvents(new CommandListener(this, logUtils, eventRepository), this);
    }

    private void registerServerInfo() {
        ServerInfoTask serverInfoTask = new ServerInfoTask(this, logUtils, eventRepository);
        // period: interval minutes * 60 seconds * 20 ticks
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            List<World> worlds = Bukkit.getWorlds();
            serverInfoTask.countEntityInWorlds(worlds);
            serverInfoTask.countChunksLoadedInWorlds(worlds);
            serverInfoTask.countPlayerInWorlds(worlds);
        }, 0, getConfig().getLong("serverInfo.interval") * 60 * 20);
    }
}
