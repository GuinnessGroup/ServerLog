package kr.guinnessgroup.serverLog.tasks;

import kr.guinnessgroup.serverLog.db.EventRepository;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ServerInfoTask {
    private final JavaPlugin plugin;
    private final ServerLogUtils logUtils;
    private final EventRepository repository;

    public ServerInfoTask(JavaPlugin plugin, ServerLogUtils logUtils, EventRepository repository) {
        this.plugin = plugin;
        this.logUtils = logUtils;
        this.repository = repository;
    }

    public void countChunksLoadedInWorlds(List<World> worlds) {
        worlds.forEach(world -> {
            int count = world.getChunkCount();
            logUtils.appendString(Message.CHUNK_LOAD.getPath(),
                    logUtils.template(Message.CHUNK_LOAD)
                            .replace("[world]", world.getName())
                            .replace("[count]", String.valueOf(count)));
            if (repository != null) {
                String worldName = world.getName();
                Bukkit.getScheduler().runTaskAsynchronously(plugin,
                        () -> repository.insertMetric("CHUNK_LOAD", worldName, count));
            }
        });
    }

    public void countEntityInWorlds(List<World> worlds) {
        worlds.forEach(world -> {
            int count = world.getEntityCount();
            logUtils.appendString(Message.ENTITY_COUNT.getPath(),
                    logUtils.template(Message.ENTITY_COUNT)
                            .replace("[world]", world.getName())
                            .replace("[count]", String.valueOf(count)));
            if (repository != null) {
                String worldName = world.getName();
                Bukkit.getScheduler().runTaskAsynchronously(plugin,
                        () -> repository.insertMetric("ENTITY_COUNT", worldName, count));
            }
        });
    }

    public void countPlayerInWorlds(List<World> worlds) {
        worlds.forEach(world -> {
            int count = world.getPlayerCount();
            logUtils.appendString(Message.PLAYER_COUNT.getPath(),
                    logUtils.template(Message.PLAYER_COUNT)
                            .replace("[world]", world.getName())
                            .replace("[count]", String.valueOf(count)));
            if (repository != null) {
                String worldName = world.getName();
                Bukkit.getScheduler().runTaskAsynchronously(plugin,
                        () -> repository.insertMetric("PLAYER_COUNT", worldName, count));
            }
        });
    }
}
