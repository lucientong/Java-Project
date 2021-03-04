package com.tyz.spring_ioc.test;

import com.google.gson.Gson;
import com.tyz.spring_ioc.annotation.Autowired;
import com.tyz.spring_ioc.annotation.Component;

@Component
public class B {
    @Autowired
    private A a;
    @Autowired
    private Gson gson;
    @Autowired
    private Complex complex;

    public B() {}

    public void showMember() {
        System.out.println(this.a);
        System.out.println(this.gson.toJson(this.complex));
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public Complex getComplex() {
        return complex;
    }

    public void setComplex(Complex complex) {
        this.complex = complex;
    }
}
