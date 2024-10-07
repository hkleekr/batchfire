package com.hyekyoung.batchstudy.batchfire.scheduler;

import com.hyekyoung.batchstudy.batchfire.biz.erp.IF_ERP_SF_0001.job.IF_ERP_SF_0001JobConfiguration;
import com.hyekyoung.batchstudy.batchfire.biz.erp.IF_ERP_SF_0002.job.IF_ERP_SF_0002JobConfiguration;
import com.hyekyoung.batchstudy.batchfire.biz.erp.IF_ERP_SF_0003.job.IF_ERP_SF_0003JobConfiguration;
import com.hyekyoung.batchstudy.batchfire.biz.erp.IF_ERP_SF_0004.job.IF_ERP_SF_0004JobConfiguration;
import com.hyekyoung.batchstudy.batchfire.config.scheduler.AbstractSpringScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@Component
@Profile({"local", "!quartz"})
public class SpringScheduler extends AbstractSpringScheduler {
    private final IF_ERP_SF_0001JobConfiguration IF_ERP_SF_0001;
    private final IF_ERP_SF_0002JobConfiguration IF_ERP_SF_0002;
    private final IF_ERP_SF_0003JobConfiguration IF_ERP_SF_0003;
    private final IF_ERP_SF_0004JobConfiguration IF_ERP_SF_0004;

    public SpringScheduler(
            ThreadPoolTaskExecutor threadPoolTaskExecutor
            , JobLauncher jobLauncher
            , IF_ERP_SF_0001JobConfiguration IF_ERP_SF_0001
            , IF_ERP_SF_0002JobConfiguration IF_ERP_SF_0002
            , IF_ERP_SF_0003JobConfiguration IF_ERP_SF_0003
            , IF_ERP_SF_0004JobConfiguration IF_ERP_SF_0004
    )
    {
        super(threadPoolTaskExecutor, jobLauncher);
        this.IF_ERP_SF_0001 = IF_ERP_SF_0001;
        this.IF_ERP_SF_0002 = IF_ERP_SF_0002;
        this.IF_ERP_SF_0003 = IF_ERP_SF_0003;
        this.IF_ERP_SF_0004 = IF_ERP_SF_0004;
    }

    @Override
    protected void errorHandler(Exception e) {
        super.errorLogging(e);
    }

    /**
     * Description  :
     * Period       :
     * Flow         :
     * @return
     */
    @Scheduled(cron = "0 * * * * *")
    public List<JobExecution> schedule() {
        log.debug("schedule");
        List<JobExecution> result = new ArrayList<>();
        try {
            result.addAll(this.checkException(this.asyncRunJob(runner_IF_ERP_SF_0001())));
            result.addAll(this.checkException(this.asyncRunJob(runner_IF_ERP_SF_0002())));
        } catch (Exception e) {
            this.errorHandler(e);
        }
        return result;
    }

    /**
     * Description  :
     * Period       :
     * Flow         :
     * @return
     */
    @Scheduled(cron = "0 * * * * *")
    public List<JobExecution> schedule2() {
        log.debug("schedule2");
        List<JobExecution> result = new ArrayList<>();
        try {
            result.addAll(this.checkException(this.asyncRunJob(runner_IF_ERP_SF_0001(), runner_IF_ERP_SF_0002())));
            result.addAll(this.checkException(this.asyncRunJob(runner_IF_ERP_SF_0003(), runner_IF_ERP_SF_0004())));
        } catch (Exception e) {
            this.errorHandler(e);
        }
        return result;
    }

    /**
     * Interface ID  :
     * Description   :
     * Range         :
     * Period        :
     * @return
     */
    private Callable<JobExecution> runner_IF_ERP_SF_0001() {
        return () -> {
            JobExecution jobExecution = null;
            Map<String, JobParameter> confMap = new HashMap<>();
            confMap.put("time", new JobParameter(System.currentTimeMillis()));

            String fromDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(0)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String toDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(0)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // Job Default Param
            confMap.put("I_FR_DT", new JobParameter(fromDate));
            confMap.put("I_TO_DT", new JobParameter(toDate));
            try {
                jobExecution = jobLauncher.run(IF_ERP_SF_0001.createJob(), new JobParameters(confMap));
            } finally {
                return jobExecution;
            }
        };
    }

    /**
     * Interface ID  :
     * Description   :
     * Range         :
     * Period        :
     * @return
     */
    private Callable<JobExecution> runner_IF_ERP_SF_0002() {
        return () -> {
            JobExecution jobExecution = null;
            Map<String, JobParameter> confMap = new HashMap<>();
            confMap.put("time", new JobParameter(System.currentTimeMillis()));

            String fromDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(0)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String toDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(0)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // Job Default Param
            confMap.put("I_FR_DT", new JobParameter(fromDate));
            confMap.put("I_TO_DT", new JobParameter(toDate));
            try {
                jobExecution = jobLauncher.run(IF_ERP_SF_0002.createJob(), new JobParameters(confMap));
            } finally {
                return jobExecution;
            }
        };
    }

    /**
     * Interface ID  :
     * Description   :
     * Range         :
     * Period        :
     * @return
     */
    private Callable<JobExecution> runner_IF_ERP_SF_0003() {
        return () -> {
            JobExecution jobExecution = null;
            Map<String, JobParameter> confMap = new HashMap<>();
            confMap.put("time", new JobParameter(System.currentTimeMillis()));

            String fromDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(0)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String toDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(0)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // Job Default Param
            confMap.put("I_FR_DT", new JobParameter(fromDate));
            confMap.put("I_TO_DT", new JobParameter(toDate));
            try {
                jobExecution = jobLauncher.run(IF_ERP_SF_0003.createJob(), new JobParameters(confMap));
            } finally {
                return jobExecution;
            }
        };
    }

    /**
     * Interface ID  :
     * Description   :
     * Range         :
     * Period        :
     * @return
     */
    private Callable<JobExecution> runner_IF_ERP_SF_0004() {
        return () -> {
            JobExecution jobExecution = null;
            Map<String, JobParameter> confMap = new HashMap<>();
            confMap.put("time", new JobParameter(System.currentTimeMillis()));

            String fromDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(0)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String toDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(0)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // Job Default Param
            confMap.put("I_FR_DT", new JobParameter(fromDate));
            confMap.put("I_TO_DT", new JobParameter(toDate));
            try {
                jobExecution = jobLauncher.run(IF_ERP_SF_0004.createJob(), new JobParameters(confMap));
            } finally {
                return jobExecution;
            }
        };
    }
}
