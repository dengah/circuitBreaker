package com.personal.oyl.circuitBreaker;

import java.util.concurrent.Callable;

public interface CircuitBreaker {
    <V> V invoke(Callable<V> c) throws Throwable;

    void trip();
    
    void reset();
}
