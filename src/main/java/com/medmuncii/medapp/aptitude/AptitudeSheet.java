package com.medmuncii.medapp.aptitude;

import com.medmuncii.medapp.employee.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "aptitude_sheets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AptitudeSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate examDate;

    private LocalDate nextExamDate;

    @Column(nullable = false)
    private String medicalConclusion; // Apt, Apt conditionat, etc.

    private String observations;
}
