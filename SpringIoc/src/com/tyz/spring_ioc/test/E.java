package com.tyz.spring_ioc.test;

import com.tyz.spring_ioc.annotation.Autowired;
import com.tyz.spring_ioc.annotation.Component;

@Component
public class E {
    @Autowired
    private F f;

    public E() {}

    public F getF() {
        return f;
    }

    public void setF(F f) {
        this.f = f;
    }

    @Override
    public String toString() {
        return this.f != null ? "E.f is exist." : "E.f is not exist";
    }
}
