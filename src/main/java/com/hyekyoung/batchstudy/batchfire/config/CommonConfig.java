package com.hyekyoung.batchstudy.batchfire.config;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CommonConfig {
    /**
     * 공통 ModelMapper Bean 설정
     * @return ModelMapper Bean
     */
    @Bean(name = "modelMapper")
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * 공통 XmlMapper Bean 설정
     * @return XmlMapper Bean
     */
    @Bean(name = "xmlMapper")
    public XmlMapper xmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
        return xmlMapper;
    }

    /**
     * 공통 ObjectMapper Bean 설정
     * @return ObjectMapper Bean
     */
    @Bean(name = "objectMapper")
    public ObjectMapper objectMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 공통 ThreadPoolTaskExecutor Bean 설정
     * @return ThreadPoolTaskExecutor Bean
     */
    @Bean(name = "processThreadExecutor")
    public ThreadPoolTaskExecutor processThreadExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(15);
        threadPoolTaskExecutor.setQueueCapacity(500);
        threadPoolTaskExecutor.setThreadNamePrefix("processor-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    /**
     * 내부 REST 발송 공통 WebClient Bean 설정
     * @return WebClient.Builder Bean
     */
    @Bean(name = "webClientBuilder")
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
