package com.personal.oyl.circuitBreaker;

public interface ExceptionCircuitBreakerInterceptor {
    boolean including(Throwable cause);
}
