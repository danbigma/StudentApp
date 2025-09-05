package com.studentapp.db;

import com.studentapp.config.AppConfig;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.SQLTransientException;
import java.time.Duration;

public class Database {
    private static final Logger log = Logger.getLogger(Database.class);

    @FunctionalInterface
    public interface SqlFunction<T> { T apply(Connection c) throws SQLException; }
    @FunctionalInterface
    public interface SqlConsumer { void accept(Connection c) throws SQLException; }
    @FunctionalInterface
    public interface SupplierX<T> { T get() throws SQLException; }

    private final DataSource dataSource;
    private final AppConfig cfg = AppConfig.get();

    public Database(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T query(SqlFunction<T> fn) throws SQLException {
        return withRetry(() -> {
            try (Connection c = dataSource.getConnection()) {
                return fn.apply(c);
            }
        });
    }

    public void execute(SqlConsumer fn) throws SQLException {
        withRetry(() -> {
            try (Connection c = dataSource.getConnection()) {
                fn.accept(c);
                return null;
            }
        });
    }

    public <T> T tx(SqlFunction<T> fn) throws SQLException {
        return withRetry(() -> {
            try (Connection c = dataSource.getConnection()) {
                boolean orig = c.getAutoCommit();
                c.setAutoCommit(false);
                try {
                    T res = fn.apply(c);
                    c.commit();
                    return res;
                } catch (SQLException e) {
                    try { c.rollback(); } catch (Exception ignore) {}
                    throw new DatabaseOperationException("Transaction failed", e);
                } catch (Exception e) {
                    try { c.rollback(); } catch (Exception ignore) {}
                    throw new DatabaseOperationException("Transaction failed due to unexpected error", e);
                } finally {
                    try { c.setAutoCommit(orig); } catch (Exception ignore) {}
                }
            }
        });
    }

    public boolean ping() {
        try {
            return query(c -> {
                try (PreparedStatement ps = c.prepareStatement("SELECT 1"); ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            });
        } catch (SQLException e) {
            log.warn("DB ping failed", e);
            return false;
        }
    }

    private <T> T withRetry(SupplierX<T> supplier) throws SQLException {
        final boolean retryEnabled = cfg.getBoolean("db.retry.enabled", true);
        final int maxAttempts = cfg.getInt("db.retry.maxAttempts", 3);
        final long baseBackoffMs = cfg.getInt("db.retry.backoff.ms", 200);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return supplier.get();
            } catch (SQLException e) {
                boolean transientErr = isTransient(e);
                if (!retryEnabled || attempt >= maxAttempts || !transientErr) {
                    throw new DatabaseOperationException("Database operation failed after retries", e);
                }
                long sleepMs = Math.min(baseBackoffMs * attempt, Duration.ofSeconds(2).toMillis());
                log.warn(String.format(
                        "DB operation failed (attempt %d/%d, transient=%s, SQLState=%s, errorCode=%d): %s. Retrying in %d ms",
                        attempt, maxAttempts, transientErr, e.getSQLState(), e.getErrorCode(), e.getMessage(), sleepMs));
                performBackoff(sleepMs);
            } catch (Exception e) {
                // Wrap non-SQL exceptions
                if (!retryEnabled || attempt >= maxAttempts) {
                    throw new DatabaseOperationException("DB operation failed", e);
                }
                long sleepMs = Math.min(baseBackoffMs * attempt, Duration.ofSeconds(2).toMillis());
                log.warn(String.format(
                        "DB operation failed (attempt %d/%d, non-SQL): %s. Retrying in %d ms",
                        attempt, maxAttempts, e.getMessage(), sleepMs));
                performBackoff(sleepMs);
            }
        }
        // Should be unreachable
        throw new DatabaseOperationException("Database operation failed: exhausted retries");
    }

    private void performBackoff(long sleepMs) throws SQLException {
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new DatabaseOperationException("Database operation interrupted during backoff", ie);
        }
    }

    private boolean isTransient(SQLException e) {
        if (e instanceof SQLTransientException || e instanceof SQLRecoverableException) {
            return true;
        }
        // Check SQLState class '08' (connection exception)
        String state = e.getSQLState();
        if (state != null && state.startsWith("08")) {
            return true;
        }
        // MySQL specific transient error codes: deadlock 1213, lock wait timeout 1205
        int code = e.getErrorCode();
        return code == 1213 || code == 1205;
    }
}
