package com.tyz.spring_ioc.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tyz.spring_ioc.annotation.Bean;
import com.tyz.spring_ioc.annotation.Component;
import com.tyz.spring_ioc.test.Complex;
import com.tyz.spring_ioc.test.D;
import com.tyz.spring_ioc.test.E;
import com.tyz.spring_ioc.test.F;

/**
 * 配置无法添加注解的Bean以及需要成员变量赋值的Bean
 *
 * 通过这样的方式生成Bean，和懒汉模式的扫描不一样，懒汉模式
 * 会先将对象生成，在注入的时候其所依赖的元素已经存在于BeanFactory
 * 中了，因此在注入的时候只需要防止循环递归。
 *
 * 但是若通过参数的形式得到一个Bean，它所依赖的参数不一定存在在
 * BeanFactory中，所以要检测是否在参数之间存在循环依赖。
 *
 */
@Component
public class DeployBean {

    @Bean
    public Gson getGson() {
        return new GsonBuilder().create();
    }

    @Bean
    public Complex getComplex() {
        Complex complex = new Complex();
        complex.setReal(7);
        complex.setVir(10);

        return complex;
    }

//    @Bean
//    public D getD(E e) {
//        D d = new D();
//        d.setE(e);
//
//        return d;
//    }
//
//    @Bean
//    public E getE(F f) {
//        E e = new E();
//        e.setF(f);
//
//        return e;
//    }
}
