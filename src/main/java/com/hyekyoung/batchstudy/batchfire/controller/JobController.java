package com.hyekyoung.batchstudy.batchfire.controller;

import com.hyekyoung.batchstudy.batchfire.config.job.JobConstant;
import com.hyekyoung.batchstudy.batchfire.exception.DetailException;
import com.hyekyoung.batchstudy.batchfire.exception.ErrorCode;
import com.hyekyoung.batchstudy.batchfire.model.dto.JobInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "batch/", produces = "application/json; charset=UTF-8")
@RestController
public class JobController {
    private final JobLauncher jobLauncher;
    private final ApplicationContext applicationContext;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;


    /**
     * On-Demand Batch Job 실행
     * @param onDemandJobParams
     * @return
     * @throws Exception
     */
    @PostMapping("/start/Job")
    public ResponseEntity<List<JobInfo>> runJob(@RequestBody List<OnDemandJobParam> onDemandJobParams) throws Exception {
        log.debug("runJob");
        List<JobInfo> results = new ArrayList<>();
        List<JobExecution> jobExecutions = new ArrayList<>();

        try {
            for (OnDemandJobParam onDemandJobParam : onDemandJobParams) {
                Map<String, Object> result = new HashMap<>();
                Map<String, JobParameter> confMap = new HashMap<>();
                confMap.put("time", new JobParameter(System.currentTimeMillis()));
                for (String key : onDemandJobParam.getJobParam().keySet()) {
                    confMap.put(key, new JobParameter(onDemandJobParam.getJobParam().get(key)));
                }
                Job job = applicationContext.getBean(onDemandJobParam.getBeanName(), Job.class);
                jobExecutions.add(jobLauncher.run(job, new JobParameters(confMap)));
            }

            results = jobExecutions.stream()
                    .map(jobExecution -> (JobInfo) jobExecution.getExecutionContext().get(JobConstant.JOB_INFO))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(results);
    }

    /**
     * On-Demand Batch Parallel Job 실행
     * @param onDemandJobParams
     * @return
     * @throws Exception
     */
    @PostMapping("/start/parallelJob")
    public ResponseEntity<List<JobInfo>> runParallelJob(@RequestBody List<OnDemandJobParam> onDemandJobParams) throws Exception {
        List<JobInfo> results = new ArrayList<>();
        List<JobExecution> jobExecutions = new ArrayList<>();

        try {
            Callable<JobExecution>[] callables = new Callable[onDemandJobParams.size()];
            for (int i = 0; i < onDemandJobParams.size(); i++) {
                int idx = i;
                callables[idx] = (() -> {
                    JobExecution jobExecution = null;
                    Map<String, Object> result = new HashMap<>();
                    Map<String, JobParameter> confMap = new HashMap<>();
                    confMap.put("time", new JobParameter(System.currentTimeMillis()));
                    if (onDemandJobParams.get(idx).getJobParam() != null) {
                        for (String key : onDemandJobParams.get(idx).getJobParam().keySet()) {
                            confMap.put(key, new JobParameter(onDemandJobParams.get(idx).getJobParam().get(key)));
                        }
                    }
                    Job job = applicationContext.getBean(onDemandJobParams.get(idx).getBeanName(), Job.class);
                    try {
                        jobExecution = jobLauncher.run(job, new JobParameters(confMap));
                    } catch (Exception e) {
                        throw e;
                    }
                    return jobExecution;
                });
            }

            CompletableFuture<JobExecution>[] futures = new CompletableFuture[callables.length];
            for (int i = 0; i < callables.length; i++) {
                int idx = i;
                futures[i] = CompletableFuture.supplyAsync(() -> {
                    try {
                        return callables[idx].call();
                    } catch (Exception e) {
                        throw new DetailException(ErrorCode.SCHEDULER_FAIL, e);
                    }
                }, this.threadPoolTaskExecutor
                );
            }
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures);
            allOf.get();
            Arrays.stream(futures).forEach(f -> {
                jobExecutions.add(f.join());
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(results);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class OnDemandJobParam {
        private String beanName;
        private Map<String, String> jobParam;
    }
}

