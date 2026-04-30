package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.db.EventRepository;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandListener extends BaseListener {

    public CommandListener(JavaPlugin plugin, ServerLogUtils logUtils, EventRepository repository) {
        super(plugin, logUtils, repository);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Location loc = event.getPlayer().getLocation();
        String uuid = event.getPlayer().getUniqueId().toString();
        String name = event.getPlayer().getName();
        String world = event.getPlayer().getWorld().getName();
        String command = event.getMessage();

        log(Message.COMMAND,
                template(Message.COMMAND)
                        .replace("[player]", name)
                        .replace("[command]", command)
                        .replace("[world]", world)
                        .replace("[x]", String.valueOf(loc.getBlockX()))
                        .replace("[y]", String.valueOf(loc.getBlockY()))
                        .replace("[z]", String.valueOf(loc.getBlockZ()))
        );
        logDb("COMMAND", uuid, name, world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                "{\"command\":\"" + esc(command) + "\"}");
    }
}
