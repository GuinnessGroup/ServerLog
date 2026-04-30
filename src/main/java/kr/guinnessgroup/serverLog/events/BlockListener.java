package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.db.EventRepository;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockListener extends BaseListener {

    public BlockListener(JavaPlugin plugin, ServerLogUtils logUtils, EventRepository repository) {
        super(plugin, logUtils, repository);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String world = player.getWorld().getName();
        String blockType = block.getType().toString();

        log(Message.BLOCK_BREAK,
                template(Message.BLOCK_BREAK)
                        .replace("[player]", name)
                        .replace("[world]", world)
                        .replace("[block]", blockType)
                        .replace("[x]", String.valueOf(block.getX()))
                        .replace("[y]", String.valueOf(block.getY()))
                        .replace("[z]", String.valueOf(block.getZ()))
        );
        logDb("BLOCK_BREAK", uuid, name, world, block.getX(), block.getY(), block.getZ(),
                "{\"block\":\"" + blockType + "\"}");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String world = player.getWorld().getName();
        String blockType = block.getType().toString();

        log(Message.BLOCK_PLACE,
                template(Message.BLOCK_PLACE)
                        .replace("[player]", name)
                        .replace("[world]", world)
                        .replace("[item]", logUtils.toPlainText(event.getItemInHand().displayName()))
                        .replace("[block]", blockType)
                        .replace("[x]", String.valueOf(block.getX()))
                        .replace("[y]", String.valueOf(block.getY()))
                        .replace("[z]", String.valueOf(block.getZ()))
        );
        logDb("BLOCK_PLACE", uuid, name, world, block.getX(), block.getY(), block.getZ(),
                "{\"block\":\"" + blockType + "\"}");
    }
}
