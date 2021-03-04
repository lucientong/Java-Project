package com.tyz.util.orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClassTableDefinition {
    private Class<?> klass;
    private String table;
    private List<FieldColumnDefinition> fieldList;
    private FieldColumnDefinition key;
    private Object object;

    ClassTableDefinition() {
        this.fieldList = new ArrayList<FieldColumnDefinition>();
    }

    FieldColumnDefinition getKey() {
        return key;
    }

    int getFieldCount() {
        return fieldList.size();
    }

    Object getObject() {
        return object;
    }

    void setObject(Object object) {
        this.object = object;
    }

    void setKey(FieldColumnDefinition key) {
        this.key = key;
    }

    String getKeyColumn() {
        return this.table + "." + key.getColumn();
    }

    FieldColumnDefinition getFieldColumnDefinition(String fieldName) {
        for (FieldColumnDefinition fcd : fieldList) {
            if (fcd.getField().getName().equals(fieldName)) {
                return fcd;
            }
        }
        return null;
    }

    void setColumnName(String fieldName, String columnName) {
        FieldColumnDefinition fcd = getFieldColumnDefinition(fieldName);
        fcd.setColumn(columnName);
    }

    String getColumnList() {
        StringBuffer result = new StringBuffer();
        boolean isFirst = true;

        for (FieldColumnDefinition fcd : fieldList) {
            result.append(isFirst ? "" : ",");
            result.append(this.table + "." + fcd.getColumn());
            isFirst = false;
        }

        return result.toString();
    }

    Class<?> getKlass() {
        return klass;
    }

    void setKlass(String className) {
        try {
            this.klass = Class.forName(className);
            Field[] fields = klass.getDeclaredFields();

            for (Field field : fields) {
                FieldColumnDefinition fieldColumn = new FieldColumnDefinition();
                fieldColumn.setField(field);
                fieldColumn.setColumn(field.getName());
                this.fieldList.add(fieldColumn);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    List<FieldColumnDefinition> getFieldList() {
        return fieldList;
    }

    String getTable() {
        return table;
    }

    void setTable(String table) {
        this.table = table;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append(this.klass.getSimpleName()).append(" <=> ").append(this.table);

        for (FieldColumnDefinition fcd : fieldList) {
            result.append("\n\t").append(fcd);
        }
        result.append("\n\tkey:").append(getKeyColumn());

        return result.toString();
    }
}
