package com.tyz.util;

import java.util.HashMap;
import java.util.Map;

public class TypeParser {
    private static final Map<String, Class<?>> typePool =
            new HashMap<String, Class<?>>();
    static {
        typePool.put("byte", byte.class);
        typePool.put("boolean", boolean.class);
        typePool.put("char", char.class);
        typePool.put("short", short.class);
        typePool.put("int", int.class);
        typePool.put("float", float.class);
        typePool.put("double", double.class);
        typePool.put("long", long.class);
        typePool.put("String", String.class);
    }

    public TypeParser() {
    }

    public static Class<?> strToType(String strType) {
        if (strType == null) {
            return null;
        }
        Class<?> type = typePool.get(strType);

        if (type == null) {
            try {
                type = Class.forName(strType);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return type;
    }
}
