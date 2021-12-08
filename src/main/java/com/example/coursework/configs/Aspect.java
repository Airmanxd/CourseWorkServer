package com.example.coursework.configs;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@org.aspectj.lang.annotation.Aspect
public class Aspect {
    /**
     * adds time logging to all service methods
     */
    @Around("allServiceMethods()")
    public Object logAroundTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object res = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        log.info("Finished in: {}ms", elapsedTime);
        return res;
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void allServiceMethods() {}
}
