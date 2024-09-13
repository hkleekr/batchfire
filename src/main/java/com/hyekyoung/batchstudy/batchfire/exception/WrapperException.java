package com.hyekyoung.batchstudy.batchfire.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * =======================================================
 * @class		: WrapperException
 * @author		: 이혜경
 * @date		: 2024-09-13
 * @desc		: Job 실행 시 발생하는 최하단 DetailException을 묶는 상위 Exception
 * @history		:
 * -------------------------------------------------------
 * NO	DATE       	AUTHOR	NOTE
 * -------------------------------------------------------
 * 1.	2024-09-13 이혜경
 */
@Getter
public class WrapperException extends RuntimeException {
    private String stepName;
    private String stepInterfaceId;
    private String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    public WrapperException(String stepName, String stepInterfaceId) {
        this.stepName = stepName;
        this.stepInterfaceId = stepInterfaceId;
    }

    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("Exception: %s(%s)", this.stepName, this.stepInterfaceId));
        for (Throwable throwable : getSuppressed()) {
            DetailException detailException = null;
            if (throwable instanceof DetailException) detailException = (DetailException) throwable;
            else detailException = new DetailException(throwable.getMessage(), throwable);
            sb.append("\nCause: " + detailException.getCode() + " =>" + detailException.getMessage());
        }
        return sb.toString();
    }
}
