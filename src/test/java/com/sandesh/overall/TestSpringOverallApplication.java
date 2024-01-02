package com.sandesh.overall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class TestSpringOverallApplication {

    @Bean
    @RestartScope
    @ServiceConnection
    MySQLContainer<?> mysqlContainer() {
        MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.30"));
        return mySQLContainer;
    }

    public static void main(String[] args) {
        SpringApplication.from(SpringOverallApplication::main).with(TestSpringOverallApplication.class).run(args);
    }

}
