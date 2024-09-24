package com.hyekyoung.batchstudy.batchfire.connect;

import com.hyekyoung.batchstudy.batchfire.exception.DetailException;
import com.hyekyoung.batchstudy.batchfire.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * =======================================================
 * @class		: RestExecutor
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
public class RestExecutor<T> {
    private String host;
    private WebClient webClient;
    private WebClient.Builder webClientBuilder;

    private MediaType acceptType;
    private MediaType contentType;
    private final int timeout = 600_000;
    private final int fallBackTime = 300;
    private final int maxRetry = 3;

    public RestExecutor(String host, WebClient.Builder webClientBuilder) {
        if (host == null || "".equals(host))
            throw new DetailException(ErrorCode.REST_FAIL);
        this.host = host;
        this.webClientBuilder = webClientBuilder;
        initWebClient();
    }

    public void setHost(String host) {
        if (this.host != null && !this.host.equals(host)) {
            this.host = host;
        }
    }

    public void initWebClient() {
        if (this.host == null || "".equals(this.host)) {
            throw new DetailException(ErrorCode.REST_FAIL, "HOST 미설정");
        }
        if (this.webClientBuilder == null) this.webClientBuilder = WebClient.builder();
        this.webClient = this.webClientBuilder
                .baseUrl(this.host)
                .build();
    }
}
