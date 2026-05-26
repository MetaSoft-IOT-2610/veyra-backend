package com.novaperutech.veyra.platform.activities.infrastructure.persistence.jpa.repositories;

import com.novaperutech.veyra.platform.activities.domain.model.aggregates.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByResidentId(Long residentId);
}
