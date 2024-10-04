package com.hyekyoung.batchstudy.batchfire.config.database;

import com.hyekyoung.batchstudy.batchfire.model.property.ConnectInfo;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;


import javax.sql.DataSource;
import java.io.File;
import java.util.Map;

@Slf4j
@Configuration
@DependsOn("jdbcConnectProperties")
public class DBConfig {
    private final Map<String, ConnectInfo.Jdbc> connectProperties;

    public DBConfig(Map<String, ConnectInfo.Jdbc> connectProperties) {
        this.connectProperties = connectProperties;
    }

    @Bean(name = "h2DataSource")
    @Primary
    public DataSource h2DataSource() {
        ConnectInfo.Jdbc jdbc = connectProperties.get("h2");
        this.deleteH2File(jdbc);
        return DataSourceBuilder
                .create()
                .url(jdbc.getUrl())
                .driverClassName(jdbc.getDriverClassName())
                .username(jdbc.getUserName())
                .password(jdbc.getPassword())
                .type(HikariDataSource.class)
                .build();
    }

    private void deleteH2File(ConnectInfo.Jdbc jdbc) {
        String h2Path = "";
        if (jdbc.getUrl().contains(":file")) {
            h2Path = jdbc.getUrl().split("file:")[1];
            File h2Dir = new File(h2Path).getParentFile();
            if (h2Dir.exists()) {
                for (File file : h2Dir.listFiles()) {
                    log.info("{}: DELETE({})", file.getName(), file.delete());
                }
            } else {
                log.info("{}: CREATE({})", h2Dir.getName(), h2Dir.mkdirs());
            }
        }
    }
}
