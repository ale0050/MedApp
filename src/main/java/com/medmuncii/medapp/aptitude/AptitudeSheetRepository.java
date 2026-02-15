package com.medmuncii.medapp.aptitude;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AptitudeSheetRepository extends JpaRepository<AptitudeSheet, Long> {
    List<AptitudeSheet> findByEmployeeId(Long employeeId);
}
