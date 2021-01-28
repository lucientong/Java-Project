package com.tyz.csframework.annotation;

import com.tyz.csframework.actionbean.ActionBeanFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解须在外层类标有 {@link Action}注解时 时标注在方
 * 法上.被注解为 {@code ActionMapping} 的方法会被注册进
 * {@link ActionBeanFactory}中，
 * 和用户想要实现的具体功能形成映射，从而可以被执行。
 *
 * @author tyz
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionMapping {
    /** 和注解的方法映射的行为(action) */
    String action();
}
