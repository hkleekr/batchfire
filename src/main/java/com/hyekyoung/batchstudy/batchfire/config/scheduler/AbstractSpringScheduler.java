package com.hyekyoung.batchstudy.batchfire.config.scheduler;

import com.hyekyoung.batchstudy.batchfire.exception.WrapperException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.batch.api.listener.JobListener;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractSpringScheduler {
    protected final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    protected final JobListener jobListener;

    public AbstractSpringScheduler(ThreadPoolTaskExecutor threadPoolTaskExecutor, JobListener jobListener) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.jobListener = jobListener;
    }

    /**
     * 비동기 JOB 실행 및 결과 콜백
     * @param callables
     * @return
     * @throws Exception
     */
    @SafeVarargs
    protected final List<JobExecution> asyncRunJob(@NotNull Callable<JobExecution>... callables) throws Exception {
        List<JobExecution> result = new ArrayList<>();
        CompletableFuture<JobExecution>[] futures = new CompletableFuture[callables.length];
        for (int i = 0; i < callables.length; i++) {
            int idx = i;
            futures[i] = CompletableFuture.supplyAsync(() -> {
                    try {
                        return callables[idx].call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, this.threadPoolTaskExecutor
            );
        }
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures);
        allOf.get();
        Arrays.stream(futures).forEach(f -> {
            result.add(f.join());
        });
        return result;
    }

    /**
     * Job 실행 결과 JobExecution 분석 및 예외 처리
     * @param jobExecutions
     * @return
     * @throws Exception
     */
    protected List<JobExecution> checkException(List<JobExecution> jobExecutions) throws Exception {
        boolean isNotSuccess = false;
        List<Throwable> throwables = new ArrayList<>();

        for (JobExecution jobExecution : jobExecutions) {
            if (jobExecution.getStatus().isUnsuccessful() || jobExecution.getExitStatus().getExitCode() != ExitStatus.COMPLETED.getExitCode())
                isNotSuccess = true;
            if (jobExecution.getFailureExceptions() != null && !jobExecution.getFailureExceptions().isEmpty()) {
                throwables.addAll(jobExecution.getFailureExceptions());
            }
        }

        if (isNotSuccess || !throwables.isEmpty()) {
            RuntimeException exception = new RuntimeException("스케쥴 실패");
            throwables.forEach(throwable -> exception.addSuppressed(throwable));
            throw exception;
        }
        return jobExecutions;
    }

    protected abstract void errorHandler(Exception e);

    /**
     * Error Logging
     * @param e
     */
    protected void errorLogging(Exception e) {
        if (e.getSuppressed() != null && e.getSuppressed().length > 0) {
            for (int i = 0; i < e.getSuppressed().length; i++) {
                if (e.getSuppressed()[i] instanceof WrapperException) log.error("Exception[{}]: {}", i, (e.getSuppressed()[i]).getMessage());
                else log.error("Exception[{}]: {}", i, (e.getSuppressed()[i]. getMessage()));
            }
        } else {
            log.error("Exception: {}", e.getMessage());
        }
    }
}
