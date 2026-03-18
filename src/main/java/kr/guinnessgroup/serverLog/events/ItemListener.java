package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.ServerLog;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ItemListener implements Listener {
    private final ServerLogUtils logUtils;

    public ItemListener(ServerLog serverLog) {
        this.logUtils = new ServerLogUtils(serverLog);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item itemDrop = event.getItemDrop();
        ItemStack itemStack = itemDrop.getItemStack();

        logUtils.appendString(
                Message.ITEM_DROP_ITEM.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.ITEM_DROP_ITEM.getLangKey()))
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[item]", logUtils.toPlainText(itemStack.displayName()) + "x" + itemStack.getAmount())
                        .replace("[x]", String.valueOf(itemDrop.getLocation().getBlockX()))
                        .replace("[y]", String.valueOf(itemDrop.getLocation().getBlockY()))
                        .replace("[z]", String.valueOf(itemDrop.getLocation().getBlockZ()))
        );
    }

    @EventHandler
    public void onPickupItem(PlayerAttemptPickupItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();

        logUtils.appendString(
                Message.ITEM_PICKUP_ITEM.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString(Message.ITEM_PICKUP_ITEM.getLangKey()))
                        .replace("[player]", player.getName())
                        .replace("[world]", player.getWorld().getName())
                        .replace("[item]", logUtils.toPlainText(itemStack.displayName()) + "x" + itemStack.getAmount())
                        .replace("[x]", String.valueOf(item.getLocation().getBlockX()))
                        .replace("[y]", String.valueOf(item.getLocation().getBlockY()))
                        .replace("[z]", String.valueOf(item.getLocation().getBlockZ()))
        );
    }

    @EventHandler
    public void onEggSpawn(PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) {
            if (event.getMaterial().toString().endsWith("_SPAWN_EGG")) {
                Player player = event.getPlayer();

                logUtils.appendString(
                        Message.ITEM_EGG_SPAWN.getPath(),
                        Objects.requireNonNull(logUtils.getConfigFile().getString(Message.ITEM_EGG_SPAWN.getLangKey()))
                                .replace("[player]", player.getName())
                                .replace("[world]", player.getWorld().getName())
                                .replace("[item]", logUtils.toPlainText(Objects.requireNonNull(event.getItem()).displayName()))
                                .replace("[x]", String.valueOf(player.getX()))
                                .replace("[y]", String.valueOf(player.getY()))
                                .replace("[z]", String.valueOf(player.getZ()))
                );
            }
        }
    }
}
