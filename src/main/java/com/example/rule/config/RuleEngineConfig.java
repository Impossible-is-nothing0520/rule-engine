package com.example.rule.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan("com.example.rule.model")
@EnableJpaRepositories("com.example.rule.repository")
@EnableTransactionManagement
public class RuleEngineConfig {
} 