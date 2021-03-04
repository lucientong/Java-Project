package com.tyz.util.orm;

import java.lang.reflect.Field;

public class FieldColumnDefinition {
    private Field field;
    private String column;

    FieldColumnDefinition() {
    }

    Field getField() {
        return field;
    }

    void setField(Field field) {
        this.field = field;
    }

    String getColumn() {
        return column;
    }

    void setColumn(String column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return field.getName() + " <=> " + column;
    }
}
