package org.nightfury.infrastructure.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.nightfury.domain.util.DatabaseManager;

public class DatabaseInit {

    private final DatabaseManager databaseManager;

    public DatabaseInit(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void initializeDatabase() {
        String ddlPath = "src/main/resources/ddl.sql";

        try (Connection conn = databaseManager.getConnection();
            Statement stmt = conn.createStatement()) {

            String sql = new String(Files.readAllBytes(Paths.get(ddlPath)));
            stmt.executeUpdate(sql);
            System.out.println("✅ База даних ініціалізована!");

        } catch (IOException e) {
            System.err.println("❌ Помилка читання файлу DDL: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Помилка виконання SQL: " + e.getMessage());
        }
    }
}
