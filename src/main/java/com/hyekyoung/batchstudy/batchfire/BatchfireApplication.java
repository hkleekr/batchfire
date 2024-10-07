package com.hyekyoung.batchstudy.batchfire;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class BatchfireApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchfireApplication.class, args);
    }

}
