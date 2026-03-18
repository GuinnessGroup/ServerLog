package kr.guinnessgroup.serverLog.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import kr.guinnessgroup.serverLog.ServerLog;
import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class ChatListener implements Listener {
    private final ServerLogUtils logUtils;

    public ChatListener(ServerLog serverLog) {
        this.logUtils = new ServerLogUtils(serverLog);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        logUtils.appendString(
                Message.CHAT.getPath(),
                Objects.requireNonNull(logUtils.getConfigFile().getString("chat"))
                        .replace("[player]", event.getPlayer().getName())
                        .replace("[message]", logUtils.toPlainText(event.message()))
        );
    }
}
