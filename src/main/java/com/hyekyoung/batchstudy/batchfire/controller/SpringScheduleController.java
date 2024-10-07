package com.hyekyoung.batchstudy.batchfire.controller;

import com.hyekyoung.batchstudy.batchfire.model.dto.JobError;
import com.hyekyoung.batchstudy.batchfire.model.dto.JobInfo;
import com.hyekyoung.batchstudy.batchfire.config.scheduler.AbstractSpringScheduler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/batch/", produces = "application/json; charset=UTF-8")
@RestController
@Profile("!quartz")
public class SpringScheduleController {
    private final JobLauncher jobLauncher;
    private final ApplicationContext applicationContext;

    /**
     * On-Demand Batch Schedule 실행
     *
     * @param onDemandScheduleParam
     * @return
     * @throws Exception
     */
    @PostMapping("/start/schedule")
    public ResponseEntity<List<JobInfo>> runSchedule(@RequestBody OnDemandScheduleParam onDemandScheduleParam) throws Exception {
        log.debug("runSchedule");
        List<JobInfo> results = new ArrayList<>();
        List<JobError> errors = new ArrayList<>();
        String errorMsg = "";

        try {
            List<JobExecution> jobExecutions = new ArrayList<>();
            AbstractSpringScheduler clazz = applicationContext.getBean(onDemandScheduleParam.getBeanName(), AbstractSpringScheduler.class);
            Method method = Arrays.stream(clazz.getClass().getDeclaredMethods()).filter(m -> m.getName().equals(onDemandScheduleParam.getMethodName())).findFirst().get();
            if (method == null)
                throw new RuntimeException(String.format("No bean named '%s' available", onDemandScheduleParam.getMethodName()));

            method.setAccessible(true);

            // 스케줄은 파라미터 없음
            Class<?> returnType = method.getReturnType();
            Object invokeReturn = method.invoke(clazz);
        } catch (Exception e) {
            throw e;
        }
        return ResponseEntity.ok(results);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class OnDemandScheduleParam {
        private String beanName;
        private String methodName;
    }
}
