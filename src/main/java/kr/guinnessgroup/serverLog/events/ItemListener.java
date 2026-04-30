package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.db.EventRepository;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ItemListener extends BaseListener {

    public ItemListener(JavaPlugin plugin, ServerLogUtils logUtils, EventRepository repository) {
        super(plugin, logUtils, repository);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item itemDrop = event.getItemDrop();
        ItemStack itemStack = itemDrop.getItemStack();
        Location dropLoc = itemDrop.getLocation();
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String world = player.getWorld().getName();
        String material = itemStack.getType().toString();
        int amount = itemStack.getAmount();

        log(Message.ITEM_DROP_ITEM,
                template(Message.ITEM_DROP_ITEM)
                        .replace("[player]", name)
                        .replace("[world]", world)
                        .replace("[item]", logUtils.toPlainText(itemStack.displayName()) + "x" + amount)
                        .replace("[x]", String.valueOf(dropLoc.getBlockX()))
                        .replace("[y]", String.valueOf(dropLoc.getBlockY()))
                        .replace("[z]", String.valueOf(dropLoc.getBlockZ()))
        );
        logDb("ITEM_DROP_ITEM", uuid, name, world, dropLoc.getBlockX(), dropLoc.getBlockY(), dropLoc.getBlockZ(),
                "{\"material\":\"" + material + "\",\"amount\":" + amount + "}");
    }

    @EventHandler
    public void onPickupItem(PlayerAttemptPickupItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();
        Location itemLoc = item.getLocation();
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String world = player.getWorld().getName();
        String material = itemStack.getType().toString();
        int amount = itemStack.getAmount();

        log(Message.ITEM_PICKUP_ITEM,
                template(Message.ITEM_PICKUP_ITEM)
                        .replace("[player]", name)
                        .replace("[world]", world)
                        .replace("[item]", logUtils.toPlainText(itemStack.displayName()) + "x" + amount)
                        .replace("[x]", String.valueOf(itemLoc.getBlockX()))
                        .replace("[y]", String.valueOf(itemLoc.getBlockY()))
                        .replace("[z]", String.valueOf(itemLoc.getBlockZ()))
        );
        logDb("ITEM_PICKUP_ITEM", uuid, name, world, itemLoc.getBlockX(), itemLoc.getBlockY(), itemLoc.getBlockZ(),
                "{\"material\":\"" + material + "\",\"amount\":" + amount + "}");
    }

    @EventHandler
    public void onEggSpawn(PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) {
            if (event.getMaterial().toString().endsWith("_SPAWN_EGG")) {
                Player player = event.getPlayer();
                Location loc = player.getLocation();
                String uuid = player.getUniqueId().toString();
                String name = player.getName();
                String world = player.getWorld().getName();
                String material = event.getMaterial().toString();

                log(Message.ITEM_EGG_SPAWN,
                        template(Message.ITEM_EGG_SPAWN)
                                .replace("[player]", name)
                                .replace("[world]", world)
                                .replace("[item]", logUtils.toPlainText(Objects.requireNonNull(event.getItem()).displayName()))
                                .replace("[x]", String.valueOf(player.getX()))
                                .replace("[y]", String.valueOf(player.getY()))
                                .replace("[z]", String.valueOf(player.getZ()))
                );
                logDb("ITEM_EGG_SPAWN", uuid, name, world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                        "{\"material\":\"" + material + "\"}");
            }
        }
    }
}
