package com.hyekyoung.batchstudy.batchfire.model.property;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * =======================================================
 * @class		: MappingInfo
 * @author		: 이혜경
 * @date		: 2024-09-13
 * @desc		: application.yml - interface.mapping-info 프로퍼티 class
 * @history		:
 * -------------------------------------------------------
 * NO	DATE       	AUTHOR	NOTE
 * -------------------------------------------------------
 * 1.	2024-09-13 이혜경
 */
@Data
@Slf4j
@NoArgsConstructor
public class MappingInfo {
    @NonNull
    private String interfaceId;
    private Integer chunkSize;
    private String sourceSystem;
    private String sourceEndpoint;
    private Map<String, String> sourceParams;
    private String targetSystem;
    private String targetEndpoint;

    public static class SourceSystem {
        private String sourceSystem;
        private String sourceEndpoint;
        private Map<String, String> SourceParams;   // 용성대리님 프로젝트에는 대문자로 시작함
    }
}
