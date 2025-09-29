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
public class DatabaseProperties {
    @Value("${DB_URL}")
    private String dbUrl;

    @Value("${DB_USERNAME}")
    private String dbUsername;

    @Value("${DB_PASSWORD}")
    private String dbPassword;

    @Value("${db.driver}")
    private String dbDriver;

    @Value("${db.hibernate.dialect}")
    private String hibernateDialect;

    @Value("${db.hibernate.show-sql}")
    private boolean showSql;

    @Value("${db.hibernate.format-sql}")
    private boolean formatSql;

    @Value("${db.hibernate.mode}")
    private String hibernateMode;
}
