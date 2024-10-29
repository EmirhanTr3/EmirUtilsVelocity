package xyz.emirdev.emirutilsvelocity.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import xyz.emirdev.emirutilsvelocity.EmirUtilsVelocity;
import xyz.emirdev.emirutilsvelocity.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private HikariConfig config;
    private HikariDataSource hikari;

    public Database() {
        DatabaseConfig databaseConfig = EmirUtilsVelocity.getConfig().getDatabase();
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

}
