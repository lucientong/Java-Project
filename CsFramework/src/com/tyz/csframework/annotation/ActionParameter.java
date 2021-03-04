<<<<<<< HEAD
package com.tyz.csframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解必须标注在方法的参数上，且方法同时必须有
 * {@link ActionMapping} 注解。由于在对象的传
 * 输过程中，参数的名字是被消除了的，如果我们需要
 * 根据参数名来找对应的值，就可以通过使用
 * {@code ActionParameter} 注解的方式，
 * 将参数的名称通过 name() 来记录。
 *
 * @see #name()
 *
 * @author tyz
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionParameter {
    /** 参数名称 */
    String name();
}
=======
package com.tyz.csframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解必须标注在方法的参数上，且方法同时必须有
 * {@link ActionMapping} 注解。由于在对象的传
 * 输过程中，参数的名字是被消除了的，如果我们需要
 * 根据参数名来找对应的值，就可以通过使用
 * {@code ActionParameter} 注解的方式，
 * 将参数的名称通过 name() 来记录。
 *
 * @see #name()
 *
 * @author tyz
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionParameter {
    /** 参数名称 */
    String name();
}
>>>>>>> d4037e3d4c4890c361da0833e8f35f765b5789b1
