package kr.guinnessgroup.serverLog.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventRepository {
    private final DatabaseManager db;
    private final Logger logger;

    public EventRepository(DatabaseManager db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    public void insertEvent(String eventType, String playerUuid, String playerName,
                            String world, int x, int y, int z, String dataJson) {
        String sql = "INSERT INTO `" + db.getTablePrefix() + "events` "
                   + "(event_type,player_uuid,player_name,world,x,y,z,data) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventType);
            ps.setString(2, playerUuid);
            ps.setString(3, playerName);
            ps.setString(4, world);
            ps.setInt(5, x);
            ps.setInt(6, y);
            ps.setInt(7, z);
            ps.setString(8, dataJson);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Failed to insert event [" + eventType + "]", e);
        }
    }

    public void insertMetric(String metricType, String world, int count) {
        String sql = "INSERT INTO `" + db.getTablePrefix() + "server_metrics` "
                   + "(metric_type,world,count) VALUES (?,?,?)";
        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, metricType);
            ps.setString(2, world);
            ps.setInt(3, count);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Failed to insert metric [" + metricType + "]", e);
        }
    }
}
