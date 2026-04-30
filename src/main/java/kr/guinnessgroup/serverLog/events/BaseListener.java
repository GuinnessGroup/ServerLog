package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.db.EventRepository;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BaseListener implements Listener {
    protected final ServerLogUtils logUtils;
    protected final JavaPlugin plugin;
    protected final EventRepository repository;

    protected BaseListener(JavaPlugin plugin, ServerLogUtils logUtils, EventRepository repository) {
        this.plugin = plugin;
        this.logUtils = logUtils;
        this.repository = repository;
    }

    protected String template(Message message) {
        return logUtils.template(message);
    }

    protected void log(Message message, String line) {
        logUtils.appendString(message.getPath(), line);
    }

    protected void logDb(String eventType, String uuid, String name,
                         String world, int x, int y, int z, String dataJson) {
        if (repository == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin,
                () -> repository.insertEvent(eventType, uuid, name, world, x, y, z, dataJson));
    }

    protected void logDbDirect(String eventType, String uuid, String name,
                               String world, int x, int y, int z, String dataJson) {
        if (repository == null) return;
        repository.insertEvent(eventType, uuid, name, world, x, y, z, dataJson);
    }

    protected static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
