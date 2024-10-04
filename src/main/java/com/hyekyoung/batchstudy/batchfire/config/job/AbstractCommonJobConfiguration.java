package com.hyekyoung.batchstudy.batchfire.config.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hyekyoung.batchstudy.batchfire.connect.ConnectManagers;
import com.hyekyoung.batchstudy.batchfire.model.property.MappingInfo;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

import java.util.Map;

public abstract class AbstractCommonJobConfiguration<T> {
    /**
     * 생성자
     * @param jpbRepository
     * @param jobBuilderFactory
     * @param stepBuilderFactory
     * @param mappingProperties
     * @param objectMapper
     * @param xmlMapper
     * @param modelMapper
     * @param connectManagers
     */
    protected AbstractCommonJobConfiguration(
            JobRepository jpbRepository
            , JobBuilderFactory jobBuilderFactory
            , StepBuilderFactory stepBuilderFactory
            , Map<String, MappingInfo> mappingProperties
            , ObjectMapper objectMapper
            , XmlMapper xmlMapper
            , ModelMapper modelMapper
            , ConnectManagers connectManagers
    ){
        this.jpbRepository = jpbRepository;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.mappingProperties = mappingProperties;
        this.objectMapper = objectMapper;
        this.xmlMapper = xmlMapper;
        this.modelMapper = modelMapper;
        this.connectManagers = connectManagers;
    }

    protected final JobRepository jpbRepository;
    protected final JobBuilderFactory jobBuilderFactory;
    protected final StepBuilderFactory stepBuilderFactory;
    protected final Map<String, MappingInfo> mappingProperties;
    protected final ObjectMapper objectMapper;
    protected final XmlMapper xmlMapper;
    protected final ModelMapper modelMapper;
    protected final ConnectManagers connectManagers;
    public final String jobSuffix = "_JOB";
    public final String stepSuffix = "_STEP";
    public final String readerSuffix = "_READER";
    public final String writerSuffix = "_WRITER";

    public abstract Job createJob();
    public abstract Step createStep();
    public abstract ItemReader<T> createReader();
    public abstract ItemWriter<T> createWriter();
}
