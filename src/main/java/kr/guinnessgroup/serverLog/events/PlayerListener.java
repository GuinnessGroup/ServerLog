package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.db.EventRepository;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class PlayerListener extends BaseListener {

    public PlayerListener(JavaPlugin plugin, ServerLogUtils logUtils, EventRepository repository) {
        super(plugin, logUtils, repository);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String world = player.getWorld().getName();

        log(Message.PLAYER_JOIN,
                template(Message.PLAYER_JOIN)
                        .replace("[player]", name)
                        .replace("[world]", world)
                        .replace("[x]", String.valueOf(player.getX()))
                        .replace("[y]", String.valueOf(player.getY()))
                        .replace("[z]", String.valueOf(player.getZ()))
                        .replace("[ip]", Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress())
        );
        logDb("PLAYER_JOIN", uuid, name, world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), "{}");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String world = player.getWorld().getName();

        log(Message.PLAYER_QUIT,
                template(Message.PLAYER_QUIT)
                        .replace("[player]", name)
                        .replace("[world]", world)
                        .replace("[x]", String.valueOf(loc.getBlockX()))
                        .replace("[y]", String.valueOf(loc.getBlockY()))
                        .replace("[z]", String.valueOf(loc.getBlockZ()))
        );
        logDb("PLAYER_QUIT", uuid, name, world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), "{}");
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String world = player.getWorld().getName();
        String reason = logUtils.toPlainText(event.reason());

        log(Message.PLAYER_KICK,
                template(Message.PLAYER_KICK)
                        .replace("[player]", name)
                        .replace("[world]", world)
                        .replace("[reason]", "\"" + reason + "\"")
        );
        logDb("PLAYER_KICK", uuid, name, world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                "{\"reason\":\"" + esc(reason) + "\"}");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location loc = player.getLocation();
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String world = player.getWorld().getName();
        String cause = event.deathMessage() != null ? logUtils.toPlainText(event.deathMessage()) : "UNKNOWN";

        log(Message.PLAYER_DEATH,
                template(Message.PLAYER_DEATH)
                        .replace("[player]", name)
                        .replace("[world]", world)
                        .replace("[reason]", cause)
                        .replace("[x]", String.valueOf(loc.getBlockX()))
                        .replace("[y]", String.valueOf(loc.getBlockY()))
                        .replace("[z]", String.valueOf(loc.getBlockZ()))
        );
        logDb("PLAYER_DEATH", uuid, name, world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                "{\"cause\":\"" + esc(cause) + "\"}");
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String fromWorld = Objects.requireNonNull(from.getWorld()).getName();
        String toWorld = Objects.requireNonNull(to.getWorld()).getName();

        log(Message.PLAYER_TELEPORT,
                template(Message.PLAYER_TELEPORT)
                        .replace("[player]", name)
                        .replace("[to.world]", toWorld)
                        .replace("[to.x]", String.valueOf(to.getBlockX()))
                        .replace("[to.y]", String.valueOf(to.getBlockY()))
                        .replace("[to.z]", String.valueOf(to.getBlockZ()))
                        .replace("[from.world]", fromWorld)
                        .replace("[from.x]", String.valueOf(from.getBlockX()))
                        .replace("[from.y]", String.valueOf(from.getBlockY()))
                        .replace("[from.z]", String.valueOf(from.getBlockZ()))
        );
        logDb("PLAYER_TELEPORT", uuid, name, fromWorld,
                from.getBlockX(), from.getBlockY(), from.getBlockZ(),
                "{\"to_world\":\"" + esc(toWorld) + "\",\"to_x\":" + to.getBlockX()
                        + ",\"to_y\":" + to.getBlockY() + ",\"to_z\":" + to.getBlockZ() + "}");
    }

    @EventHandler
    public void onPlayerChangeGameMode(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String world = player.getWorld().getName();
        String from = player.getGameMode().toString();
        String to = event.getNewGameMode().toString();

        log(Message.PLAYER_GAMEMODE,
                template(Message.PLAYER_GAMEMODE)
                        .replace("[player]", name)
                        .replace("[world]", world)
                        .replace("[x]", String.valueOf(player.getX()))
                        .replace("[y]", String.valueOf(player.getY()))
                        .replace("[z]", String.valueOf(player.getZ()))
                        .replace("[previous]", from)
                        .replace("[new]", to)
                        .replace("[cause]", event.getCause().toString())
        );
        logDb("PLAYER_GAMEMODE", uuid, name, world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                "{\"from\":\"" + from + "\",\"to\":\"" + to + "\"}");
    }
}
