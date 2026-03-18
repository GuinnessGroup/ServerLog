package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.ServerLog;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Objects;

public class BlockListener implements Listener {
    private final ServerLogUtils logUtils;

    public BlockListener(ServerLog serverLog) {
        this.logUtils = new ServerLogUtils(serverLog);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        logUtils.appendString(
                Message.BLOCK_BREAK.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.BLOCK_BREAK.getLangKey()))
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[block]", block.getType().toString())
                        .replace("[x]", String.valueOf(block.getX()))
                        .replace("[y]", String.valueOf(block.getY()))
                        .replace("[z]", String.valueOf(block.getZ())));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        logUtils.appendString(
                Message.BLOCK_PLACE.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.BLOCK_PLACE.getLangKey()))
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[item]", logUtils.toPlainText(event.getItemInHand().displayName()))
                        .replace("[block]", block.getType().toString())
                        .replace("[x]", String.valueOf(block.getX()))
                        .replace("[y]", String.valueOf(block.getY()))
                        .replace("[z]", String.valueOf(block.getZ()))
        );
    }
}
