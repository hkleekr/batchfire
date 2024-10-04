package com.hyekyoung.batchstudy.batchfire.biz.erp.IF_ERP_SF_0001.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hyekyoung.batchstudy.batchfire.biz.erp.IF_ERP_SF_0001.dto.RTDataDto;
import com.hyekyoung.batchstudy.batchfire.config.job.AbstractItemWriter;
import com.hyekyoung.batchstudy.batchfire.config.job.JobConstant;
import com.hyekyoung.batchstudy.batchfire.connect.ConnectManagers;
import com.hyekyoung.batchstudy.batchfire.model.dto.SalesforceRequestDto;
import com.hyekyoung.batchstudy.batchfire.model.property.MappingInfo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;

import java.util.List;

@Slf4j
public class IF_ERP_SF_0001JobWriter extends AbstractItemWriter<RTDataDto> {
    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper;
    private final ModelMapper modelMapper;

    public IF_ERP_SF_0001JobWriter(
            MappingInfo mappingInfo
            , ConnectManagers connectManagers
            , ObjectMapper objectMapper
            , XmlMapper xmlMapper
            , ModelMapper modelMapper) {
        super(mappingInfo, connectManagers);
        this.objectMapper = objectMapper;
        this.xmlMapper = xmlMapper;
        this.modelMapper = modelMapper;
    }

    @BeforeStep
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.jobExecution = stepExecution.getJobExecution();
        this.stepExecution = stepExecution;
    }

    @Override
    public void sendItems(List<? extends RTDataDto> items) {
        SalesforceRequestDto<RTDataDto> requestBody = new SalesforceRequestDto<>();

        requestBody.setCurrentPage(stepExecution.getCommitCount() + 1);
        requestBody.setInterfaceId(stepExecution.getExecutionContext().getString(JobConstant.STEP_EXECUTION_PARAMS_INTERFACE_ID));
        requestBody.setTotalCount(stepExecution.getExecutionContext().getInt(JobConstant.STEP_EXECUTION_PARAMS_TOTAL_COUNT));
        requestBody.setTotalPage(stepExecution.getExecutionContext().getInt(JobConstant.STEP_EXECUTION_PARAMS_TOTAL_PAGE));
        requestBody.setJobParameters(jobExecution.getJobParameters());
        requestBody.setData((List<RTDataDto>) items);
    }
}
