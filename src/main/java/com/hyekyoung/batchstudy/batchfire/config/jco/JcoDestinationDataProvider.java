package com.hyekyoung.batchstudy.batchfire.config.jco;

import com.hyekyoung.batchstudy.batchfire.exception.DetailException;
import com.hyekyoung.batchstudy.batchfire.exception.ErrorCode;
import com.hyekyoung.batchstudy.batchfire.model.property.ConnectInfo;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class JcoDestinationDataProvider implements DestinationDataProvider {
    private DestinationDataEventListener eL;
    private HashMap<String, Properties> secureDBStorage = new HashMap<String, Properties>();

    /**
     * application.yml 설정 정보 로드 및 Jco init
     * @param jcoConnectProperties
     */
    public JcoDestinationDataProvider(Map<String, ConnectInfo.Jco> jcoConnectProperties) {
        Environment.registerDestinationDataProvider(this);
        jcoConnectProperties.keySet().stream().forEach(connectInfo -> {
            ConnectInfo.Jco jco = jcoConnectProperties.get(connectInfo);
            this.changeProperties(jco.getSystemId(), this.createJcoProperty(jco));
            if(jco.isUseAble()) log.debug(this.checkDestination(jco.getSystemId()));
            else log.debug("\nJco Destination Connection Status {\n" +
                    "                     NAME = [" + jco.getSystemId() + "],\n" +
                    "                  UseAble = [" + jco.isUseAble() + "]\n");
        });
    }

    @Override
    public Properties getDestinationProperties(String destinationName) {
        if(secureDBStorage.containsKey(destinationName)) {
            return secureDBStorage.get(destinationName);
        }
        return null;
    }

    @Override
    public boolean supportsEvents() {
        return true;
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
        this.eL = eventListener;
    }

    /**
     * JCO 설정 정보
     * @param destinationName
     * @param properties
     */
    public void changeProperties(String destinationName, Properties properties) {
        synchronized (secureDBStorage) {
            if (properties == null) {
                if (secureDBStorage.remove(destinationName) != null) eL.deleted(destinationName);
            } else {
                secureDBStorage.put(destinationName, properties);
                eL.updated(destinationName);   // create or updated
            }
        }
    }

    /**
     * SAP JCO POOL Property 생성
     *
     * @param jco
     * @return
     */
    private Properties createJcoProperty(ConnectInfo.Jco jco) {
        Properties connectProperties = new Properties();
        if(jco.isUseAble()) {
            connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, jco.getClient());
            connectProperties.setProperty(DestinationDataProvider.JCO_USER, jco.getUser());
            connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, jco.getPassword());
            connectProperties.setProperty(DestinationDataProvider.JCO_LANG, jco.getLanguage());
            connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, jco.getSystemNumber());
            connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, jco.getPoolCapacity());
            connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, jco.getPoolPeakLimit());
            if (jco.isHostType()) {
                connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, jco.getHost());
            } else {
                // 운영 사용
                connectProperties.setProperty(DestinationDataProvider.JCO_MSHOST, jco.getHost());
                connectProperties.setProperty(DestinationDataProvider.JCO_GROUP, jco.getGroup());
            }
        }
        log.debug(jco.toString());
        return connectProperties;
    }

    /**
     * Destination 설정 및 연결 확인 로그 생성
     * @param destinationName
     * @return
     */
    private String checkDestination(String destinationName) {
        try {
            JCoDestination jCoDestination = JCoDestinationManager.getDestination(destinationName);
            StringBuffer sbuf = new StringBuffer("\n");
            sbuf.append("Jco Destination Connection Status {" + "\n");
            sbuf.append("                     Name = [").append(destinationName).append("], " + "\n");
            sbuf.append("                   TPHost = [").append(jCoDestination.getTPHost()).append("], " + "\n");
            sbuf.append("              GatewayHost = [").append(jCoDestination.getGatewayHost()).append("], " + "\n");
            sbuf.append("        MessageServerHost = [").append(jCoDestination.getMessageServerHost()).append("], " + "\n");
            sbuf.append("    ApplicationServerHost = [").append(jCoDestination.getApplicationServerHost()).append("], " + "\n");
            sbuf.append("            destinationID = [").append(jCoDestination.getDestinationID()).append("], " + "\n");
            sbuf.append("                 maxConns = [").append(jCoDestination.getPoolCapacity()).append("], " + "\n");
            sbuf.append("       timeoutCheckPeriod = [").append(jCoDestination.getExpirationTime()).append("], " + "\n");
            sbuf.append("}");
            return sbuf.toString();
        } catch (JCoException e) {
            log.error(e.toString(), e);
            throw new DetailException(ErrorCode.JCO_FAIL, e);
        }
    }
}
