package com.tyz.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 在对象传输中记录参数
 *
 * @author tyz
 */
public class ArgumentMaker {
    private static final Type TYPE = new TypeToken<Map<String, String>>() {}.getType();
    public static final Gson GSON = new GsonBuilder().create();
    private Map<String, String> argMap;

    public ArgumentMaker() {
        this.argMap = new HashMap<String, String>();
    }

    public ArgumentMaker(String parameter) {
        this.argMap = GSON.fromJson(parameter, TYPE);
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(String name, Class<?> type) {
        String str = this.argMap.get(name);
        if (str == null) {
            return null;
        }
        return (T) GSON.fromJson(str, type);
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(String name, Type type) {
        String str = this.argMap.get(name);
        if (str == null) {
            return null;
        }
        return (T) GSON.fromJson(str, type);
    }

    public ArgumentMaker addArg(String name, Object value) {
        argMap.put(name, GSON.toJson(value));
        return this;
    }

    @Override
    public String toString() {
        return GSON.toJson(this.argMap);
    }
}
