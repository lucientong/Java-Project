package com.tyz.spring_ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被Autowired注解的成员变量将被从BeanFactory中找到对应的依赖
 * 并完成注入。</br>
 * 在此Spring的IOC模拟中，仅完成对复杂类型的DI，八大基本类型的
 * DI不做实现。</br>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
}
