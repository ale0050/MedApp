package com.medmuncii.medapp.employee;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/company/{companyId}")
    public List<Employee> getByCompany(@PathVariable Long companyId) {
        return employeeService.getEmployeesByCompany(companyId);
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Angajatul nu a fost gasit"));
    }

    @PostMapping("/import/{companyId}")
    public void importEmployees(@PathVariable Long companyId, @RequestParam("file") MultipartFile file) throws IOException {
        employeeService.importEmployeesFromExcel(companyId, file);
    }
}
