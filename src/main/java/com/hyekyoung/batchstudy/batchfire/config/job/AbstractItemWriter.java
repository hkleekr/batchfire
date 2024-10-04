package com.hyekyoung.batchstudy.batchfire.config.job;

import com.hyekyoung.batchstudy.batchfire.connect.ConnectManagers;
import com.hyekyoung.batchstudy.batchfire.model.property.MappingInfo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.spi.Mapping;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.util.StopWatch;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
public abstract class AbstractItemWriter<T> implements StepExecutionListener, ItemWriter<T> {
    /**
     * I/F 매핑 정보
     */
    protected final MappingInfo mappingInfo;

    /**
     * 멀티 커넥션 매니저 Bean 객체
     */
    protected final ConnectManagers connectManagers;

    /**
     * 현재 진행 중인 Batch Job Execution
     */
    protected JobExecution jobExecution;

    /**
     * 현재 진행 중인 Batch Job Step에 대한 Execution
     */
    protected StepExecution stepExecution;

    /**
     * I/F 매핑 정의서에 관리되어 있는 ID
     */
    protected String interfaceId;

    public AbstractItemWriter(
            MappingInfo mappingInfo
            , ConnectManagers connectManagers
            )
    {
        this.mappingInfo = mappingInfo;
        this.connectManagers = connectManagers;
        this.interfaceId = mappingInfo.getInterfaceId();
    }

    /**
     * 배치 Step이 수행되기 전, Handling 처리
     * @param stepExecution instance of {@link StepExecution}
     */
    @Override
    public void beforeStep(@NotNull StepExecution stepExecution) {
        this.jobExecution = stepExecution.getJobExecution();
        this.stepExecution = stepExecution;
    }

    /**
     * 배치 Step이 수행 완료되었을 때, Handling 처리
     * @param stepExecution instance of {@link StepExecution}
     * @return 배치 Job 또는 Step에 대한 종료 상태
     */
    @Override
    public ExitStatus afterStep(@NotNull StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }


    @Override
    public void write(List<? extends T> items) {
        try {
            Long before = 0L;
            if (stepExecution.getExecutionContext().containsKey(JobConstant.STEP_EXECUTION_WRITE_TIME)) {
                before = stepExecution.getExecutionContext().getLong(JobConstant.STEP_EXECUTION_WRITE_TIME);
            }
            StopWatch sp = new StopWatch();
            sp.start();
            sendItems(items);
            stepExecution.getExecutionContext().putLong(JobConstant.STEP_EXECUTION_WRITE_TIME, before + sp.getTotalTimeMillis());
        } catch (Exception e) {
            stepExecution.setExitStatus(ExitStatus.FAILED);
            stepExecution.addFailureException(e);
            throw e;
        } finally {
            stepExecution.getExecutionContext().putInt(JobConstant.STEP_EXECUTION_PARAMS_CURRENT_PAGE, stepExecution.getCommitCount() + 1);
        }
    }

    /**
     * 인터페이스 송신
     * @param items 조회 청크 단위 아이템
     */
    public abstract void sendItems(List<? extends T> items);

    /**
     * 에러 로깅 재귀 호출
     * @param e
     */
    private void errorLogging(Throwable e) {
        log.error(e.getMessage());
        if (e.getCause() != null) {
            errorLogging(e.getCause());
        }
    }
}
