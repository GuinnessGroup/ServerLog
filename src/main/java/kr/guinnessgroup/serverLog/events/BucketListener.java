package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.ServerLog;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BucketListener implements Listener {
    private final ServerLogUtils logUtils;

    public BucketListener(ServerLog serverLog) {
        this.logUtils = new ServerLogUtils(serverLog);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Block targetBlock = event.getBlockClicked();
        BlockFace targetBlockFace = event.getBlockFace();
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        ItemStack itemStack = event.getItemStack();

        logUtils.appendString(
                Message.BUCKET_EMPTY.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.BUCKET_EMPTY.getLangKey()))
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[item_stack]", logUtils.toPlainText(Objects.requireNonNull(itemStack).displayName()))
                        .replace("[hand]", hand.toString())
                        .replace("[block_face]", targetBlockFace.toString())
                        .replace("[block_clicked.x]", String.valueOf(targetBlock.getX()))
                        .replace("[block_clicked.y]", String.valueOf(targetBlock.getY()))
                        .replace("[block_clicked.z]", String.valueOf(targetBlock.getZ()))
                        .replace("[player.x]", String.valueOf(player.getX()))
                        .replace("[player.y]", String.valueOf(player.getY()))
                        .replace("[player.z]", String.valueOf(player.getZ()))
        );
    }

    @EventHandler
    public void onBucketCaptureEntity(PlayerBucketEntityEvent event) {
        Location entityLoc = event.getEntity().getLocation();
        Player player = event.getPlayer();
        ItemStack entityStack = event.getEntity().getPickItemStack();
        ItemStack originalBucket = event.getOriginalBucket();
        ItemStack entityBucket = event.getEntityBucket();
        EquipmentSlot hand = event.getHand();

        logUtils.appendString(
                Message.BUCKET_ENTITY.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.BUCKET_ENTITY.getLangKey()))
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[entity]", logUtils.toPlainText(entityStack.displayName()))
                        .replace("[hand]", hand.toString())
                        .replace("[original_bucket]", logUtils.toPlainText(originalBucket.displayName()))
                        .replace("[entity_bucket]", logUtils.toPlainText(entityBucket.displayName()))
                        .replace("[entity.x]", String.valueOf(entityLoc.getBlockX()))
                        .replace("[entity.y]", String.valueOf(entityLoc.getBlockY()))
                        .replace("[entity.z]", String.valueOf(entityLoc.getBlockZ()))
        );
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Block targetBlock = event.getBlock();
        BlockFace targetBlockFace = event.getBlockFace();
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemStack();

        logUtils.appendString(
                Message.BUCKET_FILL.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.BUCKET_FILL.getLangKey()))
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[item_stack]", logUtils.toPlainText(Objects.requireNonNull(itemStack).displayName()))
                        .replace("[hand]", event.getHand().toString())
                        .replace("[block_face]", targetBlockFace.toString())
                        .replace("[block_clicked.x]", String.valueOf(targetBlock.getX()))
                        .replace("[block_clicked.y]", String.valueOf(targetBlock.getY()))
                        .replace("[block_clicked.z]", String.valueOf(targetBlock.getZ()))
                        .replace("[player.x]", String.valueOf(player.getX()))
                        .replace("[player.y]", String.valueOf(player.getY()))
                        .replace("[player.z]", String.valueOf(player.getZ()))
        );
    }
}
