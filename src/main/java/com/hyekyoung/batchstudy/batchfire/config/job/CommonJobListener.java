package com.hyekyoung.batchstudy.batchfire.config.job;

import com.hyekyoung.batchstudy.batchfire.model.dto.JobInfo;
import com.hyekyoung.batchstudy.batchfire.model.dto.StepInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ExecutionContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class CommonJobListener extends AbstractJobListener {
    private final JobRepository jobRepository;

    public CommonJobListener(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void beforeDo(JobExecution jobExecution) {
        
    }

    @Override
    public void afterDo(JobExecution jobExecution) {
        JobInfo jobInfo = this.createJobInfo(jobExecution);
        log.info(this.createLogMessage(jobInfo));
    }

    /**
     * JobInfo 생성
     * @param jobExecution
     * @return
     */
    private JobInfo createJobInfo(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        List<StepInfo> stepInfoList = (List<StepInfo>) jobExecution.getExecutionContext().get(JobConstant.JOB_EXECUTION_STEP_INFO_LIST);
        
        String jobExitCode = jobExecution.getExitStatus().getExitCode();
        Long startTimeRaw = jobExecution.getCreateTime().getTime();
        Long endTimeRaw = jobExecution.getEndTime().getTime();
        Long durationTimeRaw = endTimeRaw - startTimeRaw;
        String startStr = new SimpleDateFormat(JobConstant.DATE_FORMAT).format(new Date(startTimeRaw));
        String endStr = new SimpleDateFormat(JobConstant.DATE_FORMAT).format(new Date(endTimeRaw));
        String durationStr = durationTimeRaw + "ns";

        JobInfo jobInfo = new JobInfo();
        jobInfo.setJobName(jobName);
        if (jobExecution.getExecutionContext().get(JobConstant.JOB_EXECUTION_PARAMS_TRACE_ID) != null)
            jobInfo.setTraceId(jobExecution.getExecutionContext().get(JobConstant.JOB_EXECUTION_PARAMS_TRACE_ID).toString());
        jobInfo.setJobStatus(jobExitCode);
        jobInfo.setStartTimeStr(startStr);
        jobInfo.setEndTimeStr(endStr);
        jobInfo.setDurationTimeStr(durationStr);
        jobInfo.setStartTimeRaw(startTimeRaw);
        jobInfo.setEndTimeRaw(endTimeRaw);
        jobInfo.setDurationTimeRaw(durationTimeRaw);
        if (stepInfoList != null && stepInfoList.size() > 0) {
            jobInfo.setStepInfos(stepInfoList);
            jobInfo.setStepCount(stepInfoList.size());
        }
        return jobInfo;
    }

    /**
     * JobInfo 기반 로그 메시지 생성
     * @param jobInfo
     * @return
     */
    public String createLogMessage(JobInfo jobInfo) {
        List<StepInfo> stepInfoList =  jobInfo.getStepInfos();

        StringBuffer sb = new StringBuffer();
        String lineSeparator = "\n";
        sb.append(lineSeparator);
        sb.append("=============JOB_FINISH_STATUS=============" + lineSeparator);
        sb.append("JOB_NAME:             " + jobInfo.getJobName() + lineSeparator);
        sb.append("JOB_STATUS:           " + jobInfo.getJobStatus() + lineSeparator);
        sb.append("JOB_START_TIME:       " + jobInfo.getStartTimeStr() + lineSeparator);
        sb.append("JOB_END_TIME:         " + jobInfo.getEndTimeStr() + lineSeparator);
        sb.append("JOB_DURATION_TIME:    " + jobInfo.getDurationTimeStr() + lineSeparator);
        if (stepInfoList != null && stepInfoList.size() > 0) {
            sb.append("================JOB_DETAILS================" + lineSeparator);
            for (int i = 0; i < stepInfoList.size(); i++) {
                sb.append(String.format("----------------STEP[%d]----------------", i) + lineSeparator);
                sb.append("STEP_NAME:             " + stepInfoList.get(i).getStepName() + lineSeparator);
                sb.append("STEP_INTERFACE_ID:     " + stepInfoList.get(i).getInterfaceId() + lineSeparator);
                sb.append("STEP_TOTAL_COUNT:      " + stepInfoList.get(i).getStepTotalCount() + "(PAGE: " + stepInfoList.get(i).getStepTotalPage() + ")" + lineSeparator);
                sb.append("STEP_STATUS:           " + stepInfoList.get(i).getStepStatus() + lineSeparator);
                sb.append("STEP_START_TIME:       " + stepInfoList.get(i).getStartTimeStr() + lineSeparator);
                sb.append("STEP_END_TIME:         " + stepInfoList.get(i).getEndTimeStr() + lineSeparator);
                sb.append("STEP_DURATION_TIME:    " + stepInfoList.get(i).getDurationTimeStr() + lineSeparator);
                if (stepInfoList.get(i).getReadTime() != null) {
                    sb.append("STEP_READ_TIME:    " + stepInfoList.get(i).getReadTime() + "ns" +  lineSeparator);
                }
                if (stepInfoList.get(i).getWriteTime() != null) {
                    sb.append("STEP_WRITE_TIME:   " + stepInfoList.get(i).getWriteTime() + "ns" + lineSeparator);
                }
            }
        }
        sb.append("===========================================");

        return sb.toString();
    }

    /**
     * JobExecution 정보 최소화 - InMemory 용량
     * @param jobRepository
     * @param jobExecution
     * @param jobInfo
     */
    private void minimizeJobContext(
            JobRepository jobRepository
            , JobExecution jobExecution
            , JobInfo jobInfo)
    {
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(JobConstant.JOB_INFO, jobInfo);
        jobExecution.setExecutionContext(executionContext);
        jobRepository.updateExecutionContext(jobExecution);
    }
}
