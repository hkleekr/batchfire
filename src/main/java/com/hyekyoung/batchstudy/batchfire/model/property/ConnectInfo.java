package com.hyekyoung.batchstudy.batchfire.model.property;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * =======================================================
 * @class		: ConnectInfo
 * @author		: 이혜경
 * @date		: 2024-09-12
 * @desc		: application.yml - interface.connect-info 프로퍼티 class
 * @history		:
 * -------------------------------------------------------
 * NO	DATE       	AUTHOR	NOTE
 * -------------------------------------------------------
 * 1.	2024-09-12 이혜경
 */
@Data
@NoArgsConstructor
public class ConnectInfo {
    @Data
    @ToString
    @NoArgsConstructor
    public static class Jco {
        private boolean useAble;
        private String destination;
        private String client;
        private String user;
        private String password;
        private String language;
        private String poolCapacity;
        private String poolPeakLimit;
        private String host;
        private String systemId;
        private boolean hostType;
        private String systemNumber;
        private String group;
        private String r3name;
    }

    @Data
    @ToString
    @NoArgsConstructor
    public static class Rest {
        private String name;
        private String host;
    }
}
