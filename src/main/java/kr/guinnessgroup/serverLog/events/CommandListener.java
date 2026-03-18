package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.ServerLog;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Objects;

public class CommandListener implements Listener {
    private final ServerLogUtils logUtils;

    public CommandListener(ServerLog serverLog) {
        this.logUtils = new ServerLogUtils(serverLog);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        logUtils.appendString(
                Message.COMMAND.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.COMMAND.getLangKey()))
                        .replace("[player]", event.getPlayer().getName())
                        .replace("[command]", event.getMessage())
                        .replace("[world]", event.getPlayer().getWorld().getName())
                        .replace("[x]", String.valueOf(event.getPlayer().getLocation().getBlockX()))
                        .replace("[y]", String.valueOf(event.getPlayer().getLocation().getBlockY()))
                        .replace("[z]", String.valueOf(event.getPlayer().getLocation().getBlockZ()))
        );
    }
}
