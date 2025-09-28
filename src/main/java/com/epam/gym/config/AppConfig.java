package com.epam.gym.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.epam.gym")
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class AppConfig {
}
