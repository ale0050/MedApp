package com.medmuncii.medapp.aptitude;

import com.medmuncii.medapp.employee.Employee;
import com.medmuncii.medapp.employee.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AptitudeSheetService {

    private final AptitudeSheetRepository aptitudeSheetRepository;
    private final EmployeeRepository employeeRepository;

    public AptitudeSheetService(AptitudeSheetRepository aptitudeSheetRepository, EmployeeRepository employeeRepository) {
        this.aptitudeSheetRepository = aptitudeSheetRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<AptitudeSheet> getAllSheets() {
        return aptitudeSheetRepository.findAll();
    }

    public List<AptitudeSheet> getSheetsByEmployee(Long employeeId) {
        return aptitudeSheetRepository.findByEmployeeId(employeeId);
    }

    @Transactional
    public AptitudeSheet createSheet(AptitudeSheet sheet) {
        try {
            if (sheet.getEmployee() == null || sheet.getEmployee().getId() == null) {
                throw new RuntimeException("Angajatul nu a fost selectat!");
            }

            Employee employee = employeeRepository.findById(sheet.getEmployee().getId())
                    .orElseThrow(() -> new RuntimeException("Angajatul cu ID-ul " + sheet.getEmployee().getId() + " nu exista!"));
            
            sheet.setEmployee(employee);
            
            // Asiguram date valide
            if (sheet.getMedicalConclusion() == null) sheet.setMedicalConclusion("APT");
            if (sheet.getExamType() == null) sheet.setExamType("Control Medical Periodic");

            return aptitudeSheetRepository.save(sheet);
        } catch (Exception e) {
            System.err.println("Eroare la salvarea fisei: " + e.getMessage());
            throw new RuntimeException("Eroare interna la salvare: " + e.getMessage());
        }
    }
}
