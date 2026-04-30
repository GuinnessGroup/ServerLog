package kr.guinnessgroup.serverLog.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import kr.guinnessgroup.serverLog.ServerLog;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final HikariDataSource dataSource;
    private final String tablePrefix;

    public DatabaseManager(ServerLog plugin) throws SQLException {
        FileConfiguration cfg = plugin.getConfig();
        tablePrefix = cfg.getString("database.table-prefix", "sl_");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mariadb://"
                + cfg.getString("database.host", "localhost") + ":"
                + cfg.getInt("database.port", 3306) + "/"
                + cfg.getString("database.name", "serverlog"));
        config.setUsername(cfg.getString("database.username", "root"));
        config.setPassword(cfg.getString("database.password", ""));
        config.setMaximumPoolSize(cfg.getInt("database.pool-size", 5));
        config.setConnectionTimeout(10_000);
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource = new HikariDataSource(config);
        createTables();
    }

    private void createTables() throws SQLException {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `" + tablePrefix + "events` (" +
                "  id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
                "  event_type   VARCHAR(32)  NOT NULL," +
                "  player_uuid  CHAR(36)     NOT NULL," +
                "  player_name  VARCHAR(16)  NOT NULL," +
                "  world        VARCHAR(64)  NOT NULL," +
                "  x            INT          NOT NULL," +
                "  y            INT          NOT NULL," +
                "  z            INT          NOT NULL," +
                "  timestamp    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)," +
                "  data         JSON         NOT NULL," +
                "  INDEX idx_type (event_type)," +
                "  INDEX idx_uuid (player_uuid)," +
                "  INDEX idx_ts   (timestamp)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `" + tablePrefix + "server_metrics` (" +
                "  id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
                "  metric_type  VARCHAR(32)  NOT NULL," +
                "  world        VARCHAR(64)  NOT NULL," +
                "  count        INT          NOT NULL," +
                "  timestamp    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3)," +
                "  INDEX idx_ts (timestamp)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void close() {
        dataSource.close();
    }
}
