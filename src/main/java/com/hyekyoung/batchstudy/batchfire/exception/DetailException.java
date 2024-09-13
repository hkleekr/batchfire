package com.hyekyoung.batchstudy.batchfire.exception;

import lombok.Getter;

@Getter
public class DetailException extends RuntimeException {
    private String code;
    private String message;

    /**
     *
     * @param errorCode
     */
    public DetailException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * Cause Exception write  생성
     * @param errorCode
     * @param cause
     */
    public DetailException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 확인 되지 않은 Exception(ErrorCode 정의 x), Message write / Cause Exception write 생성
     * @param causeMessage
     * @param cause
     */
    public DetailException(String causeMessage, Throwable cause) {
        super(cause);
        this.code = "NON_CHECKED";
        this.message = causeMessage;
    }
}
