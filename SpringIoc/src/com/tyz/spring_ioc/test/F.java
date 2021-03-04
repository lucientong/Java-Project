package com.tyz.spring_ioc.test;

import com.tyz.spring_ioc.annotation.Autowired;
import com.tyz.spring_ioc.annotation.Component;

@Component
public class F {
    @Autowired
    private D d;

    public F() {}

    public D getD() {
        return d;
    }

    public void setD(D d) {
        this.d = d;
    }

    @Override
    public String toString() {
        return this.d != null ? "F.d is exist." : "F.d is not exist";
    }
}
