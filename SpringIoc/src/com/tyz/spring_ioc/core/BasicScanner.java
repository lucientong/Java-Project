package com.tyz.spring_ioc.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 对于用config来配置装载Bean的方法，可能会出现参数之间的循环依赖问题。
 * A(B b), B(A a)
 *
 * 因此在扫描被Bean注解的方法时，建立起两个映射，一个映射是方法和它所依
 * 赖的所有的参数，一个是参数和依赖于它的所有方法。这里的参数是指未装载的
 * Bean，这里的方法则是还存在依赖尚未被满足情况的方法。
 *
 */
public class BasicScanner {
    protected static final Map<MethodDefinition, Set<Class<?>>> methodPool;
    protected static final Map<Class<?>, Set<MethodDefinition>> argumentsPool;

    static {
        methodPool = new HashMap<>();
        argumentsPool = new HashMap<>();
    }

    /**
     * 向以方法为键和以参数为键的两张表中插入一个扫描到的方法
     */
    protected static void addMethodDefinition(MethodDefinition methodDefinition) {
        Method method = methodDefinition.getMethod();
        Parameter[] parameters = method.getParameters();

        if (parameters.length < 1) return;

        Set<Class<?>> set = new HashSet<>();

        for (Parameter parameter : parameters) {
            Class<?> parameterType = parameter.getType();
            //这一步的判断，是因为有的方法可能会依赖于Autowired注解所
            //生成的Bean，所以我们需要做一个判断，将其跳过。
            if (BeanFactory.getBeanDefinition(parameterType) != null) {
                continue;
            }
            set.add(parameterType);

            argumentsPool.getOrDefault(parameterType, new HashSet<>()).add(methodDefinition);
        }
        methodPool.put(methodDefinition, set);
    }

    /**
     * 每装载好一个Bean，就可以找寻依赖于这个Bean的方法，并在argumentPool和methodPool
     * 中更新这个Bean对应的所有依赖关系。
     *
     * 若一个参数已经装载完成（完成了注入），先从argumentPool中取得依赖这个参数的所有
     * 方法，在methodPool中将这些方法中的这个参数删除，最后再从argumentPool中删除这
     * 个参数的键。
     */
    protected static void updateMethodTable(BeanDefinition beanDefinition) {
        if (argumentsPool.isEmpty()) {
            return;
        }
        Class<?> clazz = beanDefinition.getClazz();
        Set<MethodDefinition> set = argumentsPool.get(clazz);

        if (set == null || set.isEmpty()) {
            return;
        }
        for (MethodDefinition methodDefinition : set) {
            methodPool.get(methodDefinition).remove(clazz);
        }
        argumentsPool.remove(clazz);
    }

    /**
     * 得到所有的依赖已经被满足的方法。
     *
     * 在多轮的扫描中，不断有新的Bean会产生，产生出来的Bean可能就满足了某个方法的依赖，
     * 这时候就可以尝试执行这个方法，因此会得到新的Bean，在这个相互作用的过程中将逐渐把
     * Bean配置完成。
     * 因此，需要在每一次依赖关系发生变动时调用这个方法，及时找到已经可以运行的方法，及时
     * 用它来生成新的Bean。
     *
     * 这里需要注意迭代器的规则，不可以在做查找的时候删除元素。
     */
    static List<MethodDefinition> getMethodsToInvoke() {
        List<MethodDefinition> methodList = new ArrayList<>();

        for (MethodDefinition methodDefinition : methodPool.keySet()) {
            if (methodPool.get(methodDefinition).isEmpty()) {
                methodList.add(methodDefinition);
            }
        }

        for (MethodDefinition methodDefinition : methodList) {
            methodPool.remove(methodDefinition);
        }

        return methodList;
    }
}
