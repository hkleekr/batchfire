package com.hyekyoung.batchstudy.batchfire.biz.erp.IF_ERP_SF_0002.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hyekyoung.batchstudy.batchfire.biz.erp.IF_ERP_SF_0002.dto.RTDataDto;
import com.hyekyoung.batchstudy.batchfire.config.job.AbstractItemReader;
import com.hyekyoung.batchstudy.batchfire.connect.ConnectManagers;
import com.hyekyoung.batchstudy.batchfire.model.property.MappingInfo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class IF_ERP_SF_0002JobReader extends AbstractItemReader<RTDataDto> {


    /**
     * Jackson Object 매퍼 Bean 객체
     */
    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper;
    private final ModelMapper modelMapper;

    public IF_ERP_SF_0002JobReader(
            MappingInfo mappingInfo
            , ConnectManagers connectManagers
            , ObjectMapper objectMapper
            , XmlMapper xmlMapper
            , ModelMapper modelMapper)
    {
        super(mappingInfo, connectManagers);
        this.objectMapper = objectMapper;
        this.xmlMapper = xmlMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<RTDataDto> getItems() {
        List<RTDataDto> results = new ArrayList<>();
        results.add(new RTDataDto());

        this.setStepExecutionParam(mappingInfo.getChunkSize(), ObjectUtils.isEmpty(results) ? 0 : results.size());
        return results;
    }
}

