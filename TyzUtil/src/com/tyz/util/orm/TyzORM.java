package com.tyz.util.orm;

import com.tyz.util.PropertiesParse;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TyzORM {
    private static Connection connection;

    public TyzORM() {
    }

    public static void initTyzORM(String configPath) {
        PropertiesParse.loadPropreties(configPath);
        try {
            connection = DriverManager.getConnection(
                    PropertiesParse.getValue("url"),
                    PropertiesParse.getValue("user"),
                    PropertiesParse.getValue("passward"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    private void preparedStatement(PreparedStatement state, ClassTableDefinition ctd,
                                   Object object) throws Exception {
        Class<?> klass = ctd.getKlass();
        int fieldCount = ctd.getFieldCount();
        List<FieldColumnDefinition> fieldList = ctd.getFieldList();

        for (int index = 0; index < fieldCount; index++) {
            Field field = fieldList.get(index).getField();

            Object value = getFieldValue(field, klass, object);

            state.setObject(index + 1, value);
        }
    }

    private Object getFieldValue(Field field, Class<?> klass, Object object) throws Exception {
        String methodName = field.getName();
        methodName = "get" + methodName.substring(0, 1).toUpperCase() +
                methodName.substring(1);
        Method method = klass.getMethod(methodName, new Class<?>[] {});
        Object value = method.invoke(object, new Object[] {});

        return value;
    }

    public void update(Object object) {
        Class<?> klass = object.getClass();
        ClassTableDefinition ctd = ClassTableFactory.getClassTableDefinition(klass);

        if (ctd == null) {
            System.out.println("类[" + klass.getName() + "]没有映射关系");
            return;
        }

        StringBuffer updateCommand = new StringBuffer();
        updateCommand.append("UPDATE ").append(ctd.getTable()).append(" SET");

        List<FieldColumnDefinition> fieldList = ctd.getFieldList();
        boolean isFirst = true;

        for (FieldColumnDefinition fcd : fieldList) {
            if (ctd.getKey().equals(fcd)) {
                continue;
            }
            String columnName = fcd.getColumn();
            updateCommand.append(isFirst ? " " : ", ").append(columnName).append("=?");
            isFirst = false;
        }
        updateCommand.append(" WHERE ").append(ctd.getKeyColumn()).append("=?");

        try {
            PreparedStatement state = connection.prepareStatement(updateCommand.toString());
            int index = 0;

            for (FieldColumnDefinition fcd : fieldList) {
                if (fcd.equals(ctd.getKey())) {
                    continue;
                }
                Object value = getFieldValue(fcd.getField(), klass, object);
                state.setObject(++index, value);
            }
            Object keyValue = getFieldValue(ctd.getKey().getField(), klass, object);
            state.setObject(++index, keyValue);

            state.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(Object object) {
        Class<?> klass = object.getClass();
        ClassTableDefinition ctd = ClassTableFactory.getClassTableDefinition(klass);

        if (ctd == null) {
            System.out.println("类[" + klass.getName() + "]没有映射关系");
            return;
        }

        StringBuffer saveCommand = new StringBuffer();
        saveCommand.append("INSERT INTO ")
                .append(ctd.getTable())
                .append(" (")
                .append(ctd.getColumnList())
                .append(" ) VALUES(");
        for (int i = 0; i < ctd.getFieldCount(); i++) {
            saveCommand.append(i == 0 ? "" : ",").append("?");
        }
        saveCommand.append(")");

        try {
            PreparedStatement state = connection.prepareStatement(saveCommand.toString());
            preparedStatement(state, ctd, object);

            state.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
