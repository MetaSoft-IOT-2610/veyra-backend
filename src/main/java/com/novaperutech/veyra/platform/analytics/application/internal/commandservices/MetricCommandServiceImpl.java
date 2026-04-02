package com.novaperutech.veyra.platform.analytics.application.internal.commandservices;
import com.novaperutech.veyra.platform.analytics.domain.model.aggregates.Metric;
import com.novaperutech.veyra.platform.analytics.domain.model.commands.RecordMetricCommand;
import com.novaperutech.veyra.platform.analytics.domain.model.valueobjects.NursingHomeId;
import com.novaperutech.veyra.platform.analytics.domain.services.MetricCommandService;
import com.novaperutech.veyra.platform.analytics.infrastructure.persistence.jpa.repositories.MetricRepository;
import org.springframework.stereotype.Service;
@Service
public class MetricCommandServiceImpl implements MetricCommandService {
    private final MetricRepository metricRepository;
    public MetricCommandServiceImpl(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }
    @Override
    public void handle(RecordMetricCommand command) {
        var nursingHomeId = new NursingHomeId(command.nursingHomeId());
        var existingMetric = metricRepository.findByNursingHomeIdAndMetricTypeAndEventDate(nursingHomeId,command.metricType(),command.eventDate());
        if (existingMetric.isPresent()) {
            var metric = existingMetric.get();
            metric.incrementValue();
            metricRepository.save(metric);
        } else {
            var newMetric = new Metric(nursingHomeId,command.metricType(),command.metricCategory(),command.eventDate());
            metricRepository.save(newMetric);
        }
    }
}