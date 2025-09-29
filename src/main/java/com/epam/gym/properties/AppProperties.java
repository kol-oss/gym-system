package com.epam.gym.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
@PropertySource(value = "file:.env", ignoreResourceNotFound = true)
@Getter
@NoArgsConstructor
public class AppProperties {
    @Value("${app.security.max-password-length}")
    private int maxPasswordLength;

    @Value("${app.security.username-delimiter}")
    private String usernameDelimiter;
}
