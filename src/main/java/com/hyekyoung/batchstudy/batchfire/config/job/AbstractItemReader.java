package com.hyekyoung.batchstudy.batchfire.config.job;

import com.hyekyoung.batchstudy.batchfire.connect.ConnectManagers;
import com.hyekyoung.batchstudy.batchfire.model.property.MappingInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.util.StopWatch;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
public abstract class AbstractItemReader<T> implements StepExecutionListener, ItemReader<T> {
    /**
     * I/F 매핑 정보
     */
    protected final MappingInfo mappingInfo;


    /**
     * 멀티 커넥션 매니저 Bean 객체
     */
    protected final ConnectManagers connectManagers;


    /**
     * 현재 진행 중인 Batch Job Execution 객체
     */
    protected JobExecution jobExecution;

    /**
     * 현재 진행 중인 Batch Job Step에 대한 Execution 객체
     */
    protected StepExecution stepExecution;

    /**
     * I/F 매핑 정의서에 관리되어 있는 ID
     */
    protected String interfaceId;

    /**
     * 수집 데이터 대상 목록
     */
    private List<T> items;

    /**
     * 수집 데이터 현재 index 위치
     */
    private int itemCursor;

    /**
     * 생성자
     * @param mappingInfo
     * @param connectManagers
     */
    protected AbstractItemReader (
            MappingInfo mappingInfo
            , ConnectManagers connectManagers
    ) {
        this.mappingInfo = mappingInfo;
        this.connectManagers = connectManagers;
        this.interfaceId = mappingInfo.getInterfaceId();
    }

    /**
     * 배치 step이 수행되기 전 handling 처리
     * @param stepExecution instance of {@link StepExecution}
     */
    @Override
    public void beforeStep(@NotNull StepExecution stepExecution) {
        this.jobExecution = stepExecution.getJobExecution();
        this.stepExecution = stepExecution;
    }

    /**
     * 배치 step 수해 완료 시, handling 처리
     * @param stepExecution instance of {@link StepExecution}
     * @return 배치 Job 또는 Step에 대한 종료 상태
     */
    @Override
    public ExitStatus afterStep(@NotNull StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }

    @Override
    public T read() {
        try {
            if (isNotInitialized()) {
                StopWatch sp = new StopWatch();
                sp.start();
                items = this.getItems();
                sp.stop();
                stepExecution.getExecutionContext().putLong(JobConstant.STEP_EXECUTION_READ_TIME, sp.getTotalTimeMillis());
            }
        } catch (Exception e) {
            stepExecution.setExitStatus(ExitStatus.FAILED);
            stepExecution.addFailureException(e);
            throw e;
        }
        T item = null;

        if (items != null && itemCursor < items.size()) {
            item = items.get(itemCursor);
            itemCursor++;
        } else {
            itemCursor = 0;
            item = null;
        }
        return item;
    }

    protected void setStepExecutionParam(int chunkSize, int totalCount) {
        stepExecution.getExecutionContext().putInt(JobConstant.STEP_EXECUTION_PARAMS_CHUNK_SIZE, chunkSize);
        stepExecution.getExecutionContext().putInt(JobConstant.STEP_EXECUTION_PARAMS_TOTAL_COUNT, totalCount);
        int totalPage = 0;
        if (totalCount > 0 && totalCount % chunkSize == 0) {
            totalPage = totalCount / chunkSize;
        } else if (totalCount > 0) {
            totalPage = totalCount / chunkSize + 1;
        }
        stepExecution.getExecutionContext().putInt(JobConstant.STEP_EXECUTION_PARAMS_TOTAL_PAGE, totalPage);
        log.info("[BATCH_START_INFO] {} / TOTAL_SIZE: {} / TOTAL_PAGE: {}", interfaceId, totalCount, totalPage);
    }

    /**
     * 수집 대상의 초기화 되어있지 않은 상태 조회
     * @return 초기화 되어있지 않은 상태
     */
    private boolean isNotInitialized() {
        return this.items == null;
    }

    /**
     * 수집 대상 아이템 목록 조회
     * @return 수집 대상 아이템 목록
     */
    public abstract List<T> getItems();

    /**
     * 에러 로깅 재귀 호출
     * @param e
     */
    private void errorLogging(Throwable e) {
        log.error(e.getMessage());

        if (e.getMessage() != null) {
            errorLogging(e.getCause());
        }
    }
}
