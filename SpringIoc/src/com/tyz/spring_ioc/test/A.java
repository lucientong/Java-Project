package com.tyz.spring_ioc.test;

import com.tyz.spring_ioc.annotation.Component;

@Component
public class A {
    private int num;
    private String str;

    public A() {
        this.num = 710;
        this.str = "yzh";
    }

    @Override
    public String toString() {
        return "num: " + this.num + " str: " + this.str;
    }
}
