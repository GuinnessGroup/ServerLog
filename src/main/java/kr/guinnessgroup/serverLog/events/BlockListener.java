package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener extends BaseListener {

    public BlockListener(ServerLogUtils logUtils) {
        super(logUtils);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        log(Message.BLOCK_BREAK,
                template(Message.BLOCK_BREAK)
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[block]", block.getType().toString())
                        .replace("[x]", String.valueOf(block.getX()))
                        .replace("[y]", String.valueOf(block.getY()))
                        .replace("[z]", String.valueOf(block.getZ()))
        );
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        log(Message.BLOCK_PLACE,
                template(Message.BLOCK_PLACE)
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
