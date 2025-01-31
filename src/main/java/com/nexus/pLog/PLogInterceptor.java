package com.nexus.pLog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PLogInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PLogInterceptor.class);

    @Pointcut("execution(* com.nexus..*(..)) && " +
            "(@within(org.springframework.web.bind.annotation.RestController) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping))"
    )
    public void loggableMethods() {}

    @Around("loggableMethods()")
    public Object  logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        Object result = joinPoint.proceed(); // Proceed with method execution
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        LOGGER.info("Execution time for {}: {} ms", joinPoint.getSignature(), duration);
        return result;
    }
}
