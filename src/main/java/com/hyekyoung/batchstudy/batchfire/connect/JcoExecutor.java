package com.hyekyoung.batchstudy.batchfire.connect;

import com.hyekyoung.batchstudy.batchfire.exception.DetailException;
import com.hyekyoung.batchstudy.batchfire.exception.ErrorCode;
import com.hyekyoung.batchstudy.batchfire.util.JcoUtil;
import com.sap.conn.jco.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JcoExecutor {
    private JCoDestination jCoDestination;
    private JCoRepository jCoRepository;
    private JCoFunction jCoFunction;
    private boolean isExecuted = false;

    public JcoExecutor() {
    }

    /**
     * Destination Name 기반 Jco Destination 설정
     * Jco 세션 생성(Deprecated) - 단일 Function 사용 구성 -> JCO 자동 관리
     *
     * @param destinationName
     * @throws
     */
    public JcoExecutor(String destinationName) {
        try {
            this.jCoDestination = JCoDestinationManager.getDestination(destinationName);
//            JCoContext.begin(this.jCoDestination);
        } catch (JCoException e) {
            throw new DetailException(ErrorCode.JCO_FAIL, e);
        }

        try {
            this.jCoRepository = this.jCoDestination.getRepository();
        } catch (JCoException e) {
            throw new DetailException(ErrorCode.JCO_FAIL, e);
        }
    }

    /**
     * JCO 세션에 RFC 설정
     *
     * @param functionName
     * @throws
     */
    public void setFunction(String functionName) {
        try {
            this.jCoFunction = jCoRepository.getFunction(functionName);
        } catch (JCoException e) {
            throw new DetailException(ErrorCode.JCO_FAIL, e);
        }
    }

    /**
     * RFC 함수의 입력 매개변수 목록을 반환
     *
     * @return RFC 함수의 입력 매개변수 목록 객체
     * @throws
     */
    public JCoParameterList getImportParams() {
        if (jCoFunction == null)
            throw new DetailException(ErrorCode.JCO_FAIL);
        return jCoFunction.getImportParameterList();
    }

    /**
     * RFC 함수의 입출력 테이블 목록에서 해당 이름을 가진 테이블을 반환
     *
     * @param tableName 입출력 테이블 목록 중 하나의 테이블 이름
     * @return RFC 함수의 입출력 테이블 목록에서 해당 이름을 가진 테이블 객체
     * @throws
     */
    public JCoTable getTable(String tableName) {
        if (jCoFunction == null)
            throw new DetailException(ErrorCode.JCO_FAIL);
        return jCoFunction.getTableParameterList().getTable(tableName);
    }

    /**
     * RFC 함수의 입출력 테이블 목록을 반환
     *
     * @return RFC 함수의 입출력 테이블 목록
     * @throws
     */
    private JCoParameterList getTable() {
        if (jCoFunction == null)
            throw new DetailException(ErrorCode.JCO_FAIL);
        return jCoFunction.getTableParameterList();
    }

    private String createLog() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        sb.append("================ DESTINATION =================").append("\n");
        sb.append("NAME: ").append(jCoRepository.getName()).append("\n");
        sb.append("DESTINATION_NAME: ").append(jCoDestination.getDestinationName()).append("\n");
        sb.append("CLIENT: ").append(jCoDestination.getClient()).append("\n");
        sb.append("=================== RESULT ===================").append("\n");
        sb.append("FUNCTION_NM: ").append(jCoFunction.getName().toUpperCase()).append("\n");
        sb.append("================ INPUT PARAM =================").append("\n");
        sb.append(JcoUtil.mapString(JcoUtil.resultMap(jCoFunction.getImportParameterList()))).append("\n");
        if (log.isDebugEnabled()) {
            sb.append("================ TABLE PARAM =================").append("\n");
            sb.append(JcoUtil.mapString(JcoUtil.resultMap(jCoFunction.getTableParameterList()))).append("\n");
        }
        sb.append("==============================================");
        return sb.toString();
    }

    /**
     * RFC 함수 실행
     */
    public void execute() {
        try {
            log.info(createLog());
            jCoFunction.execute(jCoDestination);
            this.isExecuted = true;
        } catch (JCoException e) {
            if (e.getMessage().contains("Data not found!")) {
                log.info("[{} ({})] {}", e.getKey(), e.getGroup(), e.getMessage());
                return;
            }
            throw new DetailException(ErrorCode.JCO_FAIL, e);
        }
    }

    /**
     * RFC 함수 수행 결과 출력 반환 데이터 목록을 반환
     * RFC 함수가 아직 수행되지 않은 경우 null을 반환
     * I/F 명세에 따라 개발자들이 파싱하여 사용
     *
     * @return 출력 반환 데이터 목록
     */
    public JCoParameterList getExportParams() {
        if (!isExecuted) return null;
        return jCoFunction.getExportParameterList();
    }

    /**
     * @return 함수 이름
     */
    public String getFunctionName() {
        return this.jCoFunction.getName();
    }

    /**
     * @return 함수
     */
    public JCoFunction getJCoFunction() {
        return this.jCoFunction;
    }

    /**
     * @return Jco Repository
     */
    public JCoRepository getjCoRepository() {
        return this.jCoRepository;
//        TODO: 차후 Repository Clear 기능 구현 필요 -> SAP 서버 변경에 따른 메타 데이터 재구성
//                                                    Clear는 성능에 영향 있음
    }

    /**
     * @return 함수의 실행 완료 여부
     */
    public boolean isExecuted() {
        return this.isExecuted;
    }

    /**
     * Jco Destination 모니터링 데이터 클리어
     * Jco 세션 반환(Deprecated) - 단일 Function 사용 구성 -> JCO 자동 관리
     */
    public void close() {
//        try {
            this.jCoDestination.removeThroughput();
//            JCoContext.end(this.jCoDestination);
//        } catch (JCoException e) {
//            throw new DetailException(ErrorCode.JCO_FAIL, e);
//        }
    }
}
