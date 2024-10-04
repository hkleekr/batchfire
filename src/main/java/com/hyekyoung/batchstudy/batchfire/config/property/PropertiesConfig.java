package com.hyekyoung.batchstudy.batchfire.config.property;

import com.hyekyoung.batchstudy.batchfire.model.property.ConnectInfo;
import com.hyekyoung.batchstudy.batchfire.model.property.MappingInfo;
import lombok.Data;
import org.modelmapper.spi.Mapping;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
public class PropertiesConfig {
    /**
     * ERP(SAP) 연결 Property Bean 설정
     * @return Map<String, ConnectInfo.Jco> Bean
     */
    @Bean(name = "jcoConnectProperties")
    @ConfigurationProperties(prefix = "interface.connect-info.jco")
    public Map<String, ConnectInfo.Jco> jcoIPropertiesInit() {
        return new HashMap<String, ConnectInfo.Jco>();
    }

    /**
     * REST 연결 Property Bean 설정
     * @return Map<String, ConnectInfo.Rest> Bean
     */
    @Bean(name = "restConnectProperties")
    @ConfigurationProperties(prefix = "interface.connect-info.rest")
    public Map<String, ConnectInfo.Rest> restIPropertiesInit() {
        return new HashMap<String, ConnectInfo.Rest>();
    }

    /**
     * JDBC 연결 Property Bean 설정
     * @return Map<String, ConnectInfo.jdbc> Bean
     */
    @Bean(name = "jdbcConnectProperties")
    @ConfigurationProperties(prefix = "interface.connect-info.jdbc")
    public Map<String, ConnectInfo.Jdbc> jdbcIPropertiesInit() {
        return new HashMap<String, ConnectInfo.Jdbc>();
    }

    /**
     * Interface Mapping Property Bean 설정
     * @return Map<String, MappingInfo> Bean
     */
    @Bean(name = "mappingProperties")
    @ConfigurationProperties(prefix = "interface.mapping-info")
    public Map<String, MappingInfo> mappingPropertiesInit() {
        return new HashMap<String, MappingInfo>();
    }
}
