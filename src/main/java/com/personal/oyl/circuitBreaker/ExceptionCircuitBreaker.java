/*
 * File Name:CircuitBreaker.java
 * Author:ouyangliang2
 * Date:2016年10月24日
 * Copyright (C) 2006-2016 Tuniu All rights reserved
 */

package com.personal.oyl.circuitBreaker;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ExceptionCircuitBreaker implements CircuitBreaker
{
    private volatile BreakerState state = BreakerState.CLOSED;
    private AtomicLong lastFailure = new AtomicLong(0L);
    private AtomicLong resetMillis = new AtomicLong(5 * 1000L);
    
    private AtomicBoolean isTestAllowed = new AtomicBoolean(true);
    private ExceptionCircuitBreakerInterceptor interceptor;
    
    public ExceptionCircuitBreaker(ExceptionCircuitBreakerInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public <V> V invoke(Callable<V> c) throws Throwable {
        if (!allowRequest()) {
            throw new CircuitBreakerException();
        }

        try {
            V result = c.call();
            close();
            return result;
        } catch (Throwable cause) {
            if (interceptor.including(cause.getCause())) {
                trip();
            }
            
            throw cause;
        }
    }
    
    @Override
    public void trip() {
        state = BreakerState.OPEN;
        isTestAllowed.set(true);
        lastFailure.set(System.currentTimeMillis());
    }
    
    @Override
    public void reset() {
        state = BreakerState.CLOSED;
        isTestAllowed.set(true);
    }
    
    private boolean allowRequest() {
        if (BreakerState.CLOSED == state) {
            return true;
        }

        if (BreakerState.OPEN == state && System.currentTimeMillis() - lastFailure.get() >= resetMillis.get()) {
            state = BreakerState.HALF_CLOSED;
        }
        
        if (BreakerState.HALF_CLOSED == state && isTestAllowed.compareAndSet(true, false)) {
            try
            {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            catch(InterruptedException e1)
            {
            }
            
            return true;
        }
        
        return false;
    }
    
    private void close() {
        state = BreakerState.CLOSED;
        isTestAllowed.set(true);
    }
    
}
