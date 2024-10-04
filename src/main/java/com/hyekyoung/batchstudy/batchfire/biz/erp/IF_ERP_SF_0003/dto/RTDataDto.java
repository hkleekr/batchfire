package com.hyekyoung.batchstudy.batchfire.biz.erp.IF_ERP_SF_0003.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonRootName("RT_DATA")
public class RTDataDto {
    @JsonProperty("name")
    @JsonAlias("VTEXT")
    private String name;
}

