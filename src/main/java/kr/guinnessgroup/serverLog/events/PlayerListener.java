package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.ServerLog;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.Objects;

public class PlayerListener implements Listener {
    private final ServerLogUtils logUtils;

    public PlayerListener(ServerLog serverLog) {
        this.logUtils = new ServerLogUtils(serverLog);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        logUtils.appendString(
                Message.PLAYER_JOIN.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.PLAYER_JOIN.getLangKey()))
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

        logUtils.appendString(
                Message.PLAYER_QUIT.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.PLAYER_QUIT.getLangKey()))
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

        logUtils.appendString(
                Message.PLAYER_KICK.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.PLAYER_KICK.getLangKey()))
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[reason]", "\"" + logUtils.toPlainText(event.reason()) + "\"")
        );
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location location = player.getLocation();

        logUtils.appendString(
                Message.PLAYER_DEATH.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.PLAYER_DEATH.getLangKey()))
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

        logUtils.appendString(
                Message.PLAYER_TELEPORT.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.PLAYER_TELEPORT.getLangKey()))
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

        logUtils.appendString(
                Message.PLAYER_GAMEMODE.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.PLAYER_GAMEMODE.getLangKey()))
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
