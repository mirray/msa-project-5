package com.example.batchprocessing;

import io.prometheus.client.exporter.PushGateway;
import io.prometheus.client.CollectorRegistry;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class BatchJobPushgatewayListener implements JobExecutionListener {

    private final PushGateway pushGateway;
    private final CollectorRegistry collectorRegistry;

    // Внедряем бины, которые Spring Boot создал автоматически
    public BatchJobPushgatewayListener(PushGateway pushGateway, CollectorRegistry collectorRegistry) {
        this.pushGateway = pushGateway;
        this.collectorRegistry = collectorRegistry;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // Действия перед стартом джобы
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        try {
            // Принудительно выталкиваем все метрики (включая те, что собрал наш StepExecutionListener)
            // "spring-batch-job" должен совпадать с именем в настройках
            pushGateway.pushAdd(collectorRegistry, "spring-batch-job");
            System.out.println("Метрики успешно отправлены в Pushgateway!");
        } catch (IOException e) {
            System.err.println("Ошибка отправки метрик в Pushgateway: " + e.getMessage());
        }
    }
}