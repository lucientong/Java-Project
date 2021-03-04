package com.tyz.nio.actionbean;

import java.lang.reflect.Type;

/**
 * 描述一个方法执行所需要的参数
 *
 * @author tyz
 */
public class ParameterDefinition {
    /** 参数的名称 */
    private String name;

    /** 参数的类型 */
    private Type type;

    ParameterDefinition(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @return 参数的类型 {@code type}
     */
    Type getType() {
        return type;
    }

    /**
     * @return 参数名 {@code name}
     */
    String getName() {
        return name;
    }
}
