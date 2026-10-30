package com.works.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfiguration {

    @Bean
    public RateLimiter rateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(1)) // 1 dakika
                .limitForPeriod(5) // 5 istek
                .timeoutDuration(Duration.ZERO) // bekleme yok
                .build();
        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        return registry.rateLimiter("filterRateLimiter");
    }

}
