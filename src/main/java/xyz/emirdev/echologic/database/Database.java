package xyz.emirdev.echologic.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import xyz.emirdev.echologic.EchoLogic;
import xyz.emirdev.echologic.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {
    private static final String TABLE_PLAYERS_CREATE = "CREATE TABLE IF NOT EXISTS players (uuid text, socialspy boolean)";
    private static final String PLAYER_EXISTS = "SELECT EXISTS(SELECT 1 FROM players WHERE uuid=?)";
    private static final String PLAYER_SELECT = "SELECT uuid, socialspy FROM players WHERE uuid=? LIMIT 1";
    private static final String PLAYER_INSERT = "INSERT INTO players (uuid, socialspy) VALUES(?, ?)";
    private static final String PLAYER_UPDATE_SOCIALSPY = "UPDATE players SET socialspy=? WHERE uuid=?";

    private static final String TABLE_IGNOREDPLAYERS_CREATE = "CREATE TABLE IF NOT EXISTS ignoredplayers (playeruuid text, targetuuid text)";
    private static final String IGNOREDPLAYERS_INSERT = "INSERT INTO ignoredplayers (playeruuid, targetuuid) VALUES(?, ?)";
    private static final String IGNOREDPLAYERS_SELECT_ALL = "SELECT targetuuid FROM ignoredplayers WHERE playeruuid=?";
    private static final String IGNOREDPLAYERS_CHECK = "SELECT EXISTS(SELECT 1 FROM ignoredplayers WHERE playeruuid=? AND targetuuid=?)";
    private static final String IGNOREDPLAYERS_DELETE = "DELETE FROM ignoredplayers WHERE playeruuid=? AND targetuuid=?";

    private HikariConfig config;
    private HikariDataSource hikari;

    public Database() {
        DatabaseConfig databaseConfig = EchoLogic.getConfig().getDatabase();
        config = new HikariConfig();

        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s", "mysql", databaseConfig.getAddress(), databaseConfig.getPort(), databaseConfig.getName()));
        config.setUsername(databaseConfig.getUsername());
        config.setPassword(databaseConfig.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.addDataSourceProperty("alwaysSendSetIsolation", "false");
        config.addDataSourceProperty("cacheCallableStmts", "true");
        config.addDataSourceProperty("serverTimezone", "UTC");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(10);
        config.setMaxLifetime(1800000);
        config.setKeepaliveTime(0);
        config.setConnectionTimeout(5000);

        this.hikari = new HikariDataSource(config);

        try (Connection c = getConnection()) {
            c.prepareStatement(TABLE_PLAYERS_CREATE).execute();
            c.prepareStatement(TABLE_IGNOREDPLAYERS_CREATE).execute();
        } catch (SQLException e) {
            EchoLogic.get().getLogger().error("An SQL exception has occured.", e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.hikari == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }

        Connection connection = this.hikari.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }

    public DatabaseMetadata getMeta() {
        DatabaseMetadata metadata = new DatabaseMetadata();

        boolean success = true;
        long start = System.currentTimeMillis();

        try (Connection c = getConnection()) {
            try (Statement s = c.createStatement()) {
                s.execute("/* ping */ SELECT 1");
            }
        } catch (SQLException e) {
            success = false;
        }

        if (success) {
            int duration = (int) (System.currentTimeMillis() - start);
            metadata.ping(duration);
        }

        metadata.connected(success);
        return metadata;
    }

    private void insertPlayerData(UUID uuid) {
        boolean playerExists;

        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(PLAYER_EXISTS)) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        playerExists = rs.getBoolean(1);
                    } else {
                        playerExists = false;
                    }
                }
            }

            if (!playerExists) {
                try (PreparedStatement ps = c.prepareStatement(PLAYER_INSERT)) {
                    ps.setString(1, uuid.toString());
                    ps.setBoolean(2, false);
                    ps.execute();
                }
            }
        } catch (SQLException e) {
            EchoLogic.get().getLogger().error("An SQL exception has occured.", e);
        }
    }

    public PlayerData getPlayerData(UUID uuid) {
        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(PLAYER_SELECT)) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new PlayerData(uuid, rs.getBoolean("socialspy"));
                    } else {
                        return new PlayerData(uuid, false);
                    }
                }
            }

        } catch (SQLException e) {
            EchoLogic.get().getLogger().error("An SQL exception has occured.", e);
            return null;
        }
    }

    public void updateSocialSpy(UUID uuid, boolean socialSpy) {
        insertPlayerData(uuid);

        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(PLAYER_UPDATE_SOCIALSPY)) {
                ps.setBoolean(1, socialSpy);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            EchoLogic.get().getLogger().error("An SQL exception has occured.", e);
        }
    }

    public static final class PlayerData {
        private final UUID uuid;
        private final boolean socialSpy;

        public PlayerData(UUID uuid, boolean socialSpy) {
            this.uuid = uuid;
            this.socialSpy = socialSpy;
        }

        public UUID getUniqueId() {
            return uuid;
        }

        public boolean hasSocialSpy() {
            return socialSpy;
        }
    }

    public boolean isIgnored(UUID playerUUID, UUID targetUUID) {
        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(IGNOREDPLAYERS_CHECK)) {
                ps.setString(1, playerUUID.toString());
                ps.setString(2, targetUUID.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getBoolean(1);
                    } else {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            EchoLogic.get().getLogger().error("An SQL exception has occured.", e);
            return false;
        }
    }

    public void ignorePlayer(UUID playerUUID, UUID targetUUID) {
        boolean ignored = isIgnored(playerUUID, targetUUID);

        try (Connection c = getConnection()) {
            if (!ignored) {
                try (PreparedStatement ps = c.prepareStatement(IGNOREDPLAYERS_INSERT)) {
                    ps.setString(1, playerUUID.toString());
                    ps.setString(2, targetUUID.toString());
                    ps.execute();
                }
            }
        } catch (SQLException e) {
            EchoLogic.get().getLogger().error("An SQL exception has occured.", e);
        }
    }

    public void unIgnorePlayer(UUID playerUUID, UUID targetUUID) {
        boolean ignored = isIgnored(playerUUID, targetUUID);

        try (Connection c = getConnection()) {
            if (ignored) {
                try (PreparedStatement ps = c.prepareStatement(IGNOREDPLAYERS_DELETE)) {
                    ps.setString(1, playerUUID.toString());
                    ps.setString(2, targetUUID.toString());
                    ps.execute();
                }
            }
        } catch (SQLException e) {
            EchoLogic.get().getLogger().error("An SQL exception has occured.", e);
        }
    }

    public List<UUID> getIgnoredPlayers(UUID playerUUID) {
        List<UUID> players = new ArrayList<>();

        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(IGNOREDPLAYERS_SELECT_ALL)) {
                ps.setString(1, playerUUID.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        players.add(UUID.fromString(rs.getString(1)));
                    }
                }
            }
        } catch (SQLException e) {
            EchoLogic.get().getLogger().error("An SQL exception has occured.", e);
        }

        return players;
    }
}
