package com.adam_stegienko.campaign_controller_api_gateway.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@ConditionalOnProperty(name = "spring.liquibase.enabled", havingValue = "true", matchIfMissing = true)
public class LiquibaseConfiguration {

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/changelog.sql");
        return liquibase;
    }
}
