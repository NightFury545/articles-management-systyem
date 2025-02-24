package org.nightfury.infrastructure.database;

import java.sql.*;
import org.nightfury.domain.util.DatabaseManager;

public class SQLiteDatabaseManager implements DatabaseManager {

    private static final String URL = "jdbc:sqlite:./db/blog.db";
    private static Connection connection = null;

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    @Override
    public int executeUpdate(String query, Object... params) {
        try (PreparedStatement stmt = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setParams(stmt, params);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public ResultSet executeQuery(String query, Object... params) throws SQLException {
        try {
            PreparedStatement stmt = getConnection().prepareStatement(query);
            setParams(stmt, params);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void setParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}
