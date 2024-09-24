package com.hyekyoung.batchstudy.batchfire.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * =======================================================
 * @class		: StepInfo
 * @author		: 이혜경
 * @date		: 2024-09-13
 * @desc		: Step 실행 결과 정보
 * @history		:
 * -------------------------------------------------------
 * NO	DATE       	AUTHOR	NOTE
 * -------------------------------------------------------
 * 1.	2024-09-13 이혜경
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class StepInfo {
    @JsonProperty("STEP_IDX")
    private String stepIdx;
    @JsonProperty("STEP_NAME")
    private String stepName;
    @JsonProperty("STEP_INTERFACE_ID")
    private String interfaceId;
    @JsonProperty("STEP_STATUS")
    private String stepStatus;
    @JsonProperty("STEP_DES")
    private String stepExitDescription;
    @JsonProperty("STEP_TOTAL_COUNT")
    private int stepTotalCount;
    @JsonProperty("STEP_TOTAL_PAGE")
    private int stepTotalPage;
    @JsonProperty("STEP_START_TIME")
    private String startTimeStr;
    @JsonProperty("STEP_END_TIME")
    private String endTimeStr;
    @JsonProperty("STEP_DURATION_TIME")
    private String durationTimeStr;
    @JsonProperty("STEP_START_TIME_RAW")
    private Long startTimeRaw;
    @JsonProperty("STEP_END_TIME_RAW")
    private Long endTimeRaw;
    @JsonProperty("STEP_DURATION_TIME_RAW")
    private Long durationTimeRaw;

    @JsonProperty("STEP_READ_TIME")
    private Long readTime;
    @JsonProperty("STEP_WRITE_TIME")
    private Long writeTime;
}
