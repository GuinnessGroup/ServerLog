package kr.guinnessgroup.serverLog.events;

import kr.guinnessgroup.serverLog.utils.Message;
import kr.guinnessgroup.serverLog.utils.ServerLogUtils;
import org.bukkit.event.Listener;

public abstract class BaseListener implements Listener {
    protected final ServerLogUtils logUtils;

    protected BaseListener(ServerLogUtils logUtils) {
        this.logUtils = logUtils;
    }

    protected String template(Message message) {
        return logUtils.template(message);
    }

    protected void log(Message message, String line) {
        logUtils.appendString(message.getPath(), line);
    }
}
