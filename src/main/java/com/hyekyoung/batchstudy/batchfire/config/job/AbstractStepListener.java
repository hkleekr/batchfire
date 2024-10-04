package com.hyekyoung.batchstudy.batchfire.config.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

@Slf4j
public abstract class AbstractStepListener implements StepExecutionListener {
    protected StepExecution stepExecution;
    protected JobExecution jobExecution;

    public abstract void beforeDo(StepExecution stepExecution);
    public abstract ExitStatus afterDo(StepExecution stepExecution);

    @BeforeStep
    public synchronized void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        this.jobExecution = stepExecution.getJobExecution();

        try {
            beforeDo(this.stepExecution);
        } catch (Exception e) {
            stepExecution.setExitStatus(ExitStatus.FAILED);
            stepExecution.addFailureException(e);
            throw e;
        }
    }

    @AfterStep
    public synchronized ExitStatus afterStep(StepExecution stepExecution) {
        ExitStatus exitStatus = null;
        try {
            exitStatus = afterDo(stepExecution);
        } catch (Exception e) {
            stepExecution.setExitStatus(ExitStatus.FAILED);
            stepExecution.addFailureException(e);
            throw e;
        }
        return exitStatus;
    }
}
