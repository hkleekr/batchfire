package com.hyekyoung.batchstudy.batchfire.config.job;

import com.hyekyoung.batchstudy.batchfire.exception.WrapperException;
import com.hyekyoung.batchstudy.batchfire.model.dto.StepInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class CommonStepListener extends AbstractStepListener{
    private final String interfaceId;
    private final int chunkSize;

    /**
     * 생성자
     * @param interfaceId
     * @param chunkSize
     */
    public CommonStepListener(String interfaceId, int chunkSize) {
        this.interfaceId = interfaceId;
        this.chunkSize = chunkSize;
    }

    /**
     * StepExecution Context 초기화
     * @param stepExecution
     */
    @Override
    public void beforeDo(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        this.stepExecution = stepExecution;
        this.jobExecution = jobExecution;

        // JOB & STEP  PARAM INIT
        stepExecution.getExecutionContext().putString(JobConstant.STEP_EXECUTION_PARAMS_INTERFACE_ID, interfaceId);
        stepExecution.getExecutionContext().putInt(JobConstant.STEP_EXECUTION_PARAMS_CHUNK_SIZE, chunkSize);
        stepExecution.getExecutionContext().putInt(JobConstant.STEP_EXECUTION_PARAMS_TOTAL_COUNT, 0);
        stepExecution.getExecutionContext().putInt(JobConstant.STEP_EXECUTION_PARAMS_SUB_COUNT, 0);
        stepExecution.getExecutionContext().putInt(JobConstant.STEP_EXECUTION_PARAMS_TOTAL_PAGE, 0);
        stepExecution.getExecutionContext().putInt(JobConstant.STEP_EXECUTION_PARAMS_CURRENT_PAGE, 0);
        stepExecution.getExecutionContext().putInt(JobConstant.STEP_EXECUTION_PARAMS_SKIP_COUNT, 0);
    }

    /**
     * Step Exception Job으로 전파
     * @param stepExecution
     * @return
     */
    @Override
    public ExitStatus afterDo(StepExecution stepExecution) {
        if (stepExecution.getExitStatus().getExitCode() != ExitStatus.COMPLETED.getExitCode() || stepExecution.getStatus().isUnsuccessful()) {
            if (stepExecution.getFailureExceptions() != null && !stepExecution.getFailureExceptions().isEmpty()) {
                WrapperException wrapperException = new WrapperException(this.interfaceId, this.stepExecution.getStepName());
                for (Throwable t : stepExecution.getFailureExceptions()) {
                    wrapperException.addSuppressed(t);
                }
                this.jobExecution.addFailureException(wrapperException);
            }
        }

        List<StepInfo> stepInfoList = this.createStepInfoList(stepExecution);
        this.jobExecution.getExecutionContext().put(JobConstant.JOB_EXECUTION_STEP_INFO_LIST, stepInfoList);
        return stepExecution.getExitStatus();
    }

    /**
     * StepInfo List 생성, 정의된 Step 순서에 따라 적재
     * @param stepExecution
     * @return
     */
    private List<StepInfo> createStepInfoList(StepExecution stepExecution) {
        List<StepInfo> stepInfoList = (List<StepInfo>) this.jobExecution.getExecutionContext().get(JobConstant.JOB_EXECUTION_STEP_INFO_LIST);
        if (stepInfoList == null) {
            stepInfoList = new ArrayList<>();
        }
        int stepIdx = stepInfoList.size();

        StepInfo stepInfo = new StepInfo();
        stepInfo.setStepIdx(String.valueOf(stepIdx));
        stepInfo.setStepName(stepExecution.getStepName());
        stepInfo.setInterfaceId(this.interfaceId);
        stepInfo.setStepStatus(stepExecution.getExitStatus().getExitCode());
        stepInfo.setStepExitDescription(stepExecution.getExitStatus().getExitDescription());
        stepInfo.setStepTotalCount(stepExecution.getExecutionContext().getInt(JobConstant.STEP_EXECUTION_PARAMS_TOTAL_COUNT));
        stepInfo.setStepTotalPage(stepExecution.getExecutionContext().getInt(JobConstant.STEP_EXECUTION_PARAMS_TOTAL_PAGE));

        if (stepExecution.getExecutionContext().containsKey(JobConstant.STEP_EXECUTION_READ_TIME)) {
            stepInfo.setReadTime(stepExecution.getExecutionContext().getLong(JobConstant.STEP_EXECUTION_READ_TIME));
        } else {
            stepInfo.setReadTime(0L);
        }
        if (stepExecution.getExecutionContext().containsKey(JobConstant.STEP_EXECUTION_WRITE_TIME)) {
            stepInfo.setWriteTime(stepExecution.getExecutionContext().getLong(JobConstant.STEP_EXECUTION_WRITE_TIME));
        } else {
            stepInfo.setWriteTime(0L);
        }

        Long startTimeRaw = stepExecution.getStartTime().getTime();
        Long endTimeRaw = System.currentTimeMillis();
        Long durationTimeRaw = endTimeRaw - startTimeRaw;

        stepInfo.setStartTimeRaw(startTimeRaw);
        stepInfo.setEndTimeRaw(endTimeRaw);
        stepInfo.setDurationTimeRaw(durationTimeRaw);
        stepInfo.setStartTimeStr(new SimpleDateFormat(JobConstant.DATE_FORMAT).format(new Date(startTimeRaw)));
        stepInfo.setEndTimeStr(new SimpleDateFormat(JobConstant.DATE_FORMAT).format(new Date(endTimeRaw)));
        stepInfo.setDurationTimeStr(durationTimeRaw + "ns");

        stepInfoList.add(stepInfo);
        return stepInfoList;
    }
}
