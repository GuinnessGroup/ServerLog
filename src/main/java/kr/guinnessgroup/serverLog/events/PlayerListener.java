package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.Objects;

public class PlayerListener extends BaseListener {

    public PlayerListener(ServerLogUtils logUtils) {
        super(logUtils);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        log(Message.PLAYER_JOIN,
                template(Message.PLAYER_JOIN)
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[x]", String.valueOf(player.getX()))
                        .replace("[y]", String.valueOf(player.getY()))
                        .replace("[z]", String.valueOf(player.getZ()))
                        .replace("[ip]", Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress())
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();

        log(Message.PLAYER_QUIT,
                template(Message.PLAYER_QUIT)
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[x]", String.valueOf(location.getBlockX()))
                        .replace("[y]", String.valueOf(location.getBlockY()))
                        .replace("[z]", String.valueOf(location.getBlockZ()))
        );
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        log(Message.PLAYER_KICK,
                template(Message.PLAYER_KICK)
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[reason]", "\"" + logUtils.toPlainText(event.reason()) + "\"")
        );
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location location = player.getLocation();

        log(Message.PLAYER_DEATH,
                template(Message.PLAYER_DEATH)
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[reason]", event.deathMessage() != null ? logUtils.toPlainText(event.deathMessage()) : "UNKNOWN")
                        .replace("[x]", String.valueOf(location.getBlockX()))
                        .replace("[y]", String.valueOf(location.getBlockY()))
                        .replace("[z]", String.valueOf(location.getBlockZ()))
        );
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();

        log(Message.PLAYER_TELEPORT,
                template(Message.PLAYER_TELEPORT)
                        .replace("[player]", player.getName())
                        .replace("[to.world]", Objects.requireNonNull(to.getWorld()).getName())
                        .replace("[to.x]", String.valueOf(to.getBlockX()))
                        .replace("[to.y]", String.valueOf(to.getBlockY()))
                        .replace("[to.z]", String.valueOf(to.getBlockZ()))
                        .replace("[from.world]", Objects.requireNonNull(from.getWorld()).getName())
                        .replace("[from.x]", String.valueOf(from.getBlockX()))
                        .replace("[from.y]", String.valueOf(from.getBlockY()))
                        .replace("[from.z]", String.valueOf(from.getBlockZ()))
        );
    }

    @EventHandler
    public void onPlayerChangeGameMode(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();

        log(Message.PLAYER_GAMEMODE,
                template(Message.PLAYER_GAMEMODE)
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[x]", String.valueOf(player.getX()))
                        .replace("[y]", String.valueOf(player.getY()))
                        .replace("[z]", String.valueOf(player.getZ()))
                        .replace("[previous]", player.getGameMode().toString())
                        .replace("[new]", event.getNewGameMode().toString())
                        .replace("[cause]", event.getCause().toString())
        );
    }
}
