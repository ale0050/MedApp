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
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Employee employee;

    @Column(name = "exam_type", length = 500)
    private String examType;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Column(name = "next_exam_date")
    private LocalDate nextExamDate;

    @Column(name = "medical_conclusion", nullable = false, length = 500)
    private String medicalConclusion;

    @Column(name = "observations", length = 1000)
    private String observations;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public LocalDate getExamDate() { return examDate; }
    public void setExamDate(LocalDate examDate) { this.examDate = examDate; }

    public LocalDate getNextExamDate() { return nextExamDate; }
    public void setNextExamDate(LocalDate nextExamDate) { this.nextExamDate = nextExamDate; }

    public String getMedicalConclusion() { return medicalConclusion; }
    public void setMedicalConclusion(String medicalConclusion) { this.medicalConclusion = medicalConclusion; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
}
