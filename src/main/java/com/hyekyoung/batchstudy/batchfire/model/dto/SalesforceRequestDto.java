package com.hyekyoung.batchstudy.batchfire.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * =======================================================
 * @class		: SalesforceRequestDto
 * @author		: 이혜경
 * @date		: 2024-09-13
 * @desc		: Salesforce Request Dto (수신 측과 협의 필요)
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
public class SalesforceRequestDto<T> {
    private String interfaceId;
    List<T> data;
    private long totalCount;
    private long currentPage;
    private long totalPage;
    private String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    JobParameters jobParameters;
}
