package com.tyz.util.orm;

import com.tyz.util.XmlParse;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

public class ClassTableFactory {
    private static final Map<String, ClassTableDefinition> classTablePool;
    static {
        classTablePool = new HashMap<String, ClassTableDefinition>();
    }

    public ClassTableFactory() {
    }

    public static void loadClassTableMapping(String mappingPath) {
        new XmlParse() {
            @Override
            public boolean dealElement(Element element, int index) {
                String className = element.getAttribute("class");
                String tableName = element.getAttribute("table");

                ClassTableDefinition ctd = new ClassTableDefinition();
                ctd.setKlass(className);
                ctd.setTable(tableName);
                classTablePool.put(className, ctd);

                new XmlParse() {
                    @Override
                    public boolean dealElement(Element element, int index) {
                        String fieldName = element.getAttribute("name");
                        String columnName = element.getAttribute("column");

                        ctd.setColumnName(fieldName, columnName);

                        return true;
                    }
                }.getElement(element, "field");
                new XmlParse() {

                    @Override
                    public boolean dealElement(Element element, int index) {
                        String fieldName = element.getAttribute("keyName");
                        FieldColumnDefinition fcd = ctd.getFieldColumnDefinition(fieldName);
                        ctd.setKey(fcd);
                        return true;
                    }
                }.getElement(element, "key");
                return true;
            }
        }.getElement(XmlParse.getDocument(mappingPath), "mapping");
    }

    static ClassTableDefinition getClassTableDefinition(Class<?> klass) {
        return getClassTableDefinition(klass.getName());
    }

    static ClassTableDefinition getClassTableDefinition(String className) {
        return classTablePool.get(className);
    }
}
