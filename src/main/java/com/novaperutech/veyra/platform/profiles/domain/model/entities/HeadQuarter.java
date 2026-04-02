package com.novaperutech.veyra.platform.profiles.domain.model.entities;

import com.novaperutech.veyra.platform.profiles.domain.model.aggregates.BusinessProfile;
import com.novaperutech.veyra.platform.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Getter
public class HeadQuarter extends AuditableModel {
@ManyToOne
    private BusinessProfile businessProfile;
private String name;

}
