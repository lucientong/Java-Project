package com.tyz.nio.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户需要将 {@code Action} 注解标在类上，以此表明这个类中有方法
 * 需要被注册。
 *
 * @author tyz
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
}
