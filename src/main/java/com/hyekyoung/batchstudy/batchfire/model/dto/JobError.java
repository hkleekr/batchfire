package com.hyekyoung.batchstudy.batchfire.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * =======================================================
 * @class		: JobError
 * @author		: 이혜경
 * @date		: 2024-09-13
 * @desc		: Job 내부에서 발생한 에러 관련 정보
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
public class JobError {
    private String traceId;
    private String interfaceId;
    private String startTime;
    private String endTime;
    private String jobName;
    private List<StepError> stepErrors;
}
