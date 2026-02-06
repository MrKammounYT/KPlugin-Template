package com.kammoun.api.skin;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class SQLiteSkinStorage implements SkinStorage {
    
    private static final String CREATE_TABLE = """
        CREATE TABLE IF NOT EXISTS player_skins (
            uuid TEXT PRIMARY KEY,
            texture_value TEXT NOT NULL,
            signature TEXT,
            last_seen INTEGER NOT NULL
        )
        """;
    
    private static final String SELECT_BY_UUID = "SELECT * FROM player_skins WHERE uuid = ?";
    private static final String UPSERT = """
        INSERT INTO player_skins (uuid, texture_value, signature, last_seen)
        VALUES (?, ?, ?, ?)
        ON CONFLICT(uuid) DO UPDATE SET
            texture_value = excluded.texture_value,
            signature = excluded.signature,
            last_seen = excluded.last_seen
        """;
    private static final String DELETE_BY_UUID = "DELETE FROM player_skins WHERE uuid = ?";
    private static final String EXISTS_BY_UUID = "SELECT 1 FROM player_skins WHERE uuid = ? LIMIT 1";
    
    private final File databaseFile;
    private final Logger logger;
    private final ExecutorService executor;
    private Connection connection;
    
    public SQLiteSkinStorage(@NotNull File dataFolder, @NotNull Logger logger) {
        this.databaseFile = new File(dataFolder, "skins.db");
        this.logger = logger;
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "SkinCache-Database");
            thread.setDaemon(true);
            return thread;
        });
    }
    
    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            try {
                if (!databaseFile.getParentFile().exists()) {
                    databaseFile.getParentFile().mkdirs();
                }
                connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
                connection.setAutoCommit(true);
                try (var stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA journal_mode=WAL");
                    stmt.execute("PRAGMA synchronous=NORMAL");
                    stmt.execute("PRAGMA cache_size=1000");
                    stmt.execute(CREATE_TABLE);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to initialize SQLite storage", e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Optional<SkinData>> load(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_UUID)) {
                stmt.setString(1, uuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(new SkinData(
                                UUID.fromString(rs.getString("uuid")),
                                rs.getString("texture_value"),
                                rs.getString("signature"),
                                rs.getLong("last_seen")
                        ));
                    }
                }
            } catch (SQLException e) {
                logger.warning("Failed to load skin for " + uuid + ": " + e.getMessage());
            }
            return Optional.empty();
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> save(@NotNull SkinData skinData) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(UPSERT)) {
                stmt.setString(1, skinData.uuid().toString());
                stmt.setString(2, skinData.textureValue());
                stmt.setString(3, skinData.signature());
                stmt.setLong(4, skinData.lastSeen());
                stmt.executeUpdate();
            } catch (SQLException e) {
                logger.warning("Failed to save skin for " + skinData.uuid() + ": " + e.getMessage());
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> delete(@NotNull UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(DELETE_BY_UUID)) {
                stmt.setString(1, uuid.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                logger.warning("Failed to delete skin for " + uuid + ": " + e.getMessage());
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Boolean> exists(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(EXISTS_BY_UUID)) {
                stmt.setString(1, uuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                logger.warning("Failed to check existence for " + uuid + ": " + e.getMessage());
            }
            return false;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
                logger.info("SQLite skin storage shut down gracefully");
            } catch (Exception e) {
                logger.warning("Error during storage shutdown: " + e.getMessage());
            }
        });
    }
}
