package hr.algebra.photoapp_designpatterns_galic.service.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;

@Component
public class UploadMetricsService {
    private final Counter uploadCounter;
    public UploadMetricsService(MeterRegistry meterRegistry) {
        this.uploadCounter = meterRegistry.counter("photoapp.uploads.total");
    }

    public void incrementUploadCount() {
        uploadCounter.increment();
    }
}