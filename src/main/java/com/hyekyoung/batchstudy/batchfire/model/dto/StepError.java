package com.hyekyoung.batchstudy.batchfire.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * =======================================================
 * @class		: StepError
 * @author		: 이혜경
 * @date		: 2024-09-13
 * @desc		: Step 내부에서 발생한 에러 관련 정보
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
public class StepError {
    private String stepName;
    private String stepInterfaceId;
    private String errorMessage;
}
