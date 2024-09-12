package com.hyekyoung.batchstudy.batchfire.exception;

import lombok.Getter;

@Getter
public class DetailException extends RuntimeException {
    private String code;
    private String message;

    public DetailException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public DetailException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();

    }
}
