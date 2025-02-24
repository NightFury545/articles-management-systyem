package org.nightfury.domain.entities;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nightfury.domain.util.DatabaseManager;

public abstract class Entity {

    protected int id;
    protected DatabaseManager databaseManager;

    public Entity(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    protected abstract String getTableName();

    public void save() {
        if (id == 0) {
            insert();
        } else {
            update();
        }
    }

    public void delete() {
        if (id == 0) {
            return;
        }
        String query = "DELETE FROM " + getTableName() + " WHERE id = ?";
        databaseManager.executeUpdate(query, id);
    }

    public static <T extends Entity> T findById(Class<T> clazz, int id, DatabaseManager dbManager) {
        try {
            T entity = clazz.getDeclaredConstructor(DatabaseManager.class).newInstance(dbManager);
            String query = "SELECT * FROM " + entity.getTableName() + " WHERE id = ?";
            ResultSet rs = dbManager.executeQuery(query, id);

            if (rs.next()) {
                return mapResultSetToEntity(clazz, rs, dbManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Entity> List<T> findAll(Class<T> clazz, DatabaseManager dbManager) {
        List<T> entities = new ArrayList<>();
        try {
            T entity = clazz.getDeclaredConstructor(DatabaseManager.class).newInstance(dbManager);
            String query = "SELECT * FROM " + entity.getTableName();
            ResultSet rs = dbManager.executeQuery(query);

            while (rs.next()) {
                entities.add(mapResultSetToEntity(clazz, rs, dbManager));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entities;
    }

    private static <T extends Entity> T mapResultSetToEntity(Class<T> clazz, ResultSet rs, DatabaseManager dbManager) throws Exception {
        T obj = clazz.getDeclaredConstructor(DatabaseManager.class).newInstance(dbManager);

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            if (field.getType().getSuperclass() != null && Entity.class.isAssignableFrom(field.getType().getSuperclass())) {
                handleForeignKeyField(field, obj, rs, dbManager);
            } else {
                handleRegularField(field, obj, rs);
            }
        }

        Field idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(obj, rs.getObject("id"));

        return obj;
    }

    private static void handleForeignKeyField(Field field, Object obj, ResultSet rs, DatabaseManager dbManager) {
        String foreignKeyFieldName = field.getName() + "_id";
        try {
            if (rs.findColumn(foreignKeyFieldName) != -1) {
                int relatedEntityId = rs.getInt(foreignKeyFieldName);
                if (relatedEntityId != 0) {
                    Class<?> relatedEntityClass = field.getType();
                    @SuppressWarnings("unchecked")
                    Entity relatedEntity = findById((Class<Entity>) relatedEntityClass, relatedEntityId, dbManager);
                    field.set(obj, relatedEntity);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error with foreign key field: " + field.getName() + "_id");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleRegularField(Field field, Object obj, ResultSet rs) {
        try {
            if (field.getType().equals(LocalDateTime.class)) {
                String dateStr = rs.getString(field.getName());
                if (dateStr != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
                    field.set(obj, dateTime);
                }
            } else {
                field.set(obj, rs.getObject(field.getName()));
            }
        } catch (SQLException e) {
            System.err.println("Error setting field value: " + field.getName() + " - " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void insert() {
        Field[] fields = this.getClass().getDeclaredFields();
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Object> params = new ArrayList<>();

        for (Field field : fields) {
            if (!field.getName().equals("id")) {
                field.setAccessible(true);
                try {
                    Object value = field.get(this);

                    if (value == null || value instanceof List || value instanceof Set
                        || value instanceof Map
                        || value.getClass().isArray()) {
                        continue;
                    }

                    if (value instanceof Entity) {
                        Field idField = Entity.class.getDeclaredField("id");
                        idField.setAccessible(true);
                        value = idField.get(value);
                        columns.append(field.getName()).append("_id,");
                    } else {
                        columns.append(field.getName()).append(",");
                    }

                    values.append("?,");

                    params.add(value);

                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }

        if (columns.isEmpty()) {
            return;
        }

        columns.setLength(columns.length() - 1);
        values.setLength(values.length() - 1);

        String query =
            "INSERT INTO " + getTableName() + " (" + columns + ") VALUES (" + values + ")";
        int generatedId = databaseManager.executeUpdate(query, params.toArray());
        if (generatedId > 0) {
            this.id = generatedId;
        }
    }

    private void update() {
        Field[] fields = this.getClass().getDeclaredFields();
        StringBuilder setClause = new StringBuilder();
        List<Object> params = new ArrayList<>();

        for (Field field : fields) {
            if (!field.getName().equals("id")) {
                field.setAccessible(true);
                try {
                    Object value = field.get(this);
                    if (value == null || value instanceof List || value instanceof Set
                        || value instanceof Map
                        || value.getClass().isArray()) {
                        continue;
                    }
                    setClause.append(field.getName()).append(" = ?,");

                    params.add(value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        if (setClause.isEmpty()) {
            return;
        }

        setClause.setLength(setClause.length() - 1);
        params.add(id);
        String query = "UPDATE " + getTableName() + " SET " + setClause + " WHERE id = ?";
        databaseManager.executeUpdate(query, params.toArray());
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }
}
