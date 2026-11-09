package org.example.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** Settings under {@code security.jwt} in application.yaml. */
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(String secret, Duration expiration) {
}
