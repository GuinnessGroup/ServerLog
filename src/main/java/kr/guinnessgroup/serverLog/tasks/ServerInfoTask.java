package kr.guinnessgroup.serverLog.tasks;

import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.World;

import java.util.List;

public class ServerInfoTask {
    private final ServerLogUtils logUtils;

    public ServerInfoTask(ServerLogUtils logUtils) {
        this.logUtils = logUtils;
    }

    public void countChunksLoadedInWorlds(List<World> worlds) {
        worlds.forEach(world -> logUtils.appendString(
                Message.CHUNK_LOAD.getPath(),
                logUtils.template(Message.CHUNK_LOAD)
                        .replace("[world]", world.getName())
                        .replace("[count]", String.valueOf(world.getChunkCount()))
        ));
    }

    public void countEntityInWorlds(List<World> worlds) {
        worlds.forEach(world -> logUtils.appendString(
                Message.ENTITY_COUNT.getPath(),
                logUtils.template(Message.ENTITY_COUNT)
                        .replace("[world]", world.getName())
                        .replace("[count]", String.valueOf(world.getEntityCount()))
        ));
    }

    public void countPlayerInWorlds(List<World> worlds) {
        worlds.forEach(world -> logUtils.appendString(
                Message.PLAYER_COUNT.getPath(),
                logUtils.template(Message.PLAYER_COUNT)
                        .replace("[world]", world.getName())
                        .replace("[count]", String.valueOf(world.getPlayerCount()))
        ));
    }
}
