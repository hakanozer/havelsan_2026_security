// RateLimiterConfiguration.java
package com.works.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class RateLimiterConfiguration {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.of(
                RateLimiterConfig.custom()
                        .limitRefreshPeriod(Duration.ofMinutes(1))
                        .limitForPeriod(1)
                        .timeoutDuration(Duration.ZERO)
                        .build()
        );
    }

    @Bean
    public ConcurrentMap<String, RateLimiter> rateLimiters(
            RateLimiterRegistry registry
    ) {
        return new ConcurrentHashMap<>();
    }
}