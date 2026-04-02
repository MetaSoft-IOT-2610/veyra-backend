package com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories;
import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Relative;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RelativeRepository extends JpaRepository<Relative,Long> {
    boolean existsByUserId(UserId userId);
}
