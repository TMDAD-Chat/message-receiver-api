package es.unizar.tmdad.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrometheusCustomMetricsConfiguration {

    private final Tags tags;

    public PrometheusCustomMetricsConfiguration(@Value("${spring.application.name}") String appName) {
        this.tags = Tags.of("service", appName);
    }

    @Bean
    public Counter incomingMessagesAmount(MeterRegistry meterRegistry){
        return meterRegistry.counter("messages_in_amount", tags);
    }

    @Bean
    public Counter outcomingMessagesAmount(MeterRegistry meterRegistry){
        return meterRegistry.counter("messages_out_amount", tags);
    }

    @Bean
    public Counter incomingMessagesBytes(MeterRegistry meterRegistry){
        return meterRegistry.counter("messages_in_bytes", tags);
    }

    @Bean
    public Counter outcomingMessagesBytes(MeterRegistry meterRegistry){
        return meterRegistry.counter("messages_out_bytes", tags);
    }

}
