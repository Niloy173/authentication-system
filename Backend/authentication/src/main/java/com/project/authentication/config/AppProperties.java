package com.project.authentication.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {


    private Mail mail = new Mail();
    private Security security = new Security();
    private Token token = new Token();


    @Getter
    @Setter
    public static class Security {
        private int maxFailedAttempts;
        private int lockDurationMinutes;
    }

    @Getter
    @Setter
    public static class Mail {
        private boolean enabled;
    }

    @Getter
    @Setter
    public static class Token {
        private int expiryMinutes;
    }
}
