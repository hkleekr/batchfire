package com.hyekyoung.batchstudy.batchfire.connect;

import com.hyekyoung.batchstudy.batchfire.config.jco.JcoDestinationDataProvider;
import com.hyekyoung.batchstudy.batchfire.exception.DetailException;
import com.hyekyoung.batchstudy.batchfire.exception.ErrorCode;
import com.hyekyoung.batchstudy.batchfire.model.property.ConnectInfo;
import com.sap.conn.jco.ext.Environment;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Slf4j
@Getter
@Service
public class JcoConnectManager implements ConnectManagerInterface<JcoExecutor> {
    private final Map<String, ConnectInfo.Jco> jcoConnectProperties;

    public JcoConnectManager(Map<String, ConnectInfo.Jco> jcoConnectProperties) {
        this.jcoConnectProperties = jcoConnectProperties;
    }

    /**
     * 프로퍼티 기준 Jco Destination 정보 애플리케이션 초기화
     */
    @PostConstruct
    public void init() {
        if (!Environment.isDestinationDataProviderRegistered()) {
            new JcoDestinationDataProvider(jcoConnectProperties);
        }
    }

    /**
     * 프로퍼티 기준 커넥션 생성 및 커넥션 관리/실행자 객체 반환
     * @param systemName MappingInfo 기준 SystemName
     * @return JcoExecutor 커넥션 및 실행자 객체
     * @throws Exception
     */
    @Override
    public JcoExecutor connect(@NotNull @NotEmpty String systemName) {
        if(!Environment.isDestinationDataProviderRegistered()) {
            new JcoDestinationDataProvider(jcoConnectProperties);
        }
        ConnectInfo.Jco jco = jcoConnectProperties.get(systemName);
        if(jco == null) {
            throw new DetailException(ErrorCode.JCO_FAIL);
        }
        return new JcoExecutor(jco.getSystemId());
    }

    /**
     * Eai 커넥션 반환
     * @param jcoExecutor 커넥션 및 실행자 객체
     * @throws Exception
     */
    @Override
    public void closeConnect(JcoExecutor jcoExecutor) {
        jcoExecutor.close();
        jcoExecutor = null;
    }
}
