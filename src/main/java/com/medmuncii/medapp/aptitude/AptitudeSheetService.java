package com.medmuncii.medapp.aptitude;

import com.medmuncii.medapp.employee.Employee;
import com.medmuncii.medapp.employee.EmployeeRepository;
import org.springframework.stereotype.Service;

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

    public AptitudeSheet createSheet(AptitudeSheet sheet) {
        if (sheet.getEmployee() != null && sheet.getEmployee().getId() != null) {
            Employee employee = employeeRepository.findById(sheet.getEmployee().getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            sheet.setEmployee(employee);
        }
        return aptitudeSheetRepository.save(sheet);
    }
}