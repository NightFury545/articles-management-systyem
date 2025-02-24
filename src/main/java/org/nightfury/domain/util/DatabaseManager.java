package org.nightfury.domain.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseManager {

    Connection getConnection() throws SQLException;

    int executeUpdate(String query, Object... params);

    ResultSet executeQuery(String query, Object... params) throws SQLException;
}

