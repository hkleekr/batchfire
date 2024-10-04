package com.hyekyoung.batchstudy.batchfire.biz.erp.IF_ERP_SF_0003.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hyekyoung.batchstudy.batchfire.biz.erp.IF_ERP_SF_0003.dto.RTDataDto;
import com.hyekyoung.batchstudy.batchfire.config.job.AbstractCommonJobConfiguration;
import com.hyekyoung.batchstudy.batchfire.config.job.CommonJobErrorListener;
import com.hyekyoung.batchstudy.batchfire.config.job.CommonJobListener;
import com.hyekyoung.batchstudy.batchfire.config.job.CommonStepListener;
import com.hyekyoung.batchstudy.batchfire.connect.ConnectManagers;
import com.hyekyoung.batchstudy.batchfire.model.property.MappingInfo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
public class IF_ERP_SF_0003JobConfiguration extends AbstractCommonJobConfiguration<RTDataDto> {
    protected IF_ERP_SF_0003JobConfiguration(
            JobRepository jobRepository
            , JobBuilderFactory jobBuilderFactory
            , StepBuilderFactory stepBuilderFactory
            , Map<String, MappingInfo> mappingProperties
            , ObjectMapper objectMapper
            , XmlMapper xmlMapper
            , ModelMapper modelMapper
            , ConnectManagers connectManagers)
    {
        super(jobRepository, jobBuilderFactory, stepBuilderFactory, mappingProperties, objectMapper, xmlMapper, modelMapper, connectManagers);
    }

    private final String jobName = "IF_ERP_SF_0003";
    private final String interfaceId = "IF_ERP_SF_0003";
    private int chunkSize;

    @Bean(jobName + jobSuffix)
    @Override
    public Job createJob() {
        this.chunkSize = mappingProperties.get(interfaceId).getChunkSize();
        return jobBuilderFactory.get(jobName + jobSuffix)
                .incrementer(new RunIdIncrementer())
                .start(this.createStep())
                .listener(new CommonJobErrorListener(interfaceId, connectManagers))
                .listener(new CommonJobListener(jobRepository))
                .build();
    }

    @Bean(jobName + stepSuffix)
    @Override
    public Step createStep() {
        return stepBuilderFactory.get(jobName + stepSuffix)
                .<RTDataDto, RTDataDto>chunk(chunkSize)
                .reader(this.createReader())
                .writer(this.createWriter())
                .listener(new CommonStepListener(interfaceId, chunkSize))
                .build();
    }

    /**
     * mappingProperties.get(interfaceId): Job Properties
     * connectManagers: Interface Connector
     * objectMapper, xmlMapper, modelMapper: Data Convertor
     * @return IF_ERP_SF_0003JobReader
     */
    @Bean(jobName + readerSuffix)
    @Override
    public ItemReader<RTDataDto> createReader() {
        return new IF_ERP_SF_0003JobReader(mappingProperties.get(interfaceId), connectManagers, objectMapper, xmlMapper, modelMapper);
    }

    /**
     * mappingProperties.get(interfaceId): Job Properties
     * connectManagers: Interface Connector
     * objectMapper, xmlMapper, modelMapper: Data Convertor
     * @return IF_ERP_SF_0003JobWriter
     */
    @Bean(jobName + writerSuffix)
    @Override
    public ItemWriter<RTDataDto> createWriter() {
        return new IF_ERP_SF_0003JobWriter(mappingProperties.get(interfaceId), connectManagers, objectMapper, xmlMapper, modelMapper);
    }
}
