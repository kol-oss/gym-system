package com.epam.gym.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Around("execution(* com.epam.gym..*(..))")
    public Object handleServiceExceptions(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            Signature signature = joinPoint.getSignature();
            log.error("{}.{}(): {}", signature.getDeclaringTypeName(), signature.getName(), ex.getMessage());

            return null;
        }
    }
}
