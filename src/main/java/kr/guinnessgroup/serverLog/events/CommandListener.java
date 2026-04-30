package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener extends BaseListener {

    public CommandListener(ServerLogUtils logUtils) {
        super(logUtils);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Location location = event.getPlayer().getLocation();

        log(Message.COMMAND,
                template(Message.COMMAND)
                        .replace("[player]", event.getPlayer().getName())
                        .replace("[command]", event.getMessage())
                        .replace("[world]", event.getPlayer().getWorld().getName())
                        .replace("[x]", String.valueOf(location.getBlockX()))
                        .replace("[y]", String.valueOf(location.getBlockY()))
                        .replace("[z]", String.valueOf(location.getBlockZ()))
        );
    }
}
