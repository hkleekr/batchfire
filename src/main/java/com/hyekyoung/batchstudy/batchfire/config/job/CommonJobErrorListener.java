package com.hyekyoung.batchstudy.batchfire.config.job;

import com.hyekyoung.batchstudy.batchfire.connect.ConnectManagers;
import com.hyekyoung.batchstudy.batchfire.exception.WrapperException;
import com.hyekyoung.batchstudy.batchfire.model.dto.JobError;
import com.hyekyoung.batchstudy.batchfire.model.dto.StepError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CommonJobErrorListener extends AbstractJobListener{
    private final String interfaceId;
    private final ConnectManagers connectManagers;

    /**
     * 생성자
     * @param interfaceId
     * @param connectManagers
     */
    public CommonJobErrorListener(String interfaceId, ConnectManagers connectManagers) {
        this.interfaceId = interfaceId;
        this.connectManagers = connectManagers;
    }

    @Override
    public void beforeDo(JobExecution jobExecution) {
        this.jobExecution = jobExecution;
    }

    /**
     * Job 종료 시 JOB / STEP에서 수집된 Error 객체 공통 처리
     * @param jobExecution
     */
    @Override
    public void afterDo(JobExecution jobExecution) {
        JobError jobError = this.createJobError(jobExecution);
        if (jobError != null) {
            log.error(createErrorMessage(jobError));
        }
    }

    /**
     * Job Error 생성
     * @param jobExecution
     * @return
     */
    private JobError createJobError(JobExecution jobExecution) {
        JobError jobError = null;

        if (jobExecution.getAllFailureExceptions().size() > 0) {
            jobError = new JobError();
            jobError.setJobName(jobExecution.getJobInstance().getJobName());
            if (jobExecution.getExecutionContext().containsKey(JobConstant.JOB_EXECUTION_PARAMS_TRACE_ID))
                jobError.setInterfaceId(jobExecution.getExecutionContext().getString(JobConstant.JOB_EXECUTION_PARAMS_TRACE_ID));
            jobError.setInterfaceId(this.interfaceId);
            jobError.setStartTime(String.valueOf(jobExecution.getStartTime()));
            jobError.setEndTime(String.valueOf(jobExecution.getEndTime()));

            List<StepError> stepErrorDtoList = new ArrayList<>();
            for (Throwable failureException : jobExecution.getFailureExceptions()) {
                StepError stepError = new StepError();
                WrapperException wrapperException = null;

                if (failureException instanceof WrapperException) {
                    wrapperException = (WrapperException) failureException;
                } else {
                    wrapperException = new WrapperException(this.interfaceId, "NOT_IN_STEP");
                }

                stepError.setStepInterfaceId(wrapperException.getStepInterfaceId());
                stepError.setStepName(wrapperException.getStepName());
                stepError.setErrorMessage(wrapperException.getMessage());
                stepErrorDtoList.add(stepError);
                jobError.setStepErrors(stepErrorDtoList);
            }
        }
        return jobError;
    }

    /**
     * JobError 기반 로그 메시지 생성
     * @param jobError
     * @return
     */
    private String createErrorMessage(JobError jobError) {
        if (jobError == null) return "";
        StringBuffer sb = new StringBuffer();
        String lineSeparator = "\n";

        sb.append(lineSeparator);
        sb.append("=============ERROR_STATUS_REPORT=============" + lineSeparator);
        sb.append("JOB_NAME:          " + jobError.getJobName() + lineSeparator);
        sb.append("JOB_INTERFACE_ID:  " + jobError.getInterfaceId() + lineSeparator);
        sb.append("JOB_START_TIME:    " + jobError.getStartTime() + lineSeparator);
        sb.append("JOB_END_TIME:      " + jobError.getEndTime() + lineSeparator);
        if (jobError.getStepErrors() != null) {
            for (int i = 0; i < jobError.getStepErrors().size(); i++) {
                sb.append("================ERROR_DETAILS================" + lineSeparator);
                sb.append("STEP_NAME:          " + jobError.getStepErrors().get(i).getStepName() + lineSeparator);
                sb.append("STEP_INTERFACE_ID:  " + jobError.getStepErrors().get(i).getStepInterfaceId() + lineSeparator);
                sb.append("ERROR_MESSAGE:      " + jobError.getStepErrors().get(i).getErrorMessage() + lineSeparator);
            }
        }
        sb.append("=============================================");
        return sb.toString();
    }
}
