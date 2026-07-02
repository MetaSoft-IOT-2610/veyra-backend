package com.metasoft.veyra.platform.health.domain.model.aggregates;

import com.metasoft.veyra.platform.health.domain.model.valueobjects.MeasurementId;
import com.metasoft.veyra.platform.health.domain.model.valueobjects.ResidentId;
import com.metasoft.veyra.platform.health.domain.model.valueobjects.SeverityLevel;
import com.metasoft.veyra.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class VitalSign extends AuditableAbstractAggregateRoot<VitalSign> {

    @Embedded
    private ResidentId residentId;

    @Embedded
    private MeasurementId measurementId;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeverityLevel severityLevel;

    private Integer heartRate;
    private Integer systolic;
    private Integer diastolic;
    private Double temperature;
    private Integer oxygenSaturation;
    private Integer respiratoryRate;

    protected VitalSign() {
    }

    public VitalSign(ResidentId residentId, MeasurementId measurementId) {
        this.residentId = residentId;
        this.measurementId = measurementId;
        this.severityLevel = SeverityLevel.NORMAL;
    }

    public VitalSign(
            ResidentId residentId,
            MeasurementId measurementId,
            Integer heartRate,
            Integer systolic,
            Integer diastolic,
            Double temperature,
            Integer oxygenSaturation,
            Integer respiratoryRate) {
        this(residentId, measurementId);
        this.heartRate = heartRate;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.temperature = temperature;
        this.oxygenSaturation = oxygenSaturation;
        this.respiratoryRate = respiratoryRate;
    }

}
