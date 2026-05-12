package com.shoppingCartBackend.shoppingCartBackend.loadbalancer;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoadBalancerAspect {

    @Before("execution(* com.shoppingCartBackend.shoppingCartBackend.loadbalancer.HttpLoadBalancer.forwardRequest(..))")
    public void logDispatch(JoinPoint joinPoint) {
        System.out.println("[AOP] Forwarding request via LoadBalancer from thread: "
                + Thread.currentThread().getName());
    }
}