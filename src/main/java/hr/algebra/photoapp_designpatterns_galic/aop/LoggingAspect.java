package hr.algebra.photoapp_designpatterns_galic.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggingAspect {
    @Around("execution(* hr.algebra.photoapp_designpatterns_galic.service.*.*(..))")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        log.info("Executing method: {} ", methodName);

        Object result;
        try {
            result = joinPoint.proceed();
            log.info("Method {} executed successfully with result: {}", methodName, result);
        } catch (Throwable throwable) {
            log.error("Method {} threw an exception: {}", methodName, throwable.getMessage());
            throw throwable;
        }

        return result;
    }
}