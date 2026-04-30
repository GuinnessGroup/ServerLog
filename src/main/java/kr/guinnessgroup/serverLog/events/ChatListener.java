package kr.guinnessgroup.serverLog.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import kr.guinnessgroup.serverLog.db.EventRepository;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatListener extends BaseListener {

    public ChatListener(JavaPlugin plugin, ServerLogUtils logUtils, EventRepository repository) {
        super(plugin, logUtils, repository);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String player = event.getPlayer().getName();
        String message = logUtils.toPlainText(event.message());
        String world = event.getPlayer().getWorld().getName();

        // DB: already on async thread — call directly
        logDbDirect("CHAT", uuid, player, world, 0, 0, 0,
                "{\"message\":\"" + esc(message) + "\"}");

        // File: SimpleDateFormat is not thread-safe — delegate to main thread
        Bukkit.getScheduler().runTask(plugin, () ->
                log(Message.CHAT,
                        template(Message.CHAT)
                                .replace("[player]", player)
                                .replace("[message]", message))
        );
    }
}
