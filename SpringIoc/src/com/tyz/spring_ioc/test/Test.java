package com.tyz.spring_ioc.test;

import com.tyz.spring_ioc.core.BeanFactory;
import com.tyz.spring_ioc.core.BeanScanner;
import com.tyz.spring_ioc.exception.SetterMethodNotFoundException;

import java.lang.reflect.InvocationTargetException;

public class Test {

    public static void main(String[] args) throws SetterMethodNotFoundException, InvocationTargetException, IllegalAccessException {
        BeanScanner.beanScanner("com.tyz");
        BeanFactory beanFactory = new BeanFactory();
        //对普通情况下DI的测试
        A a = beanFactory.getBean(A.class);
        System.out.println(a);

        B b = beanFactory.getBean(B.class);
        b.showMember();

        //对类对象的循环依赖DI的测试
        D d = beanFactory.getBean(D.class);
        System.out.println(d);

//        E e = beanFactory.getBean(E.class);
//        System.out.println(e);
    }
}
