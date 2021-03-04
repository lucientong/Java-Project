package com.tyz.spring_ioc.core;

import com.tyz.spring_ioc.annotation.Autowired;
import com.tyz.spring_ioc.exception.BeanNotFoundException;
import com.tyz.spring_ioc.exception.SetterMethodNotFoundException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 实现了一个储存Bean的容器，收集被Bean注解的对象。</br>
 * beanFactory的键为类名，值为一个BeanDefinition的对象。
 *
 * 这里beanFactory里存放的bean是单例的，beanFactory也是。
 */
public class BeanFactory {
    private static final Map<String, BeanDefinition> beanFactory;
    static {
        beanFactory = new HashMap<>();
    }

    public BeanFactory() {}

    /**
     * 将一个BeanDefinition类型的bean放入beanFactory中。
     */
    public void addBean(BeanDefinition bean) {
        beanFactory.put(bean.getClazz().getName(), bean);
    }

    /**
     * 通过类名得到对应的对象
     * @param beanId 类名
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanId) throws SetterMethodNotFoundException, InvocationTargetException, IllegalAccessException {
        BeanDefinition bean = beanFactory.get(beanId);
        if (bean == null) {
            invokeMethods();
            bean = beanFactory.get(beanId);
            if (bean == null) {
                throw new BeanNotFoundException("bean [" +
                        beanId + "] is not exist.");
            }
        }

        //经典多线程安全性判断。
        //这里要保证bean只被注入一次，第一个线程先进入加了锁，此时
        //bean.isInjected 仍然是false，第二个线程也进入了外层判
        //段，被挡在了锁外。
        //这时线程1对 bean完成了注入，isInjected 被修改为true，但
        //此时线程2是在锁外的，也就是已经通过了第一轮的检测。若没有内
        //层的第二轮判断，在线程1将锁打开以后，线程2仍会进入，再一次
        //对bean进行注入。
        if (!bean.isInjected()) {
            //若A->B, B->C, C->A，这样就构成了类对象的循环依赖。
            //处理注入的逻辑是，若当前Bean未注入过，找到它所有
            //被Autowired注解的成员变量。从BeanFactory中得
            //到相关依赖的Bean，实现注入。
            //在扫描到A后，A的对象已经生成，只是还未注入它的依赖
            //B，若在A还是未注入的状态下，去注入B，就会导致循环
            //依赖无法解决，A,B,C一直保持未注入的状态，递归调用。
            //所以在扫描到一个未注入过的Bean之后，我们先将它设置
            //为注入完成，然后再进行注入，这样就避免了递归无限进行
            //下去。
            synchronized (beanFactory) {
                if (!bean.isInjected()) {
                    bean.setInjected(true);
                    injection(bean);
//                    bean.setInjected(true);
                }
            }
        }
        return (T) bean.getObject();
    }

    /**
     * 通过类类型得到对应的对象
     * @param clazz 类类型
     */
    public <T> T getBean(Class<?> clazz) throws SetterMethodNotFoundException, InvocationTargetException, IllegalAccessException {
        return getBean(clazz.getName());
    }

    /**
     * 对bean进行注入。先检测bean的成员变量是否存在Autowired注解，
     * 对有该注解的成员变量进行DI。
     */
    private void injection(BeanDefinition bean) throws SetterMethodNotFoundException {
        Class<?> clazz = bean.getClazz();
        Object object = bean.getObject();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }
            //这里对成员变量的赋值有三个选择，第一个是直接操作成员变量，对
            //它赋值，第二个是通过构造函数直接进行DI，第三个是通过该类自身
            //的setter函数进行赋值。
            //其中第二个方法是最符合OOP原则的，对对象封装性最好，破坏性最小。
            //第一种方法对OOP封装破坏性最大。
            //这里通过构造函数直接进行注入的要求比较严格，因为我们每次只扫描
            //一个成员变量，在操作上可行性不高。第一种方法由于大部分的成员变
            //量都是private，为了实现代码简洁和尽可能对封装性最小的破坏。这
            //里选择用setter函数实现赋值，如果对应的类中不存在格式化的setter
            //函数，那就只能按无法注入处理。
            String filedName = field.getName();
            String setterName = "set"
                                + filedName.substring(0, 1).toUpperCase()
                                + filedName.substring(1);
            Class<?> parameterType = field.getType();
            try {
                Method method = clazz.getMethod(setterName, parameterType);
                Object parameterValue = getBean(parameterType);

                method.invoke(object, parameterValue);
            } catch (NoSuchMethodException e) {
                throw new SetterMethodNotFoundException("Can't find setter method, failed in the injection");
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行所有参数依赖关系被满足的方法
     */
    private void invokeMethods() throws SetterMethodNotFoundException, InvocationTargetException, IllegalAccessException {
        List<MethodDefinition> methodList = BasicScanner.getMethodsToInvoke();
        if (methodList.isEmpty()) {
            //一轮扫描完成后，若已经没有可以执行的方法，但是methodPool里仍然
            //有方法未执行，说明存在参数循环依赖，再继续扫描也无济于事了。
            if (!BasicScanner.methodPool.isEmpty()) {
                throw new BeanNotFoundException(showTheRestMethods());
            } else {
                return;
            }
        }

        for (MethodDefinition methodDefinition : methodList) {
            Method method = methodDefinition.getMethod();
            Object object = methodDefinition.getObject();

            Parameter[] parameters = method.getParameters();
            Object[] parameterValues = new Object[parameters.length];

            int index = 0;

            for (Parameter parameter : parameters) {
                Class<?> clazz = parameter.getType();
                Object value = getBean(clazz);
                parameterValues[index++] = value;
            }

            Object beanValue = method.invoke(object, parameterValues);
            Class<?> beanClass = method.getReturnType();

            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setClazz(beanClass);
            beanDefinition.setObject(beanValue);
            beanDefinition.setInjected(true);

            addBean(beanDefinition);
            BasicScanner.updateMethodTable(beanDefinition);
        }
        invokeMethods();
    }

    /**
     * 显示methodPool中还未被执行的方法以及方法的依赖
     */
    private String showTheRestMethods() {
        StringBuilder res = new StringBuilder();
        Map<MethodDefinition, Set<Class<?>>> methodPool = BasicScanner.methodPool;

        res.append("\nThere are dependency circle below:\n");

        for (MethodDefinition methodDefinition : methodPool.keySet()) {
            boolean isFirst = true;
            res.append("\n\t").append(methodDefinition.getMethod().getName()).append(": ");
            for (Class<?> clazz : methodPool.get(methodDefinition)) {
                res.append(isFirst ? "" : ", ").append(clazz.getName());
                isFirst = false;
            }
            res.append("\n");
        }
        return res.toString();
    }

    static BeanDefinition getBeanDefinition(Class<?> clazz) {
        return beanFactory.get(clazz.getName());
    }
}
