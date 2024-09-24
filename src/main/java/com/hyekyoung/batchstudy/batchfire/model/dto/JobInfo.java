package com.hyekyoung.batchstudy.batchfire.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * =======================================================
 * @class		: JobInfo
 * @author		: 이혜경
 * @date		: 2024-09-13
 * @desc		: Job 실행 결과 정보
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StepInfo.class)
})
public class JobInfo {
    @JsonProperty("JOB_NAME")
    private String jobName;
    @JsonProperty("JOB_TRACE_ID")
    private String traceId;
    @JsonProperty("JOB_STATUS")
    private String jobStatus;
    @JsonProperty("JOB_START_TIME")
    private String startTimeStr;
    @JsonProperty("JOB_END_TIME")
    private String endTimeStr;
    @JsonProperty("JOB_DURATION_TIME")
    private String durationTimeStr;
    @JsonProperty("JOB_START_TIME_RAW")
    private String startTimeRaw;
    @JsonProperty("JOB_END_TIME_RAW")
    private String endTimeRaw;
    @JsonProperty("JOB_DURATION_TIME_RAW")
    private String durationTimeRaw;
    @JsonProperty("STEPS")
    private List<StepInfo> stepInfos;
    @JsonProperty("STEP_COUNT")
    private int stepCount;
}
