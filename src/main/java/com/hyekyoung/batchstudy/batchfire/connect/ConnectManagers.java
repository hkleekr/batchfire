package com.hyekyoung.batchstudy.batchfire.connect;

import lombok.Getter;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * =======================================================
 * @class		: ConnectManagers
 * @author		: 이혜경
 * @date		: 2024-09-13
 * @desc		: ConnectManager 외부 상속 관련 Wrapping Class
 * @history		:
 * -------------------------------------------------------
 * NO	DATE       	AUTHOR	NOTE
 * -------------------------------------------------------
 * 1.	2024-09-13 이혜경
 */
@Getter
@Component
@DependsOn({
        "jcoConnectManager"
        , "restConnectManager"
})
public class ConnectManagers {
    private final JcoConnectManager jcoConnectManager;
    private final RestConnectManager restConnectManager;

    public ConnectManagers(
            JcoConnectManager jcoConnectManager
            , RestConnectManager restConnectManager) {
        this.jcoConnectManager = jcoConnectManager;
        this.restConnectManager = restConnectManager;
    }
}
