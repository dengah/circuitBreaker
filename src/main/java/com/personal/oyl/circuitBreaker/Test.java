/*
 * File Name:Test.java
 * Author:ouyangliang2
 * Date:2016年10月24日
 * Copyright (C) 2006-2016 Tuniu All rights reserved
 */
 
package com.personal.oyl.circuitBreaker;

import java.util.concurrent.TimeUnit;

import com.personal.oyl.circuitBreaker.service.MyService;
import com.personal.oyl.circuitBreaker.service.ServiceFactory;

public class Test
{
    public static void doStuff(MyService service, boolean flag) {
        try {
            String str = service.queryString(flag);
            
            System.out.println(str);
        }catch (CircuitBreakerException e) {
            System.out.println("service unavailable!!!");
        } catch(Throwable e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        final MyService service = ServiceFactory.getService();
        
        doStuff(service, true);
        doStuff(service, true);
        doStuff(service, true);
        doStuff(service, false);
        doStuff(service, true);
        doStuff(service, true);
        doStuff(service, true);
        
        try
        {
            TimeUnit.MILLISECONDS.sleep(6000);
        }
        catch(InterruptedException e1)
        {
        }
        
        new Thread(new Runnable(){public void run(){doStuff(service, true);}}).start();
        new Thread(new Runnable(){public void run(){doStuff(service, true);}}).start();
        new Thread(new Runnable(){public void run(){doStuff(service, true);}}).start();
        
        try
        {
            TimeUnit.MILLISECONDS.sleep(2000);
        }
        catch(InterruptedException e1)
        {
        }
        
        doStuff(service, true);
        doStuff(service, true);
        doStuff(service, true);
        
    }
    
}
