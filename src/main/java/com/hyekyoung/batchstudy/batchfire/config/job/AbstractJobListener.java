package com.hyekyoung.batchstudy.batchfire.config.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

@Slf4j
public abstract class AbstractJobListener implements JobExecutionListener {
    protected JobExecution jobExecution;
    public abstract void beforeDo(JobExecution jobExecution);
    public abstract void afterDo(JobExecution jobExecution);

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        this.jobExecution = jobExecution;
        try {
            beforeDo(this.jobExecution);
        } catch (Exception e) {
            jobExecution.setExitStatus(ExitStatus.FAILED);
            jobExecution.setStatus(BatchStatus.FAILED);
            jobExecution.addFailureException(e);
        }
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        ExitStatus exitStatus = null;
        try {
            afterDo(jobExecution);
        } catch (Exception e) {
            jobExecution.setExitStatus(ExitStatus.FAILED);
            jobExecution.setStatus(BatchStatus.FAILED);
            jobExecution.addFailureException(e);
        }
    }
}
