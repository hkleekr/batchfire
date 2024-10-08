package com.hyekyoung.batchstudy.batchfire.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    JOB_FAIL("JOB000", "[JOB_ERROR] JOB 실패"),
    JCO_FAIL("JCO000", "[JCO_ERROR] JCO 실패"),
    REST_FAIL("REST000", "[REST_ERROR] REST 실패"),
    SCHEDULER_FAIL("SCHEDULER000", "[SCHEDULER_ERROR] SCHEDULER 실패"),
    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
