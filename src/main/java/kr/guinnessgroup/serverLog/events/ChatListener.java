package kr.guinnessgroup.serverLog.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatListener extends BaseListener {
    private final JavaPlugin plugin;

    public ChatListener(JavaPlugin plugin, ServerLogUtils logUtils) {
        super(logUtils);
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String player = event.getPlayer().getName();
        String message = logUtils.toPlainText(event.message());
        Bukkit.getScheduler().runTask(plugin, () ->
                log(Message.CHAT,
                        template(Message.CHAT)
                                .replace("[player]", player)
                                .replace("[message]", message))
        );
    }
}
