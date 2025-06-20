package hr.algebra.photoapp_designpatterns_galic.aop;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Slf4j
@Component
public class PerformanceAspect {
    private final MeterRegistry meterRegistry;
    public PerformanceAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("@annotation(hr.algebra.photoapp_designpatterns_galic.aop.TrackPerformance)")
    public Object trackPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        Object result = joinPoint.proceed();
        long duration = System.nanoTime() - start;

        String methodName = joinPoint.getSignature().toShortString();
        meterRegistry.timer("app.method.execution", "method", methodName)
                .record(duration, TimeUnit.NANOSECONDS);

        log.info("[PERF] {} took {} ms", methodName, duration / 1_000_000);
        return result;
    }
}