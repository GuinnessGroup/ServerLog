package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.ServerLog;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.World;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

public class ServerInfoListener implements Listener {
    private final ServerLogUtils logUtils;

    public ServerInfoListener(ServerLog serverLog) {
        this.logUtils = new ServerLogUtils(serverLog);
    }

    public void countChunksLoadedInWorlds(List<World> worlds) {
        worlds.forEach(world -> {
            Integer loadedChunks = world.getChunkCount();
            logUtils.appendString(
                    Message.CHUNK_LOAD.getPath(),
                    Objects.requireNonNull(logUtils.getConfigFile().getString(Message.CHUNK_LOAD.getLangKey()))
                            .replace("[world]", world.getName())
                            .replace("[count]", String.valueOf(loadedChunks))
            );
        });
    }

    public void countEntityInWorlds(List<World> worlds) {
        worlds.forEach(world -> {
            Integer amountOfEntities = world.getEntityCount();
            logUtils.appendString(
                    Message.ENTITY_COUNT.getPath(),
                    Objects.requireNonNull(logUtils.getConfigFile().getString(Message.ENTITY_COUNT.getLangKey()))
                            .replace("[world]", world.getName())
                            .replace("[count]", String.valueOf(amountOfEntities))
            );
        });
    }

    public void countPlayerInWorlds(List<World> worlds) {
        worlds.forEach(world -> {
            Integer amountOfPlayers = world.getPlayerCount();
            logUtils.appendString(
                    Message.PLAYER_COUNT.getPath(),
                    Objects.requireNonNull(logUtils.getConfigFile().getString(Message.PLAYER_COUNT.getLangKey()))
                            .replace("[world]", world.getName())
                            .replace("[count]", String.valueOf(amountOfPlayers))
            );
        });
    }
}
