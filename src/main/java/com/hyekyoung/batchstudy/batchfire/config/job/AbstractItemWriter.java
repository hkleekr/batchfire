package com.hyekyoung.batchstudy.batchfire.config.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public abstract class AbstractItemWriter<T> implements StepExecutionListener, ItemWriter<T> {
}
