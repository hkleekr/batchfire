package com.hyekyoung.batchstudy.batchfire.connect;

import com.hyekyoung.batchstudy.batchfire.exception.DetailException;
import com.hyekyoung.batchstudy.batchfire.exception.ErrorCode;
import com.hyekyoung.batchstudy.batchfire.model.property.ConnectInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * =======================================================
 * @class		: RestConnectManager
 * @author		: 이혜경
 * @date		: 2024-09-13
 * @desc		:
 * @history		:
 * -------------------------------------------------------
 * NO	DATE       	AUTHOR	NOTE
 * -------------------------------------------------------
 * 1.	2024-09-13 이혜경
 */
@Slf4j
@Getter
@Service
public class RestConnectManager<T> implements ConnectManagerInterface<RestExecutor<T>> {
    private final Map<String, ConnectInfo.Rest> restConnectProperties;
    private final WebClient.Builder webClientBuilder;

    public RestConnectManager(
            Map<String, ConnectInfo.Rest> restConnectProperties
            , WebClient.Builder webClientBuilder) {
        this.restConnectProperties = restConnectProperties;
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * 프로퍼티 기준 커넥션 생성 및 커넥션 관리/실행자 객체 반환
     * @param systemName MappingInfo 기준 System Name
     * @return RestExecutor 커넥션 및 실행자 객체
     * @throws Exception
     */
    @Override
    public RestExecutor<T> connect(@NotNull @NotEmpty String systemName) {
        ConnectInfo.Rest rest = restConnectProperties.get(systemName);
        if (rest == null) {
            throw new DetailException(ErrorCode.JOB_FAIL);
        }
        RestExecutor restExecutor = new RestExecutor<T>(rest.getHost(), this.webClientBuilder);
        return restExecutor;
    }

    /**
     * 커넥션 반환
     * @param restExecutor 커넥션 및 실행자 객체
     * @throws Exception
     */
    @Override
    public void closeConnect(RestExecutor<T> restExecutor) {
        restExecutor = null;
    }
}
