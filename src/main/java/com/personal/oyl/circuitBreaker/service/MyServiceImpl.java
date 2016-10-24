package com.personal.oyl.circuitBreaker.service;

public class MyServiceImpl implements MyService {
    public String queryString(boolean flag) {
        
        if (flag) {
            return "Hello World!!!";
        } else {
            throw new RuntimeException("it's a fuck!!!");
        }
        
    }
}
