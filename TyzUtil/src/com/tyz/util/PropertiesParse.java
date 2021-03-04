package com.tyz.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesParse {
    private static final Map<String, String> keyValuePool;
    static {
        keyValuePool = new HashMap<String, String>();
    }

    public PropertiesParse() {
    }

    public static void loadPropreties(String path) {
        try {
            InputStream is = Class.class.getResourceAsStream(path);
            if (is == null) {
                throw new PropertiesFileIsNotFoundException("Properties file [" + path + "] is not exist");
            }
            Properties properties = new Properties();
            properties.load(is);

            Enumeration<Object> keys = properties.keys();

            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = properties.getProperty(key);

                keyValuePool.put(key, value);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PropertiesFileIsNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String key) {
        return keyValuePool.get(key);
    }
}
