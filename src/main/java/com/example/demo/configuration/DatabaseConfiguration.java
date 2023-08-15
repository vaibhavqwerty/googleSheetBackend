package com.example.demo.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@Slf4j
@EnableJpaRepositories(basePackages = "com.example.demo", includeFilters =
        {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Repository.class)})

public class DatabaseConfiguration implements EnvironmentAware {

    private Environment environment;

    @Value("${db.name}")
    private String dbName;

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUser;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.server}")
    private String serverName;

    @Value("${db.max-active}")
    private Integer dbMaxActive;

    @Value("${db.idle-timeout}")
    private Integer minIdle;

    @Value("${db.connection-timeout}")
    private Integer connectionTimeout;


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Autowired
    @Bean(name = "masterJdbc")
    @Primary
    public JdbcTemplate getJdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "dataSource")
    @Primary
    public DataSource dataSource() {
//        log.debug("Configuring Datasource");
        if (StringUtils.isBlank(dbUrl) && StringUtils.isBlank(dbName)) {
//            log.error("Your database connection pool configuration is incorrect! The application cannot start. Please"
//                    + " check your Spring profile, current profiles are: {}", environment.getActiveProfiles());
            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
        if (StringUtils.isBlank(dbUrl)) {
            config.addDataSourceProperty("databaseName", dbName);
            config.addDataSourceProperty("serverName", serverName);
        } else {
            config.addDataSourceProperty("url", dbUrl);
        }
        config.addDataSourceProperty("user", dbUser);
        config.addDataSourceProperty("password", dbPassword);
        config.setMinimumIdle(minIdle);
        config.setMaximumPoolSize(dbMaxActive);
        config.setConnectionTimeout(connectionTimeout);
        return new HikariDataSource(config);
    }

    @Bean(name = "entityManagerFactory")
    @Qualifier(value = "entityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("dataSource") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("com.hirelyy.base")
                .persistenceUnit("master")
                .build();
    }

    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public Flyway flyway() {
//        log.debug("Running database migrations");
        Flyway flyway = Flyway.configure().dataSource(dataSource()).baselineOnMigrate(true).outOfOrder(true)
                .validateOnMigrate(true).load();
        flyway.repair();
        flyway.migrate();
        return flyway;
    }
}



