package com.tyz.spring_ioc.core;

import com.tyz.spring_ioc.annotation.Bean;
import com.tyz.spring_ioc.annotation.Component;
import com.tyz.util.PackageScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 要完成Dependency Injection有两种思路：
 * 1.饿汉式
 *      在进行包扫描的过程中，扫描到一个Autowired注解的类，就从
 *      beanFactory中寻找对应的依赖bean，进行注入。
 *
 *      这样会导致一个问题，就是必须要按照依赖顺序进行扫描，否则
 *      会出现依赖的bean还未添加，无法完成注入。
 * 2.懒汉式
 *      先进行包扫描（不止一轮），将所有的bean先生成，此时可能有很
 *      多bean本身就需要DI。将可以生成的bean全部放入beanFactory
 *      中，等待下一次对bean的调用，调用的时候再完成相关的DI工作。
 *
 *      懒汉模式牵扯到两个问题，一个是要区分是不是第一次调用，因为
 *      我们只需要完成一次获取bean并完成相关注入，不需要第二次。
 *
 *      另一个问题是，进行多轮包扫描，需要考虑清楚 terminator，
 *      在发现存在无法解决的循环依赖或完成了全部bean的生成之后，
 *      停止包扫描。
 *
 *      这里采用懒汉模式实现DI。
 */
public class BeanScanner extends BasicScanner {
    public static void beanScanner(String packageName) {
        BeanFactory beanFactory = new BeanFactory();

        new PackageScanner() {
            @Override
            public void dealClass(Class<?> clazz) {
                if (!clazz.isAnnotationPresent(Component.class)) {
                    return;
                }
                try {
                    //newInstance意味着调用了无参构造，
                    //成员变量还未被注入。
                    Object object = clazz.newInstance();

                    BeanDefinition bean = new BeanDefinition();
                    bean.setClazz(clazz);
                    bean.setObject(object);
                    bean.setInjected(false);

                    beanFactory.addBean(bean);
                    updateMethodTable(bean);

                    //找到 DeployBean类，完成特殊Bean的扫描
                    scanAnnotationIsBean(bean, beanFactory);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }.packageScanner(packageName);
    }

    /**
     * 找到带有Bean注解的方法，通过这些方法生成新的Bean，将其装载到
     * BeanFactory中。
     *
     * 如果我们要使用Gson之类的我们无法操作源码的类，想将其装载到
     * BeanFactory中，或者要对一些类实现有参构造，就需要使用Bean
     * 注解，在config里配置好，通过扫描config包，我们就可以得到
     * 这些特殊的对象了。
     */
    private static void scanAnnotationIsBean(BeanDefinition bean, BeanFactory beanFactory) throws InvocationTargetException, IllegalAccessException {
        Class<?> clazz = bean.getClazz();
        Object object = bean.getObject();

        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (!method.isAnnotationPresent(Bean.class)) {
                continue;
            }
            if (method.getParameterCount() == 0) { //无参方法
                Object newBeanObject = method.invoke(object);
                Class<?> newBeanType = method.getReturnType();

                BeanDefinition newBean = new BeanDefinition();
                newBean.setObject(newBeanObject);
                newBean.setClazz(newBeanType);
                newBean.setInjected(false);

                beanFactory.addBean(newBean);
                updateMethodTable(newBean);//TODO
            } else {
                MethodDefinition methodDefinition = new MethodDefinition(object, method);
                addMethodDefinition(methodDefinition);
            }
        }
    }
}
