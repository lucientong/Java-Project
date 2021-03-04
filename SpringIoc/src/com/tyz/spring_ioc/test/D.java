package com.tyz.spring_ioc.test;

import com.tyz.spring_ioc.annotation.Autowired;
import com.tyz.spring_ioc.annotation.Component;

@Component
public class D {
    @Autowired
    private E e;

    public D() {}

    public E getE() {
        return e;
    }

    public void setE(E e) {
        this.e = e;
    }

    @Override
    public String toString() {
        return this.e != null ? "D.e is exist." : "D.e is not exist";
    }
}
